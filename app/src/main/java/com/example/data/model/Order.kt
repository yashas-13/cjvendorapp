package com.example.data.model

import java.util.UUID

enum class MilestoneState {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}

data class OrderMilestone(
    val stepIndex: Int,
    val title: String,
    val description: String,
    val state: MilestoneState,
    val completedTimestamp: Long? = null,
    val remarks: String = ""
)

data class FabricationOrder(
    val id: String = UUID.randomUUID().toString(),
    val orderNumber: String,
    val rfqId: String,
    val rfqReference: String,
    val quotationId: String,
    val customerName: String,
    val customerPhone: String,
    val vendorId: String,
    val vendorName: String,
    val totalAmount: Double,
    val deliveryAddress: String,
    val city: String,
    val currentStepIndex: Int = 1,
    val milestones: List<OrderMilestone>,
    val createdAt: Long = System.currentTimeMillis()
)
