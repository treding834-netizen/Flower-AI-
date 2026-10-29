package com.example.ui.screens

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.PersonalityMode
import com.example.ui.components.StarryBackground
import com.example.ui.theme.AnimeCardBorder
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeMagenta
import com.example.ui.theme.AnimePinkGlow
import com.example.ui.theme.AnimePurpleLight
import com.example.ui.theme.AnimePurplePrimary
import com.example.ui.theme.AnimeRedEnd
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextDimmed
import com.example.ui.theme.TextMutedLilac
import com.example.ui.theme.TextPureWhite

@Composable
fun SettingsScreen(
    apiKey: String,
    activePersonality: PersonalityMode,
    isFlashlightOn: Boolean,
    onOpenApiKeyDialog: () -> Unit,
    onOpenMemoryVault: () -> Unit,
    onCyclePersonality: () -> Unit,
    onToggleFlashlight: () -> Unit,
    onAdjustVolume: (Boolean) -> Unit,
    onTestFemaleVoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        StarryBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            // Action Card 1: Join community
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clickable {
                        Toast.makeText(context, "Community hub ready", Toast.LENGTH_SHORT).show()
                    },
                shape = RoundedCornerShape(20.dp),
                color = AnimeSurface.copy(alpha = 0.90f),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, AnimePurplePrimary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            tint = AnimeMagenta,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Text(
                            text = "Join community",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            ),
                            color = AnimePurpleLight
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = AnimePurpleLight,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Card 2: Watch the setup video
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onOpenApiKeyDialog),
                shape = RoundedCornerShape(20.dp),
                color = AnimeSurface.copy(alpha = 0.90f),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, AnimePinkGlow.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(AnimeRedEnd),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column {
                            Text(
                                text = "Watch the setup video",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = TextPureWhite
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "API keys, permissions and Accessibility, step by step",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = TextMutedLilac
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        tint = TextDimmed,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // SECTION 1: ✨ AI SETTINGS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✨  AI SETTINGS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 13.sp
                    ),
                    color = TextPureWhite
                )

                Text(
                    text = "Configure your AI assistant",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = TextDimmed
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = AnimeSurface.copy(alpha = 0.90f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AnimeCardBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingsRowItem(
                        icon = Icons.Default.Key,
                        iconBg = AnimePurplePrimary,
                        title = "Connections",
                        subtitle = "Gemini and Groq keys",
                        badge = if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") "Ready" else "Set Key",
                        badgeColor = AnimeCyan,
                        onClick = onOpenApiKeyDialog,
                        modifier = Modifier.testTag("settings_connections_item")
                    )

                    SettingsDivider()

                    SettingsRowItem(
                        icon = Icons.Default.RecordVoiceOver,
                        iconBg = AnimePinkGlow,
                        title = "Sweet Female Voice Engine (মেয়েদের মিষ্টি কণ্ঠ)",
                        subtitle = "Sweet Female AI formant · 1.34x pitch · Warm tone",
                        badge = "Test",
                        badgeColor = AnimeCyan,
                        onClick = onTestFemaleVoice
                    )

                    SettingsDivider()

                    SettingsRowItem(
                        icon = Icons.AutoMirrored.Filled.Chat,
                        iconBg = AnimePinkGlow,
                        title = "Chats & memory",
                        subtitle = "Learned memories & preferences",
                        onClick = onOpenMemoryVault,
                        modifier = Modifier.testTag("settings_memories_item")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECTION 2: ⚙️ APP SETTINGS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "⚙️  APP SETTINGS",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 13.sp
                    ),
                    color = TextPureWhite
                )

                Text(
                    text = "Appearance and behavior",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = TextDimmed
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = AnimeSurface.copy(alpha = 0.90f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AnimeCardBorder)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingsRowItem(
                        icon = Icons.Default.Palette,
                        iconBg = AnimeMagenta,
                        title = "Look & feel",
                        subtitle = "${activePersonality.title} · Neon ring orb",
                        onClick = onCyclePersonality
                    )

                    SettingsDivider()

                    SettingsRowItem(
                        icon = Icons.Default.Keyboard,
                        iconBg = AnimePurplePrimary,
                        title = "Voice keyboard",
                        subtitle = "Speak to type in any app",
                        badge = "Turn on",
                        badgeColor = AnimePurpleLight,
                        onClick = {
                            Toast.makeText(context, "Voice keyboard input enabled", Toast.LENGTH_SHORT).show()
                        }
                    )

                    SettingsDivider()

                    SettingsRowItem(
                        icon = Icons.Default.Cast,
                        iconBg = AnimeCyan,
                        title = "Broadcast & Voice Rooms",
                        subtitle = "Create rooms and talk with friends live",
                        onClick = {
                            Toast.makeText(context, "Voice Rooms connected", Toast.LENGTH_SHORT).show()
                        }
                    )

                    SettingsDivider()

                    SettingsRowItem(
                        icon = Icons.Default.Security,
                        iconBg = AnimePurpleLight,
                        title = "Phone control",
                        subtitle = "Quick actions and permissions: Flashlight (${if (isFlashlightOn) "ON" else "OFF"}), Volume",
                        onClick = onToggleFlashlight
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECTION 3: ℹ️ ABOUT
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ℹ️  ABOUT",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 13.sp
                    ),
                    color = TextPureWhite
                )

                Text(
                    text = "App information and support",
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = TextDimmed
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = AnimeSurface.copy(alpha = 0.90f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AnimeCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "FLOWER AI v2.5",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPureWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Creator & Master Engineer: Md Ajijul Haque",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AnimePurpleLight,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Advanced multimodal companion powered by Gemini intelligence, voice synthesis, hardware telemetry and dynamic personas.",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = TextDimmed
                    )
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun SettingsRowItem(
    icon: ImageVector,
    iconBg: Color,
    title: String,
    subtitle: String,
    badge: String? = null,
    badgeColor: Color = AnimeCyan,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg.copy(alpha = 0.25f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconBg,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = TextPureWhite
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                    color = TextMutedLilac
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (badge != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(badgeColor.copy(alpha = 0.2f))
                        .border(1.dp, badgeColor.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = badgeColor
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextDimmed,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SettingsDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(AnimeCardBorder)
    )
}
