package com.nammakelsa.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.nammakelsa.ui.components.*
import com.nammakelsa.ui.theme.LocalSpacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userName: String,
    userEmail: String,
    userPhone: String,
    workerId: String?,
    isDarkMode: Boolean,
    onDarkModeToggle: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    val spacing = LocalSpacing.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = spacing.screenPadding, vertical = spacing.sm)
        ) {
            // ── Profile Information ─────────────────────────────────
            SettingsSectionHeader("Profile Details")

            Spacer(modifier = Modifier.height(spacing.xs))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = spacing.sm),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile image placeholder
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f), shape = RoundedCornerShape(30.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (userName.firstOrNull() ?: 'U').uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(spacing.md))

                Column {
                    Text(
                        text = userName.ifBlank { "Name not provided" },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = userEmail.ifBlank { "Email not provided" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (userPhone.isNotBlank()) "+91 $userPhone" else "Phone number not added",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (workerId != null && workerId.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Worker ID: $workerId",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.sectionGap))

            // ── Appearance Section ──────────────────────────────────
            SettingsSectionHeader("Appearance")

            Spacer(modifier = Modifier.height(spacing.xs))

            // Dark Mode Toggle
            SettingsToggleItem(
                icon = if (isDarkMode) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                title = "Dark Mode",
                subtitle = if (isDarkMode) "Dark theme is active" else "Light theme is active",
                isChecked = isDarkMode,
                onCheckedChange = onDarkModeToggle
            )

            Spacer(modifier = Modifier.height(spacing.sectionGap))

            // ── Notifications Section ───────────────────────────────
            SettingsSectionHeader("Notifications")

            Spacer(modifier = Modifier.height(spacing.xs))

            SettingsToggleItem(
                icon = Icons.Outlined.Notifications,
                title = "Push Notifications",
                subtitle = "Receive alerts for new requests",
                isChecked = true,
                onCheckedChange = { /* UI only */ }
            )

            Spacer(modifier = Modifier.height(spacing.xs))

            SettingsToggleItem(
                icon = Icons.Outlined.Sms,
                title = "SMS Alerts",
                subtitle = "Get SMS for important updates",
                isChecked = false,
                onCheckedChange = { /* UI only */ }
            )

            Spacer(modifier = Modifier.height(spacing.sectionGap))

            // ── Account Section ─────────────────────────────────────
            SettingsSectionHeader("Account")

            Spacer(modifier = Modifier.height(spacing.xs))

            SettingsNavItem(
                icon = Icons.Outlined.Language,
                title = "Language",
                subtitle = "English",
                onClick = { /* UI only */ }
            )

            Spacer(modifier = Modifier.height(spacing.xs))

            SettingsNavItem(
                icon = Icons.Outlined.Info,
                title = "About",
                subtitle = "Version 1.0",
                onClick = { /* UI only */ }
            )

            Spacer(modifier = Modifier.height(spacing.xs))

            SettingsNavItem(
                icon = Icons.Outlined.PrivacyTip,
                title = "Privacy Policy",
                subtitle = "",
                onClick = { /* UI only */ }
            )

            Spacer(modifier = Modifier.height(spacing.sectionGap))

            // Logout Button
            OutlinedButton(
                onClick = { /* UI only – handled by profile screens */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(spacing.buttonHeight),
                shape = RoundedCornerShape(spacing.cornerRadius),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.error)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(spacing.xs))
                Text(
                    "Log Out",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
}


