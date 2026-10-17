package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.ui.components.WhatsAppHandoffCard
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.IndustrialSurfaceVariant
import com.example.ui.theme.LaserCyanLight
import com.example.ui.theme.SteelSlateLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RfqDetailScreen(
    rfq: RfqRequest,
    quotations: List<Quotation>,
    associatedOrder: FabricationOrder?,
    onAcceptQuote: (quoteId: String) -> Unit,
    onBack: () -> Unit,
    whatsAppUrlGenerator: (RfqRequest) -> String,
    whatsAppMessageFormatter: (RfqRequest) -> String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("rfq_detail_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = rfq.referenceNumber,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = ForgeAmberLight
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        StatusBadge(status = rfq.status)
                    }
                    Text(
                        text = "Customer: ${rfq.customerName} • ${rfq.city}",
                        fontSize = 12.sp,
                        color = SteelSlateLight
                    )
                }
            }
        }

        // Stepper Timeline Progress
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Procurement Lifecycle",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    val stages = listOf(
                        "RFQ Submitted",
                        "Vendors Assigned",
                        "Quoted",
                        "Accepted",
                        "Fabrication",
                        "Handover"
                    )

                    val activeStep = when (rfq.status) {
                        RfqStatus.DRAFT, RfqStatus.SUBMITTED -> 0
                        RfqStatus.ASSIGNED -> 1
                        RfqStatus.QUOTED -> 2
                        RfqStatus.ACCEPTED -> 3
                        RfqStatus.IN_PRODUCTION, RfqStatus.DISPATCHED, RfqStatus.INSTALLATION -> 4
                        RfqStatus.COMPLETED -> 5
                        RfqStatus.CANCELLED -> -1
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        stages.forEachIndexed { idx, stage ->
                            val isPassed = idx <= activeStep
                            val isCurrent = idx == activeStep

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(
                                            when {
                                                isPassed -> ForgeAmberLight
                                                else -> IndustrialSurfaceContainer
                                            },
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isPassed) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .background(SteelSlateLight, CircleShape)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stage,
                                    fontSize = 9.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isPassed) Color.White else SteelSlateLight,
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                    lineHeight = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // WhatsApp Communication Banner
        item {
            WhatsAppHandoffCard(
                rfq = rfq,
                formattedMessage = whatsAppMessageFormatter(rfq),
                whatsAppUrl = whatsAppUrlGenerator(rfq)
            )
        }

        // Quotations Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vendor Quotations (${quotations.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (quotations.isNotEmpty()) {
                    Text(
                        text = "Itemized GST Breakdown",
                        fontSize = 11.sp,
                        color = ForgeAmberLight
                    )
                }
            }
        }

        if (quotations.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(imageVector = Icons.Default.HourglassBottom, contentDescription = null, tint = ForgeAmberLight, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Assigned to Fabricators",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Assigned vendors are calculating material weight and laser tooling charges. Quotations appear here with technical guarantees.",
                            fontSize = 11.sp,
                            color = SteelSlateLight,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(quotations, key = { it.id }) { quote ->
                QuoteCard(
                    quote = quote,
                    isAccepted = quote.status == QuotationStatus.ACCEPTED,
                    canAccept = rfq.status == RfqStatus.QUOTED || rfq.status == RfqStatus.ASSIGNED,
                    onAccept = { onAcceptQuote(quote.id) }
                )
            }
        }

        // If order exists, show Order Milestones
        associatedOrder?.let { order ->
            item {
                Text(
                    text = "Work Order Progress: ${order.orderNumber}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            items(order.milestones, key = { it.stepIndex }) { milestone ->
                MilestoneItemCard(milestone = milestone)
            }
        }
    }
}

@Composable
private fun QuoteCard(
    quote: Quotation,
    isAccepted: Boolean,
    canAccept: Boolean,
    onAccept: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isAccepted) Color(0xFF062316) else IndustrialSurface
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isAccepted) Color(0xFF10B981) else Color.Transparent
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("quote_card_${quote.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = quote.vendorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        if (quote.isGstVerified) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.Verified,
                                contentDescription = "GST Verified",
                                tint = LaserCyanLight,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text(
                        text = "Workshop: ${quote.vendorCity} • ★ ${quote.vendorRating} (Audited)",
                        fontSize = 11.sp,
                        color = SteelSlateLight
                    )
                }

                if (isAccepted) {
                    Surface(
                        color = Color(0xFF10B981),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "ACCEPTED QUOTE",
                            color = Color.Black,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Commercial Cost Breakdown
            Surface(
                color = IndustrialSurfaceContainer,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    CostLine("Fabrication & Labour:", "₹${quote.baseFabricationCost.toInt()}")
                    CostLine("Certified Raw Material:", "₹${quote.materialCost.toInt()}")
                    CostLine("On-Site Installation:", "₹${quote.installationCost.toInt()}")
                    CostLine("Transportation / Transit:", "₹${quote.transportCost.toInt()}")
                    CostLine("GST (18% Input Credit):", "₹${quote.gstAmount.toInt()}")
                    Divider(
                        color = SteelSlateLight.copy(alpha = 0.3f),
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Grand Total (All Inclusive):", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                        Text(
                            text = "₹${quote.grandTotal.toInt()}",
                            fontWeight = FontWeight.Black,
                            color = ForgeAmberLight,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Technical Guarantee & Lead time
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = LaserCyanLight, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = quote.materialGradeGuarantee,
                    fontSize = 11.sp,
                    color = Color(0xFFBAE6FD),
                    lineHeight = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(imageVector = Icons.Default.Schedule, contentDescription = null, tint = SteelSlateLight, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Delivery & Erection Lead Time: ${quote.leadTimeDays} Days • Validity: ${quote.validityDate}",
                    fontSize = 11.sp,
                    color = SteelSlateLight
                )
            }

            if (quote.siteInspectionRequired) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    color = LaserCyanLight.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = LaserCyanLight, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Site Inspection: Workshop requested in-person measurement check before cutting.",
                            fontSize = 10.sp,
                            color = LaserCyanLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Direct Customer-to-Vendor Communication Row
            val context = LocalContext.current
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = {
                        val cleanPhone = quote.vendorPhone.replace("[^0-9]".toRegex(), "")
                        val text = java.net.URLEncoder.encode(
                            "Hello ${quote.contactPerson} (${quote.vendorName}), I am reviewing your quotation of ₹${quote.grandTotal.toInt()} for RFQ ${quote.rfqReference} on CJ Fabrications. I would like to discuss specifications.",
                            "UTF-8"
                        )
                        val url = "https://api.whatsapp.com/send?phone=$cleanPhone&text=$text"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        try { context.startActivity(intent) } catch (e: Exception) {}
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF25D366)),
                    modifier = Modifier.weight(1f).testTag("chat_vendor_btn_${quote.id}")
                ) {
                    Text("Chat on WhatsApp", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF25D366))
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = {
                        val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${quote.vendorPhone}"))
                        try { context.startActivity(dialIntent) } catch (e: Exception) {}
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier.testTag("call_vendor_btn_${quote.id}")
                ) {
                    Icon(imageVector = Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(14.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontSize = 11.sp, color = Color.White)
                }
            }

            if (canAccept && !isAccepted) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ForgeAmberLight,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("accept_quote_btn_${quote.id}")
                ) {
                    Text(
                        text = "Accept Quotation & Initiate Order",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CostLine(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = SteelSlateLight)
        Text(text = value, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun MilestoneItemCard(milestone: com.example.data.model.OrderMilestone) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (milestone.state) {
                MilestoneState.COMPLETED -> Color(0xFF0B2416)
                MilestoneState.IN_PROGRESS -> Color(0xFF1E283A)
                MilestoneState.PENDING -> IndustrialSurface
            }
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(
                        when (milestone.state) {
                            MilestoneState.COMPLETED -> Color(0xFF10B981)
                            MilestoneState.IN_PROGRESS -> LaserCyanLight
                            MilestoneState.PENDING -> IndustrialSurfaceContainer
                        },
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${milestone.stepIndex}",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = milestone.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.White
                    )
                    Surface(
                        color = when (milestone.state) {
                            MilestoneState.COMPLETED -> Color(0xFF10B981).copy(alpha = 0.2f)
                            MilestoneState.IN_PROGRESS -> LaserCyanLight.copy(alpha = 0.2f)
                            MilestoneState.PENDING -> Color.Gray.copy(alpha = 0.2f)
                        },
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = when (milestone.state) {
                                MilestoneState.COMPLETED -> "DONE"
                                MilestoneState.IN_PROGRESS -> "ACTIVE"
                                MilestoneState.PENDING -> "PENDING"
                            },
                            color = when (milestone.state) {
                                MilestoneState.COMPLETED -> Color(0xFF10B981)
                                MilestoneState.IN_PROGRESS -> LaserCyanLight
                                MilestoneState.PENDING -> SteelSlateLight
                            },
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = milestone.description,
                    fontSize = 11.sp,
                    color = SteelSlateLight
                )

                if (milestone.remarks.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Update: ${milestone.remarks}",
                        fontSize = 10.sp,
                        color = ForgeAmberLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
