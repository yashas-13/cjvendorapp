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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Pending
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.FabricationOrder
import com.example.data.model.MilestoneState
import com.example.ui.theme.ForgeAmberDark
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.IndustrialSurfaceVariant
import com.example.ui.theme.LaserCyanLight
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Screen 7: Order Tracking & Milestones
 * Features:
 * - Order Overview (Order ID, Associated RFQ, Delivery Address, Total Value)
 * - 9-Stage Visual Progress Timeline:
 *     1. Booking Confirmed
 *     2. Site Measurement
 *     3. Quotation Approved
 *     4. Advance Paid
 *     5. Fabrication in Progress
 *     6. Quality Check (QC)
 *     7. Ready for Delivery
 *     8. Installation at Site
 *     9. Completed & Handover
 * - Progress photographs of shop fabrication and QC inspection
 * - Payment milestones breakdown (40% Advance, 40% Pre-dispatch, 20% Handover)
 * - Support Contact: One-tap direct WhatsApp / phone call to project coordinator
 */
@Composable
fun OrderTrackingScreen(
    order: FabricationOrder,
    onContactCoordinator: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val progressFraction = (order.currentStepIndex.toFloat() / order.milestones.size.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    // Realistic progress photos from workshop floor
    val progressPhotos = listOf(
        "https://images.unsplash.com/photo-1504307651254-35680f356dfd?w=600&auto=format&fit=crop&q=80" to "CNC Laser Cutting Complete",
        "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=600&auto=format&fit=crop&q=80" to "MIG Frame Welding & Squaring",
        "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=600&auto=format&fit=crop&q=80" to "Electrostatic Powder Coating Booth"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .testTag("order_tracking_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 640.dp)) {

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Live Order Tracking",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Order #${order.orderNumber}",
                        fontSize = 13.sp,
                        color = ForgeAmberLight,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Surface(
                    color = LaserCyanLight.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "STAGE ${order.currentStepIndex} OF 9",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = LaserCyanLight,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Overview Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Assigned Fabricator", fontSize = 11.sp, color = Color.Gray)
                            Text(order.vendorName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Total Order Value", fontSize = 11.sp, color = Color.Gray)
                            Text("₹${order.totalAmount.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = ForgeAmberLight)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = Color(0xFF2E3440))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = ForgeAmberLight, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${order.deliveryAddress}, ${order.city}",
                            fontSize = 12.sp,
                            color = Color.LightGray
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Progress Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Overall Progress", fontSize = 11.sp, color = Color.Gray)
                        Text("${(progressFraction * 100).toInt()}%", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForgeAmberLight)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        color = ForgeAmberLight,
                        trackColor = Color(0xFF2E3440),
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp))
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 9-Stage Visual Progress Timeline
            Text(
                text = "PRODUCTION & DELIVERY TIMELINE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForgeAmberLight,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Surface(
                color = IndustrialSurface,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    order.milestones.forEachIndexed { index, milestone ->
                        MilestoneTimelineRow(
                            milestone = milestone,
                            isLast = index == order.milestones.size - 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress Photographs Gallery
            Text(
                text = "FACTORY & SITE INSPECTION PHOTOS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForgeAmberLight,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(progressPhotos) { (url, label) ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                        modifier = Modifier.width(180.dp)
                    ) {
                        Column {
                            AsyncImage(
                                model = url,
                                contentDescription = label,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth().height(120.dp)
                            )
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Payment Milestones Breakdown
            Text(
                text = "PAYMENT MILESTONES (ESCROW SECURED)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForgeAmberLight,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val advance = order.totalAmount * 0.40
                    val preDispatch = order.totalAmount * 0.40
                    val handover = order.totalAmount * 0.20

                    PaymentRow(stage = "1. Advance Mobilization (40%)", amount = advance, status = "PAID", isPaid = true)
                    PaymentRow(stage = "2. Pre-Dispatch QC Inspection (40%)", amount = preDispatch, status = "DUE ON QC", isPaid = false)
                    PaymentRow(stage = "3. Post-Installation Handover (20%)", amount = handover, status = "UPON SIGN-OFF", isPaid = false)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Support Contact Buttons
            Text(
                text = "ORDER ASSISTANCE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForgeAmberLight,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Call Coordinator
                OutlinedButton(
                    onClick = {
                        val uri = Uri.parse("tel:+919845011223")
                        context.startActivity(Intent(Intent.ACTION_DIAL, uri))
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    modifier = Modifier.weight(1f).height(48.dp).testTag("call_coordinator_button")
                ) {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = LaserCyanLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Call Supervisor", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }

                // WhatsApp Coordinator
                Button(
                    onClick = {
                        val text = "*ORDER INQUIRY - CJ FABRICATIONS*\n" +
                            "Order Ref: *${order.orderNumber}*\n" +
                            "Customer: *${order.customerName}* (${order.customerPhone})\n" +
                            "Fabricator: *${order.vendorName}*\n" +
                            "Current Stage: Stage ${order.currentStepIndex} of 9 (${order.milestones.getOrNull(order.currentStepIndex - 1)?.title ?: "Production"})\n" +
                            "Please provide transit/fabrication update."
                        val encoded = URLEncoder.encode(text, "UTF-8")
                        val uri = Uri.parse("https://api.whatsapp.com/send?text=$encoded")
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                    modifier = Modifier.weight(1f).height(48.dp).testTag("whatsapp_coordinator_button")
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("WhatsApp Update", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
private fun MilestoneTimelineRow(milestone: com.example.data.model.OrderMilestone, isLast: Boolean) {
    val dateStr = milestone.completedTimestamp?.let {
        SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(it))
    }

    Row(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                shape = CircleShape,
                color = when (milestone.state) {
                    MilestoneState.COMPLETED -> Color(0xFF10B981)
                    MilestoneState.IN_PROGRESS -> ForgeAmberLight
                    MilestoneState.PENDING -> Color(0xFF2E3440)
                },
                modifier = Modifier.size(24.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    when (milestone.state) {
                        MilestoneState.COMPLETED -> Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        MilestoneState.IN_PROGRESS -> Icon(Icons.Default.Handyman, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                        MilestoneState.PENDING -> Text("${milestone.stepIndex}", fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(48.dp)
                        .background(
                            if (milestone.state == MilestoneState.COMPLETED) Color(0xFF10B981).copy(alpha = 0.4f)
                            else Color(0xFF2E3440)
                        )
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f).padding(bottom = if (isLast) 0.dp else 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${milestone.stepIndex}. ${milestone.title}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = when (milestone.state) {
                        MilestoneState.COMPLETED -> Color.White
                        MilestoneState.IN_PROGRESS -> ForgeAmberLight
                        MilestoneState.PENDING -> Color.Gray
                    }
                )
                if (dateStr != null) {
                    Text(text = dateStr, fontSize = 10.sp, color = Color.Gray)
                }
            }

            Text(
                text = milestone.description,
                fontSize = 11.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(top = 2.dp)
            )

            if (milestone.remarks.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = IndustrialSurfaceContainer,
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Text(
                        text = "Notes: ${milestone.remarks}",
                        fontSize = 10.sp,
                        color = LaserCyanLight,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentRow(stage: String, amount: Double, status: String, isPaid: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(stage, fontSize = 12.sp, color = Color.White)
            Text("₹${amount.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ForgeAmberLight)
        }
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = if (isPaid) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFF343B4B)
        ) {
            Text(
                text = status,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (isPaid) Color(0xFF10B981) else Color.LightGray,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}
