package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.LaserCyanLight
import com.example.ui.theme.SteelSlateLight

@Composable
fun HelpPolicyScreen(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("help_policy_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = "Help, Privacy & Marketplace Terms",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "CJ Fabrications operates as an audited B2B custom metalwork marketplace connecting buyers with verified Indian fabrication workshops.",
                    fontSize = 12.sp,
                    color = SteelSlateLight,
                    lineHeight = 17.sp
                )
            }
        }

        // Contact & Support
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Marketplace Support Desk", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    ContactRow(Icons.Default.Phone, "WhatsApp & Phone:", "+91 98765 43210 (Mon - Sat, 9 AM - 7 PM IST)")
                    ContactRow(Icons.Default.Email, "Technical Queries:", "support@cjfabrication.in")
                    ContactRow(Icons.Default.LocationOn, "Head Office:", "CJ Fabrications Marketplace Hub, Bengaluru, Karnataka, India")
                }
            }
        }

        // Privacy Policy
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = LaserCyanLight, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Privacy Policy & Data Security", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Minimal Personal Data: We collect only your name, phone number, and project location to facilitate quotation generation and delivery coordination.\n\n" +
                            "2. Zero Public Exposure: Customer contact details and proprietary CAD/architectural drawings are never displayed publicly. They are strictly routed to authorized, verified fabrication workshops assigned to your specific RFQ.\n\n" +
                            "3. Unguessable RFQ Tokens: RFQ links are guarded by secure 16-character cryptographic access tokens to prevent unauthorized access.\n\n" +
                            "4. No Data Reselling: We do not sell user data to third-party telemarketers or advertisers.",
                        fontSize = 11.sp,
                        color = SteelSlateLight,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // Terms of Service & Fabrication Standards
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Policy, contentDescription = null, tint = ForgeAmberLight, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Marketplace Terms & Standards", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "1. Quotation-Led Process: All pricing shown in the catalogue represents indicative benchmark rates based on historical trade values. Binding commitments arise only upon mutual acceptance of an itemized vendor quotation.\n\n" +
                            "2. Indian Standards Compliance: Structural fabrications conform to Bureau of Indian Standards (BIS) codes, including IS 2062 for structural steel and IS 4923 for hollow sections.\n\n" +
                            "3. GST Compliance: All vendor quotes include 18% GST with valid tax invoices issued directly by verified workshops.\n\n" +
                            "4. Milestone Releases: Fabrication work proceeds in audited stages (Design Approval -> Material Procurement -> Laser Machining -> Powder Coating -> Transit -> On-site Erection).",
                        fontSize = 11.sp,
                        color = SteelSlateLight,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // CJF Residential Fabrication Guide (Business-Planning Reference)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CJF | Residential Fabrication Guide",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Security • Structures • Interiors • Utility • Architectural Metalwork",
                        fontSize = 11.sp,
                        color = ForgeAmberLight,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "1. Five Business Service Divisions\n" +
                            "• CJF Security: Main gates (swing/sliding/folding), compound fencing, safety doors, window grills & gate automation for homeowners & gated communities.\n" +
                            "• CJF Structures: Car parking sheds, outdoor staircases, PEB roofing, mezzanine floors, structural steel columns & beams for builders & contractors.\n" +
                            "• CJF Interiors: Metal bed frames, sofa frames, dining table bases, wardrobes, TV consoles, and decorative partitions for interior designers.\n" +
                            "• CJF Utility: Stainless-steel kitchen dish racks, under-sink frames, cylinder stands, washing-machine stands & overhead water-tank towers.\n" +
                            "• CJF Architectural: Toughened glass & SS 304/316 balustrades, aluminium sliding systems, and CNC laser-cut facade panels for architects & luxury villas.\n\n" +
                            "2. Suggested 7-Step Operating Model\n" +
                            "① Site Measurement & Requirement Collection\n" +
                            "② Design, Material Selection & Customer Approval\n" +
                            "③ Itemized Quotation (Material, Labour, Finishing, Transport & Installation)\n" +
                            "④ Precision Fabrication & In-shop Quality Checks\n" +
                            "⑤ Surface Preparation & Finishing (Epoxy Primer, Powder Coat or Galvanizing)\n" +
                            "⑥ Safe Delivery, On-site Installation & Customer Handover\n" +
                            "⑦ After-sales Adjustments, Repairs & Maintenance Support\n\n" +
                            "3. Key Materials & Industrial Processes\n" +
                            "• Mild Steel (MS): Common for gates, frames, sheds & structural components (IS 2062).\n" +
                            "• Stainless Steel (SS): SS 304/316 food-grade & marine-grade for railings, kitchen & exposed utility.\n" +
                            "• Galvanised Iron (GI): Outdoor & utility structures with 80-micron zinc protection.\n" +
                            "• Aluminium: Sliding window tracks, mosquito-mesh frames & lightweight louvers.\n" +
                            "• Safety Glass: 12mm clear toughened glass engineered for balustrades & shower cubicles.\n" +
                            "• Laser / CNC Cutting: Repeatable precision cutouts for decorative partitions and custom nameplates.\n\n" +
                            "4. Safety & Quality Checklist\n" +
                            "✓ Confirm exact site dimensions & mounting substrates before cutting metal.\n" +
                            "✓ Verify material certificates for specified steel grade and gauge thickness.\n" +
                            "✓ Inspect weld bead penetration, deburred smooth edges, and heavy-duty anchor bolts.\n" +
                            "✓ Apply climate-appropriate anti-corrosion primer prior to topcoat.\n" +
                            "✓ Sizing & detailing checked by qualified structural engineer for load-bearing structures.\n" +
                            "✓ Compliance with local Indian building bylaws and fire safety codes.",
                        fontSize = 11.sp,
                        color = SteelSlateLight,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        // CJF Industrial Fabrication Guide (Business-Planning Reference)
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CJF | Industrial Fabrication Guide",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Heavy Steel • Machinery • Conveyors • Sheet Metal • Piping • Logistics",
                        fontSize = 11.sp,
                        color = LaserCyanLight,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "1. Ten Industrial Service Lines\n" +
                            "01. Structural Steel: Steel columns/beams, industrial shed structures, roof trusses, mezzanine platforms, walkways, staircases & pipe racks.\n" +
                            "02. Industrial Machinery: Machine frames, mounting structures, conveyor frames, industrial guards, enclosures, hoppers & chutes.\n" +
                            "03. Conveyor & Material Handling: Belt, roller & chain conveyors, transfer systems, loading platforms & industrial trolleys.\n" +
                            "04. Sheet Metal: Electrical control-panel enclosures, machine cabinets, equipment mounting plates & ventilation ducting.\n" +
                            "05. Industrial Piping & Vessels: Process piping assemblies, stainless pipelines, storage tanks, platforms & access ladders.\n" +
                            "06. Stainless Steel Industrial: Food-grade equipment frames, pharma supports, SS tanks, sanitary worktables & wash stations.\n" +
                            "07. Automotive & Engineering: Welding fixtures/jigs, assembly inspection fixtures, component brackets & production racks.\n" +
                            "08. Warehouse & Logistics: Heavy-duty pallet racks, industrial shelving, loading dock ramps & safety collision barriers.\n" +
                            "09. Industrial Maintenance & Repair: Breakdown welding, machine-frame repairs, replacement brackets & preventive maintenance.\n" +
                            "10. Recycling & Waste Machinery: Scrap collection bins, sorting tables, crusher/shredder frames & storage silos.\n\n" +
                            "2. Recommended CJF 3-Stage Startup Business Model\n" +
                            "• Stage 1 (Lower Investment) — Job-work & Repair: Breakdown welding, brackets, equipment guards, small frames & factory maintenance.\n" +
                            "• Stage 2 (Moderate Investment) — OEM Fabrication: Machine frames, precision sheet metal parts, fixtures & conveyor structures.\n" +
                            "• Stage 3 (Higher Investment) — Heavy Industrial: Large structures, tanks, process piping & turnkey industrial engineering projects.\n\n" +
                            "3. Compliance & Safety Standards\n" +
                            "• Pressure vessels & regulated piping require certified welding procedures (WPS/PQR) and statutory inspections.\n" +
                            "• Load-bearing mezzanine and crane-gantry structures require qualified structural engineering review.\n" +
                            "• Material traceability certificates (MTC) required for food-grade SS 316L and structural steel.",
                        fontSize = 11.sp,
                        color = SteelSlateLight,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ContactRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 4.dp)) {
        Icon(imageVector = icon, contentDescription = null, tint = ForgeAmberLight, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(text = value, fontSize = 11.sp, color = SteelSlateLight)
        }
    }
}
