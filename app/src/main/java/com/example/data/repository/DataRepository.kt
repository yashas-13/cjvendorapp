package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.DesignItem
import com.example.data.model.DesignRepositoryData
import com.example.data.model.ProjectListing
import com.example.data.remote.CjFabricationApi
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class SyncSource {
    POSTGRESQL_API,
    FIRESTORE_REALTIME,
    LOCAL_CACHE,
    INITIAL_SEED
}

data class DataSyncStatus(
    val isSyncing: Boolean = false,
    val lastSyncTimestamp: Long = 0L,
    val lastSource: SyncSource = SyncSource.INITIAL_SEED,
    val itemsCount: Int = 0,
    val error: String? = null
)

/**
 * DataRepository handles real-time synchronization between the local/cloud
 * Firebase Firestore instance and the PostgreSQL backend API for CJ Fabrications
 * project listings and catalog items.
 */
class DataRepository(
    private val context: Context,
    private val api: CjFabricationApi = CjFabricationApi.create(),
    firestoreInstance: FirebaseFirestore? = null
) {
    companion object {
        private const val TAG = "DataRepository"
        const val COLLECTION_PROJECTS = "projects"
    }

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    private val firestore: FirebaseFirestore? = firestoreInstance ?: resolveFirestore(context)

    // In-memory hot state flow initialized from default seed items
    private val initialListings = DesignRepositoryData.designs.map { ProjectListing.fromDesignItem(it) }
    private val _projectListings = MutableStateFlow<List<ProjectListing>>(initialListings)
    val projectListings: StateFlow<List<ProjectListing>> = _projectListings.asStateFlow()

    private val _syncStatus = MutableStateFlow(
        DataSyncStatus(
            isSyncing = false,
            lastSyncTimestamp = System.currentTimeMillis(),
            lastSource = SyncSource.INITIAL_SEED,
            itemsCount = initialListings.size
        )
    )
    val syncStatus: StateFlow<DataSyncStatus> = _syncStatus.asStateFlow()

    private var firestoreListener: ListenerRegistration? = null

    init {
        // Start real-time Firestore listener and background PostgreSQL sync
        startRealtimeFirestoreListener()
        repositoryScope.launch {
            syncWithPostgreSql()
        }
    }

    /**
     * Resolve FirebaseFirestore instance safely with app-configured database ID if present.
     */
    private fun resolveFirestore(context: Context): FirebaseFirestore? {
        return try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            val databaseId = try {
                context.applicationContext.getString(R.string.firestore_database_id)
            } catch (e: Exception) {
                ""
            }
            if (databaseId.isNotEmpty()) {
                FirebaseFirestore.getInstance(databaseId)
            } else {
                FirebaseFirestore.getInstance()
            }
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Firestore unavailable or not provisioned: ${e.message}")
            null
        }
    }

    /**
     * Attaches a real-time snapshot listener to the Firestore "projects" collection.
     * Any change made in Firestore (or from other mobile devices/clients) will trigger
     * immediate UI and cache updates.
     */
    fun startRealtimeFirestoreListener() {
        val db = firestore ?: return
        firestoreListener?.remove()

        try {
            firestoreListener = db.collection(COLLECTION_PROJECTS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Firestore project listings listen error: ${error.message}")
                        _syncStatus.value = _syncStatus.value.copy(
                            isSyncing = false,
                            error = "Firestore sync error: ${error.message}"
                        )
                        return@addSnapshotListener
                    }

                    if (snapshot != null && !snapshot.isEmpty) {
                        val updatedListings = snapshot.documents.mapNotNull { doc ->
                            doc.data?.let { data -> ProjectListing.fromFirestoreMap(data) }
                        }.filter { it.isActive }

                        if (updatedListings.isNotEmpty()) {
                            _projectListings.value = updatedListings
                            _syncStatus.value = _syncStatus.value.copy(
                                isSyncing = false,
                                lastSyncTimestamp = System.currentTimeMillis(),
                                lastSource = SyncSource.FIRESTORE_REALTIME,
                                itemsCount = updatedListings.size,
                                error = null
                            )
                            Log.d(TAG, "Real-time sync received ${updatedListings.size} projects from Firestore")
                        }
                    } else if (snapshot != null && snapshot.isEmpty) {
                        // If Firestore collection is empty, seed it with the baseline items
                        Log.d(TAG, "Firestore projects collection is empty; uploading initial seed items.")
                        repositoryScope.launch {
                            seedFirestoreWithInitialProjects()
                        }
                    }
                }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to register Firestore listener: ${e.message}")
        }
    }

    /**
     * Synchronizes project listings with the PostgreSQL backend API:
     * 1. Fetches authoritative project listings from PostgreSQL API (/api/projects).
     * 2. Merges them into Firestore collection ("projects") to propagate to all real-time listeners.
     * 3. Updates local in-memory StateFlow.
     */
    suspend fun syncWithPostgreSql(): Result<List<ProjectListing>> = withContext(Dispatchers.IO) {
        _syncStatus.value = _syncStatus.value.copy(isSyncing = true, error = null)
        try {
            val response = api.getProjectListings()
            if (response.isSuccessful && response.body()?.success == true) {
                val backendProjects = response.body()?.data ?: emptyList()

                if (backendProjects.isNotEmpty()) {
                    // Update in-memory state
                    _projectListings.value = backendProjects

                    // Merge into Firestore in background so real-time listeners get fresh data
                    firestore?.let { db ->
                        for (project in backendProjects) {
                            try {
                                db.collection(COLLECTION_PROJECTS)
                                    .document(project.id)
                                    .set(project.toFirestoreMap(), SetOptions.merge())
                            } catch (e: Exception) {
                                Log.w(TAG, "Error pushing project ${project.id} to Firestore: ${e.message}")
                            }
                        }
                    }

                    _syncStatus.value = DataSyncStatus(
                        isSyncing = false,
                        lastSyncTimestamp = System.currentTimeMillis(),
                        lastSource = SyncSource.POSTGRESQL_API,
                        itemsCount = backendProjects.size,
                        error = null
                    )
                    Log.d(TAG, "Successfully synced ${backendProjects.size} projects from PostgreSQL")
                    Result.success(backendProjects)
                } else {
                    // Backend returned empty list; seed with local baseline
                    seedFirestoreWithInitialProjects()
                    _syncStatus.value = _syncStatus.value.copy(
                        isSyncing = false,
                        lastSyncTimestamp = System.currentTimeMillis(),
                        lastSource = SyncSource.LOCAL_CACHE
                    )
                    Result.success(_projectListings.value)
                }
            } else {
                val errMsg = response.body()?.error ?: "HTTP ${response.code()}: ${response.message()}"
                Log.w(TAG, "PostgreSQL backend returned error: $errMsg. Operating in offline/cached mode.")
                _syncStatus.value = _syncStatus.value.copy(
                    isSyncing = false,
                    error = "PostgreSQL API unreachable: $errMsg"
                )
                Result.failure(Exception(errMsg))
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to connect to PostgreSQL backend: ${e.message}. Using Firestore/cached listings.")
            _syncStatus.value = _syncStatus.value.copy(
                isSyncing = false,
                error = "Backend sync offline: ${e.message}"
            )
            Result.failure(e)
        }
    }

    /**
     * Save or update a project listing:
     * - Immediately updates Firestore in real-time (and triggers real-time listeners)
     * - Asynchronously persists to PostgreSQL backend database
     */
    suspend fun saveProjectListing(project: ProjectListing): Boolean = withContext(Dispatchers.IO) {
        var firestoreSuccess = false

        // 1. Write to Firestore
        firestore?.let { db ->
            try {
                db.collection(COLLECTION_PROJECTS)
                    .document(project.id)
                    .set(project.toFirestoreMap(), SetOptions.merge())
                firestoreSuccess = true
            } catch (e: Exception) {
                Log.w(TAG, "Error saving project to Firestore: ${e.message}")
            }
        }

        // Also update local StateFlow immediately
        val currentList = _projectListings.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == project.id }
        if (index >= 0) {
            currentList[index] = project
        } else {
            currentList.add(project)
        }
        _projectListings.value = currentList

        // 2. Persist to PostgreSQL backend API
        try {
            val apiResponse = api.createOrUpdateProject(project)
            if (apiResponse.isSuccessful && apiResponse.body()?.success == true) {
                Log.d(TAG, "Project ${project.id} persisted to PostgreSQL backend")
            } else {
                Log.w(TAG, "Project saved to Firestore, but backend responded: ${apiResponse.body()?.error}")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to reach PostgreSQL backend for project save: ${e.message}")
        }

        true
    }

    /**
     * Soft-deletes or removes a project listing from Firestore and PostgreSQL backend.
     */
    suspend fun deleteProjectListing(projectId: String): Boolean = withContext(Dispatchers.IO) {
        // Mark inactive in Firestore
        firestore?.let { db ->
            try {
                db.collection(COLLECTION_PROJECTS)
                    .document(projectId)
                    .update("isActive", false)
            } catch (e: Exception) {
                Log.w(TAG, "Error updating project inactive in Firestore: ${e.message}")
            }
        }

        // Update local StateFlow
        _projectListings.value = _projectListings.value.filter { it.id != projectId }
        true
    }

    /**
     * Seeds Firestore with the baseline catalogue project listings if empty.
     */
    private suspend fun seedFirestoreWithInitialProjects() = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext
        try {
            for (item in initialListings) {
                db.collection(COLLECTION_PROJECTS)
                    .document(item.id)
                    .set(item.toFirestoreMap(), SetOptions.merge())
            }
            Log.d(TAG, "Seeded ${initialListings.size} baseline project listings to Firestore")
        } catch (e: Exception) {
            Log.w(TAG, "Failed seeding initial projects to Firestore: ${e.message}")
        }
    }

    /**
     * Expose a reactive Flow for a single project listing by ID.
     */
    fun observeProjectListingById(id: String): Flow<ProjectListing?> {
        val db = firestore
        if (db == null) {
            return MutableStateFlow(_projectListings.value.firstOrNull { it.id == id })
        }

        return callbackFlow {
            val registration = db.collection(COLLECTION_PROJECTS)
                .document(id)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(_projectListings.value.firstOrNull { it.id == id })
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists() && snapshot.data != null) {
                        trySend(ProjectListing.fromFirestoreMap(snapshot.data!!))
                    } else {
                        trySend(_projectListings.value.firstOrNull { it.id == id })
                    }
                }

            awaitClose {
                registration.remove()
            }
        }
    }

    /**
     * Public manual trigger to refresh synchronization.
     */
    fun triggerSync() {
        repositoryScope.launch {
            syncWithPostgreSql()
        }
    }

    /**
     * Convert project listings to DesignItems for UI components.
     */
    fun getDesignItems(): List<DesignItem> {
        return _projectListings.value.map { it.toDesignItem() }
    }

    /**
     * Cleanup resources when repository is disposed.
     */
    fun cleanup() {
        firestoreListener?.remove()
        firestoreListener = null
        repositoryScope.cancel()
    }
}
