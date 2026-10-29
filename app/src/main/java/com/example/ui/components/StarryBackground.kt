package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.AnimeBackground
import com.example.ui.theme.AnimeDeepSpace
import com.example.ui.theme.AnimePurplePrimary
import kotlin.random.Random

data class StarPoint(val xRatio: Float, val yRatio: Float, val size: Float, val alphaBase: Float)

@Composable
fun StarryBackground(
    modifier: Modifier = Modifier
) {
    val stars = remember {
        val random = Random(42)
        List(40) {
            StarPoint(
                xRatio = random.nextFloat(),
                yRatio = random.nextFloat(),
                size = random.nextFloat() * 2f + 1f,
                alphaBase = random.nextFloat() * 0.45f + 0.15f
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "stars_twinkle")
    val twinkle by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Deep space gradient
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    AnimeDeepSpace,
                    AnimeBackground,
                    AnimeDeepSpace
                )
            )
        )

        // Subtle ambient purple glow at top-center
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    AnimePurplePrimary.copy(alpha = 0.15f),
                    Color.Transparent
                ),
                center = Offset(size.width * 0.5f, size.height * 0.2f),
                radius = size.width * 0.7f
            ),
            radius = size.width * 0.7f,
            center = Offset(size.width * 0.5f, size.height * 0.2f)
        )

        // Draw twinkling stars
        for (star in stars) {
            val center = Offset(star.xRatio * size.width, star.yRatio * size.height)
            drawCircle(
                color = Color.White.copy(alpha = (star.alphaBase * twinkle).coerceIn(0.1f, 0.8f)),
                radius = star.size,
                center = center
            )
        }
    }
}
