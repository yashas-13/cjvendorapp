package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RfqStatus
import com.example.ui.theme.StatusAccepted
import com.example.ui.theme.StatusAssigned
import com.example.ui.theme.StatusCancelled
import com.example.ui.theme.StatusCompleted
import com.example.ui.theme.StatusDraft
import com.example.ui.theme.StatusInProduction
import com.example.ui.theme.StatusQuoted

@Composable
fun StatusBadge(
    status: RfqStatus,
    modifier: Modifier = Modifier
) {
    val (badgeBg, badgeColor) = when (status) {
        RfqStatus.DRAFT -> StatusDraft.copy(alpha = 0.2f) to StatusDraft
        RfqStatus.SUBMITTED -> StatusAssigned.copy(alpha = 0.2f) to StatusAssigned
        RfqStatus.ASSIGNED -> StatusAssigned.copy(alpha = 0.25f) to StatusAssigned
        RfqStatus.QUOTED -> StatusQuoted.copy(alpha = 0.25f) to StatusQuoted
        RfqStatus.ACCEPTED -> StatusAccepted.copy(alpha = 0.25f) to StatusAccepted
        RfqStatus.IN_PRODUCTION,
        RfqStatus.DISPATCHED,
        RfqStatus.INSTALLATION -> StatusInProduction.copy(alpha = 0.25f) to StatusInProduction
        RfqStatus.COMPLETED -> StatusCompleted.copy(alpha = 0.25f) to StatusCompleted
        RfqStatus.CANCELLED -> StatusCancelled.copy(alpha = 0.2f) to StatusCancelled
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(badgeBg)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(badgeColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = status.label,
                color = badgeColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
