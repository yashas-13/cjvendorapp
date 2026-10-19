package com.example.data.model

import java.util.UUID

data class SavedAddress(
    val id: String = UUID.randomUUID().toString(),
    val label: String, // "Home", "Villa Site Project", "Office"
    val fullAddress: String,
    val landmark: String = "",
    val city: String = "Bengaluru",
    val pincode: String = "560066",
    val isDefault: Boolean = false
)

data class CustomerProfileData(
    val fullName: String = "Arjun Mehta",
    val phone: String = "+91 98450 67890",
    val email: String = "arjun.mehta@example.com",
    val roleTitle: String = "Independent Homeowner / Villa Builder",
    val measurementUnit: String = "Feet & Inches (ft/in)",
    val isWhatsAppNotificationEnabled: Boolean = true,
    val isSmsAlertEnabled: Boolean = true,
    val savedAddresses: List<SavedAddress> = listOf(
        SavedAddress(
            id = "addr_01",
            label = "Villa Construction Site",
            fullAddress = "Plot 14, Palm Meadows Phase 2, Ramagondanahalli, Whitefield",
            landmark = "Opposite Club House",
            city = "Bengaluru",
            pincode = "560066",
            isDefault = true
        ),
        SavedAddress(
            id = "addr_02",
            label = "Current Residence",
            fullAddress = "Flat 402, Prestige Silver Oak, ECC Road, Whitefield",
            landmark = "Near Deens Academy",
            city = "Bengaluru",
            pincode = "560066",
            isDefault = false
        )
    )
)
