package com.nammakelsa.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammakelsa.ui.theme.LocalSpacing

// ═══════════════════════════════════════════════════════════════════════
//  Loading State — pulsing dots
// ═══════════════════════════════════════════════════════════════════════

@Composable
fun LoadingState(
    message: String = "Loading…",
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current
    val infiniteTransition = rememberInfiniteTransition(label = "loadingPulse")

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Pulsing dots row
            Row(horizontalArrangement = Arrangement.spacedBy(spacing.xs)) {
                repeat(3) { index ->
                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.3f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(600, delayMillis = index * 200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot$index"
                    )
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .alpha(alpha)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
//  Empty State — icon + message + optional action
// ═══════════════════════════════════════════════════════════════════════

@Composable
fun EmptyState(
    icon: ImageVector = Icons.Default.SearchOff,
    title: String = "Nothing here yet",
    subtitle: String = "",
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.xxl),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            if (subtitle.isNotEmpty()) {
                Spacer(modifier = Modifier.height(spacing.xs))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.height(spacing.md))
                FilledTonalButton(
                    onClick = onAction,
                    shape = RoundedCornerShape(spacing.cornerRadius)
                ) {
                    Text(actionLabel)
                }
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
//  Error State — icon + message + retry
// ═══════════════════════════════════════════════════════════════════════

@Composable
fun ErrorState(
    icon: ImageVector = Icons.Default.ErrorOutline,
    title: String = "Something went wrong",
    subtitle: String = "Please try again later",
    retryLabel: String = "Retry",
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(spacing.xxl),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(72.dp)
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacing.xs))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacing.md))

            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(spacing.cornerRadius),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(retryLabel)
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════
//  Offline State (specialized error)
// ═══════════════════════════════════════════════════════════════════════

@Composable
fun OfflineState(
    onRetry: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    ErrorState(
        icon = Icons.Default.WifiOff,
        title = "You're Offline",
        subtitle = "Check your internet connection and try again",
        retryLabel = "Try Again",
        onRetry = onRetry,
        modifier = modifier
    )
}
