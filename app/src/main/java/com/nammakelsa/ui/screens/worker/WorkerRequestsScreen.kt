package com.nammakelsa.ui.screens.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammakelsa.data.RequestStatus
import com.nammakelsa.data.WorkRequest
import com.nammakelsa.ui.components.EmptyState
import com.nammakelsa.ui.theme.*

@Composable
fun WorkerRequestsScreen(
    requests: List<WorkRequest>,
    getStatus: (Int) -> RequestStatus,
    onAccept: (Int) -> Unit,
    onReject: (Int) -> Unit,
    pendingCount: Int,
    acceptedCount: Int,
    onNotificationsClick: () -> Unit
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Header ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            AccentGreen.copy(alpha = 0.9f),
                            AccentGreen.copy(alpha = 0.7f)
                        )
                    ),
                    shape = RoundedCornerShape(
                        bottomStart = spacing.lg,
                        bottomEnd = spacing.lg
                    )
                )
                .padding(horizontal = spacing.screenPadding)
                .padding(top = 52.dp, bottom = spacing.md)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Work Requests",
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(spacing.xxs))
                        Text(
                            text = "Manage your incoming requests",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.sm))

                // Stats row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.sm)
                ) {
                    StatChip(
                        label = "$pendingCount Pending",
                        color = AccentAmber,
                        modifier = Modifier.weight(1f)
                    )
                    StatChip(
                        label = "$acceptedCount Accepted",
                        color = AvailableGreen,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // ── Content ─────────────────────────────────────────────────
        if (requests.isEmpty()) {
            EmptyState(
                icon = Icons.Default.Inbox,
                title = "No requests yet",
                subtitle = "New work requests from customers will appear here"
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = spacing.screenPadding,
                    vertical = spacing.sm
                ),
                verticalArrangement = Arrangement.spacedBy(spacing.itemGap - 2.dp)
            ) {
                items(requests, key = { it.id }) { request ->
                    RequestCard(
                        request = request,
                        status = getStatus(request.id),
                        onAccept = { onAccept(request.id) },
                        onReject = { onReject(request.id) }
                    )
                }

                item { Spacer(modifier = Modifier.height(spacing.sm)) }
            }
        }
    }
}

@Composable
private fun StatChip(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.2f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(color, shape = androidx.compose.foundation.shape.CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun RequestCard(
    request: WorkRequest,
    status: RequestStatus,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    val spacing = LocalSpacing.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing.cardPadding + 2.dp)
        ) {
            // Customer name and status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.customerName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                StatusBadge(status = status)
            }

            Spacer(modifier = Modifier.height(spacing.xs))

            // Work type
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = ChipUnselected
            ) {
                Text(
                    text = request.workType,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = ChipTextUnselected,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(spacing.xs))

            Text(
                text = request.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(spacing.xs + 2.dp))

            // Details row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                DetailItem(Icons.Default.CalendarToday, request.date)
                DetailItem(Icons.Default.LocationOn, request.location)
            }

            Spacer(modifier = Modifier.height(spacing.xxs))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.sm)
            ) {
                DetailItem(Icons.Default.CurrencyRupee, request.budget)
            }

            // Action buttons (only for pending)
            if (status == RequestStatus.PENDING) {
                Spacer(modifier = Modifier.height(spacing.sm))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(spacing.xs + 4.dp)
                ) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f).height(spacing.xxl),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error)
                        )
                    ) {
                        Icon(Icons.Default.Close, null, Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject", fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f).height(spacing.xxl),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AccentGreen
                        )
                    ) {
                        Icon(Icons.Default.Check, null, Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Accept", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: RequestStatus) {
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

@Composable
private fun DetailItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
