package com.example.data.repository

import android.content.Context
import com.example.data.local.AppDatabase
import com.example.data.model.AppRole
import com.example.data.model.CategoryRepositoryData
import com.example.data.model.DesignItem
import com.example.data.model.DesignRepositoryData
import com.example.data.model.FabricationCategory
import com.example.data.model.FabricationOrder
import com.example.data.model.MilestoneState
import com.example.data.model.OrderMilestone
import com.example.data.model.Quotation
import com.example.data.model.QuotationStatus
import com.example.data.model.RfqItem
import com.example.data.model.RfqRequest
import com.example.data.model.RfqStatus
import com.example.data.model.VendorProfile
import com.example.data.model.VendorRepositoryData
import com.example.data.model.VendorVerificationStatus
import com.example.data.model.ServiceBooking
import com.example.data.model.ServiceType
import com.example.data.model.BookingStatus
import com.example.data.model.NotificationItem
import com.example.data.model.NotificationCategory
import com.example.data.model.NotificationGroup
import com.example.data.model.CustomerProfileData
import com.example.data.model.SavedAddress
import com.example.data.remote.FirestoreSyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class AuditEvent(
    val id: String = UUID.randomUUID().toString(),
    val entity: String,
    val entityId: String,
    val action: String,
    val actor: String,
    val detail: String,
    val timestamp: Long = System.currentTimeMillis()
)

class FabricationRepository(context: Context) {

    private val db = AppDatabase.getDatabase(context)
    val firestoreSync = FirestoreSyncManager.create(context)

    // Current Active Role
    private val _currentRole = MutableStateFlow(AppRole.CUSTOMER)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    // Currently Selected Vendor for Vendor Portal mode
    private val _selectedVendorId = MutableStateFlow("ven_001")
    val selectedVendorId: StateFlow<String> = _selectedVendorId.asStateFlow()

    // Audit Event Logs (Immutable)
    private val _auditLogs = MutableStateFlow<List<AuditEvent>>(emptyList())
    val auditLogs: StateFlow<List<AuditEvent>> = _auditLogs.asStateFlow()

    // Active "My Requirement" items in current draft session
    private val _requirementDraftItems = MutableStateFlow<List<RfqItem>>(emptyList())
    val requirementDraftItems: StateFlow<List<RfqItem>> = _requirementDraftItems.asStateFlow()

    // Submitted RFQs
    private val _rfqList = MutableStateFlow<List<RfqRequest>>(emptyList())
    val rfqList: StateFlow<List<RfqRequest>> = _rfqList.asStateFlow()

    // Quotations map: rfqId -> List<Quotation>
    private val _quotationsMap = MutableStateFlow<Map<String, List<Quotation>>>(emptyMap())
    val quotationsMap: StateFlow<Map<String, List<Quotation>>> = _quotationsMap.asStateFlow()

    // Active Orders
    private val _orders = MutableStateFlow<List<FabricationOrder>>(emptyList())
    val orders: StateFlow<List<FabricationOrder>> = _orders.asStateFlow()

    // Vendors
    private val _vendors = MutableStateFlow<List<VendorProfile>>(VendorRepositoryData.sampleVendors)
    val vendors: StateFlow<List<VendorProfile>> = _vendors.asStateFlow()

    // Service Bookings (Screen 5)
    private val _serviceBookings = MutableStateFlow<List<ServiceBooking>>(emptyList())
    val serviceBookings: StateFlow<List<ServiceBooking>> = _serviceBookings.asStateFlow()

    // Notifications Feed (Screen 8)
    private val _notifications = MutableStateFlow<List<NotificationItem>>(emptyList())
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Customer Profile & Saved Addresses (Screen 9)
    private val _customerProfile = MutableStateFlow(CustomerProfileData())
    val customerProfile: StateFlow<CustomerProfileData> = _customerProfile.asStateFlow()

    init {
        seedInitialDemoData()
    }

    private fun seedInitialDemoData() {
        // Seed an initial demo RFQ so the user can test quote review and vendor portals immediately
        val initialRef = "CJF-2026-M892"
        val initialRfqId = "demo_rfq_01"
        val initialItem = RfqItem(
            id = "item_demo_01",
            designCode = "CJ-GT-201",
            productType = "Main Gates & Sliding Gates",
            title = "CNC Laser-Cut Geometric Main Gate",
            lengthFt = 12.0,
            heightFt = 6.5,
            unit = "feet",
            material = "Mild Steel (MS IS 2062)",
            materialGrade = "IS 2062 Grade E250",
            finish = "Matte Black Powder Coated",
            quantity = 1,
            installationRequired = true,
            specialInstructions = "Includes Italian motor provision and heavy ball-bearing hinges."
        )

        val demoRfq = RfqRequest(
            id = initialRfqId,
            referenceNumber = initialRef,
            customerName = "Arjun Mehta",
            customerPhone = "+91 98450 67890",
            customerEmail = "arjun.mehta@example.com",
            projectType = "Homeowner (Villa)",
            siteAddress = "Plot 14, Palm Meadows, Whitefield",
            city = "Bengaluru",
            pincode = "560066",
            targetCompletionDate = "25-Nov-2026",
            budgetRange = "₹40,000 - ₹55,000",
            items = listOf(initialItem),
            status = RfqStatus.QUOTED,
            assignedVendorIds = listOf("ven_001", "ven_002")
        )

        val quote1 = Quotation(
            id = "quote_001",
            rfqId = initialRfqId,
            rfqReference = initialRef,
            vendorId = "ven_001",
            vendorName = "Apex Precision Metal Works",
            vendorCity = "Bengaluru",
            vendorRating = 4.85,
            isGstVerified = true,
            baseFabricationCost = 28000.0,
            materialCost = 14500.0,
            installationCost = 3500.0,
            transportCost = 1500.0,
            leadTimeDays = 14,
            validityDate = "15 Days from Issue",
            materialGradeGuarantee = "Tata/SAIL MS Sheet with 80-micron Jotun Powder Coating",
            siteInspectionRequired = true,
            technicalNotes = "Includes 4 heavy-duty ball bearing pivot hinges and laser cut tolerances within 0.5mm."
        )

        val quote2 = Quotation(
            id = "quote_002",
            rfqId = initialRfqId,
            rfqReference = initialRef,
            vendorId = "ven_002",
            vendorName = "Sri Balaji Industrial Fabricators",
            vendorCity = "Bengaluru (Regional Branch)",
            vendorRating = 4.70,
            isGstVerified = true,
            baseFabricationCost = 26500.0,
            materialCost = 13800.0,
            installationCost = 4200.0,
            transportCost = 1800.0,
            leadTimeDays = 18,
            validityDate = "20 Days from Issue",
            materialGradeGuarantee = "Heavy ISMB frame + Epoxy zinc phosphate primer + 2 coats PU paint",
            siteInspectionRequired = false,
            technicalNotes = "Ready for automation gear rack welding."
        )

        _rfqList.value = listOf(demoRfq)
        _quotationsMap.value = mapOf(initialRfqId to listOf(quote1, quote2))

        // Pre-seed an active order with all 9 visual stages for Screen 7 Order Tracking
        val initialOrderMilestones = listOf(
            OrderMilestone(
                stepIndex = 1,
                title = "Booking Confirmed",
                description = "Customer requirement accepted and work docket created.",
                state = MilestoneState.COMPLETED,
                completedTimestamp = System.currentTimeMillis() - 7 * 86400000L,
                remarks = "Order docket ref CJF-BK-2026-1042 locked."
            ),
            OrderMilestone(
                stepIndex = 2,
                title = "Site Measurement",
                description = "Senior field engineer verified exact laser dimensions at site.",
                state = MilestoneState.COMPLETED,
                completedTimestamp = System.currentTimeMillis() - 5 * 86400000L,
                remarks = "Opening: 14' 2\" width x 7' 0\" height. Slope compensation noted."
            ),
            OrderMilestone(
                stepIndex = 3,
                title = "Quotation Approved",
                description = "Customer reviewed and accepted itemized BOQ quotation.",
                state = MilestoneState.COMPLETED,
                completedTimestamp = System.currentTimeMillis() - 4 * 86400000L,
                remarks = "Approved by Arjun Mehta via in-app digital signature."
            ),
            OrderMilestone(
                stepIndex = 4,
                title = "Advance Paid",
                description = "40% mobilization advance received and acknowledged.",
                state = MilestoneState.COMPLETED,
                completedTimestamp = System.currentTimeMillis() - 3 * 86400000L,
                remarks = "Transaction UPI/NEFT Ref #TXN9942084 confirmed."
            ),
            OrderMilestone(
                stepIndex = 5,
                title = "Fabrication in Progress",
                description = "CNC laser cutting, frame welding, and alignment at certified workshop.",
                state = MilestoneState.IN_PROGRESS,
                completedTimestamp = null,
                remarks = "Laser profile cutting finished; currently tack welding frame tubes."
            ),
            OrderMilestone(
                stepIndex = 6,
                title = "Quality Check (QC)",
                description = "Weld seam penetration inspection, dimension check, and 80-micron powder coat adhesion test.",
                state = MilestoneState.PENDING,
                remarks = "Scheduled post surface blasting."
            ),
            OrderMilestone(
                stepIndex = 7,
                title = "Ready for Delivery",
                description = "Protective wrapping applied, loaded on flatbed vehicle with transit insurance.",
                state = MilestoneState.PENDING,
                remarks = "Delivery scheduled via CJF Logistics."
            ),
            OrderMilestone(
                stepIndex = 8,
                title = "Installation at Site",
                description = "Heavy anchor bolting, vertical plum alignment, pivot lubrication, and gate motor hookup.",
                state = MilestoneState.PENDING,
                remarks = "4-member installation crew assigned."
            ),
            OrderMilestone(
                stepIndex = 9,
                title = "Completed & Handover",
                description = "Final customer demonstration, joint sign-off, and 5-year workmanship warranty certificate.",
                state = MilestoneState.PENDING,
                remarks = "Warranty certificate CJF-WR-8821 will be issued upon completion."
            )
        )

        val demoOrder = FabricationOrder(
            id = "demo_ord_01",
            orderNumber = "CJF-ORD-2026-9041",
            rfqId = initialRfqId,
            rfqReference = initialRef,
            quotationId = quote1.id,
            customerName = "Arjun Mehta",
            customerPhone = "+91 98450 67890",
            vendorId = "ven_001",
            vendorName = "Apex Precision Metal Works",
            totalAmount = quote1.grandTotal,
            deliveryAddress = "Plot 14, Palm Meadows Phase 2, Whitefield",
            city = "Bengaluru",
            currentStepIndex = 5,
            milestones = initialOrderMilestones
        )
        _orders.value = listOf(demoOrder)

        // Pre-seed an initial service booking for Screen 5
        val demoBooking = ServiceBooking(
            id = "demo_bk_01",
            referenceNumber = "CJF-BK-2026-1042",
            serviceType = ServiceType.SITE_MEASUREMENT,
            customerName = "Arjun Mehta",
            customerPhone = "+91 98450 67890",
            customerEmail = "arjun.mehta@example.com",
            siteAddress = "Plot 14, Palm Meadows Phase 2, Whitefield",
            landmark = "Opposite Club House",
            city = "Bengaluru",
            pincode = "560066",
            preferredDate = "24-Oct-2026",
            preferredTimeSlot = "Morning (09:00 AM - 12:00 PM)",
            projectDetails = "Main sliding gate (14x7 ft) and first-floor balcony glass balustrade railing (42 running ft).",
            attachedPhotoCount = 3,
            assignedEngineerName = "Suresh Kumar (Senior Field Engineer)",
            assignedEngineerPhone = "+91 98451 22334",
            status = BookingStatus.ENGINEER_ASSIGNED
        )
        _serviceBookings.value = listOf(demoBooking)

        // Pre-seed notification feed grouped into TODAY, YESTERDAY, EARLIER for Screen 8
        _notifications.value = listOf(
            NotificationItem(
                id = "notif_01",
                title = "Fabrication Milestone Updated",
                message = "Laser cutting completed for your Main Gate (CJF-ORD-2026-9041). Now in structural welding.",
                category = NotificationCategory.ORDER_MILESTONE,
                group = NotificationGroup.TODAY,
                timestamp = "10:30 AM",
                referenceId = "demo_ord_01",
                isRead = false
            ),
            NotificationItem(
                id = "notif_02",
                title = "Site Measurement Confirmed",
                message = "Engineer Suresh Kumar is assigned for site visit on 24-Oct-2026 (Ref: CJF-BK-2026-1042).",
                category = NotificationCategory.BOOKING,
                group = NotificationGroup.TODAY,
                timestamp = "09:15 AM",
                referenceId = "demo_bk_01",
                isRead = false
            ),
            NotificationItem(
                id = "notif_03",
                title = "Quotation Ready for Review",
                message = "Apex Precision Metal Works submitted a formal quote of ₹56,050 for RFQ CJF-2026-M892.",
                category = NotificationCategory.QUOTATION,
                group = NotificationGroup.YESTERDAY,
                timestamp = "Yesterday, 04:45 PM",
                referenceId = "quote_001",
                isRead = true
            ),
            NotificationItem(
                id = "notif_04",
                title = "Advance Payment Acknowledged",
                message = "Mobilization advance of ₹22,420 received via UPI for Order CJF-ORD-2026-9041.",
                category = NotificationCategory.PAYMENT,
                group = NotificationGroup.YESTERDAY,
                timestamp = "Yesterday, 02:20 PM",
                referenceId = "demo_ord_01",
                isRead = true
            ),
            NotificationItem(
                id = "notif_05",
                title = "Welcome to CJ Fabrications",
                message = "Connect directly with certified workshop fabricators across India. Transparent quotes & milestone tracking.",
                category = NotificationCategory.SYSTEM,
                group = NotificationGroup.EARLIER,
                timestamp = "14-Oct-2026",
                referenceId = null,
                isRead = true
            )
        )
    }

    fun setRole(role: AppRole) {
        _currentRole.value = role
    }

    // "My Requirement" (RFQ Builder) actions
    fun addItemToRequirement(item: RfqItem) {
        val current = _requirementDraftItems.value.toMutableList()
        current.add(item)
        _requirementDraftItems.value = current
    }

    fun removeItemFromRequirement(itemId: String) {
        val current = _requirementDraftItems.value.toMutableList()
        current.removeAll { it.id == itemId }
        _requirementDraftItems.value = current
    }

    fun clearRequirementDraft() {
        _requirementDraftItems.value = emptyList()
    }

    fun getCategoryById(id: String): FabricationCategory? {
        return CategoryRepositoryData.categories.find { it.id == id }
    }

    fun getDesignById(id: String): DesignItem? {
        return DesignRepositoryData.designs.find { it.id == id }
    }

    fun getRfqById(id: String): RfqRequest? {
        return _rfqList.value.find { it.id == id }
    }

    fun getRfqByReference(ref: String): RfqRequest? {
        return _rfqList.value.find { it.referenceNumber.equals(ref.trim(), ignoreCase = true) }
    }

    // Submit RFQ transactionally
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
        customItems: List<RfqItem>? = null
    ): RfqRequest {
        val itemsToSubmit = customItems ?: _requirementDraftItems.value
        require(itemsToSubmit.isNotEmpty()) { "Cannot submit RFQ without requirement items" }

        val randomCode = (100..999).random()
        val randomChar = ('A'..'Z').random()
        val refNumber = "CJF-2026-$randomChar$randomCode"

        val newRfq = RfqRequest(
            id = UUID.randomUUID().toString(),
            referenceNumber = refNumber,
            customerName = customerName.trim(),
            customerPhone = customerPhone.trim(),
            customerEmail = customerEmail.trim(),
            projectType = projectType,
            siteAddress = siteAddress.trim(),
            city = city.trim(),
            pincode = pincode.trim(),
            targetCompletionDate = targetCompletionDate.trim(),
            budgetRange = budgetRange.trim(),
            items = itemsToSubmit,
            status = RfqStatus.SUBMITTED
        )

        // Pre-assign to top verified vendors for active simulation
        val assignedVendors = _vendors.value.take(2).map { it.id }
        val assignedRfq = newRfq.copy(
            status = RfqStatus.ASSIGNED,
            assignedVendorIds = assignedVendors
        )

        val updatedList = _rfqList.value.toMutableList()
        updatedList.add(0, assignedRfq)
        _rfqList.value = updatedList

        // Generate preliminary fabricator quotes for realistic marketplace experience
        seedQuotesForNewRfq(assignedRfq)

        // Clear draft if it was the session draft
        if (customItems == null) {
            clearRequirementDraft()
        }

        // Sync RFQ to Firestore real-time cloud store
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firestoreSync.syncRfq(assignedRfq, assignedVendors)
            } catch (e: Exception) {
                // Non-blocking sync error
            }
        }

        return assignedRfq
    }

    private fun seedQuotesForNewRfq(rfq: RfqRequest) {
        val quotes = mutableListOf<Quotation>()
        val baseMultiplier = rfq.items.sumOf { it.totalAreaSqFt }.coerceAtLeast(20.0)

        val vendor1 = _vendors.value.getOrNull(0) ?: return
        val quote1 = Quotation(
            id = UUID.randomUUID().toString(),
            rfqId = rfq.id,
            rfqReference = rfq.referenceNumber,
            vendorId = vendor1.id,
            vendorName = vendor1.businessName,
            vendorCity = vendor1.city,
            vendorRating = vendor1.rating,
            isGstVerified = true,
            baseFabricationCost = baseMultiplier * 240.0,
            materialCost = baseMultiplier * 160.0,
            installationCost = if (rfq.items.any { it.installationRequired }) 4500.0 else 0.0,
            transportCost = 1500.0,
            leadTimeDays = 12,
            validityDate = "15 Days",
            materialGradeGuarantee = "Mill Test Certified Jindal/Tata Steel with 80-micron coat",
            siteInspectionRequired = true,
            technicalNotes = "Ready for on-site measurement check. Includes all anchor bolts and mounting hardware."
        )

        val vendor2 = _vendors.value.getOrNull(1) ?: return
        val quote2 = Quotation(
            id = UUID.randomUUID().toString(),
            rfqId = rfq.id,
            rfqReference = rfq.referenceNumber,
            vendorId = vendor2.id,
            vendorName = vendor2.businessName,
            vendorCity = vendor2.city,
            vendorRating = vendor2.rating,
            isGstVerified = true,
            baseFabricationCost = baseMultiplier * 220.0,
            materialCost = baseMultiplier * 155.0,
            installationCost = if (rfq.items.any { it.installationRequired }) 4000.0 else 0.0,
            transportCost = 1800.0,
            leadTimeDays = 16,
            validityDate = "20 Days",
            materialGradeGuarantee = "Prime Heavy Gauge Steel with 2-coat anti-rust primer",
            siteInspectionRequired = false,
            technicalNotes = "Fabrication scheduled within 48h of site clearance."
        )

        quotes.add(quote1)
        quotes.add(quote2)

        val map = _quotationsMap.value.toMutableMap()
        map[rfq.id] = quotes
        _quotationsMap.value = map

        // Transition status to QUOTED
        val updatedRfqs = _rfqList.value.map {
            if (it.id == rfq.id) it.copy(status = RfqStatus.QUOTED) else it
        }
        _rfqList.value = updatedRfqs
    }

    // Format concise, professional WhatsApp handoff
    fun formatWhatsAppMessage(rfq: RfqRequest): String {
        val sb = StringBuilder()
        sb.append("*NEW FABRICATION REQUIREMENT — CJ FABRICATIONS*\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("📋 *RFQ Ref:* ${rfq.referenceNumber}\n")
        sb.append("👤 *Customer:* ${rfq.customerName} (${rfq.projectType})\n")
        sb.append("📍 *Location:* ${rfq.city} - ${rfq.pincode}\n")
        sb.append("📅 *Target Date:* ${rfq.targetCompletionDate}\n")
        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("📦 *ITEMS (${rfq.items.size}):*\n")

        rfq.items.forEachIndexed { index, item ->
            sb.append("${index + 1}. *${item.title}*\n")
            sb.append("   • Specs: ${item.lengthFt} ft (W) x ${item.heightFt} ft (H)\n")
            sb.append("   • Material: ${item.material} (${item.materialGrade})\n")
            sb.append("   • Finish: ${item.finish}\n")
            sb.append("   • Qty: ${item.quantity} | Install: ${if (item.installationRequired) "Yes" else "No"}\n")
        }

        sb.append("━━━━━━━━━━━━━━━━━━━━\n")
        sb.append("🔗 *Track Status / Compare Quotes:*\n")
        sb.append("https://parkzone.in/cjfabrication/track?ref=${rfq.referenceNumber}&token=${rfq.secureAccessToken}\n")
        sb.append("\nPlease review and assign verified fabrication vendors.")

        return sb.toString()
    }

    fun generateWhatsAppUrl(rfq: RfqRequest, businessPhone: String = "919876543210"): String {
        val text = formatWhatsAppMessage(rfq)
        val encodedText = URLEncoder.encode(text, "UTF-8")
        return "https://api.whatsapp.com/send?phone=$businessPhone&text=$encodedText"
    }

    // Customer accepts quotation -> Converts to order with milestone tracking
    fun acceptQuotation(rfqId: String, quoteId: String): FabricationOrder? {
        val rfq = getRfqById(rfqId) ?: return null
        val quotes = _quotationsMap.value[rfqId] ?: return null
        val quote = quotes.find { it.id == quoteId } ?: return null

        // Update quote status
        val updatedQuotes = quotes.map {
            if (it.id == quoteId) it.copy(status = QuotationStatus.ACCEPTED)
            else it.copy(status = QuotationStatus.DECLINED)
        }
        val quoteMap = _quotationsMap.value.toMutableMap()
        quoteMap[rfqId] = updatedQuotes
        _quotationsMap.value = quoteMap

        // Update RFQ status
        val updatedRfqs = _rfqList.value.map {
            if (it.id == rfqId) it.copy(status = RfqStatus.ACCEPTED, acceptedQuotationId = quoteId)
            else it
        }
        _rfqList.value = updatedRfqs

        // Generate Order with milestones
        val orderNum = "ORD-2026-${(1000..9999).random()}"
        val milestones = listOf(
            OrderMilestone(
                stepIndex = 1,
                title = "Order Confirmation & Engineering Approval",
                description = "CAD shop drawing approved and fabrication work order issued.",
                state = MilestoneState.COMPLETED,
                completedTimestamp = System.currentTimeMillis(),
                remarks = "Deposit confirmed and drawing specs locked."
            ),
            OrderMilestone(
                stepIndex = 2,
                title = "Raw Material Procurement & Quality Check",
                description = "Certified steel sections, grade sheets, and hardware procured from authorized mills.",
                state = MilestoneState.IN_PROGRESS,
                completedTimestamp = null,
                remarks = "Steel tubes and laser sheets delivered to workshop."
            ),
            OrderMilestone(
                stepIndex = 3,
                title = "CNC Laser Cutting & Structural Fabrication",
                description = "High-precision laser cut profiles, welding, frame assembly, and dimensional check.",
                state = MilestoneState.PENDING,
                remarks = "Scheduled on 4kW fiber laser."
            ),
            OrderMilestone(
                stepIndex = 4,
                title = "Surface Treatment & 80-Micron Powder Coating",
                description = "Phosphating, degreasing, and automated electrostatic powder coating booth curing.",
                state = MilestoneState.PENDING,
                remarks = "Jotun architectural powder."
            ),
            OrderMilestone(
                stepIndex = 5,
                title = "Factory Inspection & Transit Dispatch",
                description = "Protective bubble/foam packing and transit dispatch to customer site.",
                state = MilestoneState.PENDING,
                remarks = "Vehicle tracking will be updated."
            ),
            OrderMilestone(
                stepIndex = 6,
                title = "On-Site Installation & Handover",
                description = "Civil anchoring, alignment verification, motor testing, and customer sign-off.",
                state = MilestoneState.PENDING,
                remarks = "Final handover certificate issued."
            )
        )

        val newOrder = FabricationOrder(
            id = UUID.randomUUID().toString(),
            orderNumber = orderNum,
            rfqId = rfq.id,
            rfqReference = rfq.referenceNumber,
            quotationId = quote.id,
            customerName = rfq.customerName,
            customerPhone = rfq.customerPhone,
            vendorId = quote.vendorId,
            vendorName = quote.vendorName,
            totalAmount = quote.grandTotal,
            deliveryAddress = rfq.siteAddress,
            city = rfq.city,
            currentStepIndex = 2,
            milestones = milestones
        )

        val orderList = _orders.value.toMutableList()
        orderList.add(0, newOrder)
        _orders.value = orderList

        return newOrder
    }

    fun selectVendor(vendorId: String) {
        _selectedVendorId.value = vendorId
    }

    private fun logAudit(entity: String, entityId: String, action: String, actor: String, detail: String) {
        val event = AuditEvent(
            entity = entity,
            entityId = entityId,
            action = action,
            actor = actor,
            detail = detail
        )
        val current = _auditLogs.value.toMutableList()
        current.add(0, event)
        _auditLogs.value = current
    }

    // Vendor: submit custom quotation for assigned RFQ (Strict RBAC enforced)
    fun submitVendorQuotation(
        rfqId: String,
        vendorId: String,
        baseFabrication: Double,
        materialCost: Double,
        installationCost: Double,
        transportCost: Double,
        leadTimeDays: Int,
        validity: String,
        materialGuarantee: String,
        siteVisitNeeded: Boolean,
        notes: String
    ): Quotation? {
        val rfq = getRfqById(rfqId) ?: throw IllegalArgumentException("RFQ not found: $rfqId")
        val vendor = _vendors.value.find { it.id == vendorId } ?: throw IllegalArgumentException("Vendor not found: $vendorId")

        // RBAC Rule 1: Only VERIFIED fabricators can submit quotations
        if (vendor.verificationStatus != VendorVerificationStatus.VERIFIED) {
            throw IllegalStateException("RBAC Denied: Workshop '${vendor.businessName}' verification status is ${vendor.verificationStatus}. Only verified fabricators with audited machinery can submit quotations.")
        }

        // RBAC Rule 2: Workshop must be explicitly assigned to this RFQ
        if (!rfq.assignedVendorIds.contains(vendorId)) {
            throw IllegalStateException("RBAC Denied: Workshop '${vendor.businessName}' is NOT assigned to RFQ ${rfq.referenceNumber}. You can only bid on assigned customer requirements.")
        }

        val quote = Quotation(
            id = UUID.randomUUID().toString(),
            rfqId = rfqId,
            rfqReference = rfq.referenceNumber,
            vendorId = vendorId,
            vendorName = vendor.businessName,
            contactPerson = vendor.contactPerson,
            vendorPhone = vendor.phone,
            vendorCity = vendor.city,
            vendorRating = vendor.rating,
            isGstVerified = true,
            baseFabricationCost = baseFabrication,
            materialCost = materialCost,
            installationCost = installationCost,
            transportCost = transportCost,
            leadTimeDays = leadTimeDays,
            validityDate = validity,
            materialGradeGuarantee = materialGuarantee,
            siteInspectionRequired = siteVisitNeeded,
            technicalNotes = notes
        )

        val map = _quotationsMap.value.toMutableMap()
        val currentQuotes = map[rfqId]?.toMutableList() ?: mutableListOf()
        currentQuotes.add(quote)
        map[rfqId] = currentQuotes
        _quotationsMap.value = map

        // Update RFQ status to QUOTED if needed
        val updatedRfqs = _rfqList.value.map {
            if (it.id == rfqId && (it.status == RfqStatus.SUBMITTED || it.status == RfqStatus.ASSIGNED)) {
                it.copy(status = RfqStatus.QUOTED)
            } else it
        }
        _rfqList.value = updatedRfqs

        logAudit("quotation", quote.id, "SUBMIT_QUOTE", vendor.businessName, "Quotation of ₹${quote.grandTotal.toInt()} submitted for RFQ ${rfq.referenceNumber}")

        // Sync Quote to Firestore real-time cloud store
        CoroutineScope(Dispatchers.IO).launch {
            try {
                firestoreSync.syncQuote(quote)
            } catch (e: Exception) {
                // Non-blocking sync error
            }
        }

        return quote
    }

    // Admin: Assign RFQ to vendor
    fun assignRfqToVendor(rfqId: String, vendorId: String) {
        val rfqs = _rfqList.value.toMutableList()
        val index = rfqs.indexOfFirst { it.id == rfqId }
        val vendor = _vendors.value.find { it.id == vendorId }
        if (index != -1 && vendor != null) {
            val rfq = rfqs[index]
            val currentAssigned = rfq.assignedVendorIds.toMutableList()
            if (!currentAssigned.contains(vendorId)) {
                currentAssigned.add(vendorId)
            }
            rfqs[index] = rfq.copy(
                assignedVendorIds = currentAssigned,
                status = if (rfq.status == RfqStatus.SUBMITTED) RfqStatus.ASSIGNED else rfq.status
            )
            _rfqList.value = rfqs

            logAudit("rfq", rfqId, "ASSIGN_VENDOR", "Marketplace Admin", "Assigned RFQ ${rfq.referenceNumber} to fabricator '${vendor.businessName}'")
        }
    }

    // Admin: Update vendor verification status
    fun updateVendorVerificationStatus(vendorId: String, newStatus: VendorVerificationStatus) {
        val list = _vendors.value.toMutableList()
        val index = list.indexOfFirst { it.id == vendorId }
        if (index != -1) {
            val updated = list[index].copy(verificationStatus = newStatus)
            list[index] = updated
            _vendors.value = list
            logAudit("vendor_profile", vendorId, "UPDATE_VERIFICATION", "Marketplace Admin", "Updated '${updated.businessName}' status to $newStatus")
        }
    }

    // Admin/Vendor: Advance order milestone
    fun advanceOrderMilestone(orderId: String, stepIndex: Int, remarks: String) {
        val ordersList = _orders.value.toMutableList()
        val index = ordersList.indexOfFirst { it.id == orderId }
        if (index != -1) {
            val order = ordersList[index]
            val updatedMilestones = order.milestones.map { m ->
                if (m.stepIndex == stepIndex) {
                    m.copy(
                        state = MilestoneState.COMPLETED,
                        completedTimestamp = System.currentTimeMillis(),
                        remarks = remarks.ifBlank { m.remarks }
                    )
                } else if (m.stepIndex == stepIndex + 1) {
                    m.copy(state = MilestoneState.IN_PROGRESS)
                } else {
                    m
                }
            }
            ordersList[index] = order.copy(
                currentStepIndex = (stepIndex + 1).coerceAtMost(order.milestones.size),
                milestones = updatedMilestones
            )
            _orders.value = ordersList

            logAudit("order", orderId, "ADVANCE_MILESTONE", order.vendorName, "Step $stepIndex marked COMPLETED: $remarks")
        }
    }

    // Direct Customer-to-Vendor WhatsApp link
    fun generateDirectVendorWhatsAppUrl(rfq: RfqRequest, quote: Quotation): String {
        val cleanPhone = quote.vendorPhone.replace("[^0-9]".toRegex(), "")
        val text = "*INQUIRY FROM CJ FABRICATIONS MARKETPLACE*\n" +
            "Hello ${quote.contactPerson} (${quote.vendorName}),\n" +
            "Regarding RFQ: *${rfq.referenceNumber}*\n" +
            "Customer: *${rfq.customerName}* (${rfq.city})\n" +
            "Your Quotation: *₹${quote.grandTotal.toInt()}* (Lead Time: ${quote.leadTimeDays}d)\n" +
            "I would like to discuss engineering specifications and site measurement schedule."
        val encoded = URLEncoder.encode(text, "UTF-8")
        return "https://api.whatsapp.com/send?phone=$cleanPhone&text=$encoded"
    }

    // Service Booking (Screen 5)
    fun createServiceBooking(
        serviceType: ServiceType,
        customerName: String,
        customerPhone: String,
        customerEmail: String,
        siteAddress: String,
        landmark: String,
        city: String,
        pincode: String,
        preferredDate: String,
        preferredTimeSlot: String,
        projectDetails: String,
        attachedPhotoCount: Int
    ): ServiceBooking {
        val rand = (1000..9999).random()
        val ref = "CJF-BK-2026-$rand"
        val booking = ServiceBooking(
            id = UUID.randomUUID().toString(),
            referenceNumber = ref,
            serviceType = serviceType,
            customerName = customerName.trim(),
            customerPhone = customerPhone.trim(),
            customerEmail = customerEmail.trim(),
            siteAddress = siteAddress.trim(),
            landmark = landmark.trim(),
            city = city.trim().ifBlank { "Bengaluru" },
            pincode = pincode.trim().ifBlank { "560066" },
            preferredDate = preferredDate.trim(),
            preferredTimeSlot = preferredTimeSlot,
            projectDetails = projectDetails.trim(),
            attachedPhotoCount = attachedPhotoCount,
            status = BookingStatus.CONFIRMED
        )

        val list = _serviceBookings.value.toMutableList()
        list.add(0, booking)
        _serviceBookings.value = list

        // Add corresponding notification to Screen 8
        addNotification(
            title = "Service Booking Confirmed ($ref)",
            message = "${serviceType.displayName} scheduled on $preferredDate ($preferredTimeSlot). Field engineer will call before arrival.",
            category = NotificationCategory.BOOKING,
            group = NotificationGroup.TODAY,
            referenceId = booking.id
        )

        logAudit("booking", booking.id, "CREATE_BOOKING", customerName, "Created service booking $ref for ${serviceType.displayName}")
        return booking
    }

    // Notifications Centre (Screen 8)
    fun markNotificationAsRead(notifId: String) {
        val list = _notifications.value.map {
            if (it.id == notifId) it.copy(isRead = true) else it
        }
        _notifications.value = list
    }

    fun markAllNotificationsAsRead() {
        val list = _notifications.value.map { it.copy(isRead = true) }
        _notifications.value = list
    }

    fun addNotification(
        title: String,
        message: String,
        category: NotificationCategory,
        group: NotificationGroup = NotificationGroup.TODAY,
        referenceId: String? = null
    ) {
        val notif = NotificationItem(
            id = UUID.randomUUID().toString(),
            title = title,
            message = message,
            category = category,
            group = group,
            timestamp = "Just now",
            referenceId = referenceId,
            isRead = false
        )
        val list = _notifications.value.toMutableList()
        list.add(0, notif)
        _notifications.value = list
    }

    // Customer Profile & Saved Addresses (Screen 9)
    fun updateCustomerProfile(profile: CustomerProfileData) {
        _customerProfile.value = profile
        logAudit("profile", profile.email, "UPDATE_PROFILE", profile.fullName, "Updated profile preferences")
    }

    fun addSavedAddress(address: SavedAddress) {
        val current = _customerProfile.value
        val updatedList = current.savedAddresses.toMutableList()
        updatedList.add(address)
        _customerProfile.value = current.copy(savedAddresses = updatedList)
    }

    fun deleteSavedAddress(addressId: String) {
        val current = _customerProfile.value
        val updatedList = current.savedAddresses.filter { it.id != addressId }
        _customerProfile.value = current.copy(savedAddresses = updatedList)
    }

    // Quotation actions: Reject & Request Revision (Screen 6)
    fun rejectQuotation(rfqId: String, quoteId: String, reason: String) {
        val quotes = _quotationsMap.value[rfqId] ?: return
        val updatedQuotes = quotes.map {
            if (it.id == quoteId) it.copy(status = QuotationStatus.DECLINED) else it
        }
        val map = _quotationsMap.value.toMutableMap()
        map[rfqId] = updatedQuotes
        _quotationsMap.value = map

        addNotification(
            title = "Quotation Declined",
            message = "You declined quotation $quoteId. Reason: $reason",
            category = NotificationCategory.QUOTATION,
            group = NotificationGroup.TODAY,
            referenceId = quoteId
        )
        logAudit("quotation", quoteId, "DECLINE_QUOTE", "Customer", "Declined quote $quoteId: $reason")
    }

    fun requestQuotationRevision(rfqId: String, quoteId: String, revisionNotes: String) {
        addNotification(
            title = "Revision Requested for Quote",
            message = "Fabricator notified with revision notes: $revisionNotes",
            category = NotificationCategory.QUOTATION,
            group = NotificationGroup.TODAY,
            referenceId = quoteId
        )
        logAudit("quotation", quoteId, "REQUEST_REVISION", "Customer", "Requested revision: $revisionNotes")
    }
}
