package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.RainbowColorSpectrum

/**
 * Animated Rainbow Lighting Modifier that gives ANY button or component
 * a continuous glowing RGB Rainbow border.
 */
@Composable
fun Modifier.rainbowLightingBorder(
    shape: Shape = RoundedCornerShape(16.dp),
    borderWidth: Dp = 2.dp,
    durationMillis: Int = 2400
): Modifier {
    val infiniteTransition = rememberInfiniteTransition(label = "rainbow_border_anim")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainbow_angle"
    )

    val rainbowBrush = Brush.sweepGradient(
        colors = RainbowColorSpectrum
    )

    return this
        .clip(shape)
        .border(
            width = borderWidth,
            brush = rainbowBrush,
            shape = shape
        )
}

/**
 * 3D Rainbow Button wrapper that makes any button feel 3D, tactile,
 * and surrounded with active RGB Rainbow lighting.
 */
@Composable
fun Rainbow3DButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(16.dp),
    backgroundColor: Color = Color(0xFF220728),
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rainbow_button_sweep")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainbow_btn_angle"
    )

    val rainbowBrush = Brush.sweepGradient(colors = RainbowColorSpectrum)

    Box(
        modifier = modifier
            .clip(shape)
            .background(rainbowBrush)
            .padding(1.8.dp) // Rainbow border thickness
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        backgroundColor.copy(alpha = 0.98f),
                        Color(0xFF140319)
                    )
                )
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}
