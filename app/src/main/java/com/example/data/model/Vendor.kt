package com.example.data.model

enum class VendorVerificationStatus {
    PENDING_VERIFICATION,
    VERIFIED,
    SUSPENDED
}

data class VendorProfile(
    val id: String,
    val businessName: String,
    val contactPerson: String,
    val phone: String,
    val email: String,
    val workshopAddress: String,
    val city: String,
    val state: String,
    val pincode: String,
    val gstNumber: String,
    val verificationStatus: VendorVerificationStatus,
    val yearsInBusiness: Int,
    val rating: Double,
    val completedProjects: Int,
    val machineryCapabilities: List<String>,
    val categoriesServed: List<String>,
    val serviceRadiusKm: Int
)

object VendorRepositoryData {
    val sampleVendors = listOf(
        VendorProfile(
            id = "ven_001",
            businessName = "Apex Precision Metal Works",
            contactPerson = "Rajesh Sharma",
            phone = "+91 98450 11223",
            email = "rajesh@apexmetalworks.in",
            workshopAddress = "Plot 42, Phase II Industrial Area, Peenya",
            city = "Bengaluru",
            state = "Karnataka",
            pincode = "560058",
            gstNumber = "29AAACA1234D1Z5",
            verificationStatus = VendorVerificationStatus.VERIFIED,
            yearsInBusiness = 12,
            rating = 4.85,
            completedProjects = 340,
            machineryCapabilities = listOf(
                "Fiber CNC Laser 4kW (Sheet metal cutting up to 16mm MS)",
                "CNC Hydraulic Press Brake 150T",
                "TIG & MIG Welding Stations (SS/MS certified)",
                "Automated In-house Powder Coating Booth"
            ),
            categoriesServed = listOf("cat_main_gates", "cat_railings", "cat_staircases", "cat_custom_metalwork"),
            serviceRadiusKm = 75
        ),
        VendorProfile(
            id = "ven_002",
            businessName = "Sri Balaji Industrial Fabricators",
            contactPerson = "G. Venkatesh",
            phone = "+91 97412 88990",
            email = "venkatesh@balajifabricators.in",
            workshopAddress = "Shed 18, SIDCO Industrial Estate",
            city = "Chennai",
            state = "Tamil Nadu",
            pincode = "600098",
            gstNumber = "33AAEFB5678K1Z2",
            verificationStatus = VendorVerificationStatus.VERIFIED,
            yearsInBusiness = 18,
            rating = 4.70,
            completedProjects = 520,
            machineryCapabilities = listOf(
                "Heavy Plate Rolling Machine (up to 20mm)",
                "Submerged Arc Welding (SAW)",
                "Section Bending & Truss Fabricator",
                "Airless Sandblasting & Epoxy Painting"
            ),
            categoriesServed = listOf("cat_roofing", "cat_structural", "cat_main_gates", "cat_staircases"),
            serviceRadiusKm = 120
        ),
        VendorProfile(
            id = "ven_003",
            businessName = "Modern Steel Craft & Grills",
            contactPerson = "Amit Patel",
            phone = "+91 99201 44556",
            email = "amit@modernsteelcraft.com",
            workshopAddress = "Gala 7, GIDC Estate, Makarpura",
            city = "Vadodara",
            state = "Gujarat",
            pincode = "390010",
            gstNumber = "24AABCM9012P1ZF",
            verificationStatus = VendorVerificationStatus.VERIFIED,
            yearsInBusiness = 9,
            rating = 4.90,
            completedProjects = 210,
            machineryCapabilities = listOf(
                "SS Tube Orbital Polishing Machine",
                "CNC Wire EDM & Laser Marking",
                "High Precision TIG Welding",
                "Glass Fitting & Spigot Balustrade Tooling"
            ),
            categoriesServed = listOf("cat_railings", "cat_window_grills", "cat_custom_metalwork"),
            serviceRadiusKm = 50
        ),
        VendorProfile(
            id = "ven_004",
            businessName = "Omkar Steel Fabrications (New Applicant)",
            contactPerson = "Omkar Patil",
            phone = "+91 98230 77889",
            email = "omkar@omkarsteel.in",
            workshopAddress = "MIDC Industrial Area, Bhosari",
            city = "Pune",
            state = "Maharashtra",
            pincode = "411026",
            gstNumber = "27AABCO3456Q1Z8",
            verificationStatus = VendorVerificationStatus.PENDING_VERIFICATION,
            yearsInBusiness = 3,
            rating = 4.20,
            completedProjects = 45,
            machineryCapabilities = listOf(
                "Manual Metal Arc & Gas Cutting",
                "Basic Tube Bending Press"
            ),
            categoriesServed = listOf("cat_window_grills", "cat_railings"),
            serviceRadiusKm = 30
        )
    )
}
