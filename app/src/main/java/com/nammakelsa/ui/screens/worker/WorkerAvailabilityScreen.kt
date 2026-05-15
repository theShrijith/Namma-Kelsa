package com.nammakelsa.ui.screens.worker

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammakelsa.ui.components.SectionHeader
import com.nammakelsa.ui.theme.*

@Composable
fun WorkerAvailabilityScreen(
    isAvailable: Boolean,
    onToggle: () -> Unit,
    pendingRequests: Int,
    acceptedJobs: Int
) {
    val spacing = LocalSpacing.current

    val cardBg by animateColorAsState(
        targetValue = if (isAvailable) AvailableGreen.copy(alpha = 0.08f)
                      else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        animationSpec = tween(400),
        label = "availBg"
    )
    val accentColor by animateColorAsState(
        targetValue = if (isAvailable) AvailableGreen else UnavailableDim,
        animationSpec = tween(400),
        label = "availAccent"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Header ──────────────────────────────────────────────────
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.xxl, bottom = spacing.sm)
                    .padding(horizontal = spacing.screenPadding)
            ) {
                Text(
                    text = "Availability",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(spacing.xxs))

                Text(
                    text = "Manage your work availability",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ── Content ─────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = spacing.screenPadding,
                    vertical = spacing.md
                )
        ) {
            // ── Large Availability Toggle Card ──────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = cardBg),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.md + spacing.xs),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Large status indicator
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(accentColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing.sm))

                    Text(
                        text = if (isAvailable) "Available for Work Today" else "Currently Not Available",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(spacing.xxs))

                    Text(
                        text = if (isAvailable) "Customers can discover and contact you"
                               else "Toggle below to go online and receive requests",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(spacing.md))

                    // Large toggle
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = { onToggle() },
                        modifier = Modifier
                            .width(64.dp)
                            .height(36.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AvailableGreen,
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = UnavailableDim
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.sectionGap))

            // ── Today's Summary ─────────────────────────────────────
            SectionHeader(title = "Today's Summary")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.itemGap - 2.dp)
            ) {
                SummaryCard(
                    icon = Icons.Outlined.Pending,
                    value = "$pendingRequests",
                    label = "Pending",
                    color = AccentAmber,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    icon = Icons.Outlined.CheckCircle,
                    value = "$acceptedJobs",
                    label = "Accepted",
                    color = AvailableGreen,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(spacing.sectionGap))

            // ── Upcoming Jobs Placeholder ────────────────────────────
            SectionHeader(title = "Upcoming Jobs")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(spacing.cornerRadius),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.md),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CalendarMonth,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(spacing.xs))
                    Text(
                        text = "No upcoming jobs scheduled",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(spacing.xxs))
                    Text(
                        text = "Accepted requests will appear here",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
}


