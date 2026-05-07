package com.nammakelsa.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ═══════════════════════════════════════════════════════════════════════
//  Light Color Scheme
// ═══════════════════════════════════════════════════════════════════════

private val LightColorScheme = lightColorScheme(
    primary              = Primary,
    onPrimary            = Color.White,
    primaryContainer     = PrimaryContainer,
    onPrimaryContainer   = OnPrimaryContainer,

    secondary            = Secondary,
    onSecondary          = Color.White,
    secondaryContainer   = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,

    tertiary             = Tertiary,
    onTertiary           = Color.White,
    tertiaryContainer    = TertiaryContainer,
    onTertiaryContainer  = Color(0xFF27132E),

    error                = AccentRed,
    onError              = Color.White,
    errorContainer       = Color(0xFFFCDAD7),
    onErrorContainer     = Color(0xFF410002),

    background           = LightBackground,
    onBackground         = TextOnLight,

    surface              = LightSurface,
    onSurface            = TextOnLight,
    surfaceVariant       = LightSurfaceVariant,
    onSurfaceVariant     = TextOnLightMedium,

    outline              = LightOutline,
    outlineVariant       = LightOutlineVariant,

    inverseSurface       = Color(0xFF2F3033),
    inverseOnSurface     = Color(0xFFF1F0F4),
    inversePrimary       = PrimaryLight,
)

// ═══════════════════════════════════════════════════════════════════════
//  Dark Color Scheme
// ═══════════════════════════════════════════════════════════════════════

private val DarkColorScheme = darkColorScheme(
    primary              = PrimaryLight,
    onPrimary            = Color(0xFF003258),
    primaryContainer     = Color(0xFF00497D),
    onPrimaryContainer   = PrimaryContainer,

    secondary            = Color(0xFFBCC7DC),
    onSecondary          = Color(0xFF263141),
    secondaryContainer   = Color(0xFF3D4758),
    onSecondaryContainer = SecondaryContainer,

    tertiary             = Color(0xFFDABDE2),
    onTertiary           = Color(0xFF3D2846),
    tertiaryContainer    = Color(0xFF553F5D),
    onTertiaryContainer  = TertiaryContainer,

    error                = Color(0xFFFFB4AB),
    onError              = Color(0xFF690005),
    errorContainer       = Color(0xFF93000A),
    onErrorContainer     = Color(0xFFFCDAD7),

    background           = DarkBackground,
    onBackground         = TextOnDark,

    surface              = DarkSurface,
    onSurface            = TextOnDark,
    surfaceVariant       = DarkSurfaceVariant,
    onSurfaceVariant     = TextOnDarkMedium,

    outline              = DarkOutline,
    outlineVariant       = DarkOutlineVariant,

    inverseSurface       = Color(0xFFE2E2E6),
    inverseOnSurface     = Color(0xFF2F3033),
    inversePrimary       = Primary,
)

// ═══════════════════════════════════════════════════════════════════════
//  Theme Composable
// ═══════════════════════════════════════════════════════════════════════

@Composable
fun NammaKelsaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalSpacing provides Spacing()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}
