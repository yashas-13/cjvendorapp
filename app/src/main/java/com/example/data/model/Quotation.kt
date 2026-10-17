package com.example.data.model

import java.util.UUID

enum class QuotationStatus {
    PENDING,
    ACCEPTED,
    DECLINED,
    EXPIRED
}

data class Quotation(
    val id: String = UUID.randomUUID().toString(),
    val rfqId: String,
    val rfqReference: String,
    val vendorId: String,
    val vendorName: String,
    val contactPerson: String = "Rajesh Sharma",
    val vendorPhone: String = "+91 98450 11223",
    val vendorCity: String,
    val vendorRating: Double,
    val isGstVerified: Boolean,
    val baseFabricationCost: Double,
    val materialCost: Double,
    val installationCost: Double,
    val transportCost: Double,
    val gstRatePercent: Double = 18.0,
    val leadTimeDays: Int,
    val validityDate: String,
    val materialGradeGuarantee: String,
    val siteInspectionRequired: Boolean = false,
    val technicalNotes: String,
    val status: QuotationStatus = QuotationStatus.PENDING,
    val submittedAt: Long = System.currentTimeMillis()
) {
    val subtotal: Double
        get() = baseFabricationCost + materialCost + installationCost + transportCost

    val gstAmount: Double
        get() = subtotal * (gstRatePercent / 100.0)

    val grandTotal: Double
        get() = subtotal + gstAmount
}
