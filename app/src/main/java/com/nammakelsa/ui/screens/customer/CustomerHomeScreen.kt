package com.nammakelsa.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammakelsa.data.Skills
import com.nammakelsa.data.Worker
import com.nammakelsa.ui.components.*
import com.nammakelsa.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    customerName: String,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    activeFilter: String?,
    onFilterSelected: (String?) -> Unit,
    nearbyWorkers: List<Worker>,
    popularWorkers: List<Worker>,
    totalWorkersCount: Int,
    onWorkerClick: (Worker) -> Unit,
    onSearchClick: () -> Unit,
    isWorkerSaved: (String) -> Boolean,
    onToggleSave: (String) -> Unit,
    onNotificationsClick: () -> Unit
) {
    val spacing = LocalSpacing.current

    val greeting = remember {
        val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
        when {
            hour < 12 -> "Good Morning"
            hour < 17 -> "Good Afternoon"
            else      -> "Good Evening"
        }
    }

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
                        listOf(SplashGradientTop, SplashGradientBottom)
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
                            text = "$greeting 👋",
                            fontSize = 15.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                        Spacer(modifier = Modifier.height(spacing.xxs))
                        Text(
                            text = customerName.ifBlank { "Customer" },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    IconButton(onClick = onNotificationsClick) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(spacing.sm))

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            "Search for workers…",
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.7f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.buttonHeight),
                    shape = RoundedCornerShape(spacing.cornerRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.White.copy(alpha = 0.4f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedContainerColor = Color.White.copy(alpha = 0.1f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                        cursorColor = Color.White,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        }

        // ── Content ─────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(vertical = spacing.sm)
        ) {
            // ── Filter Chips ────────────────────────────────────────
            LazyRow(
                contentPadding = PaddingValues(horizontal = spacing.screenPadding),
                horizontalArrangement = Arrangement.spacedBy(spacing.chipGap + 2.dp)
            ) {
                item {
                    SkillChip(
                        label = "All",
                        isSelected = activeFilter == null,
                        onClick = { onFilterSelected(null) }
                    )
                }
                items(Skills.all.take(5)) { skill ->
                    SkillChip(
                        label = skill,
                        isSelected = activeFilter == skill,
                        onClick = { onFilterSelected(skill) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // ── Workers Found Count ─────────────────────────────────
            Text(
                text = "$totalWorkersCount workers found near you",
                modifier = Modifier.padding(horizontal = spacing.screenPadding),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            // ── Nearby Workers ──────────────────────────────────────
            SectionHeader(
                title = "Nearby Workers",
                modifier = Modifier.padding(horizontal = spacing.screenPadding)
            )

            if (nearbyWorkers.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.SearchOff,
                    title = "No workers available nearby",
                    subtitle = "Try adjusting your filters",
                    modifier = Modifier.height(200.dp)
                )
            } else {
                nearbyWorkers.forEach { worker ->
                    WorkerCard(
                        worker = worker,
                        onClick = { onWorkerClick(worker) },
                        showBookmark = true,
                        isBookmarked = isWorkerSaved(worker.id),
                        onBookmarkClick = { onToggleSave(worker.id) },
                        modifier = Modifier
                            .padding(
                                horizontal = spacing.screenPadding - 4.dp,
                                vertical = spacing.xxs + 2.dp
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.md))

            // ── Popular Workers ─────────────────────────────────────
            SectionHeader(
                title = "Popular Workers",
                modifier = Modifier.padding(horizontal = spacing.screenPadding)
            )

            popularWorkers.forEach { worker ->
                WorkerCard(
                    worker = worker,
                    onClick = { onWorkerClick(worker) },
                    showBookmark = true,
                    isBookmarked = isWorkerSaved(worker.id),
                    onBookmarkClick = { onToggleSave(worker.id) },
                    modifier = Modifier
                        .padding(
                            horizontal = spacing.screenPadding - 4.dp,
                            vertical = spacing.xxs + 2.dp
                        )
                )
            }

            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
}
