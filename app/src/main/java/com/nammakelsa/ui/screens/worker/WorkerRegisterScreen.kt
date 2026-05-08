package com.nammakelsa.ui.screens.worker

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nammakelsa.data.Skills
import com.nammakelsa.ui.components.*
import com.nammakelsa.ui.theme.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun WorkerRegisterScreen(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    location: String,
    onLocationChange: (String) -> Unit,
    dailyRate: String,
    onDailyRateChange: (String) -> Unit,
    selectedSkills: Set<String>,
    onSkillToggle: (String) -> Unit,
    isComplete: Boolean,
    isLoading: Boolean,
    generatedWorkerId: String,
    onRegisterClick: () -> Unit,
    onContinueClick: () -> Unit,
    onLoginClick: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    val spacing = LocalSpacing.current
    var passwordVisible by remember { mutableStateOf(false) }

    val formValid = name.isNotBlank() && phone.length == 10 && email.isNotBlank() &&
            password.length >= 6 && selectedSkills.isNotEmpty() &&
            dailyRate.isNotBlank() && location.isNotBlank()

    Box(modifier = Modifier.fillMaxSize()) {
        // Success overlay
        AnimatedVisibility(
            visible = isComplete,
            enter = fadeIn() + scaleIn()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = spacing.lg)
                ) {
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
                        text = "Registration Successful!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    Text(
                        text = "Your Worker ID has been created successfully",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(spacing.md))

                    // Worker ID card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = AccentGreen.copy(alpha = 0.08f)
                        ),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(spacing.md),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Your Worker ID",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(spacing.xs))
                            Text(
                                text = generatedWorkerId,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = AccentGreen,
                                letterSpacing = 2.sp
                            )
                            Spacer(modifier = Modifier.height(spacing.xs))
                            Text(
                                text = "Save this ID for login",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.lg))

                    PrimaryButton(
                        text = "Continue to Dashboard",
                        onClick = onContinueClick,
                        icon = Icons.Default.ArrowForward
                    )
                }
            }
        }

        // Registration form
        if (!isComplete) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ) {
                // ── Header ──────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    AccentGreen.copy(alpha = 0.9f),
                                    AccentGreen.copy(alpha = 0.7f)
                                )
                            ),
                            shape = RoundedCornerShape(
                                bottomStart = spacing.xl,
                                bottomEnd = spacing.xl
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Worker Registration",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Create your worker profile",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                // ── Form ────────────────────────────────────────────
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = spacing.screenPadding, vertical = spacing.md)
                ) {
                    // Profile Image Picker
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(3.dp, AccentGreen, CircleShape)
                            .clickable { /* image picker placeholder */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Pick profile photo",
                                tint = AccentGreen,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Add Photo",
                                style = MaterialTheme.typography.labelSmall,
                                color = AccentGreen,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.md))

                    AppTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = "Full Name",
                        leadingIcon = Icons.Default.Person
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    AppTextField(
                        value = phone,
                        onValueChange = onPhoneChange,
                        label = "Phone Number",
                        leadingIcon = Icons.Default.Phone
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    AppTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = "Email Address",
                        leadingIcon = Icons.Default.Email
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    AppTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = "Password",
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    AppTextField(
                        value = location,
                        onValueChange = onLocationChange,
                        label = "Location",
                        leadingIcon = Icons.Default.LocationOn
                    )

                    Spacer(modifier = Modifier.height(spacing.md))

                    // Skills Section
                    SectionHeader(title = "Select Your Skills")

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(spacing.chipGap + 2.dp),
                        verticalArrangement = Arrangement.spacedBy(spacing.chipGap + 2.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Skills.all.forEach { skill ->
                            SkillChip(
                                label = skill,
                                isSelected = skill in selectedSkills,
                                onClick = { onSkillToggle(skill) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.md))

                    AppTextField(
                        value = dailyRate,
                        onValueChange = onDailyRateChange,
                        label = "Daily Rate (₹)",
                        leadingIcon = Icons.Default.CurrencyRupee
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    // Upload work images placeholder
                    Card(
                        onClick = { /* image picker placeholder */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(spacing.cornerRadius),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(spacing.cardPadding),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhotoLibrary,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(spacing.sm))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Upload Work Images",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Showcase your best work",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.lg))

                    PrimaryButton(
                        text = "Register as Worker",
                        onClick = {
                            if (formValid) {
                                onRegisterClick()
                            } else {
                                onShowSnackbar("Please fill all required fields correctly")
                            }
                        },
                        icon = Icons.Default.Check,
                        enabled = !isLoading
                    )

                    Spacer(modifier = Modifier.height(spacing.sm))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Already registered? ",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Sign In",
                            style = MaterialTheme.typography.bodyMedium,
                            color = AccentGreen,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable(onClick = onLoginClick)
                        )
                    }

                    Spacer(modifier = Modifier.height(spacing.lg))
                }
            }
        }
    }
}
