package com.nammakelsa.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nammakelsa.data.Worker
import com.nammakelsa.ui.components.EmptyState
import com.nammakelsa.ui.components.WorkerCard
import com.nammakelsa.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedWorkersScreen(
    savedWorkers: List<Worker>,
    onWorkerClick: (Worker) -> Unit,
    onToggleSave: (Int) -> Unit
) {
    val spacing = LocalSpacing.current

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
                    text = "Saved Workers",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(spacing.xxs))

                Text(
                    text = "${savedWorkers.size} workers saved",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // ── Content ─────────────────────────────────────────────────
        if (savedWorkers.isEmpty()) {
            EmptyState(
                icon = Icons.Default.BookmarkBorder,
                title = "No saved workers yet",
                subtitle = "Bookmark workers you like to find them easily later"
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    horizontal = spacing.screenPadding - 4.dp,
                    vertical = spacing.sm
                ),
                verticalArrangement = Arrangement.spacedBy(spacing.itemGap - 2.dp)
            ) {
                items(savedWorkers, key = { it.id }) { worker ->
                    WorkerCard(
                        worker = worker,
                        onClick = { onWorkerClick(worker) },
                        showBookmark = true,
                        isBookmarked = true,
                        onBookmarkClick = { onToggleSave(worker.id) }
                    )
                }

                item { Spacer(modifier = Modifier.height(spacing.sm)) }
            }
        }
    }
}
