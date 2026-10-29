package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PersonalityMode
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CyberpunkHeader(
    activePersonality: PersonalityMode,
    isFlashlightOn: Boolean,
    volumePercent: Int,
    isTtsEnabled: Boolean,
    onPersonalitySelect: (PersonalityMode) -> Unit,
    onToggleFlashlight: () -> Unit,
    onAdjustVolume: (Boolean) -> Unit,
    onToggleTts: () -> Unit,
    onOpenMemoryVault: () -> Unit,
    onOpenApiKeyDialog: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showPersonalityMenu by remember { mutableStateOf(false) }

    Surface(
        color = CyberSurface.copy(alpha = 0.95f),
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(activePersonality.accentColor.copy(alpha = 0.4f), CyberCardBorder)
                ),
                shape = androidx.compose.ui.graphics.RectangleShape
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Top Row: Brand, Creator Attribution & Quick Action Icons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Identity
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("app_identity_row")
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    listOf(activePersonality.accentColor, NeonCyan)
                                )
                            )
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(CircleShape)
                                .background(CyberBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "✿",
                                color = activePersonality.accentColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "FLOWER AI",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontFamily = FontFamily.Monospace,
                                    letterSpacing = 1.5.sp,
                                    fontWeight = FontWeight.Black
                                ),
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(activePersonality.accentColor.copy(alpha = 0.2f))
                                    .border(0.5.dp, activePersonality.accentColor, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "v2.5 LIVE",
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = activePersonality.accentColor,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Text(
                            text = "Creator: Md Ajijul Haque",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                }

                // Action Icons (Flashlight, Volume, TTS, Key, Memory)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Flashlight
                    IconButton(
                        onClick = onToggleFlashlight,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("header_flashlight_button")
                    ) {
                        Icon(
                            imageVector = if (isFlashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                            contentDescription = "Flashlight",
                            tint = if (isFlashlightOn) NeonAmber else TextTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // TTS Voice Mute/Unmute
                    IconButton(
                        onClick = onToggleTts,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("header_tts_button")
                    ) {
                        Icon(
                            imageVector = if (isTtsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                            contentDescription = "TTS Voice",
                            tint = if (isTtsEnabled) NeonCyan else TextTertiary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Memory Vault
                    IconButton(
                        onClick = onOpenMemoryVault,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("header_memory_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = "Memory Vault",
                            tint = NeonMagenta,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // API Key Settings
                    IconButton(
                        onClick = onOpenApiKeyDialog,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("header_api_key_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = "API Key Settings",
                            tint = NeonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Row: Dynamic Personality Switcher Chips & Language Bar
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                PersonalityMode.entries.forEach { mode ->
                    val isSelected = mode == activePersonality
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) mode.accentColor.copy(alpha = 0.22f)
                                else CyberSurfaceVariant.copy(alpha = 0.6f)
                            )
                            .border(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) mode.accentColor else CyberCardBorder,
                                shape = RoundedCornerShape(8.dp)
                            )
                            .clickable { onPersonalitySelect(mode) }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                            .testTag("persona_chip_${mode.name}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) mode.accentColor else TextTertiary)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = mode.shortBadge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 11.sp
                                ),
                                color = if (isSelected) mode.accentColor else TextSecondary
                            )
                        }
                    }
                }
            }
        }
    }
}
