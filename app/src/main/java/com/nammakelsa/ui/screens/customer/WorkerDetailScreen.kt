package com.nammakelsa.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammakelsa.models.Worker
import com.nammakelsa.ui.components.*
import com.nammakelsa.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkerDetailScreen(
    worker: Worker,
    isSaved: Boolean,
    onBackClick: () -> Unit,
    onCallClick: () -> Unit,
    onRequestWorkClick: () -> Unit,
    onSaveClick: () -> Unit
) {
    val spacing = LocalSpacing.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Top Profile Header ──────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(SplashGradientTop, SplashGradientBottom)
                    ),
                    shape = RoundedCornerShape(
                        bottomStart = 36.dp,
                        bottomEnd = 36.dp
                    )
                )
                .padding(top = spacing.xl, bottom = 36.dp)
        ) {
            // Back button
            IconButton(
                onClick = onBackClick,
                modifier = Modifier
                    .padding(start = spacing.xs, top = spacing.xxs)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            // Save button
            IconButton(
                onClick = onSaveClick,
                modifier = Modifier
                    .padding(end = spacing.xs, top = spacing.xxs)
                    .align(Alignment.TopEnd)
            ) {
                Icon(
                    imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                    contentDescription = if (isSaved) "Unsave" else "Save",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = spacing.md),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(spacing.avatarLarge)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(3.dp, Color.White.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (worker.name.firstOrNull() ?: 'W').uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp
                    )
                }

                Spacer(modifier = Modifier.height(spacing.sm))

                Text(
                    text = worker.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(6.dp))

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = worker.skill,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 6.dp),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(spacing.xs + 2.dp))

                AvailabilityBadge(isAvailable = worker.isAvailable)
            }
        }

        // ── Detail Content ──────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = spacing.screenPadding,
                    vertical = spacing.md
                )
        ) {
            // ── Info Cards Row ──────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(spacing.itemGap - 2.dp)
            ) {
                InfoCard(
                    icon = Icons.Default.CurrencyRupee,
                    label = "Daily Rate",
                    value = "₹${worker.dailyRate}",
                    modifier = Modifier.weight(1f)
                )
                InfoCard(
                    icon = Icons.Default.LocationOn,
                    label = "Distance",
                    value = worker.distance,
                    modifier = Modifier.weight(1f)
                )
                InfoCard(
                    icon = Icons.Default.Star,
                    label = "Rating",
                    value = "${worker.rating} ★",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(spacing.sectionGap + spacing.xxs))

            // ── About ───────────────────────────────────────────────
            SectionHeader(title = "About")

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(spacing.cornerRadius),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Text(
                    text = worker.about.ifBlank {
                        "Experienced ${worker.skill.lowercase()} with expertise in residential and commercial projects. " +
                                "Known for quality work, punctuality, and fair pricing. Available for both short-term and long-term assignments."
                    },
                    modifier = Modifier.padding(spacing.cardPadding),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(spacing.sectionGap + spacing.xxs))

            // ── Work Gallery ────────────────────────────────────────
            SectionHeader(title = "Work Gallery")

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.xs + 4.dp)
            ) {
                items(5) { index ->
                    Card(
                        modifier = Modifier.size(width = 140.dp, height = 120.dp),
                        shape = RoundedCornerShape(spacing.cornerRadius),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(spacing.lg)
                                )
                                Spacer(modifier = Modifier.height(spacing.xxs))
                                Text(
                                    text = "Work ${index + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.sectionGap + spacing.xxs))

            // ── Reviews Placeholder ─────────────────────────────────
            SectionHeader(title = "Reviews")

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
                        .padding(spacing.cardPadding),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.RateReview,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(spacing.xs))
                    Text(
                        text = "Reviews coming soon",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.sectionGap))

            // ── Actions ─────────────────────────────────────────────
            PrimaryButton(
                text = "Request Work",
                onClick = onRequestWorkClick,
                icon = Icons.Default.Send
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            SecondaryButton(
                text = "Call ${worker.name.split(" ").first()}",
                onClick = onCallClick,
                icon = Icons.Default.Call
            )

            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
}

// ── Info Card ───────────────────────────────────────────────────────


