package com.nammakelsa.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.nammakelsa.ui.theme.LocalSpacing
import com.nammakelsa.ui.theme.SplashGradientBottom
import com.nammakelsa.ui.theme.SplashGradientTop

@Composable
fun ProfileHeader(
    name: String,
    subtitle: String? = null,
    extraContent: @Composable (ColumnScope.() -> Unit)? = null,
    onAvatarClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val spacing = LocalSpacing.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    listOf(SplashGradientTop, SplashGradientBottom)
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
                    .then(if (onAvatarClick != null) Modifier.clickable { onAvatarClick() } else Modifier),
                contentAlignment = Alignment.Center
            ) {
                if (name.isBlank()) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = name.first().uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 40.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(spacing.sm))

            if (name.isBlank()) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .width(100.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
            } else {
                Text(
                    text = name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = Color.White
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(spacing.xxs))
                    Text(
                        text = subtitle,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            extraContent?.invoke(this)
        }
    }
}
