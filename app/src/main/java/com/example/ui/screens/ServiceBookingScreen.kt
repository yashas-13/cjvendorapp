package com.example.ui.screens

import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerProfileData
import com.example.data.model.SavedAddress
import com.example.data.model.ServiceBooking
import com.example.data.model.ServiceType
import com.example.ui.theme.ForgeAmberDark
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.IndustrialSurfaceVariant
import com.example.ui.theme.LaserCyanLight
import java.net.URLEncoder

/**
 * Screen 5: Service Booking
 * Features:
 * - Service type selection (Site Measurement, Fabrication, Repair, Automation)
 * - Project specifics & scope notes
 * - Preferred date & time slot selection
 * - Site address with Saved Address quick selector
 * - Photo attachments for site openings
 * - Contact details (Name, phone, email)
 * - Booking Summary & Confirmation modal with generated reference (CJF-BK-2026-XXXX) & WhatsApp handoff
 */
@Composable
fun ServiceBookingScreen(
    customerProfile: CustomerProfileData,
    onBookingSubmitted: (
        serviceType: ServiceType,
        customerName: String,
        customerPhone: String,
        customerEmail: String,
        siteAddress: String,
        landmark: String,
        city: String,
        pincode: String,
        preferredDate: String,
        preferredTimeSlot: String,
        projectDetails: String,
        attachedPhotoCount: Int
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var selectedServiceType by remember { mutableStateOf(ServiceType.SITE_MEASUREMENT) }
    var customerName by remember { mutableStateOf(customerProfile.fullName) }
    var customerPhone by remember { mutableStateOf(customerProfile.phone) }
    var customerEmail by remember { mutableStateOf(customerProfile.email) }

    val defaultAddr = customerProfile.savedAddresses.firstOrNull { it.isDefault } ?: customerProfile.savedAddresses.firstOrNull()
    var siteAddress by remember { mutableStateOf(defaultAddr?.fullAddress ?: "") }
    var landmark by remember { mutableStateOf(defaultAddr?.landmark ?: "") }
    var city by remember { mutableStateOf(defaultAddr?.city ?: "Bengaluru") }
    var pincode by remember { mutableStateOf(defaultAddr?.pincode ?: "560066") }

    var preferredDate by remember { mutableStateOf("26-Oct-2026") }
    var preferredTimeSlot by remember { mutableStateOf("Morning (09:00 AM - 12:00 PM)") }
    var projectDetails by remember { mutableStateOf("") }
    var attachedPhotoCount by remember { mutableIntStateOf(2) }

    var confirmedBooking by remember { mutableStateOf<ServiceBooking?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }

    val dateOptions = listOf("24-Oct-2026", "25-Oct-2026", "26-Oct-2026", "27-Oct-2026", "28-Oct-2026")
    val timeSlotOptions = listOf(
        "Morning (09:00 AM - 12:00 PM)",
        "Afternoon (12:00 PM - 04:00 PM)",
        "Evening (04:00 PM - 07:00 PM)"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
            .testTag("service_booking_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 640.dp)) {

            // Header
            Text(
                text = "Book a Fabrication Service",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Schedule on-site laser measurements, engineering consultations, or gate automation setup with verified specialists.",
                fontSize = 13.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
            )

            // Section 1: Service Type Selection
            Text(
                text = "1. SELECT SERVICE TYPE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForgeAmberLight,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ServiceType.values().forEach { st ->
                    val isSel = selectedServiceType == st
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSel) IndustrialSurfaceContainer else IndustrialSurface
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = if (isSel) ForgeAmberLight else Color(0xFF343B4B)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedServiceType = st }
                            .testTag("service_type_${st.name}")
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = if (isSel) ForgeAmberLight else Color(0xFF2E3440),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = when (st) {
                                            ServiceType.SITE_MEASUREMENT -> Icons.Default.Engineering
                                            ServiceType.FABRICATION_ERECTION -> Icons.Default.Build
                                            ServiceType.REPAIR_MAINTENANCE -> Icons.Default.Build
                                            ServiceType.AUTOMATION_RETROFIT -> Icons.Default.Engineering
                                        },
                                        contentDescription = null,
                                        tint = if (isSel) Color.Black else Color.LightGray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = st.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) ForgeAmberLight else Color.White
                                )
                                Text(
                                    text = st.description,
                                    fontSize = 12.sp,
                                    color = Color.LightGray,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 2: Preferred Schedule
            Text(
                text = "2. PREFERRED DATE & TIME WINDOW",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForgeAmberLight,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Surface(
                color = IndustrialSurface,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Select Preferred Date", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(dateOptions) { d ->
                            val isSel = preferredDate == d
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ForgeAmberLight else IndustrialSurfaceContainer)
                                    .clickable { preferredDate = d }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = d,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) Color.Black else Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Select Time Window", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        timeSlotOptions.forEach { slot ->
                            val isSel = preferredTimeSlot == slot
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ForgeAmberLight.copy(alpha = 0.15f) else IndustrialSurfaceContainer)
                                    .border(1.dp, if (isSel) ForgeAmberLight else Color(0xFF343B4B), RoundedCornerShape(8.dp))
                                    .clickable { preferredTimeSlot = slot }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = if (isSel) ForgeAmberLight else Color.Transparent,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isSel) ForgeAmberLight else Color.Gray),
                                    modifier = Modifier.size(16.dp)
                                ) {}
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = slot,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) ForgeAmberLight else Color.White
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 3: Site Address Details
            Text(
                text = "3. SITE ADDRESS & LOCATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForgeAmberLight,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Surface(
                color = IndustrialSurface,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Quick saved address chips
                    if (customerProfile.savedAddresses.isNotEmpty()) {
                        Text("Use Saved Site Address:", fontSize = 12.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(customerProfile.savedAddresses) { addr ->
                                val isMatched = siteAddress == addr.fullAddress
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isMatched) ForgeAmberLight.copy(alpha = 0.2f) else IndustrialSurfaceContainer,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isMatched) ForgeAmberLight else Color(0xFF343B4B)),
                                    modifier = Modifier.clickable {
                                        siteAddress = addr.fullAddress
                                        landmark = addr.landmark
                                        city = addr.city
                                        pincode = addr.pincode
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = null, tint = ForgeAmberLight, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = addr.label, fontSize = 11.sp, color = Color.White)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    OutlinedTextField(
                        value = siteAddress,
                        onValueChange = { siteAddress = it },
                        label = { Text("Site / Property Street Address") },
                        placeholder = { Text("Plot/Door No, Street Name, Layout") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeAmberLight,
                            unfocusedBorderColor = Color(0xFF3B4354)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("booking_address_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = landmark,
                            onValueChange = { landmark = it },
                            label = { Text("Landmark") },
                            placeholder = { Text("Near Club House") },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForgeAmberLight,
                                unfocusedBorderColor = Color(0xFF3B4354)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = pincode,
                            onValueChange = { pincode = it },
                            label = { Text("Pincode") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForgeAmberLight,
                                unfocusedBorderColor = Color(0xFF3B4354)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeAmberLight,
                            unfocusedBorderColor = Color(0xFF3B4354)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Section 4: Contact & Project Specifics
            Text(
                text = "4. CONTACT & SCOPE NOTES",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = ForgeAmberLight,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Surface(
                color = IndustrialSurface,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = customerName,
                        onValueChange = { customerName = it },
                        label = { Text("Contact Person Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = ForgeAmberLight) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeAmberLight,
                            unfocusedBorderColor = Color(0xFF3B4354)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("booking_contact_name")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("Phone Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = ForgeAmberLight) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForgeAmberLight,
                                unfocusedBorderColor = Color(0xFF3B4354)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("booking_contact_phone")
                        )
                        OutlinedTextField(
                            value = customerEmail,
                            onValueChange = { customerEmail = it },
                            label = { Text("Email") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ForgeAmberLight,
                                unfocusedBorderColor = Color(0xFF3B4354)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = projectDetails,
                        onValueChange = { projectDetails = it },
                        label = { Text("Project Details / Gate Opening Info") },
                        placeholder = { Text("e.g., Sloping driveway, existing concrete pillars with 14 ft gap, need motorized Italian sliding gate.") },
                        minLines = 3,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeAmberLight,
                            unfocusedBorderColor = Color(0xFF3B4354)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("booking_project_details")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Attached site photo counter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null, tint = LaserCyanLight, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Attached Site Photos: $attachedPhotoCount", fontSize = 13.sp, color = Color.White)
                        }
                        TextButton(onClick = { attachedPhotoCount += 1 }) {
                            Text("+ Attach More", fontSize = 12.sp, color = ForgeAmberLight)
                        }
                    }
                }
            }

            validationError?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = it, color = Color(0xFFEF4444), fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Booking Button
            Button(
                onClick = {
                    if (customerName.isBlank() || customerPhone.isBlank() || siteAddress.isBlank()) {
                        validationError = "Please fill in contact name, phone, and site address."
                        return@Button
                    }
                    validationError = null
                    onBookingSubmitted(
                        selectedServiceType,
                        customerName,
                        customerPhone,
                        customerEmail,
                        siteAddress,
                        landmark,
                        city,
                        pincode,
                        preferredDate,
                        preferredTimeSlot,
                        projectDetails,
                        attachedPhotoCount
                    )
                    // Set mock confirmed booking for modal review
                    val rand = (1000..9999).random()
                    confirmedBooking = ServiceBooking(
                        referenceNumber = "CJF-BK-2026-$rand",
                        serviceType = selectedServiceType,
                        customerName = customerName,
                        customerPhone = customerPhone,
                        customerEmail = customerEmail,
                        siteAddress = siteAddress,
                        landmark = landmark,
                        city = city,
                        pincode = pincode,
                        preferredDate = preferredDate,
                        preferredTimeSlot = preferredTimeSlot,
                        projectDetails = projectDetails,
                        attachedPhotoCount = attachedPhotoCount
                    )
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("submit_service_booking_button")
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm & Book Service Visit", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // Confirmation Modal Dialog (Screen 5 Requirement)
    confirmedBooking?.let { bk ->
        AlertDialog(
            onDismissRequest = { confirmedBooking = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Booking Confirmed!")
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Surface(
                        color = IndustrialSurfaceContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "REFERENCE NUMBER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                            Text(
                                text = bk.referenceNumber,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = ForgeAmberLight
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "Service: ${bk.serviceType.displayName}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = "Date: ${bk.preferredDate} (${bk.preferredTimeSlot})", fontSize = 12.sp, color = Color.LightGray)
                    Text(text = "Site: ${bk.siteAddress}, ${bk.city}", fontSize = 12.sp, color = Color.LightGray)
                    Text(text = "Field Engineer: ${bk.assignedEngineerName}", fontSize = 12.sp, color = LaserCyanLight, modifier = Modifier.padding(top = 4.dp))

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "Our field engineer will call you 30 minutes prior to arrival. Laser measurements will be uploaded directly to your CJ Fabrications account.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // WhatsApp Handoff
                        val text = "*SERVICE BOOKING - CJ FABRICATIONS*\n" +
                            "Booking Ref: *${bk.referenceNumber}*\n" +
                            "Service: *${bk.serviceType.displayName}*\n" +
                            "Date: *${bk.preferredDate} (${bk.preferredTimeSlot})*\n" +
                            "Customer: *${bk.customerName}* (${bk.customerPhone})\n" +
                            "Site Address: *${bk.siteAddress}, ${bk.city}*\n" +
                            "Scope: ${bk.projectDetails.ifBlank { "Standard site measurement and laser inspection." }}"
                        val encoded = URLEncoder.encode(text, "UTF-8")
                        val uri = Uri.parse("https://api.whatsapp.com/send?text=$encoded")
                        val intent = Intent(Intent.ACTION_VIEW, uri)
                        context.startActivity(intent)
                        confirmedBooking = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366))
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Share on WhatsApp", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmedBooking = null }) {
                    Text("Done", color = Color.White)
                }
            },
            containerColor = IndustrialSurface
        )
    }
}
