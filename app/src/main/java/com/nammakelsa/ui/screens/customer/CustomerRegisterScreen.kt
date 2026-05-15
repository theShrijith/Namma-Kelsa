package com.nammakelsa.ui.screens.customer

import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
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
import com.nammakelsa.ui.components.*
import com.nammakelsa.ui.theme.*

@Composable
fun CustomerRegisterScreen(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    onShowSnackbar: (String) -> Unit,
    isLoading: Boolean
) {
    val spacing = LocalSpacing.current
    var passwordVisible by remember { mutableStateOf(false) }

    var animateIn by remember { mutableStateOf(false) }
    val contentAlpha by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0f,
        animationSpec = tween(600),
        label = "contentAlpha"
    )
    val contentScale by animateFloatAsState(
        targetValue = if (animateIn) 1f else 0.95f,
        animationSpec = tween(600, easing = EaseOutBack),
        label = "contentScale"
    )

    LaunchedEffect(Unit) { animateIn = true }

    val passwordsMatch = password == confirmPassword || confirmPassword.isEmpty()
    val formValid = name.isNotBlank() && phone.length == 10 &&
            email.isNotBlank() && password.length >= 6 &&
            password == confirmPassword

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header
        AuthHeader(
            title = "Create Account",
            subtitle = "Join as a Customer",
            modifier = Modifier
                .scale(contentScale)
                .alpha(contentAlpha),
            height = 200.dp
        )

        // ── Form Content ────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp)
                .scale(contentScale)
                .alpha(contentAlpha),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                label = "Confirm Password",
                leadingIcon = Icons.Default.Lock,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = !passwordsMatch,
                errorMessage = if (!passwordsMatch) "Passwords do not match" else null
            )

            Spacer(modifier = Modifier.height(spacing.md))

            PrimaryButton(
                text = "Create Account",
                onClick = {
                    if (formValid) {
                        onRegisterClick()
                    } else if (!passwordsMatch) {
                        onShowSnackbar("Passwords do not match")
                    } else {
                        onShowSnackbar("Please fill all fields correctly")
                    }
                },
                enabled = !isLoading,
                icon = Icons.Default.PersonAdd
            )

            Spacer(modifier = Modifier.height(spacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Sign In",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onLoginClick)
                )
            }

            Spacer(modifier = Modifier.height(spacing.lg))
        }
    }
}
