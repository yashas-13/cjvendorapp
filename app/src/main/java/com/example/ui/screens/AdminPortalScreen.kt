package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FabricationCategory
import com.example.data.model.FabricationOrder
import com.example.data.model.Quotation
import com.example.data.model.RfqRequest
import com.example.data.model.RfqStatus
import com.example.data.model.ServiceBooking
import com.example.data.model.VendorProfile
import com.example.data.model.VendorVerificationStatus
import com.example.data.repository.AuditEvent
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.IndustrialSurfaceVariant
import com.example.ui.theme.LaserCyanLight
import com.example.ui.theme.SteelSlateLight

/**
 * Screen 10: Admin Dashboard
 * Features:
 * - Role-protected interface for administrators and fabrication staff
 * - Display new enquiries, pending bookings, quotations awaiting approval, active orders, and completed projects
 * - Management panes:
 *     1. Enquiries & RFQ Routing
 *     2. Booking Management (Assign field engineers)
 *     3. Quotation Generation & Review
 *     4. Order-Status Updates (9-stage milestones progression)
 *     5. Catalogue & Vendor Management
 *     6. Notification Controls & Broadcast
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPortalScreen(
    rfqs: List<RfqRequest>,
    vendors: List<VendorProfile>,
    orders: List<FabricationOrder>,
    serviceBookings: List<ServiceBooking> = emptyList(),
    quotationsMap: Map<String, List<Quotation>> = emptyMap(),
    auditLogs: List<AuditEvent> = emptyList(),
    onAssignVendor: (rfqId: String, vendorId: String) -> Unit,
    onUpdateVendorVerificationStatus: (vendorId: String, status: VendorVerificationStatus) -> Unit = { _, _ -> },
    onAdvanceMilestone: (orderId: String, stepIndex: Int, remarks: String) -> Unit = { _, _, _ -> },
    onBroadcastNotification: (title: String, message: String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    var selectedAdminTab by remember { mutableIntStateOf(0) }
    var assignRfq by remember { mutableStateOf<RfqRequest?>(null) }
    var showBroadcastDialog by remember { mutableStateOf(false) }
    var broadcastTitle by remember { mutableStateOf("") }
    var broadcastMessage by remember { mutableStateOf("") }

    val pendingRfqsCount = rfqs.count { it.status == RfqStatus.SUBMITTED }
    val pendingBookingsCount = serviceBookings.size
    val allQuotes = quotationsMap.values.flatten()
    val quotesAwaitingApproval = allQuotes.count { it.status == com.example.data.model.QuotationStatus.PENDING }
    val activeOrdersCount = orders.count { it.currentStepIndex < 9 }
    val completedProjectsCount = orders.count { it.currentStepIndex >= 9 }
    val totalGmv = orders.sumOf { it.totalAmount }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("admin_portal_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Admin Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = LaserCyanLight, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Admin & Staff Control",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }

                    Button(
                        onClick = { showBroadcastDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = LaserCyanLight),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("admin_broadcast_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Send, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Broadcast", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "B2B RFQ routing, booking engineer assignments, quotation issuance, and order milestones.",
                    fontSize = 11.sp,
                    color = SteelSlateLight
                )
            }
        }

        // Metrics Grid (Screen 10 Requirement)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminMetricCard(
                        title = "New Enquiries",
                        value = "${rfqs.size}",
                        subtitle = "$pendingRfqsCount pending",
                        color = ForgeAmberLight,
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Pending Bookings",
                        value = "$pendingBookingsCount",
                        subtitle = "Site Visits",
                        color = LaserCyanLight,
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Quotes Pending",
                        value = "$quotesAwaitingApproval",
                        subtitle = "Awaiting review",
                        color = Color(0xFF6366F1),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminMetricCard(
                        title = "Active Orders",
                        value = "$activeOrdersCount",
                        subtitle = "In production",
                        color = Color(0xFF10B981),
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Completed",
                        value = "$completedProjectsCount",
                        subtitle = "Handed over",
                        color = Color.LightGray,
                        modifier = Modifier.weight(1f)
                    )
                    AdminMetricCard(
                        title = "Total GMV",
                        value = "₹${(totalGmv / 1000).toInt()}k",
                        subtitle = "Platform volume",
                        color = ForgeAmberLight,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Admin Management Tabs
        item {
            TabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = IndustrialSurface,
                contentColor = ForgeAmberLight,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedAdminTab]),
                        color = ForgeAmberLight,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
            ) {
                listOf(
                    "Enquiries (${rfqs.size})",
                    "Bookings (${serviceBookings.size})",
                    "Orders (${orders.size})",
                    "Vendors (${vendors.size})",
                    "Audit Logs"
                ).forEachIndexed { index, title ->
                    Tab(
                        selected = selectedAdminTab == index,
                        onClick = { selectedAdminTab = index },
                        text = { Text(title, fontSize = 11.sp, fontWeight = if (selectedAdminTab == index) FontWeight.Bold else FontWeight.Normal) },
                        modifier = Modifier.testTag("admin_tab_$index")
                    )
                }
            }
        }

        // Tab Content
        when (selectedAdminTab) {
            0 -> {
                // TAB 0: Enquiries & RFQ Routing
                item {
                    Text(
                        text = "Customer Enquiries & Fabricator Routing",
                        fontSize = 15.sp,
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
                                text = "Site: ${rfq.siteAddress}, ${rfq.city} - ${rfq.pincode} • Target: ${rfq.targetCompletionDate}",
                                fontSize = 11.sp,
                                color = SteelSlateLight
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Requirement Items: " + rfq.items.joinToString(", ") { "${it.title} (${it.quantity}x)" },
                                fontSize = 11.sp,
                                color = Color.LightGray
                            )

                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Assigned Workshops: ${rfq.assignedVendorIds.size}",
                                    fontSize = 11.sp,
                                    color = SteelSlateLight
                                )

                                OutlinedButton(
                                    onClick = { assignRfq = rfq },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = LaserCyanLight),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("assign_vendor_btn_${rfq.referenceNumber}")
                                ) {
                                    Icon(imageVector = Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Assign Vendor", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // TAB 1: Booking Management
                item {
                    Text(
                        text = "Site Measurement & Service Bookings",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (serviceBookings.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("No pending service bookings.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(16.dp))
                        }
                    }
                } else {
                    items(serviceBookings, key = { it.id }) { bk ->
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
                                    Text(bk.referenceNumber, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ForgeAmberLight)
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = LaserCyanLight.copy(alpha = 0.2f)
                                    ) {
                                        Text(bk.status.displayName, fontSize = 10.sp, color = LaserCyanLight, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                }

                                Text(text = "Customer: ${bk.customerName} (${bk.customerPhone})", fontSize = 12.sp, color = Color.White, modifier = Modifier.padding(top = 4.dp))
                                Text(text = "Address: ${bk.siteAddress}, ${bk.city} - ${bk.pincode}", fontSize = 11.sp, color = Color.LightGray)
                                Text(text = "Schedule: ${bk.preferredDate} • ${bk.preferredTimeSlot}", fontSize = 11.sp, color = SteelSlateLight)
                                Text(text = "Assigned Engineer: ${bk.assignedEngineerName}", fontSize = 11.sp, color = LaserCyanLight, modifier = Modifier.padding(top = 2.dp))

                                Spacer(modifier = Modifier.height(10.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = { /* assign engineer */ },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Engineering, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Re-assign Engineer", fontSize = 11.sp, color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // TAB 2: Orders & Milestone Status Updates
                item {
                    Text(
                        text = "Orders & 9-Stage Milestone Status Updates",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                items(orders, key = { it.id }) { ord ->
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
                                Text(ord.orderNumber, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ForgeAmberLight)
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.2f)
                                ) {
                                    Text("Stage ${ord.currentStepIndex} of 9", fontSize = 10.sp, color = Color(0xFF10B981), modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }

                            Text("Customer: ${ord.customerName} • Fabricator: ${ord.vendorName}", fontSize = 12.sp, color = Color.White, modifier = Modifier.padding(top = 4.dp))
                            Text("Total Amount: ₹${ord.totalAmount.toInt()} • Site: ${ord.deliveryAddress}", fontSize = 11.sp, color = Color.LightGray)

                            Spacer(modifier = Modifier.height(10.dp))

                            // Current Milestone details
                            val currMilestone = ord.milestones.getOrNull(ord.currentStepIndex - 1)
                            if (currMilestone != null) {
                                Surface(
                                    color = IndustrialSurfaceContainer,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(currMilestone.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForgeAmberLight)
                                            Text(currMilestone.description, fontSize = 10.sp, color = Color.LightGray)
                                        }
                                        Button(
                                            onClick = {
                                                onAdvanceMilestone(ord.id, ord.currentStepIndex, "Stage marked complete by Marketplace Admin.")
                                            },
                                            shape = RoundedCornerShape(6.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = LaserCyanLight),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("Advance", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            3 -> {
                // TAB 3: Vendors & Verification Audits
                item {
                    Text(
                        text = "Vendor Machinery Audit & Verification",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                items(vendors, key = { it.id }) { vendor ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                        modifier = Modifier.fillMaxWidth().testTag("admin_vendor_card_${vendor.id}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = vendor.businessName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = Color.White
                                    )
                                    if (vendor.gstNumber.isNotBlank()) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(imageVector = Icons.Default.Verified, contentDescription = null, tint = LaserCyanLight, modifier = Modifier.size(14.dp))
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = when (vendor.verificationStatus) {
                                        VendorVerificationStatus.VERIFIED -> Color(0xFF10B981).copy(alpha = 0.2f)
                                        VendorVerificationStatus.PENDING_VERIFICATION -> ForgeAmberLight.copy(alpha = 0.2f)
                                        VendorVerificationStatus.SUSPENDED -> Color.Gray.copy(alpha = 0.2f)
                                    }
                                ) {
                                    Text(
                                        text = vendor.verificationStatus.name.replace("_", " "),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (vendor.verificationStatus) {
                                            VendorVerificationStatus.VERIFIED -> Color(0xFF10B981)
                                            VendorVerificationStatus.PENDING_VERIFICATION -> ForgeAmberLight
                                            VendorVerificationStatus.SUSPENDED -> Color.Gray
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            Text(
                                text = "Contact: ${vendor.contactPerson} • ${vendor.phone} • ${vendor.city}",
                                fontSize = 11.sp,
                                color = SteelSlateLight
                            )
                            Text(
                                text = "Machinery: " + vendor.machineryCapabilities.joinToString(", "),
                                fontSize = 11.sp,
                                color = Color.LightGray,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (vendor.verificationStatus != VendorVerificationStatus.VERIFIED) {
                                    Button(
                                        onClick = { onUpdateVendorVerificationStatus(vendor.id, VendorVerificationStatus.VERIFIED) },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(30.dp).testTag("verify_vendor_btn_${vendor.id}")
                                    ) {
                                        Text("Approve & Verify", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (vendor.verificationStatus == VendorVerificationStatus.VERIFIED) {
                                    OutlinedButton(
                                        onClick = { onUpdateVendorVerificationStatus(vendor.id, VendorVerificationStatus.SUSPENDED) },
                                        shape = RoundedCornerShape(6.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(30.dp).testTag("suspend_vendor_btn_${vendor.id}")
                                    ) {
                                        Text("Suspend", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            4 -> {
                // TAB 4: Audit Logs
                item {
                    Text(
                        text = "Immutable Compliance Audit Log",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                if (auditLogs.isEmpty()) {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("No audit log events recorded yet.", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(16.dp))
                        }
                    }
                } else {
                    items(auditLogs, key = { it.id }) { log ->
                        Card(
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(log.action, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = LaserCyanLight)
                                    Text(log.actor, fontSize = 10.sp, color = Color.Gray)
                                }
                                Text(log.detail, fontSize = 11.sp, color = Color.White, modifier = Modifier.padding(top = 2.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal Sheet for Assigning Vendor
    if (assignRfq != null) {
        ModalBottomSheet(
            onDismissRequest = { assignRfq = null },
            containerColor = IndustrialSurfaceVariant
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Text(
                    text = "Assign Vendor to ${assignRfq!!.referenceNumber}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Select certified workshop to receive specifications and drawings:",
                    fontSize = 12.sp,
                    color = SteelSlateLight,
                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                )

                val availableVendors = vendors.filter { it.verificationStatus == VendorVerificationStatus.VERIFIED }

                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(availableVendors) { v ->
                        val isAssigned = assignRfq!!.assignedVendorIds.contains(v.id)
                        Card(
                            onClick = {
                                onAssignVendor(assignRfq!!.id, v.id)
                                assignRfq = null
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(v.businessName, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                    Text("${v.city} • Rating: ${v.rating}", fontSize = 11.sp, color = SteelSlateLight)
                                }
                                if (isAssigned) {
                                    Text("Assigned", color = Color(0xFF10B981), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                } else {
                                    Text("+ Assign", color = ForgeAmberLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Broadcast Notification Dialog
    if (showBroadcastDialog) {
        AlertDialog(
            onDismissRequest = { showBroadcastDialog = false },
            title = { Text("Broadcast Marketplace Alert") },
            text = {
                Column {
                    OutlinedTextField(
                        value = broadcastTitle,
                        onValueChange = { broadcastTitle = it },
                        label = { Text("Notification Title") },
                        placeholder = { Text("e.g. Steel Mill Price Adjustment Notice") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForgeAmberLight),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = broadcastMessage,
                        onValueChange = { broadcastMessage = it },
                        label = { Text("Message Body") },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForgeAmberLight),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (broadcastTitle.isNotBlank()) {
                            onBroadcastNotification(broadcastTitle, broadcastMessage)
                            showBroadcastDialog = false
                            broadcastTitle = ""
                            broadcastMessage = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight)
                ) {
                    Text("Broadcast Alert", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showBroadcastDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = IndustrialSurface
        )
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
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(title, fontSize = 10.sp, color = SteelSlateLight, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Black, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 9.sp, color = Color.Gray)
        }
    }
}
