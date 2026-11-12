package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.StarryBackground
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeDeepSpace
import com.example.ui.theme.AnimeMagenta
import com.example.ui.theme.AnimePinkGlow
import com.example.ui.theme.AnimePurpleLight
import com.example.ui.theme.AnimePurplePrimary
import com.example.ui.theme.AnimeRedEnd
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMutedLilac
import com.example.ui.theme.TextPureWhite

@Composable
fun LiveVoiceCallingScreen(
    isListening: Boolean,
    isSpeaking: Boolean,
    isGenerating: Boolean,
    audioRms: Float,
    isMuted: Boolean,
    lastSpeechText: String,
    latestAssistantResponse: String?,
    onToggleMute: () -> Unit,
    onTapToSpeak: () -> Unit,
    onQuickVoicePrompt: (String) -> Unit,
    onReplaySpeech: () -> Unit,
    onEndCall: () -> Unit,
    onMinimize: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_rings")

    val pulse1 by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.10f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse1"
    )

    val pulse2 by infiniteTransition.animateFloat(
        initialValue = 1.05f,
        targetValue = 0.88f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse2"
    )

    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glowAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AnimeDeepSpace)
    ) {
        StarryBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Bar: "Flower" & Minimize dropdown
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Flower",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 28.sp
                        ),
                        color = TextPureWhite
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AnimePurplePrimary.copy(alpha = 0.25f))
                            .border(0.5.dp, AnimePurplePrimary.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "LIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AnimeCyan
                            )
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(AnimeSurface.copy(alpha = 0.85f))
                        .clickable(onClick = onMinimize),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Minimize voice call",
                        tint = TextPureWhite,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Scrollable / Content Body Area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Animated Concentric Holographic Calling Rings (Tap to speak)
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .clickable(onClick = onTapToSpeak),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val baseRadius = size.minDimension / 2.5f
                        val rmsBonus = if (isSpeaking) 8f else (audioRms.coerceIn(0f, 10f) * 3f)

                        // Soft aura glow
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    (if (isSpeaking) AnimeMagenta else AnimeCyan).copy(alpha = 0.40f),
                                    AnimePurplePrimary.copy(alpha = 0.15f),
                                    Color.Transparent
                                ),
                                center = center,
                                radius = (baseRadius * 1.35f) * pulse1
                            ),
                            radius = (baseRadius * 1.35f) * pulse1,
                            center = center
                        )

                        // Outer Cyan/Magenta Ring
                        drawCircle(
                            color = (if (isSpeaking) AnimeMagenta else AnimeCyan).copy(alpha = 0.85f),
                            radius = (baseRadius * 1.15f * pulse1) + rmsBonus,
                            center = center,
                            style = Stroke(width = 24f)
                        )

                        // Inner White / Lilac Ring
                        drawCircle(
                            color = Color.White.copy(alpha = 0.90f),
                            radius = (baseRadius * 0.82f * pulse2) + (rmsBonus * 0.5f),
                            center = center,
                            style = Stroke(width = 10f)
                        )
                    }

                    // Center Mic Icon (Always active, no mic-off)
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(AnimeSurface.copy(alpha = 0.85f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.Default.VolumeUp else Icons.Default.Mic,
                            contentDescription = "Microphone",
                            tint = if (isSpeaking) AnimePinkGlow else AnimeCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Status Header Text
                Text(
                    text = when {
                        isSpeaking -> "Flower is speaking..."
                        isGenerating -> "Flower is thinking..."
                        else -> "Flower is listening..."
                    },
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        fontWeight = FontWeight.Medium,
                        fontSize = 24.sp,
                        color = when {
                            isSpeaking -> AnimePinkGlow
                            isGenerating -> AnimeCyan
                            else -> AnimeCyan
                        }
                    )
                )

                // User's Last Spoken Query
                if (lastSpeechText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "“$lastSpeechText”",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.SemiBold,
                            color = AnimePurpleLight,
                            textAlign = TextAlign.Center
                        ),
                        maxLines = 2,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }

            val rainbowBrush = Brush.sweepGradient(colors = com.example.ui.theme.RainbowColorSpectrum)

            // Floating Bottom Call Control Pill (Mute, End, Minimize) with Rainbow Lighting
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.94f)
                    .height(76.dp)
                    .padding(bottom = 6.dp),
                shape = RoundedCornerShape(38.dp),
                color = AnimeSurface.copy(alpha = 0.90f),
                border = BorderStroke(1.5.dp, rainbowBrush)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 32.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // Prominent Red End Call Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable(onClick = onEndCall)
                            .testTag("end_voice_call_button")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(AnimeRedEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CallEnd,
                                contentDescription = "End call",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "End",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = TextPureWhite
                        )
                    }

                    // Minimize Button
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable(onClick = onMinimize)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(AnimeSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = "Minimize",
                                tint = TextPureWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Minimize",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMutedLilac
                        )
                    }
                }
            }
        }
    }
}


