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
import com.nammakelsa.models.RequestStatus
import com.nammakelsa.models.WorkRequest
import com.nammakelsa.ui.components.*
import com.nammakelsa.ui.theme.*

@Composable
fun WorkerRequestsScreen(
    requests: List<WorkRequest>,
    getStatus: (String) -> RequestStatus,
    onAccept: (String) -> Unit,
    onReject: (String) -> Unit,
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




