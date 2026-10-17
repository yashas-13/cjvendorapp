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
