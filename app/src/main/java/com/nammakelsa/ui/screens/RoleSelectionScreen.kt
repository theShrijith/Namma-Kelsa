package com.nammakelsa.ui.screens

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammakelsa.ui.components.*
import com.nammakelsa.ui.theme.*

@Composable
fun RoleSelectionScreen(
    onCustomerSelected: () -> Unit,
    onWorkerSelected: () -> Unit
) {
    val spacing = LocalSpacing.current

    var animateIn by remember { mutableStateOf(false) }
    val contentAlpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(600),
        label = "contentAlpha"
    )
    val contentScale by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.92f,
        animationSpec = tween(700, easing = EaseOutBack),
        label = "contentScale"
    )

    LaunchedEffect(Unit) { animateIn = true }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        AuthHeader(
            title = "Namma Kelsa",
            subtitle = "Your local labor marketplace",
            modifier = Modifier
                .scale(contentScale)
                .alpha(contentAlpha),
            height = 260.dp
        ) {
            Icon(
                imageVector = Icons.Default.Handyman,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(spacing.xs))
        }

        // ── Content area ────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = spacing.screenPadding)
                .scale(contentScale)
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Welcome! How would you\nlike to continue?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacing.xs))

            Text(
                text = "Choose your role to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(spacing.lg + spacing.xs))

            // ── Customer Card ───────────────────────────────────────
            RoleCard(
                icon = Icons.Default.Search,
                title = "Continue as Customer",
                description = "Find nearby skilled workers quickly",
                onClick = onCustomerSelected,
                containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                iconTint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            // ── Worker Card ─────────────────────────────────────────
            RoleCard(
                icon = Icons.Default.Build,
                title = "Continue as Worker",
                description = "Get discovered and receive work requests",
                onClick = onWorkerSelected,
                containerColor = AccentGreen.copy(alpha = 0.12f),
                iconTint = AccentGreen
            )

            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
}


