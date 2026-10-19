package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.AuthState
import com.example.data.model.CustomerProfileData
import com.example.data.model.FabricationOrder
import com.example.data.model.Quotation
import com.example.data.model.SavedAddress
import com.example.data.model.ServiceBooking
import com.example.ui.theme.ForgeAmberDark
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.IndustrialSurfaceVariant
import com.example.ui.theme.LaserCyanLight
import java.util.UUID

/**
 * Screen 9: Customer Profile & Settings
 * Features:
 * - Customer Identity: Full Name, Verified Mobile Number, Email, Avatar
 * - Saved Site Addresses: Multi-address management (Home, Site Project, Office) with Add/Edit/Delete
 * - Activity Hub Tabs:
 *     * My Bookings (List of past and active site measurement bookings)
 *     * My Quotations (Historical and pending quotation records)
 *     * My Orders (Active and completed orders with live milestone indicator)
 * - Preferences & Security: Measurement units toggle (ft/in vs mm), WhatsApp alerts toggle, Sign Out action
 */
@Composable
fun CustomerProfileScreen(
    profile: CustomerProfileData,
    authState: AuthState,
    bookings: List<ServiceBooking>,
    quotations: List<Quotation>,
    orders: List<FabricationOrder>,
    onUpdateProfile: (CustomerProfileData) -> Unit,
    onAddAddress: (SavedAddress) -> Unit,
    onDeleteAddress: (String) -> Unit,
    onSelectBooking: (ServiceBooking) -> Unit,
    onSelectQuotation: (Quotation) -> Unit,
    onSelectOrder: (FabricationOrder) -> Unit,
    onOpenAuthModal: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedActivityTab by remember { mutableIntStateOf(0) } // 0: Bookings, 1: Quotes, 2: Orders

    var showAddAddressDialog by remember { mutableStateOf(false) }
    var newAddressLabel by remember { mutableStateOf("New Project Site") }
    var newAddressFull by remember { mutableStateOf("") }
    var newAddressLandmark by remember { mutableStateOf("") }
    var newAddressCity by remember { mutableStateOf("Bengaluru") }
    var newAddressPincode by remember { mutableStateOf("560066") }

    var showSecurityDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .testTag("customer_profile_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 640.dp)) {

            // Customer Identity Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = ForgeAmberLight,
                            modifier = Modifier.size(58.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = profile.fullName.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("").ifBlank { "CJ" },
                                    fontWeight = FontWeight.Black,
                                    fontSize = 22.sp,
                                    color = Color.Black
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = profile.fullName,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Text(
                                text = profile.roleTitle,
                                fontSize = 12.sp,
                                color = ForgeAmberLight,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                            Text(
                                text = "${profile.phone} • ${profile.email}",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFF2E3440))
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Account Status", fontSize = 11.sp, color = Color.Gray)
                            Text(
                                text = if (authState is AuthState.Authenticated) "Google Verified Account" else "Guest Account (Local)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (authState is AuthState.Authenticated) Color(0xFF10B981) else LaserCyanLight
                            )
                        }

                        if (authState is AuthState.Authenticated) {
                            OutlinedButton(
                                onClick = onSignOut,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF4444)),
                                modifier = Modifier.testTag("profile_sign_out_button")
                            ) {
                                Text("Sign Out", fontSize = 11.sp)
                            }
                        } else {
                            Button(
                                onClick = onOpenAuthModal,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight),
                                modifier = Modifier.testTag("profile_sign_in_button")
                            ) {
                                Text("Sign In / Register", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Saved Site Addresses Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SAVED SITE ADDRESSES",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = ForgeAmberLight,
                    letterSpacing = 1.sp
                )
                TextButton(
                    onClick = { showAddAddressDialog = true },
                    modifier = Modifier.testTag("add_address_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = ForgeAmberLight, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Site", fontSize = 12.sp, color = ForgeAmberLight)
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                profile.savedAddresses.forEach { addr ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = ForgeAmberLight, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = addr.label, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    if (addr.isDefault) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = ForgeAmberLight.copy(alpha = 0.2f)
                                        ) {
                                            Text("Default", fontSize = 9.sp, color = ForgeAmberLight, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                }
                                Text(text = addr.fullAddress, fontSize = 12.sp, color = Color.LightGray, modifier = Modifier.padding(top = 2.dp))
                                if (addr.landmark.isNotBlank()) {
                                    Text(text = "Landmark: ${addr.landmark} • ${addr.city} - ${addr.pincode}", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                            IconButton(
                                onClick = { onDeleteAddress(addr.id) },
                                modifier = Modifier.testTag("delete_address_${addr.id}")
                            ) {
                                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Activity Hub Tabs: My Bookings, My Quotes, My Orders
            Text(
                text = "MY FABRICATION ACTIVITY HUB",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForgeAmberLight,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            TabRow(
                selectedTabIndex = selectedActivityTab,
                containerColor = IndustrialSurface,
                contentColor = ForgeAmberLight,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedActivityTab]),
                        color = ForgeAmberLight,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedActivityTab == 0,
                    onClick = { selectedActivityTab = 0 },
                    text = { Text("Bookings (${bookings.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedActivityTab == 1,
                    onClick = { selectedActivityTab = 1 },
                    text = { Text("Quotes (${quotations.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedActivityTab == 2,
                    onClick = { selectedActivityTab = 2 },
                    text = { Text("Orders (${orders.size})", fontSize = 12.sp) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Content
            when (selectedActivityTab) {
                0 -> {
                    // Bookings Tab
                    if (bookings.isEmpty()) {
                        EmptyStateCard("No service bookings yet. Schedule a measurement from the Book Service tab.")
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            bookings.forEach { bk ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                                    modifier = Modifier.fillMaxWidth().clickable { onSelectBooking(bk) }
                                ) {
                                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(bk.referenceNumber, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ForgeAmberLight)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = LaserCyanLight.copy(alpha = 0.2f)
                                                ) {
                                                    Text(bk.status.displayName, fontSize = 9.sp, color = LaserCyanLight, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                }
                                            }
                                            Text(bk.serviceType.displayName, fontSize = 12.sp, color = Color.White, modifier = Modifier.padding(top = 2.dp))
                                            Text("Scheduled: ${bk.preferredDate} (${bk.preferredTimeSlot})", fontSize = 11.sp, color = Color.Gray)
                                        }
                                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Quotations Tab
                    if (quotations.isEmpty()) {
                        EmptyStateCard("No quotations received yet. Submit an RFQ to receive competitive bids.")
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            quotations.forEach { qt ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                                    modifier = Modifier.fillMaxWidth().clickable { onSelectQuotation(qt) }
                                ) {
                                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(qt.vendorName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = ForgeAmberLight.copy(alpha = 0.2f)
                                                ) {
                                                    Text(qt.status.name, fontSize = 9.sp, color = ForgeAmberLight, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                                }
                                            }
                                            Text("Grand Total: ₹${qt.grandTotal.toInt()} (Lead: ${qt.leadTimeDays}d)", fontSize = 12.sp, color = ForgeAmberLight, modifier = Modifier.padding(top = 2.dp))
                                            Text("Guarantee: ${qt.materialGradeGuarantee}", fontSize = 11.sp, color = Color.Gray, maxLines = 1)
                                        }
                                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Orders Tab
                    if (orders.isEmpty()) {
                        EmptyStateCard("No active fabrication orders. Accepted quotations progress into live orders.")
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            orders.forEach { ord ->
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                                    modifier = Modifier.fillMaxWidth().clickable { onSelectOrder(ord) }
                                ) {
                                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(ord.orderNumber, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = ForgeAmberLight)
                                            Text("Fabricator: ${ord.vendorName}", fontSize = 12.sp, color = Color.White, modifier = Modifier.padding(top = 2.dp))
                                            Text("Stage ${ord.currentStepIndex} of 9 • ₹${ord.totalAmount.toInt()}", fontSize = 11.sp, color = LaserCyanLight)
                                        }
                                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // App Preferences & Settings
            Text(
                text = "PREFERENCES & SECURITY",
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
                    // Measurement Units Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Measurement Units", fontSize = 13.sp, color = Color.White)
                            Text("Current: ${profile.measurementUnit}", fontSize = 11.sp, color = Color.Gray)
                        }
                        TextButton(
                            onClick = {
                                val nextUnit = if (profile.measurementUnit.contains("Feet")) "Millimeters & Meters (mm/m)" else "Feet & Inches (ft/in)"
                                onUpdateProfile(profile.copy(measurementUnit = nextUnit))
                            }
                        ) {
                            Text("Switch", color = ForgeAmberLight, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFF2E3440))
                    Spacer(modifier = Modifier.height(10.dp))

                    // WhatsApp Alerts Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("WhatsApp Milestone Alerts", fontSize = 13.sp, color = Color.White)
                            Text("Receive instant status updates on WhatsApp", fontSize = 11.sp, color = Color.Gray)
                        }
                        Switch(
                            checked = profile.isWhatsAppNotificationEnabled,
                            onCheckedChange = { onUpdateProfile(profile.copy(isWhatsAppNotificationEnabled = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = ForgeAmberLight)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFF2E3440))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Security / Password modal button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSecurityDialog = true }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = LaserCyanLight, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Security, Passwords & Sessions", fontSize = 13.sp, color = Color.White)
                        }
                        Icon(imageVector = Icons.Default.ArrowForward, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Add Address Dialog
    if (showAddAddressDialog) {
        AlertDialog(
            onDismissRequest = { showAddAddressDialog = false },
            title = { Text("Add Saved Site Address") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    OutlinedTextField(
                        value = newAddressLabel,
                        onValueChange = { newAddressLabel = it },
                        label = { Text("Label (e.g., Whitefield Villa Site)") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForgeAmberLight),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newAddressFull,
                        onValueChange = { newAddressFull = it },
                        label = { Text("Full Street Address") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForgeAmberLight),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newAddressLandmark,
                        onValueChange = { newAddressLandmark = it },
                        label = { Text("Landmark") },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ForgeAmberLight),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = newAddressCity,
                            onValueChange = { newAddressCity = it },
                            label = { Text("City") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newAddressPincode,
                            onValueChange = { newAddressPincode = it },
                            label = { Text("Pincode") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newAddressFull.isNotBlank()) {
                            onAddAddress(
                                SavedAddress(
                                    label = newAddressLabel.ifBlank { "Site" },
                                    fullAddress = newAddressFull,
                                    landmark = newAddressLandmark,
                                    city = newAddressCity,
                                    pincode = newAddressPincode
                                )
                            )
                            showAddAddressDialog = false
                            newAddressFull = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight)
                ) {
                    Text("Save Address", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddAddressDialog = false }) {
                    Text("Cancel", color = Color.Gray)
                }
            },
            containerColor = IndustrialSurface
        )
    }

    // Security Dialog
    if (showSecurityDialog) {
        AlertDialog(
            onDismissRequest = { showSecurityDialog = false },
            title = { Text("Account Security") },
            text = {
                Column {
                    Text(
                        text = "• Firebase Authentication protects your account with multi-factor tokens.\n\n" +
                            "• Google One-Tap Credential Manager provides cryptographic sign-in without storing raw passwords.\n\n" +
                            "• All RFQs and Quotation documents are protected by server-side authorization.",
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )
                }
            },
            confirmButton = {
                Button(onClick = { showSecurityDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight)) {
                    Text("Understood", color = Color.Black)
                }
            },
            containerColor = IndustrialSurface
        )
    }
}

@Composable
private fun EmptyStateCard(message: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
    ) {
        Text(
            text = message,
            fontSize = 12.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(20.dp).fillMaxWidth()
        )
    }
}
