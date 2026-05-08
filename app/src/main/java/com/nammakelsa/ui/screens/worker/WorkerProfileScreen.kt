package com.nammakelsa.ui.screens.worker

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.nammakelsa.ui.theme.*

@Composable
fun WorkerProfileScreen(
    workerName: String,
    workerPhone: String,
    workerEmail: String,
    workerId: String,
    workerSkill: String,
    workerDailyRate: String,
    profileImageUrl: String?,
    galleryImages: List<String>,
    onEditProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onUploadProfileImage: (Uri) -> Unit,
    onUploadGalleryImage: (Uri) -> Unit
) {
    val spacing = LocalSpacing.current

    val profileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onUploadProfileImage(it) }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onUploadGalleryImage(it) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // ── Profile Header ──────────────────────────────────────────
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
                .padding(top = 52.dp, bottom = spacing.lg + spacing.xs),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(spacing.avatarLarge)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(3.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                        .clickable { profileLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImageUrl != null) {
                        // Coil AsyncImage can be placed here. Fallback to letter for now
                        Text(
                            text = "Img",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    } else {
                        if (workerName.isBlank()) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = workerName.first().uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 40.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(spacing.sm))

                if (workerName.isBlank()) {
                    LinearProgressIndicator(
                        modifier = Modifier.width(100.dp).height(4.dp).clip(RoundedCornerShape(2.dp)),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = 0.3f)
                    )
                } else {
                    Text(
                        text = workerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(spacing.xxs))
                    Text(
                        text = workerEmail,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Spacer(modifier = Modifier.height(spacing.xxs))
                    Text(
                        text = if (workerPhone.isNotBlank()) "+91 $workerPhone" else "Phone number not added",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }

                Spacer(modifier = Modifier.height(spacing.xxs))

                // Worker ID badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = workerId,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 5.dp),
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        letterSpacing = 1.sp
                    )
                }

                Spacer(modifier = Modifier.height(spacing.xs))

                Text(
                    text = "$workerSkill • ₹$workerDailyRate/day",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.85f)
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
            // ── Work Gallery Preview ────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Work Gallery",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { galleryLauncher.launch("image/*") }) {
                    Text("Add Image")
                }
            }

            Spacer(modifier = Modifier.height(spacing.xs))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(spacing.xs + 4.dp)
            ) {
                val count = if (galleryImages.isEmpty()) 1 else galleryImages.size
                items(count) { index ->
                    Card(
                        modifier = Modifier.size(width = 110.dp, height = 90.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        elevation = CardDefaults.cardElevation(1.dp)
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            if (galleryImages.isNotEmpty()) {
                                // Real image loaded here
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = AccentGreen,
                                    modifier = Modifier.size(spacing.md)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(spacing.md)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(spacing.sectionGap))

            // ── Edit Profile ────────────────────────────────────────
            Card(
                onClick = onEditProfileClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(spacing.cornerRadius),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.cardPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.sm))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Edit Profile",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Update your skills and daily rate",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            // Settings
            Card(
                onClick = onSettingsClick,
                shape = RoundedCornerShape(spacing.cornerRadius),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(spacing.cardPadding),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(spacing.sm))
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.lg))

            // Logout
            OutlinedButton(
                onClick = onLogoutClick,
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
