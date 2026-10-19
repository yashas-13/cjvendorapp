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
    val imageUrl: String,
    val sector: String = "Residential"
)

object CategoryRepositoryData {
    val categories = listOf(
        // ==========================================
        // RESIDENTIAL DIVISIONS (Security, Structures, Interiors, Utility, Architectural)
        // ==========================================
        FabricationCategory(
            id = "cat_main_gates",
            name = "Main Gates & Entrance Fabrication",
            shortName = "Security",
            description = "Main gates (swing, sliding, folding), pedestrian gates, perimeter fencing, motor automation, safety doors & nameplates.",
            indicativeRate = "₹350 - ₹750",
            rateUnit = "per sq. ft.",
            primaryMaterials = listOf("Mild Steel (MS)", "Stainless Steel (SS 304)", "Forged Wrought Iron"),
            popularUses = listOf("Villas", "Gated Communities", "Residential Bungalows", "Independent Houses"),
            imageUrl = "https://images.unsplash.com/photo-1590381105924-c72589b9ef3f?auto=format&fit=crop&w=600&q=80",
            sector = "Residential"
        ),
        FabricationCategory(
            id = "cat_railings",
            name = "Balcony & Staircase Railings",
            shortName = "Architectural",
            description = "Architectural balcony & staircase railings in MS, SS 304/316, toughened glass balustrades, and terrace safety barriers.",
            indicativeRate = "₹550 - ₹1,600",
            rateUnit = "per running ft.",
            primaryMaterials = listOf("SS 304 Grade", "SS 316 Marine Grade", "MS Powder Coated", "Toughened Glass"),
            popularUses = listOf("Apartment Balconies", "Duplex Staircases", "Rooftop Decks", "Corridors"),
            imageUrl = "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=600&q=80",
            sector = "Residential"
        ),
        FabricationCategory(
            id = "cat_staircases",
            name = "Metal Staircases & Handrails",
            shortName = "Stairs",
            description = "Custom spiral staircases, floating cantilever stringers, industrial straight-run fire escapes, and wall handrails.",
            indicativeRate = "₹2,200 - ₹5,800",
            rateUnit = "per step / riser",
            primaryMaterials = listOf("Heavy MS IS 2062", "Chequered Tread Plates", "SS Handrails"),
            popularUses = listOf("Duplex Houses", "Warehouse Mezzanines", "Industrial Plants", "Cafes"),
            imageUrl = "https://images.unsplash.com/photo-1505691938895-1758d7feb511?auto=format&fit=crop&w=600&q=80",
            sector = "Residential"
        ),
        FabricationCategory(
            id = "cat_roofing",
            name = "Roofing, Canopies & Car Parking Sheds",
            shortName = "Structures",
            description = "Tubular steel truss sheds, polycarbonate car parking shades, factory PEB structures, terrace pergolas & awnings.",
            indicativeRate = "₹180 - ₹420",
            rateUnit = "per sq. ft.",
            primaryMaterials = listOf("Galvanized Iron (GI)", "Colour Coated Sheets", "Polycarbonate", "MS Pipe"),
            popularUses = listOf("Car Ports", "Terrace Coverings", "Commercial Sheds", "Entry Canopies"),
            imageUrl = "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?auto=format&fit=crop&w=600&q=80",
            sector = "Residential"
        ),
        FabricationCategory(
            id = "cat_window_grills",
            name = "Window Grills, Doors & Security",
            shortName = "Grills",
            description = "Burglar-proof window safety grills, aluminium sliding windows, SS security doors and collapsible security gates.",
            indicativeRate = "₹140 - ₹280",
            rateUnit = "per sq. ft.",
            primaryMaterials = listOf("Solid MS Square Bar", "Flat Iron MS", "SS Hollow Pipe", "Aluminium"),
            popularUses = listOf("Apartment Windows", "Ground Floor Security", "Utility Cages"),
            imageUrl = "https://images.unsplash.com/photo-1541888946425-d0fbb186f5f7?auto=format&fit=crop&w=600&q=80",
            sector = "Residential"
        ),
        FabricationCategory(
            id = "cat_kitchen_utility",
            name = "Kitchen & Utility Fabrication",
            shortName = "Utility",
            description = "Stainless-steel kitchen racks, sink frames, pantry shelves, gas-cylinder stands, and washing-machine stands.",
            indicativeRate = "₹1,200 - ₹4,500",
            rateUnit = "per custom unit",
            primaryMaterials = listOf("SS 304 Food Grade", "MS Painted Frames"),
            popularUses = listOf("Modular Kitchens", "Utility Balconies", "Pantry Areas"),
            imageUrl = "https://images.unsplash.com/photo-1556911220-e15b29be8c8f?auto=format&fit=crop&w=600&q=80",
            sector = "Residential"
        ),
        FabricationCategory(
            id = "cat_interiors_furniture",
            name = "Interior & Furniture Metalwork",
            shortName = "Interiors",
            description = "Metal bed frames, sofa support structures, dining table bases, designer partitions, wardrobes and shelving units.",
            indicativeRate = "₹3,500 - ₹15,000",
            rateUnit = "per structure",
            primaryMaterials = listOf("MS Hollow Section", "Brass Inlay", "Wrought Iron"),
            popularUses = listOf("Living Rooms", "Bedrooms", "Boutique Offices", "Cafes"),
            imageUrl = "https://images.unsplash.com/photo-1538688525198-9b88f6f53126?auto=format&fit=crop&w=600&q=80",
            sector = "Residential"
        ),
        FabricationCategory(
            id = "cat_designer",
            name = "Decorative & Designer Metalwork",
            shortName = "Designer",
            description = "Laser-cut metal partitions, decorative screens, designer entrance nameplates, wall art and pooja-room frames.",
            indicativeRate = "₹450 - ₹1,200",
            rateUnit = "per sq. ft.",
            primaryMaterials = listOf("Laser-Cut MS Sheet", "SS 304 Brushed", "Brass Sheet"),
            popularUses = listOf("Luxury Entrances", "Pooja Rooms", "Hotel Lobbies", "Living Room Partitions"),
            imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=600&q=80",
            sector = "Residential"
        ),

        // ==========================================
        // INDUSTRIAL FABRICATION DIVISIONS (10 Major Service Lines)
        // ==========================================
        // 01 Structural Steel Fabrication
        FabricationCategory(
            id = "cat_structural",
            name = "01. Structural Steel Fabrication",
            shortName = "Structural",
            description = "Steel columns & beams, industrial shed structures, roof trusses, mezzanine floors, platforms, walkways & pipe racks.",
            indicativeRate = "₹68 - ₹95",
            rateUnit = "per kg fabricated",
            primaryMaterials = listOf("IS 2062 Grade Steel", "Universal I-Beams", "ISMB Channels"),
            popularUses = listOf("Industrial Contractors", "Infrastructure", "Warehouse Developers", "Factory Owners"),
            imageUrl = "https://images.unsplash.com/photo-1504307651254-35680f356dfd?auto=format&fit=crop&w=600&q=80",
            sector = "Industrial"
        ),
        // 02 Industrial Machinery Fabrication
        FabricationCategory(
            id = "cat_industrial_machinery",
            name = "02. Industrial Machinery Fabrication",
            shortName = "Machinery",
            description = "Machine frames & bases, equipment mounting structures, industrial machine guards, fabricated enclosures, hoppers & chutes.",
            indicativeRate = "₹90 - ₹165",
            rateUnit = "per kg / frame",
            primaryMaterials = listOf("Heavy MS Sections", "CNC Machined Plates", "Stainless Steel"),
            popularUses = listOf("Machinery Manufacturers", "Automation Companies", "Industrial OEMs"),
            imageUrl = "https://images.unsplash.com/photo-1581092335397-9583fe92d232?auto=format&fit=crop&w=600&q=80",
            sector = "Industrial"
        ),
        // 03 Conveyor & Material-Handling Fabrication
        FabricationCategory(
            id = "cat_conveyors",
            name = "03. Conveyor & Material-Handling",
            shortName = "Conveyors",
            description = "Belt, roller & chain conveyors, conveyor support structures, loading/unloading platforms, handling trolleys & transfer systems.",
            indicativeRate = "₹95 - ₹175",
            rateUnit = "per meter / unit",
            primaryMaterials = listOf("Structural MS Channel", "UHMWPE Guides", "Drive Mounting Plates"),
            popularUses = listOf("Automotive Plants", "Packaging", "Food Processing", "Recycling Hubs"),
            imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=600&q=80",
            sector = "Industrial"
        ),
        // 04 Sheet Metal Fabrication
        FabricationCategory(
            id = "cat_sheet_metal",
            name = "04. Sheet Metal Fabrication",
            shortName = "Sheet Metal",
            description = "Electrical control-panel enclosures, machine covers, cabinets, equipment mounting plates & industrial ventilation ducts.",
            indicativeRate = "₹110 - ₹240",
            rateUnit = "per kg / enclosure",
            primaryMaterials = listOf("CRCA 1.6-3.0mm Sheet", "GI Sheet", "Aluminium 5052", "SS 304 2B"),
            popularUses = listOf("Electrical Switchboards", "Machine Housings", "HVAC Ducting"),
            imageUrl = "https://images.unsplash.com/photo-1504917599217-d4dc5ebe6122?auto=format&fit=crop&w=600&q=80",
            sector = "Industrial"
        ),
        // 05 Industrial Piping & Pressure Vessels
        FabricationCategory(
            id = "cat_piping_vessels",
            name = "05. Industrial Piping & Pressure Vessels",
            shortName = "Piping",
            description = "Process-piping assemblies, stainless steel pipelines, storage tanks, industrial vessels, pipe racks & tank platforms.",
            indicativeRate = "₹180 - ₹380",
            rateUnit = "per inch-dia / kg",
            primaryMaterials = listOf("Seamless CS ASTM A106", "SS 304/316L Schedule Pipe", "Flanges"),
            popularUses = listOf("Chemical Plants", "Pharma Companies", "Water Treatment", "Process Industries"),
            imageUrl = "https://images.unsplash.com/photo-1504307651254-35680f356dfd?auto=format&fit=crop&w=600&q=80",
            sector = "Industrial"
        ),
        // 06 Stainless Steel Industrial Fabrication
        FabricationCategory(
            id = "cat_ss_industrial",
            name = "06. Stainless Steel Industrial Fabrication",
            shortName = "SS Industrial",
            description = "Food-grade equipment components, pharma equipment supports, industrial worktables, SS tanks, sinks & cleanroom frames.",
            indicativeRate = "₹240 - ₹450",
            rateUnit = "per kg fabricated",
            primaryMaterials = listOf("SS 304 Jindal", "SS 316L Pharma Grade", "Electropolished Finish"),
            popularUses = listOf("Food Processing Plants", "Pharma Manufacturers", "Commercial Kitchens"),
            imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?auto=format&fit=crop&w=600&q=80",
            sector = "Industrial"
        ),
        // 07 Automotive & Engineering Components
        FabricationCategory(
            id = "cat_automotive_fixtures",
            name = "07. Automotive & Engineering Components",
            shortName = "Automotive",
            description = "Welding fixtures & jigs, assembly & inspection fixtures, machine frames, component brackets & production-line racks.",
            indicativeRate = "₹120 - ₹280",
            rateUnit = "per fixture / kg",
            primaryMaterials = listOf("Tool Steel Plates", "Precision Machined MS", "Pneumatic Mounts"),
            popularUses = listOf("Automotive OEMs", "Tier-1 & Tier-2 Suppliers", "Assembly Lines"),
            imageUrl = "https://images.unsplash.com/photo-1581092335397-9583fe92d232?auto=format&fit=crop&w=600&q=80",
            sector = "Industrial"
        ),
        // 08 Warehouse & Logistics Fabrication
        FabricationCategory(
            id = "cat_warehouse_logistics",
            name = "08. Warehouse & Logistics Fabrication",
            shortName = "Logistics",
            description = "Heavy-duty pallet racks, industrial shelving, loading platforms, dock ramps, warehouse safety barriers & forklift guards.",
            indicativeRate = "₹78 - ₹125",
            rateUnit = "per kg / bay",
            primaryMaterials = listOf("High-Tensile Cold Formed Steel", "Heavy MS Channel", "Anchor Bolts"),
            popularUses = listOf("Warehouses", "Logistics Hubs", "E-Commerce Fulfilment", "Distribution Centres"),
            imageUrl = "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?auto=format&fit=crop&w=600&q=80",
            sector = "Industrial"
        ),
        // 09 Industrial Maintenance & Repair
        FabricationCategory(
            id = "cat_industrial_maintenance",
            name = "09. Industrial Maintenance & Repair",
            shortName = "Maintenance",
            description = "Breakdown welding repairs, damaged machine-frame/conveyor repairs, replacement brackets, covers & on-site welding.",
            indicativeRate = "Custom Service",
            rateUnit = "per breakdown / shift",
            primaryMaterials = listOf("Low Hydrogen Electrodes", "MS Heavy Plates", "Wear Liners"),
            popularUses = listOf("Factory Maintenance", "Plant Shutdowns", "Emergency Breakdown Teams"),
            imageUrl = "https://images.unsplash.com/photo-1504307651254-35680f356dfd?auto=format&fit=crop&w=600&q=80",
            sector = "Industrial"
        ),
        // 10 Recycling & Waste-Processing Machinery
        FabricationCategory(
            id = "cat_recycling_machinery",
            name = "10. Recycling & Waste-Processing",
            shortName = "Recycling",
            description = "Scrap collection bins, hoppers, chutes, conveyor structures, sorting tables, storage silos & shredder support frames.",
            indicativeRate = "₹82 - ₹130",
            rateUnit = "per kg fabricated",
            primaryMaterials = listOf("Heavy Abrasion-Resistant MS", "Structural Hollow Sections"),
            popularUses = listOf("Plastic Recyclers", "Scrap Processors", "Waste Management Facilities"),
            imageUrl = "https://images.unsplash.com/photo-1532996122724-e3c354a0b15b?auto=format&fit=crop&w=600&q=80",
            sector = "Industrial"
        )
    )
}
