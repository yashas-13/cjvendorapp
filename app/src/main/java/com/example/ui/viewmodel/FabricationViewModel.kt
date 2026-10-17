package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthManager
import com.example.data.auth.AuthState
import com.example.data.model.AppRole
import com.example.data.model.CategoryRepositoryData
import com.example.data.model.DesignItem
import com.example.data.model.DesignRepositoryData
import com.example.data.model.FabricationCategory
import com.example.data.model.FabricationOrder
import com.example.data.model.ProjectListing
import com.example.data.model.Quotation
import com.example.data.model.RfqItem
import com.example.data.model.RfqRequest
import com.example.data.model.VendorProfile
import com.example.data.repository.DataRepository
import com.example.data.repository.DataSyncStatus
import com.example.data.repository.FabricationRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class FabricationViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FabricationRepository(application.applicationContext)
    val dataRepository = DataRepository(application.applicationContext)
    val authManager = AuthManager(application.applicationContext)
    val authState: StateFlow<AuthState> = authManager.authState
    val firestoreSync = repository.firestoreSync
    val projectListings: StateFlow<List<ProjectListing>> = dataRepository.projectListings
    val projectSyncStatus: StateFlow<DataSyncStatus> = dataRepository.syncStatus

    val currentRole: StateFlow<AppRole> = repository.currentRole
    val requirementDraft: StateFlow<List<RfqItem>> = repository.requirementDraftItems
    val rfqList: StateFlow<List<RfqRequest>> = repository.rfqList
    val quotationsMap: StateFlow<Map<String, List<Quotation>>> = repository.quotationsMap
    val orders: StateFlow<List<FabricationOrder>> = repository.orders
    val vendors: StateFlow<List<VendorProfile>> = repository.vendors

    val categories: List<FabricationCategory> = CategoryRepositoryData.categories

    // Catalog filtering state
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _materialFilter = MutableStateFlow<String?>(null)
    val materialFilter: StateFlow<String?> = _materialFilter.asStateFlow()

    // Filtered designs flow reacting in real-time to DataRepository project listings
    val filteredDesigns: StateFlow<List<DesignItem>> = combine(
        dataRepository.projectListings,
        _selectedCategoryId,
        _searchQuery,
        _materialFilter
    ) { projects, catId, query, material ->
        val designs = projects.map { it.toDesignItem() }
        designs.filter { design ->
            val matchesCategory = catId == null || design.categoryId == catId
            val matchesQuery = query.isBlank() ||
                design.title.contains(query, ignoreCase = true) ||
                design.description.contains(query, ignoreCase = true) ||
                design.code.contains(query, ignoreCase = true)
            val matchesMaterial = material == null ||
                design.materialsAvailable.any { it.contains(material, ignoreCase = true) }
            matchesCategory && matchesQuery && matchesMaterial
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DesignRepositoryData.designs)

    // Current viewed RFQ detail in tracking screen
    private val _currentTrackedRfq = MutableStateFlow<RfqRequest?>(null)
    val currentTrackedRfq: StateFlow<RfqRequest?> = _currentTrackedRfq.asStateFlow()

    // UI Toast / Notification banner
    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun setRole(role: AppRole) {
        repository.setRole(role)
        _userMessage.value = "Switched to ${role.title} mode"
    }

    fun selectCategory(catId: String?) {
        _selectedCategoryId.value = catId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setMaterialFilter(material: String?) {
        _materialFilter.value = material
    }

    // Add item to Requirement
    fun addToRequirement(item: RfqItem) {
        repository.addItemToRequirement(item)
        _userMessage.value = "Added '${item.title}' to My Requirement"
    }

    fun removeFromRequirement(itemId: String) {
        repository.removeItemFromRequirement(itemId)
    }

    fun clearDraft() {
        repository.clearRequirementDraft()
    }

    // Submit RFQ
    fun submitRfq(
        customerName: String,
        customerPhone: String,
        customerEmail: String,
        projectType: String,
        siteAddress: String,
        city: String,
        pincode: String,
        targetCompletionDate: String,
        budgetRange: String,
        customItems: List<RfqItem>? = null,
        onSuccess: (RfqRequest) -> Unit
    ) {
        try {
            val rfq = repository.submitRfq(
                customerName = customerName,
                customerPhone = customerPhone,
                customerEmail = customerEmail,
                projectType = projectType,
                siteAddress = siteAddress,
                city = city,
                pincode = pincode,
                targetCompletionDate = targetCompletionDate,
                budgetRange = budgetRange,
                customItems = customItems
            )
            _currentTrackedRfq.value = rfq
            _userMessage.value = "RFQ ${rfq.referenceNumber} created successfully! Vendors assigned."
            onSuccess(rfq)
        } catch (e: Exception) {
            _userMessage.value = "Submission failed: ${e.message}"
        }
    }

    fun trackRfqByReference(ref: String): Boolean {
        val rfq = repository.getRfqByReference(ref)
        if (rfq != null) {
            _currentTrackedRfq.value = rfq
            return true
        }
        return false
    }

    fun setTrackedRfq(rfq: RfqRequest?) {
        _currentTrackedRfq.value = rfq
    }

    fun getWhatsAppUrl(rfq: RfqRequest): String {
        return repository.generateWhatsAppUrl(rfq)
    }

    fun acceptQuote(rfqId: String, quoteId: String, onOrderCreated: (FabricationOrder) -> Unit) {
        val order = repository.acceptQuotation(rfqId, quoteId)
        if (order != null) {
            _userMessage.value = "Quotation accepted! Work Order ${order.orderNumber} initiated."
            // Update tracked RFQ
            _currentTrackedRfq.value = repository.getRfqById(rfqId)
            onOrderCreated(order)
        }
    }

    val selectedVendorId: StateFlow<String> = repository.selectedVendorId
    val auditLogs = repository.auditLogs

    fun selectVendor(vendorId: String) {
        repository.selectVendor(vendorId)
        val v = vendors.value.find { it.id == vendorId }
        _userMessage.value = "Active workshop set to: ${v?.businessName ?: vendorId}"
    }

    fun submitVendorQuotation(
        rfqId: String,
        vendorId: String,
        baseFab: Double,
        material: Double,
        install: Double,
        transport: Double,
        leadDays: Int,
        validity: String,
        materialGuarantee: String,
        siteVisit: Boolean,
        notes: String
    ) {
        try {
            val quote = repository.submitVendorQuotation(
                rfqId = rfqId,
                vendorId = vendorId,
                baseFabrication = baseFab,
                materialCost = material,
                installationCost = install,
                transportCost = transport,
                leadTimeDays = leadDays,
                validity = validity,
                materialGuarantee = materialGuarantee,
                siteVisitNeeded = siteVisit,
                notes = notes
            )
            if (quote != null) {
                _userMessage.value = "Quotation for RFQ ${quote.rfqReference} submitted successfully!"
            }
        } catch (e: Exception) {
            _userMessage.value = e.message ?: "Quotation submission failed."
        }
    }

    fun assignVendor(rfqId: String, vendorId: String) {
        repository.assignRfqToVendor(rfqId, vendorId)
        _userMessage.value = "Assigned RFQ to fabricator."
    }

    fun updateVendorVerificationStatus(vendorId: String, newStatus: com.example.data.model.VendorVerificationStatus) {
        repository.updateVendorVerificationStatus(vendorId, newStatus)
        _userMessage.value = "Updated vendor verification status to $newStatus"
    }

    fun advanceOrderMilestone(orderId: String, stepIndex: Int, remarks: String) {
        repository.advanceOrderMilestone(orderId, stepIndex, remarks)
        _userMessage.value = "Milestone step $stepIndex updated."
    }

    fun getDirectVendorWhatsAppUrl(rfq: RfqRequest, quote: Quotation): String {
        return repository.generateDirectVendorWhatsAppUrl(rfq, quote)
    }

    fun getDesignById(id: String): com.example.data.model.DesignItem? {
        return projectListings.value.firstOrNull { it.id == id }?.toDesignItem()
            ?: repository.getDesignById(id)
    }
    fun getCategoryById(id: String): com.example.data.model.FabricationCategory? = repository.getCategoryById(id)

    fun signInWithGoogle(onResult: (Result<FirebaseUser>) -> Unit = {}) {
        viewModelScope.launch {
            val result = authManager.signInWithGoogle()
            result.onSuccess { user ->
                _userMessage.value = "Signed in as ${user.displayName ?: user.email ?: "User"}"
                // Sync profile to Firestore
                firestoreSync.syncUserProfile(
                    userId = user.uid,
                    email = user.email,
                    displayName = user.displayName,
                    phoneNumber = user.phoneNumber,
                    role = currentRole.value.name.lowercase()
                )
            }.onFailure { e ->
                if (e !is androidx.credentials.exceptions.GetCredentialCancellationException) {
                    _userMessage.value = "Sign in failed: ${e.message}"
                }
            }
            onResult(result)
        }
    }

    fun signOut() {
        authManager.signOut()
        _userMessage.value = "Signed out"
    }

    fun syncUserProfile(phone: String, role: String) {
        val user = authManager.currentUser ?: return
        viewModelScope.launch {
            firestoreSync.syncUserProfile(
                userId = user.uid,
                email = user.email,
                displayName = user.displayName,
                phoneNumber = phone,
                role = role
            )
        }
    }

    fun triggerProjectSync() {
        dataRepository.triggerSync()
    }

    fun saveProjectListing(project: ProjectListing) {
        viewModelScope.launch {
            dataRepository.saveProjectListing(project)
        }
    }

    override fun onCleared() {
        super.onCleared()
        dataRepository.cleanup()
    }
}

