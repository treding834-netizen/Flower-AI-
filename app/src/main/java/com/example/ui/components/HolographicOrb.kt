package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.example.model.PersonalityMode
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HolographicOrb(
    isListening: Boolean,
    isSpeaking: Boolean,
    personality: PersonalityMode,
    audioRms: Float = 0f,
    modifier: Modifier = Modifier,
    onOrbClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_rotation")

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isListening) 4000 else 12000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    val reverseRotationAngle by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isSpeaking) 5000 else 16000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "reverse_rotation"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = if (isListening || isSpeaking) 700 else 2200,
                easing = FastOutSlowInEasing
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val primaryColor = personality.accentColor
    val secondaryColor = if (personality == PersonalityMode.ROMANTIC_PARTNER) NeonCyan else NeonMagenta

    Box(
        modifier = modifier
            .size(96.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onOrbClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(96.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val baseRadius = size.minDimension / 2.6f
            val dynamicRadius = baseRadius * pulseScale + (audioRms.coerceIn(0f, 10f) * 1.5f)

            // Outer Soft Halo
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.45f),
                        secondaryColor.copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = dynamicRadius * 1.35f
                ),
                radius = dynamicRadius * 1.35f,
                center = center
            )

            // Outer Rotating HUD segmented ring
            rotate(rotationAngle, pivot = center) {
                for (i in 0 until 12) {
                    val angleDeg = i * 30.0
                    val angleRad = Math.toRadians(angleDeg)
                    val r1 = dynamicRadius * 0.95f
                    val r2 = dynamicRadius * 1.12f
                    val start = Offset(
                        center.x + (r1 * cos(angleRad)).toFloat(),
                        center.y + (r1 * sin(angleRad)).toFloat()
                    )
                    val end = Offset(
                        center.x + (r2 * cos(angleRad)).toFloat(),
                        center.y + (r2 * sin(angleRad)).toFloat()
                    )
                    drawLine(
                        color = if (i % 2 == 0) primaryColor.copy(alpha = 0.8f) else secondaryColor.copy(alpha = 0.5f),
                        start = start,
                        end = end,
                        strokeWidth = 2.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            // Inner Reverse Rotating Arc Petals (Flower aperture)
            rotate(reverseRotationAngle, pivot = center) {
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(primaryColor, secondaryColor, primaryColor),
                        center = center
                    ),
                    startAngle = 15f,
                    sweepAngle = 120f,
                    useCenter = false,
                    style = Stroke(width = 3.dp.toPx())
                )
                drawArc(
                    brush = Brush.sweepGradient(
                        listOf(secondaryColor, primaryColor, secondaryColor),
                        center = center
                    ),
                    startAngle = 195f,
                    sweepAngle = 120f,
                    useCenter = false,
                    style = Stroke(width = 3.dp.toPx())
                )
            }

            // Central Glowing AI Core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        primaryColor,
                        secondaryColor,
                        Color.Transparent
                    ),
                    center = center,
                    radius = dynamicRadius * 0.55f
                ),
                radius = dynamicRadius * 0.55f,
                center = center
            )
        }
    }
}
