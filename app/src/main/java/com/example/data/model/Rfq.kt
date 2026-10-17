package com.example.data.model

import java.util.UUID

enum class RfqStatus(val label: String, val stepIndex: Int) {
    DRAFT("Draft Requirement", 0),
    SUBMITTED("Submitted & Under Review", 1),
    ASSIGNED("Assigned to Fabricators", 2),
    QUOTED("Quotations Received", 3),
    ACCEPTED("Quotation Accepted", 4),
    IN_PRODUCTION("Fabrication in Progress", 5),
    DISPATCHED("Dispatched / In-Transit", 6),
    INSTALLATION("Site Installation", 7),
    COMPLETED("Completed & Handed Over", 8),
    CANCELLED("Cancelled", -1)
}

data class RfqItem(
    val id: String = UUID.randomUUID().toString(),
    val designCode: String? = null,
    val productType: String,
    val title: String,
    val lengthFt: Double,
    val heightFt: Double,
    val depthFt: Double = 0.0,
    val unit: String = "feet",
    val material: String,
    val materialGrade: String,
    val finish: String,
    val quantity: Int = 1,
    val installationRequired: Boolean = true,
    val specialInstructions: String = "",
    val referenceAttachmentNames: List<String> = emptyList()
) {
    val totalAreaSqFt: Double
        get() = if (heightFt > 0 && lengthFt > 0) lengthFt * heightFt * quantity else 0.0

    val runningLengthFt: Double
        get() = lengthFt * quantity
}

data class RfqRequest(
    val id: String = UUID.randomUUID().toString(),
    val referenceNumber: String,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String = "",
    val projectType: String = "Homeowner", // Homeowner, Contractor, Architect, Builder, Interior Firm
    val siteAddress: String,
    val city: String,
    val pincode: String,
    val targetCompletionDate: String,
    val budgetRange: String = "",
    val items: List<RfqItem>,
    val status: RfqStatus = RfqStatus.SUBMITTED,
    val createdAt: Long = System.currentTimeMillis(),
    val secureAccessToken: String = UUID.randomUUID().toString().replace("-", "").substring(0, 16),
    val assignedVendorIds: List<String> = emptyList(),
    val acceptedQuotationId: String? = null
)
