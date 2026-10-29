package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.model.NavigationTab
import com.example.model.PersonalityMode
import com.example.ui.theme.AnimeAmber
import com.example.ui.theme.AnimeCardBorder
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeDeepSpace
import com.example.ui.theme.AnimeMagenta
import com.example.ui.theme.AnimePinkGlow
import com.example.ui.theme.AnimePurpleLight
import com.example.ui.theme.AnimePurplePrimary
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextDimmed
import com.example.ui.theme.TextMutedLilac
import com.example.ui.theme.TextPureWhite

@Composable
fun SideMenuSheet(
    activePersonality: PersonalityMode,
    isFlashlightOn: Boolean,
    onSelectPersonality: (PersonalityMode) -> Unit,
    onNavigateTab: (NavigationTab) -> Unit,
    onToggleFlashlight: () -> Unit,
    onAdjustVolume: (Boolean) -> Unit,
    onOpenApiKeyDialog: () -> Unit,
    onOpenMemoryVault: () -> Unit,
    onTestFemaleVoice: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.65f))
                .clickable(onClick = onDismiss),
            contentAlignment = Alignment.CenterStart
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .fillMaxHeight()
                    .clickable(enabled = false) {}, // prevent clicks from dismissing
                color = AnimeSurface,
                shape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, AnimeCardBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp)
                ) {
                    // Top Header Row with Close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, AnimePurplePrimary, CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.flower_anime_hero),
                                    contentDescription = "Flower AI",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = "FLOWER AI",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 17.sp,
                                        letterSpacing = 1.sp
                                    ),
                                    color = TextPureWhite
                                )
                                Text(
                                    text = "Creator: Md Ajijul Haque",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                    color = AnimePurpleLight
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close Menu",
                                tint = TextMutedLilac,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // SECTION 1: PERSONALITY MODES
                    Text(
                        text = "PERSONALITY MODE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        ),
                        color = AnimePurpleLight
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PersonalityMode.entries.forEach { mode ->
                        val isSelected = mode == activePersonality
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AnimePurplePrimary.copy(alpha = 0.2f) else Color.Transparent)
                                .border(
                                    width = if (isSelected) 1.dp else 0.5.dp,
                                    color = if (isSelected) AnimePurplePrimary else Color.Transparent,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    onSelectPersonality(mode)
                                    onDismiss()
                                }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) AnimeMagenta else TextDimmed)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = mode.title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    ),
                                    color = if (isSelected) TextPureWhite else TextMutedLilac
                                )
                                Text(
                                    text = mode.subtitle,
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = TextDimmed
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // SECTION 2: HARDWARE DEVICE CONTROLS
                    Text(
                        text = "PHONE HARDWARE CONTROLS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        ),
                        color = AnimeCyan
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Flashlight Toggle
                    MenuActionRow(
                        icon = if (isFlashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                        title = if (isFlashlightOn) "Flashlight (Turn OFF)" else "Flashlight (Turn ON)",
                        iconTint = if (isFlashlightOn) AnimeAmber else TextMutedLilac,
                        onClick = onToggleFlashlight
                    )

                    // Volume Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clickable { onAdjustVolume(true) },
                            shape = RoundedCornerShape(10.dp),
                            color = AnimeSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AnimeCardBorder)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Volume +", style = MaterialTheme.typography.labelSmall.copy(color = TextPureWhite))
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clickable { onAdjustVolume(false) },
                            shape = RoundedCornerShape(10.dp),
                            color = AnimeSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, AnimeCardBorder)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.AutoMirrored.Filled.VolumeDown, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Volume -", style = MaterialTheme.typography.labelSmall.copy(color = TextPureWhite))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Spacer(modifier = Modifier.height(18.dp))

                    // SECTION 3: SWEET FEMALE VOICE ENGINE
                    Text(
                        text = "SWEET FEMALE VOICE ENGINE (মেয়েদের মিষ্টি কণ্ঠ)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        ),
                        color = AnimePinkGlow
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable(onClick = onTestFemaleVoice),
                        color = AnimePurplePrimary.copy(alpha = 0.20f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AnimePurplePrimary)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "🌸 Female Voice Active",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp
                                    ),
                                    color = TextPureWhite
                                )
                                Text(
                                    text = "Sweet Pitch: 1.34x • Warm Cadence: 0.92x",
                                    style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                    color = AnimePurpleLight
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AnimeMagenta.copy(alpha = 0.35f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = null,
                                    tint = AnimeCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Test",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPureWhite
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // SECTION 4: SYSTEM & VAULT (No chat or history leaks)
                    Text(
                        text = "SYSTEM & VAULT",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 11.sp
                        ),
                        color = AnimeCyan
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MenuActionRow(
                        icon = Icons.Default.Key,
                        title = "Gemini API Key Connection",
                        iconTint = AnimeAmber,
                        onClick = {
                            onDismiss()
                            onOpenApiKeyDialog()
                        }
                    )

                    MenuActionRow(
                        icon = Icons.Default.Psychology,
                        title = "Long-Term Memory Vault",
                        iconTint = AnimeMagenta,
                        onClick = {
                            onDismiss()
                            onOpenMemoryVault()
                        }
                    )

                    MenuActionRow(
                        icon = Icons.Default.Settings,
                        title = "App Settings",
                        iconTint = TextMutedLilac,
                        onClick = {
                            onNavigateTab(NavigationTab.SETTINGS)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuActionRow(
    icon: ImageVector,
    title: String,
    iconTint: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.5.sp),
            color = TextPureWhite
        )
    }
}
