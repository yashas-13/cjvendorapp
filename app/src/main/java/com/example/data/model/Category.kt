package com.example.data.model

data class FabricationCategory(
    val id: String,
    val name: String,
    val shortName: String,
    val description: String,
    val indicativeRate: String,
    val rateUnit: String,
    val primaryMaterials: List<String>,
    val popularUses: List<String>,
    val imageUrl: String
)

object CategoryRepositoryData {
    val categories = listOf(
        FabricationCategory(
            id = "cat_main_gates",
            name = "Main Gates & Sliding Gates",
            shortName = "Gates",
            description = "Heavy-duty driveway gates, CNC laser-cut MS gates, automated sliding gates, and ornamental wrought iron entrance gates.",
            indicativeRate = "₹350 - ₹750",
            rateUnit = "per sq. ft.",
            primaryMaterials = listOf("Mild Steel (MS)", "Stainless Steel (SS 304)", "Forged Wrought Iron"),
            popularUses = listOf("Villas", "Gated Communities", "Factories", "Residential Bungalows"),
            imageUrl = "https://images.unsplash.com/photo-1590381105924-c72589b9ef3f?auto=format&fit=crop&w=600&q=80"
        ),
        FabricationCategory(
            id = "cat_railings",
            name = "MS & SS Balcony Railings",
            shortName = "Railings",
            description = "Architectural balcony railings, toughened glass mounted SS 304 balustrades, geometric MS railings, and terrace safety barriers.",
            indicativeRate = "₹550 - ₹1,600",
            rateUnit = "per running ft.",
            primaryMaterials = listOf("SS 304 Grade", "SS 316 Marine Grade", "MS Powder Coated", "Aluminium"),
            popularUses = listOf("Apartment Balconies", "Staircases", "Rooftop Decks", "Commercial Corridors"),
            imageUrl = "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=600&q=80"
        ),
        FabricationCategory(
            id = "cat_staircases",
            name = "Metal Staircases & Handrails",
            shortName = "Stairs",
            description = "Custom spiral staircases, floating cantilever stringers, industrial straight-run fire escapes, and mezzanine access ladders.",
            indicativeRate = "₹2,200 - ₹5,800",
            rateUnit = "per step / riser",
            primaryMaterials = listOf("Heavy MS IS 2062", "Chequered Tread Plates", "SS Handrails"),
            popularUses = listOf("Duplex Houses", "Warehouse Mezzanines", "Industrial Plants", "Cafes"),
            imageUrl = "https://images.unsplash.com/photo-1505691938895-1758d7feb511?auto=format&fit=crop&w=600&q=80"
        ),
        FabricationCategory(
            id = "cat_roofing",
            name = "Roofing, Canopies & Sheds",
            shortName = "Roofing",
            description = "Tubular steel truss sheds, polycarbonate car parking sheds, factory PEB structures, and terrace pergolas.",
            indicativeRate = "₹180 - ₹420",
            rateUnit = "per sq. ft.",
            primaryMaterials = listOf("Galvanized Iron (GI)", "Colour Coated Sheets", "Polycarbonate", "MS Pipe"),
            popularUses = listOf("Car Ports", "Terrace Coverings", "Commercial Sheds", "Entry Canopies"),
            imageUrl = "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?auto=format&fit=crop&w=600&q=80"
        ),
        FabricationCategory(
            id = "cat_window_grills",
            name = "Window Grills & Safety Grates",
            shortName = "Grills",
            description = "Burglar-proof window safety grills, French window grilles, AC outdoor cage brackets, and decorative safety screens.",
            indicativeRate = "₹140 - ₹280",
            rateUnit = "per sq. ft.",
            primaryMaterials = listOf("Solid MS Square Bar", "Flat Iron MS", "SS Hollow Pipe"),
            popularUses = listOf("Apartment Windows", "Ground Floor Security", "Balcony Enclosures"),
            imageUrl = "https://images.unsplash.com/photo-1541888946425-d0fbb186f5f7?auto=format&fit=crop&w=600&q=80"
        ),
        FabricationCategory(
            id = "cat_structural",
            name = "Structural Steel & Mezzanine",
            shortName = "Structural",
            description = "Engineered steel frames, I-beam columns, heavy machinery support platforms, mezzanine floors, and PEB fabrication.",
            indicativeRate = "₹68 - ₹95",
            rateUnit = "per kg fabricated",
            primaryMaterials = listOf("IS 2062 Grade Steel", "Universal I-Beams", "C-Channels", "Baseplates"),
            popularUses = listOf("Warehouses", "Commercial Showrooms", "Factory Extensions"),
            imageUrl = "https://images.unsplash.com/photo-1504307651254-35680f356dfd?auto=format&fit=crop&w=600&q=80"
        ),
        FabricationCategory(
            id = "cat_custom_metalwork",
            name = "Custom Fabrication & CNC Cutting",
            shortName = "Custom",
            description = "Bespoke laser-cut facade screens, metal furniture bases, signage frames, brass inlay metalwork, and prototype fabrications.",
            indicativeRate = "Custom Quote",
            rateUnit = "by engineering drawing",
            primaryMaterials = listOf("MS / SS / Corten Steel / Aluminium / Brass"),
            popularUses = listOf("Architectural Facades", "Luxury Interiors", "Custom Metal Partitions"),
            imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=600&q=80"
        )
    )
}
