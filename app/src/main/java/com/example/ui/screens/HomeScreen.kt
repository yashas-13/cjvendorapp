package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.FabricationCategory
import com.example.ui.theme.ForgeAmberDark
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.IndustrialSurfaceVariant
import com.example.ui.theme.LaserCyanLight
import com.example.ui.theme.SteelSlateLight

@Composable
fun HomeScreen(
    categories: List<FabricationCategory>,
    onCategoryClick: (String) -> Unit,
    onNavigateToCatalog: () -> Unit,
    onNavigateToCustomRfq: () -> Unit,
    onNavigateToBookService: () -> Unit = {},
    onNavigateToUploadPhotos: () -> Unit = {},
    onNavigateToTrackOrders: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    onTrackRfq: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var trackRefInput by remember { mutableStateOf("") }
    var searchInput by remember { mutableStateOf("") }
    var selectedSector by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // Hero Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                IndustrialSurfaceVariant,
                                IndustrialSurface,
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = ForgeAmberLight.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = ForgeAmberLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "VERIFIED FABRICATORS • INDIA",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ForgeAmberLight,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // Notifications Icon with Badge
                        IconButton(
                            onClick = onNavigateToNotifications,
                            modifier = Modifier.testTag("home_notifications_bell")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadNotificationsCount > 0) {
                                        Badge(containerColor = ForgeAmberLight, contentColor = Color.Black) {
                                            Text("$unreadNotificationsCount", fontSize = 10.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Precision Metalwork\nDirect From Certified Workshops.",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        lineHeight = 30.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Gates, SS Balcony Railings, Custom Staircases, PEB Sheds & Architectural Metalwork.",
                        fontSize = 12.sp,
                        color = SteelSlateLight,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Global Search Bar (Screen 2 Requirement)
                    OutlinedTextField(
                        value = searchInput,
                        onValueChange = { searchInput = it },
                        placeholder = {
                            Text("Search gates, grills, SS railings, sheds...", fontSize = 13.sp, color = Color.Gray)
                        },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = ForgeAmberLight)
                        },
                        trailingIcon = {
                            if (searchInput.isNotBlank()) {
                                Button(
                                    onClick = onNavigateToCatalog,
                                    shape = RoundedCornerShape(6.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = ForgeAmberLight),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(32.dp).padding(end = 4.dp)
                                ) {
                                    Text("Find", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeAmberLight,
                            unfocusedBorderColor = Color(0xFF3B4354),
                            focusedContainerColor = IndustrialSurface,
                            unfocusedContainerColor = IndustrialSurface
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_input")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4 Quick-Action Shortcuts Grid (Screen 2 Requirement)
                    Text(
                        text = "QUICK SHORTCUTS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = ForgeAmberLight,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Book a Service
                        QuickActionCard(
                            title = "Book a Service",
                            subtitle = "Site Measurement",
                            icon = Icons.Default.Engineering,
                            iconTint = ForgeAmberLight,
                            onClick = onNavigateToBookService,
                            modifier = Modifier.weight(1f).testTag("quick_action_book_service")
                        )

                        // 2. Upload Photos
                        QuickActionCard(
                            title = "Upload Photos",
                            subtitle = "CAD & Drawings",
                            icon = Icons.Default.AddAPhoto,
                            iconTint = LaserCyanLight,
                            onClick = onNavigateToUploadPhotos,
                            modifier = Modifier.weight(1f).testTag("quick_action_upload_photos")
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 3. Request a Quote
                        QuickActionCard(
                            title = "Request a Quote",
                            subtitle = "Custom RFQ",
                            icon = Icons.Default.Build,
                            iconTint = Color(0xFF10B981),
                            onClick = onNavigateToCustomRfq,
                            modifier = Modifier.weight(1f).testTag("quick_action_request_quote")
                        )

                        // 4. Track Orders
                        QuickActionCard(
                            title = "Track Orders",
                            subtitle = "9-Stage Live Status",
                            icon = Icons.Default.Schedule,
                            iconTint = Color(0xFF818CF8),
                            onClick = onNavigateToTrackOrders,
                            modifier = Modifier.weight(1f).testTag("quick_action_track_orders")
                        )
                    }
                }
            }
        }

        // CJF 5 Business Service Divisions Showcase
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "CJF Service Divisions",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Security • Structures • Interiors • Utility • Architectural Metalwork",
                        fontSize = 11.sp,
                        color = ForgeAmberLight,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(listOf(
                            Triple("Security", "Gates, grills, safety doors, fencing", "Homeowners"),
                            Triple("Structures", "Parking sheds, staircases, roofing, PEB", "Builders & Contractors"),
                            Triple("Interiors", "Furniture frames, partitions, racks", "Interior Designers"),
                            Triple("Utility", "Kitchen racks, drying stands, tank stands", "Homeowners & Apartments"),
                            Triple("Architectural", "Glass & SS railings, aluminium, luxury gates", "Architects & Luxury Homes")
                        )) { (division, products, target) ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = IndustrialSurfaceContainer,
                                modifier = Modifier.width(180.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text("CJF $division", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(products, fontSize = 10.sp, color = SteelSlateLight, maxLines = 2, lineHeight = 13.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("For: $target", fontSize = 9.sp, color = LaserCyanLight, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick RFQ Tracking Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurfaceContainer)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = trackRefInput,
                        onValueChange = { trackRefInput = it },
                        placeholder = {
                            Text("Track RFQ (e.g., CJF-2026-M892)", fontSize = 12.sp, color = Color.Gray)
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ForgeAmberLight,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("track_rfq_input")
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (trackRefInput.isNotBlank()) {
                                onTrackRfq(trackRefInput.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = LaserCyanLight, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("track_rfq_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Track")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Track", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Fabrication Categories Grid
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Fabrication Categories",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = "View All",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ForgeAmberLight,
                        modifier = Modifier.clickable { onNavigateToCatalog() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sector Filter: All Sectors / Residential / Industrial
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(
                        null to "All Sectors",
                        "Residential" to "Residential (5 Divisions)",
                        "Industrial" to "Industrial (10 Lines)"
                    ).forEach { (sectorKey, label) ->
                        val isSelected = selectedSector == sectorKey
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) ForgeAmberLight else IndustrialSurface,
                            modifier = Modifier.clickable { selectedSector = sectorKey }
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else Color.LightGray,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val displayedCategories = if (selectedSector == null) categories else categories.filter { it.sector == selectedSector }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    displayedCategories.forEach { category ->
                        Card(
                            onClick = { onCategoryClick(category.id) },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("category_card_${category.id}")
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = category.imageUrl,
                                    contentDescription = category.name,
                                    modifier = Modifier
                                        .size(64.dp)
                                        .clip(RoundedCornerShape(8.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = category.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = category.description,
                                        fontSize = 11.sp,
                                        color = SteelSlateLight,
                                        maxLines = 2,
                                        lineHeight = 15.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Benchmark: ${category.indicativeRate} ${category.rateUnit}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = ForgeAmberLight
                                        )
                                    }
                                }

                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = SteelSlateLight,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Value Proposition Pillars
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = IndustrialSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Why Choose CJ Fabrications?",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    PillarItem(
                        icon = Icons.Default.Verified,
                        title = "GST & Machinery Audited Fabricators",
                        desc = "Every vendor is inspected for laser cutting, CNC bending, and qualified welders before quoting."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PillarItem(
                        icon = Icons.Default.Security,
                        title = "Jindal / Tata Certified Raw Materials",
                        desc = "Mill test certificates verified for stainless steel grades (SS 304/316) and structural steel."
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PillarItem(
                        icon = Icons.Default.Handyman,
                        title = "Transparent Quotation Breakdown",
                        desc = "Compare itemized metal cost, fabrication labour, powder coating, and on-site installation."
                    )
                }
            }
        }
    }
}

@Composable
private fun PillarItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(ForgeAmberLight.copy(alpha = 0.2f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = ForgeAmberLight,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = desc,
                fontSize = 11.sp,
                color = SteelSlateLight,
                lineHeight = 15.sp
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = IndustrialSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF343B4B)),
        modifier = modifier
            .clickable { onClick() }
            .height(82.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = iconTint.copy(alpha = 0.15f),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

