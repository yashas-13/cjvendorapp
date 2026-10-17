package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Send
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
import com.example.data.model.RfqItem
import com.example.data.model.RfqRequest
import com.example.ui.components.MaterialChip
import com.example.ui.components.WhatsAppHandoffCard
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.IndustrialSurfaceVariant
import com.example.ui.theme.LaserCyanLight
import com.example.ui.theme.SteelSlateLight

@Composable
fun MyRequirementScreen(
    items: List<RfqItem>,
    onRemoveItem: (String) -> Unit,
    onSubmitRfq: (
        customerName: String,
        customerPhone: String,
        customerEmail: String,
        projectType: String,
        siteAddress: String,
        city: String,
        pincode: String,
        targetDate: String,
        budget: String
    ) -> Unit,
    onNavigateToCatalog: () -> Unit,
    onNavigateToTrackRfq: (RfqRequest) -> Unit,
    whatsAppUrlGenerator: (RfqRequest) -> String,
    whatsAppMessageFormatter: (RfqRequest) -> String,
    modifier: Modifier = Modifier
) {
    var customerName by remember { mutableStateOf("Kavita Rao") }
    var customerPhone by remember { mutableStateOf("9880123456") }
    var customerEmail by remember { mutableStateOf("kavita.rao@example.com") }
    var projectType by remember { mutableStateOf("Architect / Interior Designer") }
    var siteAddress by remember { mutableStateOf("Plot 88, JP Nagar 7th Phase") }
    var city by remember { mutableStateOf("Bengaluru") }
    var pincode by remember { mutableStateOf("560078") }
    var targetDate by remember { mutableStateOf("15-Dec-2026") }
    var budgetRange by remember { mutableStateOf("₹50,000 - ₹80,000") }

    var submittedRfq by remember { mutableStateOf<RfqRequest?>(null) }
    var formError by remember { mutableStateOf<String?>(null) }

    val projectTypes = listOf("Homeowner", "Architect / Interior Designer", "Civil Contractor", "Commercial Builder")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("my_requirement_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "My Requirement (RFQ)",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                    Text(
                        text = "${items.size} item(s) in active quotation collection",
                        fontSize = 12.sp,
                        color = SteelSlateLight
                    )
                }

                if (items.isNotEmpty()) {
                    OutlinedButton(
                        onClick = onNavigateToCatalog,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp), tint = ForgeAmberLight)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+ Add Item", fontSize = 11.sp, color = ForgeAmberLight)
                    }
                }
            }
        }

        // If submitted, show celebration & WhatsApp card
        submittedRfq?.let { rfq ->
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "RFQ Submitted Successfully!",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Reference No: ${rfq.referenceNumber}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForgeAmberLight
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Your requirement has been saved and routed to verified local fabrication workshops. You do not need to create an account — track quotations anytime using this reference.",
                            fontSize = 12.sp,
                            color = SteelSlateLight,
                            lineHeight = 17.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // WhatsApp Handoff
                        WhatsAppHandoffCard(
                            rfq = rfq,
                            formattedMessage = whatsAppMessageFormatter(rfq),
                            whatsAppUrl = whatsAppUrlGenerator(rfq)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { onNavigateToTrackRfq(rfq) },
                            colors = ButtonDefaults.buttonColors(containerColor = LaserCyanLight, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("view_rfq_tracking_btn")
                        ) {
                            Text("View Tracking & Compare Quotations", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Empty state
        if (items.isEmpty() && submittedRfq == null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Assignment,
                            contentDescription = null,
                            tint = SteelSlateLight,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Your Requirement List is Empty",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 17.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Browse our metalwork catalogue and tap 'Add to My Requirement', or fill out a custom technical specification.",
                            color = SteelSlateLight,
                            fontSize = 12.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 17.sp
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                        Button(
                            onClick = onNavigateToCatalog,
                            colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("empty_explore_catalog_btn")
                        ) {
                            Text("Browse Catalogue", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        // Requirement items list
        if (items.isNotEmpty()) {
            items(items, key = { it.id }) { item ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                if (item.designCode != null) {
                                    Text(
                                        text = item.designCode,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ForgeAmberLight
                                    )
                                }
                                Text(
                                    text = item.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                            }
                            IconButton(
                                onClick = { onRemoveItem(item.id) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Remove",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Column {
                                Text("Dimensions", fontSize = 10.sp, color = SteelSlateLight)
                                Text("${item.lengthFt} ft x ${item.heightFt} ft", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text("Material", fontSize = 10.sp, color = SteelSlateLight)
                                Text(item.material, fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text("Qty", fontSize = 10.sp, color = SteelSlateLight)
                                Text("${item.quantity} unit(s)", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text("Installation", fontSize = 10.sp, color = SteelSlateLight)
                                Text(if (item.installationRequired) "Required" else "Supply only", fontSize = 12.sp, color = if (item.installationRequired) ForgeAmberLight else Color.LightGray)
                            }
                        }

                        if (item.finish.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Finish: ${item.finish}",
                                fontSize = 11.sp,
                                color = SteelSlateLight
                            )
                        }
                    }
                }
            }

            // Contact & Site Delivery Details (Only collected when ready to submit)
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = LaserCyanLight, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Contact & Site Delivery Details",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                        }
                        Text(
                            text = "No login required. We use your mobile number only for sending quotation updates and vendor coordination.",
                            fontSize = 11.sp,
                            color = SteelSlateLight,
                            modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                        )

                        // Project Type Chips
                        Text("I am a:", fontSize = 11.sp, color = SteelSlateLight)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                                .padding(vertical = 4.dp)
                        ) {
                            projectTypes.forEach { pt ->
                                MaterialChip(
                                    label = pt,
                                    isSelected = projectType == pt,
                                    onClick = { projectType = pt }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Your Full Name", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = customerPhone,
                                onValueChange = { customerPhone = it },
                                label = { Text("Mobile Number (10 Digits)", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1.2f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = customerEmail,
                                onValueChange = { customerEmail = it },
                                label = { Text("Email (Optional)", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = siteAddress,
                            onValueChange = { siteAddress = it },
                            label = { Text("Site Address / Project Location", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = city,
                                onValueChange = { city = it },
                                label = { Text("City", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = pincode,
                                onValueChange = { pincode = it },
                                label = { Text("Pincode", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = targetDate,
                                onValueChange = { targetDate = it },
                                label = { Text("Target Handover Date", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedTextField(
                                value = budgetRange,
                                onValueChange = { budgetRange = it },
                                label = { Text("Target Budget (Optional)", fontSize = 11.sp) },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = OutlinedTextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White)
                            )
                        }

                        formError?.let { err ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = err, color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                if (customerName.isBlank() || customerPhone.isBlank() || city.isBlank()) {
                                    formError = "Please enter your name, 10-digit mobile number, and city."
                                    return@Button
                                }
                                formError = null
                                onSubmitRfq(
                                    customerName,
                                    customerPhone,
                                    customerEmail,
                                    projectType,
                                    siteAddress,
                                    city,
                                    pincode,
                                    targetDate,
                                    budgetRange
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("submit_rfq_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ForgeAmberLight,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Submit RFQ & Get Verified Quotes",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
