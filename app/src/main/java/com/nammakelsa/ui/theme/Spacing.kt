package com.nammakelsa.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 8dp grid spacing system.
 * Every spacing value is a multiple of 8dp for visual consistency.
 */
@Immutable
data class Spacing(
    val none: Dp     = 0.dp,
    val xxs: Dp      = 4.dp,     // half-step for fine adjustments
    val xs: Dp       = 8.dp,     // 1×
    val sm: Dp       = 16.dp,    // 2×
    val md: Dp       = 24.dp,    // 3×
    val lg: Dp       = 32.dp,    // 4×
    val xl: Dp       = 40.dp,    // 5×
    val xxl: Dp      = 48.dp,    // 6×
    val xxxl: Dp     = 56.dp,    // 7×
    val huge: Dp     = 64.dp,    // 8×

    // ── Semantic aliases ────────────────────────────────────────────
    val screenPadding: Dp     = 24.dp,    // Horizontal screen padding
    val cardPadding: Dp       = 16.dp,    // Inside card padding
    val sectionGap: Dp        = 24.dp,    // Between sections
    val itemGap: Dp           = 16.dp,    // Between list items
    val chipGap: Dp           = 8.dp,     // Between chips
    val iconTextGap: Dp       = 8.dp,     // Icon to text
    val buttonHeight: Dp      = 56.dp,    // Touch-friendly button height
    val inputHeight: Dp       = 64.dp,    // Touch-friendly input height
    val avatarSmall: Dp       = 40.dp,
    val avatarMedium: Dp      = 56.dp,
    val avatarLarge: Dp       = 100.dp,
    val cornerRadius: Dp      = 16.dp,    // Default rounded corner
    val cornerRadiusLarge: Dp = 24.dp,    // Larger rounded corner (chips, etc.)
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }
