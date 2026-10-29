package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.PersonalityMode
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberVoid
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlin.math.sin

@Composable
fun InputDock(
    onStartVoice: () -> Unit,
    onStopVoice: () -> Unit,
    isListening: Boolean,
    isSpeaking: Boolean,
    isGenerating: Boolean,
    audioRms: Float,
    activePersonality: PersonalityMode,
    attachedImageUri: String?,
    onPickImage: () -> Unit,
    onClearAttachedImage: () -> Unit,
    onOpenImageGenDialog: () -> Unit,
    onQuickAction: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showQuickTools by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "voice_dock_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(650, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    val wavePhase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_phase"
    )

    Surface(
        color = CyberSurface.copy(alpha = 0.98f),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(
                        activePersonality.accentColor.copy(alpha = 0.4f),
                        CyberCardBorder
                    )
                ),
                shape = RectangleShape
            )
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            // Attached Vision Image preview banner
            if (attachedImageUri != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyberSurfaceVariant)
                        .padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = attachedImageUri,
                            contentDescription = "Visual Analysis Target",
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(6.dp))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "VISION TARGET LOADED",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NeonCyan,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                            Text(
                                text = "Speak your question about this image",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                        }
                    }
                    IconButton(
                        onClick = onClearAttachedImage,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear Image",
                            tint = NeonRose,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Quick Voice Commands & Hardware Controls Strip
            AnimatedVisibility(visible = showQuickTools) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(bottom = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    QuickChip(label = "Wake: 'Hey Flower'", icon = null, accent = activePersonality.accentColor) {
                        onQuickAction("Hey Flower")
                    }
                    QuickChip(label = "Flashlight Toggle", icon = Icons.Default.FlashlightOn, accent = NeonAmber) {
                        onQuickAction("Turn on the flashlight")
                    }
                    QuickChip(label = "Volume Up", icon = Icons.Default.VolumeUp, accent = NeonCyan) {
                        onQuickAction("Increase media volume")
                    }
                    QuickChip(label = "Volume Down", icon = Icons.Default.VolumeDown, accent = NeonCyan) {
                        onQuickAction("Decrease media volume")
                    }
                    QuickChip(label = "Open YouTube", icon = null, accent = NeonRose) {
                        onQuickAction("Open YouTube")
                    }
                    QuickChip(label = "Open Chrome", icon = null, accent = NeonCyan) {
                        onQuickAction("Open Chrome")
                    }
                    QuickChip(label = "Open Settings", icon = null, accent = TextSecondary) {
                        onQuickAction("Open phone Settings")
                    }
                }
            }

            // Voice-Only Interaction Bar (No text writing field)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Tools Expand Toggle
                IconButton(
                    onClick = { showQuickTools = !showQuickTools },
                    modifier = Modifier.size(42.dp)
                ) {
                    Icon(
                        imageVector = if (showQuickTools) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = "Quick hardware controls",
                        tint = if (showQuickTools) activePersonality.accentColor else TextTertiary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Photo Picker (Vision for voice questions)
                IconButton(
                    onClick = onPickImage,
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("dock_photo_picker_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Attach image for vision",
                        tint = if (attachedImageUri != null) NeonCyan else TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Primary Large Glowing Voice Communicator Pill
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(54.dp)
                        .clip(RoundedCornerShape(27.dp))
                        .background(
                            when {
                                isListening -> Brush.horizontalGradient(
                                    listOf(NeonRose.copy(alpha = 0.35f), NeonMagenta.copy(alpha = 0.35f))
                                )
                                isSpeaking -> Brush.horizontalGradient(
                                    listOf(activePersonality.accentColor.copy(alpha = 0.3f), NeonCyan.copy(alpha = 0.25f))
                                )
                                isGenerating -> Brush.horizontalGradient(
                                    listOf(NeonAmber.copy(alpha = 0.25f), CyberSurfaceVariant)
                                )
                                else -> Brush.horizontalGradient(
                                    listOf(CyberSurfaceVariant, CyberVoid)
                                )
                            }
                        )
                        .border(
                            width = if (isListening || isSpeaking) 1.8.dp else 1.dp,
                            brush = Brush.horizontalGradient(
                                when {
                                    isListening -> listOf(NeonRose, NeonMagenta)
                                    isSpeaking -> listOf(activePersonality.accentColor, NeonCyan)
                                    isGenerating -> listOf(NeonAmber, NeonCyan)
                                    else -> listOf(activePersonality.accentColor.copy(alpha = 0.7f), CyberCardBorder)
                                }
                            ),
                            shape = RoundedCornerShape(27.dp)
                        )
                        .clickable {
                            if (isListening) onStopVoice() else onStartVoice()
                        }
                        .padding(horizontal = 14.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left: Animated Glowing Mic Icon
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .scale(if (isListening) pulseScale else 1f)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        isListening -> Brush.radialGradient(listOf(NeonRose, NeonMagenta))
                                        isSpeaking -> Brush.radialGradient(listOf(activePersonality.accentColor, NeonCyan))
                                        isGenerating -> Brush.radialGradient(listOf(NeonAmber, CyberVoid))
                                        else -> Brush.radialGradient(listOf(activePersonality.accentColor.copy(alpha = 0.3f), CyberVoid))
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isGenerating) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = NeonAmber,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = when {
                                        isListening -> Icons.Default.MicOff
                                        isSpeaking -> Icons.Default.GraphicEq
                                        else -> Icons.Default.Mic
                                    },
                                    contentDescription = if (isListening) "Stop Listening" else "Start Voice Conversation",
                                    tint = if (isListening || isSpeaking) Color.White else activePersonality.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Center: Live Voice Status Label & Visualizer
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = when {
                                    isListening -> "LISTENING... SPEAK NOW"
                                    isSpeaking -> "FLOWER AI SPEAKING..."
                                    isGenerating -> "SYNTHESIZING..."
                                    else -> "TAP TO TALK"
                                },
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                color = when {
                                    isListening -> NeonRose
                                    isSpeaking -> activePersonality.accentColor
                                    isGenerating -> NeonAmber
                                    else -> TextPrimary
                                }
                            )

                            if (isListening) {
                                // Live Soundwave Canvas
                                Canvas(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(14.dp)
                                ) {
                                    val barCount = 14
                                    val barWidth = 3.dp.toPx()
                                    val spacing = (size.width - (barCount * barWidth)) / (barCount - 1)
                                    val midY = size.height / 2

                                    for (i in 0 until barCount) {
                                        val x = i * (barWidth + spacing)
                                        val angle = Math.toRadians((wavePhase + (i * 25)).toDouble())
                                        val waveAmp = (sin(angle).toFloat() * 0.5f + 0.5f)
                                        val dynamicHeight = (4.dp.toPx() + (waveAmp * (audioRms.coerceIn(0f, 10f) * 2f + 8f))).coerceIn(4f, size.height)

                                        drawLine(
                                            color = if (i % 2 == 0) NeonRose else NeonMagenta,
                                            start = Offset(x, midY - dynamicHeight / 2),
                                            end = Offset(x, midY + dynamicHeight / 2),
                                            strokeWidth = barWidth,
                                            cap = StrokeCap.Round
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = when {
                                        isSpeaking -> "Tap anywhere to interrupt"
                                        isGenerating -> "Processing voice intent"
                                        else -> "Voice-only communication active"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = TextTertiary
                                )
                            }
                        }

                        // Right: Live Status Indicator Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (isListening) NeonRose.copy(alpha = 0.2f)
                                    else activePersonality.accentColor.copy(alpha = 0.15f)
                                )
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = if (isListening) "REC" else "VOICE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    color = if (isListening) NeonRose else activePersonality.accentColor
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // AI Art / Image Generator Modal Trigger
                IconButton(
                    onClick = onOpenImageGenDialog,
                    modifier = Modifier
                        .size(42.dp)
                        .testTag("dock_image_gen_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Generate 1K/2K/4K Artwork",
                        tint = NeonAmber,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    accent: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurfaceVariant.copy(alpha = 0.8f))
            .border(0.5.dp, accent.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 5.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Medium
                ),
                color = TextPrimary
            )
        }
    }
}
