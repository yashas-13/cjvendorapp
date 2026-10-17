package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FabricationOrder
import com.example.data.model.MilestoneState
import com.example.data.model.Quotation
import com.example.data.model.QuotationStatus
import com.example.data.model.RfqRequest
import com.example.data.model.RfqStatus
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.IndustrialSurfaceVariant
import com.example.ui.theme.LaserCyanLight
import com.example.ui.theme.SteelSlateLight

@Composable
fun CustomerTrackingPortalScreen(
    rfqs: List<RfqRequest>,
    quotationsMap: Map<String, List<Quotation>>,
    orders: List<FabricationOrder>,
    onSelectRfq: (RfqRequest) -> Unit,
    onTrackByRef: (String) -> Unit,
    onNavigateToCatalog: () -> Unit,
    onNavigateToCustomRfq: () -> Unit,
    whatsAppUrlGenerator: (RfqRequest) -> String,
    modifier: Modifier = Modifier
) {
    var searchRef by remember { mutableStateOf("") }
    val context = LocalContext.current

    val quotedCount = rfqs.count { rfq ->
        val quotes = quotationsMap[rfq.id] ?: emptyList()
        quotes.isNotEmpty() && rfq.status != RfqStatus.ACCEPTED && rfq.status != RfqStatus.COMPLETED
    }
    val inProductionCount = orders.count { it.currentStepIndex < 6 }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("customer_tracking_portal_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = ForgeAmberLight,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "My Requirements & Quotations",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Track quotation status, compare verified fabricator bids, and monitor fabrication milestones.",
                    fontSize = 11.sp,
                    color = SteelSlateLight
                )
            }
        }

        // Metrics Banner
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    color = IndustrialSurface,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Total RFQs", fontSize = 10.sp, color = SteelSlateLight)
                        Text(
                            text = "${rfqs.size}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Text(text = "Submitted", fontSize = 9.sp, color = SteelSlateLight)
                    }
                }

                Surface(
                    color = if (quotedCount > 0) Color(0xFF1E3A8A).copy(alpha = 0.4f) else IndustrialSurface,
                    shape = RoundedCornerShape(10.dp),
                    border = if (quotedCount > 0) androidx.compose.foundation.BorderStroke(1.dp, LaserCyanLight) else null,
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "Quotes Ready", fontSize = 10.sp, color = if (quotedCount > 0) LaserCyanLight else SteelSlateLight)
                        Text(
                            text = "$quotedCount",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = if (quotedCount > 0) LaserCyanLight else Color.White
                        )
                        Text(text = "Tap to Compare", fontSize = 9.sp, color = ForgeAmberLight)
                    }
                }

                Surface(
                    color = IndustrialSurface,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "In Production", fontSize = 10.sp, color = SteelSlateLight)
                        Text(
                            text = "$inProductionCount",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF10B981)
                        )
                        Text(text = "Active Orders", fontSize = 9.sp, color = SteelSlateLight)
                    }
                }
            }
        }

        // Search & Reference Track Box
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Track by Reference Number",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = searchRef,
                            onValueChange = { searchRef = it },
                            placeholder = { Text("e.g. CJF-2026-A101", fontSize = 12.sp, color = Color.Gray) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("tracking_ref_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForgeAmberLight,
                                unfocusedBorderColor = Color.DarkGray,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (searchRef.isNotBlank()) {
                                    onTrackByRef(searchRef.trim())
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForgeAmberLight,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("track_ref_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Track", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // RFQ List
        item {
            Text(
                text = "Submitted Requirements (${rfqs.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (rfqs.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Build,
                            contentDescription = null,
                            tint = ForgeAmberLight,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Requirements Submitted Yet",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Browse our curated designs catalogue or submit a custom metalwork requirement to receive verified fabricator quotations.",
                            fontSize = 12.sp,
                            color = SteelSlateLight,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = onNavigateToCatalog,
                                colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight, contentColor = Color.Black),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Browse Designs", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = onNavigateToCustomRfq,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                            ) {
                                Text("Custom RFQ", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        } else {
            items(rfqs, key = { it.id }) { rfq ->
                val quotes = quotationsMap[rfq.id] ?: emptyList()
                val order = orders.find { it.rfqId == rfq.id }
                val hasAcceptedQuote = quotes.any { it.status == QuotationStatus.ACCEPTED }

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                    border = if (quotes.isNotEmpty() && !hasAcceptedQuote) {
                        androidx.compose.foundation.BorderStroke(1.dp, ForgeAmberLight.copy(alpha = 0.5f))
                    } else if (order != null) {
                        androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.5f))
                    } else null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelectRfq(rfq) }
                        .testTag("customer_rfq_card_${rfq.referenceNumber}")
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = rfq.referenceNumber,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = ForgeAmberLight
                            )
                            StatusBadge(status = rfq.status)
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Project: ${rfq.customerName} (${rfq.projectType}) • ${rfq.city} - ${rfq.pincode}",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Target Completion: ${rfq.targetCompletionDate ?: "Standard"} • Budget: ${rfq.budgetRange ?: "Flexible"}",
                            fontSize = 11.sp,
                            color = SteelSlateLight
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Items pill summary
                        Surface(
                            color = IndustrialSurfaceContainer,
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = "Requirements (${rfq.items.size} items):",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = LaserCyanLight
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                rfq.items.forEach { item ->
                                    Text(
                                        text = "• ${item.title} (${item.material}, ${item.materialGrade}) - ${item.lengthFt}x${item.heightFt} ft [${item.quantity}x]",
                                        fontSize = 11.sp,
                                        color = Color.LightGray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quotations & Order Status callout
                        if (order != null) {
                            val activeMilestone = order.milestones.find { it.state == MilestoneState.IN_PROGRESS }
                                ?: order.milestones.lastOrNull { it.state == MilestoneState.COMPLETED }
                            Surface(
                                color = Color(0xFF062316),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF10B981),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Order ${order.orderNumber}: Stage ${activeMilestone?.stepIndex ?: 1} - ${activeMilestone?.title ?: "In Production"}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF6EE7B7)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        } else if (quotes.isNotEmpty()) {
                            val minQuote = quotes.minOf { it.grandTotal }
                            Surface(
                                color = ForgeAmberLight.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.FormatQuote,
                                        contentDescription = null,
                                        tint = ForgeAmberLight,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "${quotes.size} Verified Fabricator Quote(s) Received • Lowest: ₹${minQuote.toInt()}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForgeAmberLight
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        // Bottom Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    val sendIntent = Intent(Intent.ACTION_VIEW).apply {
                                        data = Uri.parse(whatsAppUrlGenerator(rfq))
                                    }
                                    try {
                                        context.startActivity(sendIntent)
                                    } catch (_: Exception) {}
                                },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366)),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("WhatsApp", fontSize = 11.sp)
                            }

                            Button(
                                onClick = { onSelectRfq(rfq) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (quotes.isNotEmpty() && !hasAcceptedQuote) ForgeAmberLight else LaserCyanLight,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("view_quotes_btn_${rfq.referenceNumber}")
                            ) {
                                Text(
                                    text = if (quotes.isNotEmpty() && !hasAcceptedQuote) "Compare Quotes (${quotes.size}) →" else "View Details →",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
