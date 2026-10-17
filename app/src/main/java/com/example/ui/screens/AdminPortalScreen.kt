package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FabricationOrder
import com.example.data.model.RfqRequest
import com.example.data.model.RfqStatus
import com.example.data.model.VendorProfile
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.IndustrialSurfaceVariant
import com.example.ui.theme.LaserCyanLight
import com.example.ui.theme.SteelSlateLight

import com.example.data.model.VendorVerificationStatus
import com.example.data.repository.AuditEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPortalScreen(
    rfqs: List<RfqRequest>,
    vendors: List<VendorProfile>,
    orders: List<FabricationOrder>,
    auditLogs: List<AuditEvent> = emptyList(),
    onAssignVendor: (rfqId: String, vendorId: String) -> Unit,
    onUpdateVendorVerificationStatus: (vendorId: String, status: VendorVerificationStatus) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var assignRfq by remember { mutableStateOf<RfqRequest?>(null) }

    val pendingCount = rfqs.count { it.status == RfqStatus.SUBMITTED }
    val activeOrdersCount = orders.size
    val totalGmv = orders.sumOf { it.totalAmount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_portal_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Admin Header
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = LaserCyanLight, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Marketplace Admin Control",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "B2B RFQ routing, vendor machinery auditing, and quotation oversight.",
                    fontSize = 11.sp,
                    color = SteelSlateLight
                )
            }
        }

        // Metrics Grid
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AdminMetricCard(
                    title = "Total RFQs",
                    value = "${rfqs.size}",
                    subtitle = "$pendingCount pending assignment",
                    color = ForgeAmberLight,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "Verified Fabricators",
                    value = "${vendors.count { it.verificationStatus == VendorVerificationStatus.VERIFIED }}",
                    subtitle = "${vendors.size} total applicants",
                    color = LaserCyanLight,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "Active Orders",
                    value = "$activeOrdersCount",
                    subtitle = "₹${(totalGmv / 1000).toInt()}k GMV",
                    color = Color(0xFF10B981),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Pending RFQ Review & Assignment
        item {
            Text(
                text = "RFQ Routing & Vendor Assignment",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        items(rfqs, key = { it.id }) { rfq ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                modifier = Modifier.fillMaxWidth().testTag("admin_rfq_card_${rfq.referenceNumber}")
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
                            fontSize = 15.sp,
                            color = ForgeAmberLight
                        )
                        StatusBadge(status = rfq.status)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${rfq.customerName} (${rfq.projectType}) • Phone: ${rfq.customerPhone}",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Location: ${rfq.city} - ${rfq.pincode} • Target: ${rfq.targetCompletionDate}",
                        fontSize = 11.sp,
                        color = SteelSlateLight
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Items summary
                    Text(
                        text = "Items: " + rfq.items.joinToString(", ") { "${it.title} (${it.quantity}x)" },
                        fontSize = 11.sp,
                        color = Color.LightGray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Assigned Vendors: ${rfq.assignedVendorIds.size}",
                            fontSize = 11.sp,
                            color = SteelSlateLight
                        )

                        Button(
                            onClick = { assignRfq = rfq },
                            colors = ButtonDefaults.buttonColors(containerColor = LaserCyanLight, contentColor = Color.Black),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("admin_assign_btn_${rfq.referenceNumber}")
                        ) {
                            Text("+ Assign Fabricator", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Fabrication Workshops & RBAC Verification Control
        item {
            Text(
                text = "Fabrication Workshops & RBAC Auditing (${vendors.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        items(vendors, key = { it.id }) { v ->
            val isVerified = v.verificationStatus == VendorVerificationStatus.VERIFIED
            val isPending = v.verificationStatus == VendorVerificationStatus.PENDING_VERIFICATION

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = v.businessName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                        Surface(
                            color = when (v.verificationStatus) {
                                VendorVerificationStatus.VERIFIED -> Color(0xFF10B981).copy(alpha = 0.2f)
                                VendorVerificationStatus.PENDING_VERIFICATION -> ForgeAmberLight.copy(alpha = 0.2f)
                                VendorVerificationStatus.SUSPENDED -> Color(0xFFEF4444).copy(alpha = 0.2f)
                            },
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = v.verificationStatus.name,
                                color = when (v.verificationStatus) {
                                    VendorVerificationStatus.VERIFIED -> Color(0xFF10B981)
                                    VendorVerificationStatus.PENDING_VERIFICATION -> ForgeAmberLight
                                    VendorVerificationStatus.SUSPENDED -> Color(0xFFEF4444)
                                },
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(text = "City: ${v.city} • GST: ${v.gstNumber} • Score: ★ ${v.rating}", fontSize = 11.sp, color = SteelSlateLight)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Machinery: " + v.machineryCapabilities.joinToString(" • "), fontSize = 10.sp, color = Color.Gray, lineHeight = 14.sp)

                    Spacer(modifier = Modifier.height(8.dp))

                    // RBAC Verification Action Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (isPending) {
                            Button(
                                onClick = { onUpdateVendorVerificationStatus(v.id, VendorVerificationStatus.VERIFIED) },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981), contentColor = Color.Black),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("admin_approve_vendor_${v.id}")
                            ) {
                                Text("Audit & Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        } else if (isVerified) {
                            OutlinedButton(
                                onClick = { onUpdateVendorVerificationStatus(v.id, VendorVerificationStatus.SUSPENDED) },
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("admin_suspend_vendor_${v.id}")
                            ) {
                                Text("Suspend", fontSize = 11.sp, color = Color(0xFFEF4444))
                            }
                        } else {
                            Button(
                                onClick = { onUpdateVendorVerificationStatus(v.id, VendorVerificationStatus.VERIFIED) },
                                colors = ButtonDefaults.buttonColors(containerColor = LaserCyanLight, contentColor = Color.Black),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Re-instate", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Immutable Audit Trail Section
        item {
            Text(
                text = "Marketplace Security & Audit Trail",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (auditLogs.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        Text(text = "Audit trail initialized. Security and assignment events will appear here.", fontSize = 11.sp, color = SteelSlateLight)
                    }
                }
            }
        } else {
            items(auditLogs.take(8), key = { it.id }) { log ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurfaceContainer),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "[${log.action}] by ${log.actor}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ForgeAmberLight)
                            Text(text = "Entity: ${log.entity}", fontSize = 10.sp, color = SteelSlateLight)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = log.detail, fontSize = 11.sp, color = Color.White)
                    }
                }
            }
        }
    }

    // Modal Sheet for Assigning Vendor
    assignRfq?.let { rfq ->
        ModalBottomSheet(
            onDismissRequest = { assignRfq = null },
            containerColor = IndustrialSurfaceContainer
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "Assign Fabricator to ${rfq.referenceNumber}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(text = "Match requirement category with audited workshop machinery.", fontSize = 11.sp, color = SteelSlateLight)

                Spacer(modifier = Modifier.height(14.dp))

                vendors.forEach { v ->
                    val isAlreadyAssigned = rfq.assignedVendorIds.contains(v.id)
                    Surface(
                        color = if (isAlreadyAssigned) IndustrialSurfaceVariant else IndustrialSurface,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = v.businessName, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 13.sp)
                                Text(text = "${v.city} • ★ ${v.rating} • ${v.yearsInBusiness} yrs exp", fontSize = 10.sp, color = SteelSlateLight)
                            }
                            if (isAlreadyAssigned) {
                                Text(text = "Assigned", fontSize = 11.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                            } else {
                                Button(
                                    onClick = {
                                        onAssignVendor(rfq.id, v.id)
                                        assignRfq = null
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight, contentColor = Color.Black),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text("Assign", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(text = title, fontSize = 10.sp, color = SteelSlateLight, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 9.sp, color = Color.LightGray)
        }
    }
}
