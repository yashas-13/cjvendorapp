package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.R
import com.example.data.model.Quotation
import com.example.data.model.RfqRequest
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOf
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

data class SyncStatus(
    val isOnline: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val error: String? = null
)

class FirestoreSyncManager(
    private val firestore: FirebaseFirestore?,
    private val auth: FirebaseAuth? = try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
) {

    companion object {
        private const val TAG = "FirestoreSyncManager"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_RFQ_SYNC = "rfq_sync"
        private const val COLLECTION_QUOTES_SYNC = "quotes_sync"

        fun create(context: Context): FirestoreSyncManager {
            return try {
                if (FirebaseApp.getApps(context).isEmpty()) {
                    FirebaseApp.initializeApp(context)
                }
                val databaseId = try {
                    context.applicationContext.getString(R.string.firestore_database_id)
                } catch (e: Exception) {
                    ""
                }
                val db = if (databaseId.isNotEmpty()) {
                    FirebaseFirestore.getInstance(databaseId)
                } else {
                    FirebaseFirestore.getInstance()
                }
                val authInstance = try {
                    FirebaseAuth.getInstance()
                } catch (e: Exception) {
                    null
                }
                FirestoreSyncManager(db, authInstance)
            } catch (e: Exception) {
                Log.w(TAG, "Firebase initialization skipped or unavailable in environment: ${e.message}")
                FirestoreSyncManager(null, null)
            }
        }
    }

    val currentUserId: String?
        get() = auth?.currentUser?.uid

    /**
     * Mirror user profile with role and phone details into Firestore.
     */
    suspend fun syncUserProfile(
        userId: String,
        email: String?,
        displayName: String?,
        phoneNumber: String?,
        role: String
    ): Boolean {
        val db = firestore ?: return false
        return suspendCoroutine { continuation ->
            val data = hashMapOf(
                "uid" to userId,
                "email" to (email ?: ""),
                "displayName" to (displayName ?: ""),
                "phoneNumber" to (phoneNumber ?: ""),
                "role" to role,
                "updatedAt" to System.currentTimeMillis().toString()
            )

            db.collection(COLLECTION_USERS)
                .document(userId)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "User profile synced: $userId")
                    continuation.resume(true)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed to sync user profile: ${e.message}")
                    continuation.resume(false)
                }
        }
    }

    /**
     * Mirror RFQ details into Firestore for real-time mobile sync.
     */
    suspend fun syncRfq(rfq: RfqRequest, assignedVendorIds: List<String> = emptyList()): Boolean {
        val db = firestore ?: return false
        return suspendCoroutine { continuation ->
            val data = hashMapOf(
                "id" to rfq.id,
                "referenceNumber" to rfq.referenceNumber,
                "customerUserId" to (auth?.currentUser?.uid ?: "guest_${rfq.customerPhone}"),
                "customerName" to rfq.customerName,
                "customerPhone" to rfq.customerPhone,
                "itemType" to (rfq.items.firstOrNull()?.productType ?: rfq.items.firstOrNull()?.title ?: "Custom Fabrication"),
                "material" to (rfq.items.firstOrNull()?.material ?: "MS"),
                "quantity" to rfq.items.sumOf { it.quantity },
                "status" to rfq.status.name,
                "assignedVendorIds" to assignedVendorIds,
                "updatedAt" to System.currentTimeMillis().toString()
            )

            db.collection(COLLECTION_RFQ_SYNC)
                .document(rfq.id)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "RFQ synced to Firestore: ${rfq.referenceNumber}")
                    continuation.resume(true)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed to sync RFQ: ${e.message}")
                    continuation.resume(false)
                }
        }
    }

    /**
     * Mirror vendor quotation into Firestore for real-time customer comparison.
     */
    suspend fun syncQuote(quote: Quotation, customerUserId: String? = null): Boolean {
        val db = firestore ?: return false
        return suspendCoroutine { continuation ->
            val data = hashMapOf(
                "id" to quote.id,
                "rfqId" to quote.rfqId,
                "rfqReferenceNumber" to quote.rfqReference,
                "vendorId" to quote.vendorId,
                "vendorName" to quote.vendorName,
                "customerUserId" to (customerUserId ?: ""),
                "totalAmount" to quote.grandTotal,
                "status" to quote.status.name,
                "submittedAt" to System.currentTimeMillis().toString()
            )

            db.collection(COLLECTION_QUOTES_SYNC)
                .document(quote.id)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    Log.d(TAG, "Quote synced to Firestore: ${quote.id}")
                    continuation.resume(true)
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Failed to sync Quote: ${e.message}")
                    continuation.resume(false)
                }
        }
    }

    /**
     * Real-time listener for an RFQ's live sync updates (e.g. status changes, new quotes).
     */
    fun listenToRfqUpdates(rfqId: String): Flow<Map<String, Any>?> {
        val db = firestore ?: return flowOf(null)
        return callbackFlow {
            val registration: ListenerRegistration = db.collection(COLLECTION_RFQ_SYNC)
                .document(rfqId)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Listen failed for RFQ $rfqId", error)
                        trySend(null)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && snapshot.exists()) {
                        trySend(snapshot.data)
                    } else {
                        trySend(null)
                    }
                }

            awaitClose {
                registration.remove()
            }
        }
    }
}
