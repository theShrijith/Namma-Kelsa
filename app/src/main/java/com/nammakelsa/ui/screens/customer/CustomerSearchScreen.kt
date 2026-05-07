package com.nammakelsa.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nammakelsa.data.Skills
import com.nammakelsa.data.Worker
import com.nammakelsa.ui.components.EmptyState
import com.nammakelsa.ui.components.SkillChip
import com.nammakelsa.ui.components.WorkerCard
import com.nammakelsa.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerSearchScreen(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    activeFilter: String?,
    onFilterSelected: (String?) -> Unit,
    workers: List<Worker>,
    onWorkerClick: (Worker) -> Unit,
    isWorkerSaved: (String) -> Boolean,
    onToggleSave: (String) -> Unit
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Search Header ───────────────────────────────────────────
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.xxl, bottom = spacing.sm)
                    .padding(horizontal = spacing.screenPadding - 4.dp)
            ) {
                Text(
                    text = "Find Workers",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(spacing.xxs))

                Text(
                    text = "Search skilled workers near you",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(spacing.sm))

                // Search bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            "Search by name or skill…",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { /* filter dialog placeholder */ }) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = "Filter",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(spacing.buttonHeight),
                    shape = RoundedCornerShape(spacing.cornerRadius),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )

                Spacer(modifier = Modifier.height(spacing.sm - 2.dp))

                // Filter chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(spacing.chipGap + 2.dp),
                    contentPadding = PaddingValues(end = spacing.xxs)
                ) {
                    item {
                        SkillChip(
                            label = "All",
                            isSelected = activeFilter == null,
                            onClick = { onFilterSelected(null) }
                        )
                    }
                    items(Skills.all) { skill ->
                        SkillChip(
                            label = skill,
                            isSelected = activeFilter == skill,
                            onClick = { onFilterSelected(skill) }
                        )
                    }
                }
            }
        }

        // ── Results ─────────────────────────────────────────────────
        if (workers.isEmpty()) {
            EmptyState(
                icon = Icons.Default.SearchOff,
                title = "No workers found",
                subtitle = "Try adjusting your search or filters",
                actionLabel = "Clear Filters",
                onAction = { onFilterSelected(null) }
            )
        } else {
            // Results count
            Text(
                text = "${workers.size} workers found",
                modifier = Modifier.padding(
                    start = spacing.screenPadding,
                    top = spacing.sm,
                    bottom = spacing.xs
                ),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = spacing.screenPadding - 4.dp,
                    vertical = spacing.xxs
                ),
                verticalArrangement = Arrangement.spacedBy(spacing.itemGap - 2.dp)
            ) {
                items(workers, key = { it.id }) { worker ->
                    WorkerCard(
                        worker = worker,
                        onClick = { onWorkerClick(worker) },
                        showBookmark = true,
                        isBookmarked = isWorkerSaved(worker.id),
                        onBookmarkClick = { onToggleSave(worker.id) }
                    )
                }

                item { Spacer(modifier = Modifier.height(spacing.sm)) }
            }
        }
    }
}
