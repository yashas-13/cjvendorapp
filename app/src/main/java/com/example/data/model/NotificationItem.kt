package com.example.data.model

import java.util.UUID

enum class NotificationGroup {
    TODAY,
    YESTERDAY,
    EARLIER
}

enum class NotificationCategory {
    BOOKING,
    QUOTATION,
    ORDER_MILESTONE,
    PAYMENT,
    SYSTEM
}

data class NotificationItem(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val category: NotificationCategory,
    val group: NotificationGroup,
    val timestamp: String,
    val referenceId: String? = null,
    val isRead: Boolean = false
)
