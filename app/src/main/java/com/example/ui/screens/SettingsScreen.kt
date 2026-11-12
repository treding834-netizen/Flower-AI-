package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.provider.Settings
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PermMedia
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.theme.AnimePinkLight
import com.example.ui.theme.AnimePinkPrimary
import com.example.ui.theme.AnimePurpleLight
import com.example.ui.theme.AnimePurplePrimary
import com.example.ui.theme.AnimeRedEnd
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.RainbowColorSpectrum
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
    onOpenAiModeDialog: () -> Unit,
    onClearHistory: () -> Unit,
    onCyclePersonality: () -> Unit,
    onToggleFlashlight: () -> Unit,
    onAdjustVolume: (Boolean) -> Unit,
    onTestFemaleVoice: () -> Unit,
    onOpenVoiceSelection: () -> Unit,
    onOpenThemeSelection: () -> Unit,
    onOpenVoiceKeyboard: () -> Unit,
    onOpenVoiceRooms: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val rainbowBrush = Brush.sweepGradient(colors = RainbowColorSpectrum)

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
            // Top Title Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.5.dp, rainbowBrush, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = AnimeSurface.copy(alpha = 0.95f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "⚙️ AJIJUL AI SETTINGS",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            ),
                            color = TextPureWhite
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Developer: Md. Ajijul Haque",
                            style = MaterialTheme.typography.labelSmall,
                            color = AnimePinkPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION 1: 🔐 AI & VOICE CONFIGURATION
            Text(
                text = "🔐 AI & VOICE CONFIGURATION",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 13.sp
                ),
                color = TextPureWhite
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, rainbowBrush, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = AnimeSurface.copy(alpha = 0.90f)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingsRowItem(
                        icon = Icons.Default.Key,
                        iconBg = AnimePinkPrimary,
                        title = "Gemini API Key Setup",
                        subtitle = "Enter & secure your free API key",
                        badge = if (apiKey.isNotBlank() && apiKey != "MY_GEMINI_API_KEY") "Configured" else "Set Key",
                        badgeColor = AnimePinkPrimary,
                        onClick = onOpenApiKeyDialog
                    )

                    SettingsDivider()

                    SettingsRowItem(
                        icon = Icons.Default.RecordVoiceOver,
                        iconBg = AnimePinkGlow,
                        title = "Voice Select (3D Voice Engine)",
                        subtitle = "Choose sweet female voice (5 options)",
                        badge = "Select",
                        badgeColor = AnimeCyan,
                        onClick = onOpenVoiceSelection
                    )

                    SettingsDivider()

                    SettingsRowItem(
                        icon = Icons.Default.Star,
                        iconBg = AnimeMagenta,
                        title = "AI Mode (GF, BF, Doctor, Tutor)",
                        subtitle = "Current: ${activePersonality.title}",
                        badge = "Change",
                        badgeColor = AnimeMagenta,
                        onClick = onOpenAiModeDialog
                    )

                    SettingsDivider()

                    SettingsRowItem(
                        icon = Icons.AutoMirrored.Filled.Chat,
                        iconBg = AnimePurpleLight,
                        title = "Chats & Memory Vault",
                        subtitle = "Learned memories & preferences",
                        onClick = onOpenMemoryVault
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // SECTION 2: 🛡️ DEVICE PERMISSIONS & ACCESS CONTROL (Accessibility, Notification, Overlays, Location, Gallery, Contacts, Microphone)
            Text(
                text = "🛡️ DEVICE PERMISSIONS & ACCESS CONTROL",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 13.sp
                ),
                color = TextPureWhite
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, rainbowBrush, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = AnimeSurface.copy(alpha = 0.90f)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    SettingsRowItem(
                        icon = Icons.Default.Security,
                        iconBg = AnimeCyan,
                        title = "1. Accessibility Service (অ্যাক্সেসিবিলিটি)",
                        subtitle = "Enable automated control & screen observation",
                        badge = "Grant",
                        badgeColor = AnimeCyan,
                        onClick = {
                            try {
                                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
                            } catch (_: Exception) {
                                Toast.makeText(context, "Open Accessibility settings manually", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )

                    SettingsDivider()

                    SettingsRowItem(
                        icon = Icons.Default.Notifications,
                        iconBg = AnimePinkPrimary,
                        title = "2. Notifications (নোটিফিকেশন)",
                        subtitle = "Allow persistent background voice notifications",
                        badge = "Grant",
                        badgeColor = AnimePinkPrimary,
                        onClick = {
                            try {
                                context.startActivity(Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                    putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
                                })
                            } catch (_: Exception) {}
                        }
                    )

                    SettingsDivider()

                    SettingsRowItem(
                        icon = Icons.Default.Visibility,
                        iconBg = AnimeMagenta,
                        title = "3. Display Over Other Apps (অ্যাপ ওভার দ্য অ্যাপ)",
                        subtitle = "Allow floating voice overlay windows",
                        badge = "Grant",
                        badgeColor = AnimeMagenta,
                        onClick = {
                            try {
                                context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}")))
                            } catch (_: Exception) {}
                        }
                    )

                    SettingsDivider()

                    SettingsRowItem(
                        icon = Icons.Default.LocationOn,
                        iconBg = AnimeRedEnd,
                        title = "4. Location Permission (লোকেশন)",
                        subtitle = "Allow location queries and navigation",
                        badge = "Grant",
                        badgeColor = AnimeRedEnd,
                        onClick = {
                            try {
                                context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                            } catch (_: Exception) {}
                        }
                    )

                    SettingsDivider()

                    SettingsRowItem(
                        icon = Icons.Default.PermMedia,
                        iconBg = AnimePurplePrimary,
                        title = "5. Gallery / Storage (গ্যালারি)",
                        subtitle = "Allow photo selection & AI vision analysis",
                        badge = "Grant",
                        badgeColor = AnimePurplePrimary,
                        onClick = {
                            try {
                                context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")))
                            } catch (_: Exception) {}
                        }
                    )

                    SettingsDivider()

                    SettingsRowItem(
                        icon = Icons.Default.People,
                        iconBg = AnimeCyan,
                        title = "6. Contacts Access (কন্টাক্টস)",
                        subtitle = "Allow finding and messaging contacts",
                        badge = "Grant",
                        badgeColor = AnimeCyan,
                        onClick = {
                            try {
                                context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")))
                            } catch (_: Exception) {}
                        }
                    )

                    SettingsDivider()

                    SettingsRowItem(
                        icon = Icons.Default.Mic,
                        iconBg = AnimePinkGlow,
                        title = "7. Microphone Access (মাইক্রোফোন)",
                        subtitle = "Allow voice input and hands-free calling",
                        badge = "Grant",
                        badgeColor = AnimePinkGlow,
                        onClick = {
                            try {
                                context.startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${context.packageName}")))
                            } catch (_: Exception) {}
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // SECTION 3: 🕒 HISTORY SETTINGS
            Text(
                text = "🕒 HISTORY SETTINGS",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    fontSize = 13.sp
                ),
                color = TextPureWhite
            )

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, rainbowBrush, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = AnimeSurface.copy(alpha = 0.90f)
            ) {
                SettingsRowItem(
                    icon = Icons.Default.Delete,
                    iconBg = AnimeRedEnd,
                    title = "Clear Conversation History",
                    subtitle = "Delete all stored chat & voice history",
                    badge = "Clear",
                    badgeColor = AnimeRedEnd,
                    onClick = onClearHistory
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // SECTION 4: ℹ️ ABOUT
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, rainbowBrush, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                color = AnimeSurface.copy(alpha = 0.90f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "AJIJUL AI v3.0",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = TextPureWhite
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Lead Developer & Architect: Md. Ajijul Haque",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = AnimePinkPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Advanced multimodal AI assistant with 3D anime UI, speech engine, device control, and multiple AI personas.",
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
                        fontSize = 14.sp
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
