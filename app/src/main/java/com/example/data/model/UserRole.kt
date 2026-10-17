package com.example.data.model

enum class AppRole(val title: String, val badge: String) {
    CUSTOMER("Customer / Buyer", "Customer"),
    VENDOR("Verified Fabricator", "Vendor"),
    ADMIN("Marketplace Admin", "Admin")
}
