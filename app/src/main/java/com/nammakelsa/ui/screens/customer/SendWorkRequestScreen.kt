package com.nammakelsa.ui.screens.customer

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammakelsa.ui.components.AppTextField
import com.nammakelsa.ui.components.PrimaryButton
import com.nammakelsa.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendWorkRequestScreen(
    workerName: String,
    description: String,
    onDescriptionChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    date: String,
    onDateChange: (String) -> Unit,
    budget: String,
    onBudgetChange: (String) -> Unit,
    notes: String,
    onNotesChange: (String) -> Unit,
    isSubmitted: Boolean,
    onSubmitClick: () -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val spacing = LocalSpacing.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Send Work Request",
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Success overlay
            AnimatedVisibility(
                visible = isSubmitted,
                enter = fadeIn() + scaleIn()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(AccentGreen.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = AccentGreen,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(spacing.md))

                        Text(
                            text = "Request Sent!",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(spacing.xs))

                        Text(
                            text = "Your work request has been sent\nto $workerName successfully",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(spacing.lg))

                        PrimaryButton(
                            text = "Done",
                            onClick = onBackClick,
                            modifier = Modifier.padding(horizontal = spacing.xxl)
                        )
                    }
                }
            }

            // Form
            if (!isSubmitted) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = spacing.screenPadding, vertical = spacing.sm)
                ) {
                    // Worker info
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(spacing.cornerRadius),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
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
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(spacing.xs))
                            Text(
                                text = "Sending request to $workerName",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.md))

                    AppTextField(
                        value = description,
                        onValueChange = onDescriptionChange,
                        label = "Work Description",
                        leadingIcon = Icons.Default.Description,
                        singleLine = false
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    AppTextField(
                        value = location,
                        onValueChange = onLocationChange,
                        label = "Location",
                        leadingIcon = Icons.Default.LocationOn
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    AppTextField(
                        value = date,
                        onValueChange = onDateChange,
                        label = "Preferred Date",
                        leadingIcon = Icons.Default.CalendarToday
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    AppTextField(
                        value = budget,
                        onValueChange = onBudgetChange,
                        label = "Budget (₹)",
                        leadingIcon = Icons.Default.CurrencyRupee
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    AppTextField(
                        value = notes,
                        onValueChange = onNotesChange,
                        label = "Additional Notes (optional)",
                        leadingIcon = Icons.Default.Notes,
                        singleLine = false
                    )

                    Spacer(modifier = Modifier.height(spacing.lg))

                    PrimaryButton(
                        text = "Send Request",
                        onClick = {
                            if (description.isNotBlank() && location.isNotBlank() && date.isNotBlank()) {
                                onSubmitClick()
                            } else {
                                onShowSnackbar("Please fill all required fields")
                            }
                        },
                        icon = Icons.Default.Send,
                        enabled = true
                    )

                    Spacer(modifier = Modifier.height(spacing.lg))
                }
            }
        }
    }
}
