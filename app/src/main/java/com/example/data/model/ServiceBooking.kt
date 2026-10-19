package com.example.data.model

import java.util.UUID

enum class ServiceType(val displayName: String, val description: String) {
    SITE_MEASUREMENT("Site Measurement & Inspection", "Expert engineer visits site with laser tools for accurate dimensions"),
    FABRICATION_ERECTION("Fabrication & Erection", "Complete fabrication, delivery, and on-site welding/anchoring"),
    REPAIR_MAINTENANCE("Repair & Maintenance", "Hinge repair, re-alignment, motor servicing, and anti-rust repainting"),
    AUTOMATION_RETROFIT("Gate Automation Retrofit", "Motorized sliding/swing gate motor installation and remote setup")
}

enum class BookingStatus(val displayName: String) {
    CONFIRMED("Confirmed"),
    ENGINEER_ASSIGNED("Engineer Assigned"),
    VISIT_COMPLETED("Measurement Done"),
    QUOTED("Quotation Ready"),
    CANCELLED("Cancelled")
}

data class ServiceBooking(
    val id: String = UUID.randomUUID().toString(),
    val referenceNumber: String,
    val serviceType: ServiceType,
    val customerName: String,
    val customerPhone: String,
    val customerEmail: String,
    val siteAddress: String,
    val landmark: String = "",
    val city: String = "Bengaluru",
    val pincode: String = "560066",
    val preferredDate: String,
    val preferredTimeSlot: String, // "Morning (09:00 AM - 12:00 PM)", "Afternoon (12:00 PM - 04:00 PM)", "Evening (04:00 PM - 07:00 PM)"
    val projectDetails: String = "",
    val attachedPhotoCount: Int = 0,
    val assignedEngineerName: String = "Suresh Kumar (Senior Field Engineer)",
    val assignedEngineerPhone: String = "+91 98451 22334",
    val status: BookingStatus = BookingStatus.CONFIRMED,
    val createdAt: Long = System.currentTimeMillis()
)
