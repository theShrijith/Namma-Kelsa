package com.nammakelsa.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammakelsa.models.RequestStatus
import com.nammakelsa.ui.theme.*

@Composable
fun AvailabilityBadge(
    isAvailable: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isAvailable) AvailableGreen.copy(alpha = 0.15f)
                  else UnavailableDim.copy(alpha = 0.15f)
    val textColor = if (isAvailable) AvailableGreen
                    else MaterialTheme.colorScheme.onSurfaceVariant
    val label = if (isAvailable) "Available" else "Busy"

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = textColor,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun StatusBadge(status: RequestStatus) {
    val (bgColor, textColor, label) = when (status) {
        RequestStatus.PENDING -> Triple(
            AccentAmber.copy(alpha = 0.15f),
            AccentAmber,
            "Pending"
        )
        RequestStatus.ACCEPTED -> Triple(
            AvailableGreen.copy(alpha = 0.15f),
            AvailableGreen,
            "Accepted"
        )
        RequestStatus.REJECTED -> Triple(
            AccentRed.copy(alpha = 0.15f),
            AccentRed,
            "Rejected"
        )
        RequestStatus.COMPLETED -> Triple(
            Primary.copy(alpha = 0.15f),
            Primary,
            "Completed"
        )
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
