package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rfqs")
data class RfqEntity(
    @PrimaryKey
    val id: String,
    val referenceNumber: String,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val projectType: String,
    val siteAddress: String,
    val city: String,
    val pincode: String,
    val targetCompletionDate: String,
    val budgetRange: String,
    val status: String,
    val createdAt: Long,
    val secureAccessToken: String,
    val assignedVendorIdsJson: String,
    val acceptedQuotationId: String?
)

@Entity(tableName = "rfq_items")
data class RfqItemEntity(
    @PrimaryKey
    val id: String,
    val rfqId: String,
    val designCode: String?,
    val productType: String,
    val title: String,
    val lengthFt: Double,
    val heightFt: Double,
    val depthFt: Double,
    val unit: String,
    val material: String,
    val materialGrade: String,
    val finish: String,
    val quantity: Int,
    val installationRequired: Boolean,
    val specialInstructions: String,
    val attachmentsJson: String
)

@Entity(tableName = "vendor_quotes")
data class VendorQuoteEntity(
    @PrimaryKey
    val id: String,
    val rfqId: String,
    val rfqReference: String,
    val vendorId: String,
    val vendorName: String,
    val vendorCity: String,
    val vendorRating: Double,
    val isGstVerified: Boolean,
    val baseFabricationCost: Double,
    val materialCost: Double,
    val installationCost: Double,
    val transportCost: Double,
    val gstRatePercent: Double,
    val leadTimeDays: Int,
    val validityDate: String,
    val materialGradeGuarantee: String,
    val siteInspectionRequired: Boolean,
    val technicalNotes: String,
    val status: String,
    val submittedAt: Long
)
