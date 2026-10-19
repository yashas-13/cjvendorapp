package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.NotificationCategory
import com.example.data.model.NotificationGroup
import com.example.data.model.NotificationItem
import com.example.ui.theme.ForgeAmberDark
import com.example.ui.theme.ForgeAmberLight
import com.example.ui.theme.IndustrialSurface
import com.example.ui.theme.IndustrialSurfaceContainer
import com.example.ui.theme.IndustrialSurfaceVariant
import com.example.ui.theme.LaserCyanLight

/**
 * Screen 8: Notifications Centre
 * Features:
 * - Date-grouped feeds: Today, Yesterday, Earlier
 * - Category badges (Booking, Quotation, Order Milestone, Payment, System)
 * - Read/unread indicators and "Mark All as Read"
 * - Filter toggle (All vs Unread)
 * - Tap-to-navigate action leading to corresponding Quotation, Order, or Booking
 */
@Composable
fun NotificationsScreen(
    notifications: List<NotificationItem>,
    onNotificationClick: (NotificationItem) -> Unit,
    onMarkAllRead: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showOnlyUnread by remember { mutableStateOf(false) }

    val filteredList = if (showOnlyUnread) {
        notifications.filter { !it.isRead }
    } else {
        notifications
    }

    val todayItems = filteredList.filter { it.group == NotificationGroup.TODAY }
    val yesterdayItems = filteredList.filter { it.group == NotificationGroup.YESTERDAY }
    val earlierItems = filteredList.filter { it.group == NotificationGroup.EARLIER }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .testTag("notifications_screen"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(modifier = Modifier.widthIn(max = 640.dp).fillMaxSize()) {

            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("notifications_back_button")) {
                    Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Notifications Centre",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${notifications.count { !it.isRead }} unread alerts",
                        fontSize = 12.sp,
                        color = ForgeAmberLight
                    )
                }
                Spacer(modifier = Modifier.weight(1f))

                // Mark All Read Button
                IconButton(
                    onClick = onMarkAllRead,
                    modifier = Modifier.testTag("mark_all_read_button")
                ) {
                    Icon(imageVector = Icons.Default.DoneAll, contentDescription = "Mark All Read", tint = ForgeAmberLight)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Chips: All vs Unread
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (!showOnlyUnread) ForgeAmberLight else IndustrialSurface,
                    modifier = Modifier.clickable { showOnlyUnread = false }
                ) {
                    Text(
                        text = "All Alerts (${notifications.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (!showOnlyUnread) Color.Black else Color.LightGray,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (showOnlyUnread) ForgeAmberLight else IndustrialSurface,
                    modifier = Modifier.clickable { showOnlyUnread = true }
                ) {
                    Text(
                        text = "Unread Only (${notifications.count { !it.isRead }})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (showOnlyUnread) Color.Black else Color.LightGray,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (filteredList.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = Color.DarkGray, modifier = Modifier.size(56.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "No notifications to display", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    if (todayItems.isNotEmpty()) {
                        item {
                            NotificationSectionHeader(title = "TODAY")
                        }
                        items(todayItems) { item ->
                            NotificationCard(item = item, onClick = { onNotificationClick(item) })
                        }
                    }

                    if (yesterdayItems.isNotEmpty()) {
                        item {
                            NotificationSectionHeader(title = "YESTERDAY")
                        }
                        items(yesterdayItems) { item ->
                            NotificationCard(item = item, onClick = { onNotificationClick(item) })
                        }
                    }

                    if (earlierItems.isNotEmpty()) {
                        item {
                            NotificationSectionHeader(title = "EARLIER THIS MONTH")
                        }
                        items(earlierItems) { item ->
                            NotificationCard(item = item, onClick = { onNotificationClick(item) })
                        }
                    }

                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }
        }
    }
}

@Composable
private fun NotificationSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = ForgeAmberLight,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
    )
}

@Composable
private fun NotificationCard(item: NotificationItem, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (!item.isRead) IndustrialSurfaceContainer else IndustrialSurface
        ),
        border = if (!item.isRead) androidx.compose.foundation.BorderStroke(1.dp, ForgeAmberLight.copy(alpha = 0.5f)) else null,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("notification_card_${item.id}")
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Category Icon
            Surface(
                shape = CircleShape,
                color = when (item.category) {
                    NotificationCategory.BOOKING -> LaserCyanLight.copy(alpha = 0.2f)
                    NotificationCategory.QUOTATION -> ForgeAmberLight.copy(alpha = 0.2f)
                    NotificationCategory.ORDER_MILESTONE -> Color(0xFF10B981).copy(alpha = 0.2f)
                    NotificationCategory.PAYMENT -> Color(0xFF6366F1).copy(alpha = 0.2f)
                    NotificationCategory.SYSTEM -> Color.Gray.copy(alpha = 0.2f)
                },
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = when (item.category) {
                            NotificationCategory.BOOKING -> Icons.Default.CalendarMonth
                            NotificationCategory.QUOTATION -> Icons.Default.Description
                            NotificationCategory.ORDER_MILESTONE -> Icons.Default.Build
                            NotificationCategory.PAYMENT -> Icons.Default.Payment
                            NotificationCategory.SYSTEM -> Icons.Default.Info
                        },
                        contentDescription = null,
                        tint = when (item.category) {
                            NotificationCategory.BOOKING -> LaserCyanLight
                            NotificationCategory.QUOTATION -> ForgeAmberLight
                            NotificationCategory.ORDER_MILESTONE -> Color(0xFF10B981)
                            NotificationCategory.PAYMENT -> Color(0xFF818CF8)
                            NotificationCategory.SYSTEM -> Color.White
                        },
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.title,
                        fontSize = 13.sp,
                        fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = item.timestamp,
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Text(
                    text = item.message,
                    fontSize = 12.sp,
                    color = Color.LightGray,
                    modifier = Modifier.padding(top = 4.dp)
                )

                if (!item.isRead) {
                    Row(
                        modifier = Modifier.padding(top = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(ForgeAmberLight)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Tap to view details", fontSize = 11.sp, color = ForgeAmberLight)
                    }
                }
            }
        }
    }
}
