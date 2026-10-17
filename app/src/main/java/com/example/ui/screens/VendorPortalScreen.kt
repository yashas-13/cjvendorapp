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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.model.MilestoneState
import com.example.data.model.RfqRequest
import com.example.data.model.VendorProfile
import com.example.ui.components.StatusBadge
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.IndustrialSurfaceVariant
import com.example.ui.theme.LaserCyanLight
import com.example.ui.theme.SteelSlateLight

import com.example.data.model.VendorVerificationStatus
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.SwapHoriz

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorPortalScreen(
    currentVendor: VendorProfile,
    allVendors: List<VendorProfile> = emptyList(),
    onSelectVendor: (String) -> Unit = {},
    assignedRfqs: List<RfqRequest>,
    vendorOrders: List<FabricationOrder>,
    onSubmitQuote: (
        rfqId: String,
        baseFab: Double,
        material: Double,
        install: Double,
        transport: Double,
        leadDays: Int,
        validity: String,
        materialGuarantee: String,
        siteVisit: Boolean,
        notes: String
    ) -> Unit,
    onAdvanceOrderMilestone: (orderId: String, stepIndex: Int, remarks: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var quoteRfq by remember { mutableStateOf<RfqRequest?>(null) }
    var milestoneOrder by remember { mutableStateOf<FabricationOrder?>(null) }
    var vendorSwitcherExpanded by remember { mutableStateOf(false) }

    val isVerified = currentVendor.verificationStatus == VendorVerificationStatus.VERIFIED

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("vendor_portal_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Vendor Header Card with Workshop Switcher
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentVendor.businessName,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (isVerified) {
                                    Icon(
                                        imageVector = Icons.Default.Verified,
                                        contentDescription = "Verified Fabricator",
                                        tint = LaserCyanLight,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Contact: ${currentVendor.contactPerson} • ${currentVendor.phone}",
                                fontSize = 11.sp,
                                color = SteelSlateLight
                            )
                            Text(
                                text = "Workshop: ${currentVendor.workshopAddress}, ${currentVendor.city}",
                                fontSize = 11.sp,
                                color = SteelSlateLight
                            )
                            Text(
                                text = "GSTIN: ${currentVendor.gstNumber} • ★ ${currentVendor.rating} (${currentVendor.completedProjects} Jobs)",
                                fontSize = 11.sp,
                                color = ForgeAmberLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Workshop Switcher Dropdown Button
                        Box {
                            Surface(
                                onClick = { vendorSwitcherExpanded = true },
                                shape = RoundedCornerShape(8.dp),
                                color = if (isVerified) Color(0xFF10B981).copy(alpha = 0.2f) else Color(0xFFEF4444).copy(alpha = 0.2f),
                                modifier = Modifier.testTag("switch_workshop_btn")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = currentVendor.verificationStatus.name,
                                        color = if (isVerified) Color(0xFF10B981) else Color(0xFFEF4444),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.SwapHoriz,
                                        contentDescription = "Switch Workshop",
                                        modifier = Modifier.size(12.dp),
                                        tint = Color.LightGray
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = vendorSwitcherExpanded,
                                onDismissRequest = { vendorSwitcherExpanded = false }
                            ) {
                                allVendors.forEach { v ->
                                    DropdownMenuItem(
                                        text = {
                                            Column {
                                                Text(text = v.businessName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                Text(text = "Status: ${v.verificationStatus.name} • ${v.city}", fontSize = 10.sp, color = Color.Gray)
                                            }
                                        },
                                        onClick = {
                                            onSelectVendor(v.id)
                                            vendorSwitcherExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // RBAC Warning if unverified
                    if (!isVerified) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFF450A0A),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFCA5A5), modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "RBAC Policy: Unverified workshops cannot quote on requirements or view customer drawings. Please await Admin machinery audit.",
                                    fontSize = 11.sp,
                                    color = Color(0xFFFECACA),
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Workshop Machinery Verified:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    currentVendor.machineryCapabilities.forEach { cap ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 1.dp)) {
                            Icon(imageVector = Icons.Default.PrecisionManufacturing, contentDescription = null, tint = LaserCyanLight, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = cap, fontSize = 10.sp, color = SteelSlateLight)
                        }
                    }
                }
            }
        }

        // Assigned RFQs Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Assigned Requirements (${assignedRfqs.size})",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Strict Vendor Access Control",
                    fontSize = 11.sp,
                    color = SteelSlateLight
                )
            }
        }

        if (assignedRfqs.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No RFQs currently assigned to your workshop profile.", color = SteelSlateLight, fontSize = 12.sp)
                    }
                }
            }
        } else {
            items(assignedRfqs, key = { it.id }) { rfq ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                    modifier = Modifier.fillMaxWidth().testTag("vendor_assigned_rfq_${rfq.referenceNumber}")
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

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Customer: ${rfq.customerName} (${rfq.projectType}) • ${rfq.city} - ${rfq.pincode}",
                            fontSize = 12.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Site Address: ${rfq.siteAddress} • Target: ${rfq.targetCompletionDate}",
                            fontSize = 11.sp,
                            color = SteelSlateLight
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Item summary
                        rfq.items.forEachIndexed { i, itm ->
                            Surface(
                                color = IndustrialSurfaceContainer,
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "${i + 1}. ${itm.title} (${itm.lengthFt}x${itm.heightFt} ft) • Qty: ${itm.quantity}",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = itm.material,
                                        fontSize = 11.sp,
                                        color = ForgeAmberLight
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (isVerified) {
                                    quoteRfq = rfq
                                }
                            },
                            enabled = isVerified,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isVerified) ForgeAmberLight else Color.DarkGray,
                                contentColor = if (isVerified) Color.Black else Color.LightGray,
                                disabledContainerColor = Color(0xFF262626),
                                disabledContentColor = Color.Gray
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("vendor_create_quote_btn_${rfq.referenceNumber}")
                        ) {
                            Icon(
                                imageVector = if (isVerified) Icons.Default.Send else Icons.Default.Warning,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isVerified) "Prepare & Submit Quotation" else "Verification Audit Required to Quote",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        // Active Orders & Milestone Progress Updates
        item {
            Text(
                text = "Active Fabrication Work Orders (${vendorOrders.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (vendorOrders.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No active work orders. Submit competitive quotes on assigned RFQs above to win jobs.", color = SteelSlateLight, fontSize = 11.sp)
                    }
                }
            }
        } else {
            items(vendorOrders, key = { it.id }) { order ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Order: ${order.orderNumber}",
                                fontWeight = FontWeight.Bold,
                                color = ForgeAmberLight,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Val: ₹${order.totalAmount.toInt()}",
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "Client: ${order.customerName} • Site: ${order.deliveryAddress}",
                            fontSize = 11.sp,
                            color = SteelSlateLight
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Show active milestone
                        val activeMilestone = order.milestones.find { it.state == MilestoneState.IN_PROGRESS }
                            ?: order.milestones.find { it.state == MilestoneState.PENDING }

                        activeMilestone?.let { m ->
                            Surface(
                                color = IndustrialSurfaceContainer,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "Current Stage: Step ${m.stepIndex} - ${m.title}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                        Text(text = m.description, fontSize = 10.sp, color = SteelSlateLight)
                                    }
                                    Button(
                                        onClick = { milestoneOrder = order },
                                        colors = ButtonDefaults.buttonColors(containerColor = LaserCyanLight, contentColor = Color.Black),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text("Update", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal BottomSheet for Vendor Quotation Submission
    quoteRfq?.let { rfq ->
        VendorQuoteModalSheet(
            rfq = rfq,
            onDismiss = { quoteRfq = null },
            onSubmit = { baseFab, material, install, transport, lead, validity, guarantee, siteVisit, notes ->
                onSubmitQuote(rfq.id, baseFab, material, install, transport, lead, validity, guarantee, siteVisit, notes)
                quoteRfq = null
            }
        )
    }

    // Modal BottomSheet for Advancing Milestone
    milestoneOrder?.let { order ->
        val activeStep = order.milestones.find { it.state == MilestoneState.IN_PROGRESS }?.stepIndex ?: 2
        MilestoneUpdateModalSheet(
            order = order,
            currentStep = activeStep,
            onDismiss = { milestoneOrder = null },
            onUpdate = { step, remark ->
                onAdvanceOrderMilestone(order.id, step, remark)
                milestoneOrder = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VendorQuoteModalSheet(
    rfq: RfqRequest,
    onDismiss: () -> Unit,
    onSubmit: (
        baseFab: Double,
        material: Double,
        install: Double,
        transport: Double,
        leadDays: Int,
        validity: String,
        guarantee: String,
        siteVisit: Boolean,
        notes: String
    ) -> Unit
) {
    var baseFabStr by remember { mutableStateOf("25000") }
    var materialStr by remember { mutableStateOf("14000") }
    var installStr by remember { mutableStateOf("3500") }
    var transportStr by remember { mutableStateOf("1500") }
    var leadDaysStr by remember { mutableStateOf("14") }
    var validityStr by remember { mutableStateOf("15 Days") }
    var guaranteeStr by remember { mutableStateOf("Tata/Jindal Prime Grade with 80-micron Epoxy Powder Coat") }
    var siteVisitReq by remember { mutableStateOf(true) }
    var notesStr by remember { mutableStateOf("Tolerances within 1mm. Anchor bolts included.") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = IndustrialSurfaceContainer
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
        ) {
            Text(
                text = "Quotation Submission — ${rfq.referenceNumber}",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Customer: ${rfq.customerName} • Site: ${rfq.city}",
                fontSize = 11.sp,
                color = ForgeAmberLight
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = baseFabStr,
                    onValueChange = { baseFabStr = it },
                    label = { Text("Fabrication Labour (₹)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = materialStr,
                    onValueChange = { materialStr = it },
                    label = { Text("Raw Material Cost (₹)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = installStr,
                    onValueChange = { installStr = it },
                    label = { Text("Installation (₹)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = transportStr,
                    onValueChange = { transportStr = it },
                    label = { Text("Transport (₹)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = leadDaysStr,
                    onValueChange = { leadDaysStr = it },
                    label = { Text("Lead Time (Days)", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = validityStr,
                    onValueChange = { validityStr = it },
                    label = { Text("Quote Validity", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = guaranteeStr,
                onValueChange = { guaranteeStr = it },
                label = { Text("Material Grade Guarantee / Mill Standard", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Pre-fabrication site visit required?", fontSize = 12.sp, color = Color.White)
                Switch(
                    checked = siteVisitReq,
                    onCheckedChange = { siteVisitReq = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = ForgeAmberLight)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = {
                    val bf = baseFabStr.toDoubleOrNull() ?: 20000.0
                    val mc = materialStr.toDoubleOrNull() ?: 12000.0
                    val ic = installStr.toDoubleOrNull() ?: 3000.0
                    val tc = transportStr.toDoubleOrNull() ?: 1000.0
                    val ld = leadDaysStr.toIntOrNull() ?: 14
                    onSubmit(bf, mc, ic, tc, ld, validityStr, guaranteeStr, siteVisitReq, notesStr)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight, contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_submit_vendor_quote_btn")
            ) {
                Text("Submit Quotation to Customer", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MilestoneUpdateModalSheet(
    order: FabricationOrder,
    currentStep: Int,
    onDismiss: () -> Unit,
    onUpdate: (step: Int, remark: String) -> Unit
) {
    var remarks by remember { mutableStateOf("Work completed according to approved shop drawing specs.") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = IndustrialSurfaceContainer
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = "Update Milestone — Order ${order.orderNumber}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Advancing Step $currentStep to COMPLETED",
                fontSize = 12.sp,
                color = LaserCyanLight
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = remarks,
                onValueChange = { remarks = it },
                label = { Text("Quality Check Remarks / Photo Notes", fontSize = 11.sp) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = { onUpdate(currentStep, remarks) },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981), contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                Text("Mark Step Complete & Advance", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
