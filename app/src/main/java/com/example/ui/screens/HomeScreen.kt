package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.NavigationTab
import com.example.model.PersonalityMode
import com.example.ui.components.SideMenuSheet
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
fun HomeScreen(
    activePersonality: PersonalityMode,
    isFlashlightOn: Boolean,
    onSelectPersonality: (PersonalityMode) -> Unit,
    onToggleFlashlight: () -> Unit,
    onAdjustVolume: (Boolean) -> Unit,
    onOpenApiKeyDialog: () -> Unit,
    onOpenMemoryVault: () -> Unit,
    onNavigateTab: (NavigationTab) -> Unit,
    onStartVoiceCall: () -> Unit,
    onTriggerVoiceAction: (String) -> Unit,
    onTestFemaleVoice: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSideMenu by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "tap_to_talk_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3-Dot Menu Button (Left) -> Triggers pure Settings & Personality Drawer (no chat/history leak)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AnimeSurface.copy(alpha = 0.7f))
                    .border(1.dp, AnimeCardBorder, RoundedCornerShape(12.dp))
                    .clickable { showSideMenu = true }
                    .testTag("three_dot_menu_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Settings and Personality Panel",
                    tint = TextPureWhite,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Center Brand: "FLOWER AI"
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "FLOWER AI",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 4.sp,
                        fontSize = 22.sp
                    ),
                    color = TextPureWhite
                )
                Text(
                    text = "VOICE COMPANION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 2.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    ),
                    color = AnimeCyan
                )
            }

            // Settings Button (Right)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AnimeSurface.copy(alpha = 0.7f))
                    .border(1.dp, AnimeCardBorder, RoundedCornerShape(12.dp))
                    .clickable(onClick = onOpenSettings),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings",
                    tint = TextPureWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Hero Anime Companion Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(290.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(AnimeSurfaceVariant.copy(alpha = 0.5f))
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        listOf(AnimePurplePrimary.copy(alpha = 0.7f), AnimeCardBorder)
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
        ) {
            Image(
                painter = painterResource(id = R.drawable.flower_anime_hero),
                contentDescription = "Flower AI Companion Portrait",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(24.dp)),
                contentScale = ContentScale.Crop
            )

            // Dark gradient overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                AnimeDeepSpace.copy(alpha = 0.3f),
                                AnimeDeepSpace.copy(alpha = 0.92f)
                            )
                        )
                    )
            )

            // Left Callout: "Hi, I'm FLOWER ♡"
            Text(
                text = "Hi,\nI'm\nFLOWER\n♡",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Cursive,
                    fontSize = 20.sp,
                    lineHeight = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AnimePinkGlow
                ),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 16.dp, top = 20.dp)
            )

            // Right Callout: "Always Here For You ♡"
            Text(
                text = "Always\nHere\nFor You\n♡",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Cursive,
                    fontSize = 17.sp,
                    lineHeight = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = AnimePurpleLight
                ),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(end = 16.dp, top = 20.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sweet Female Voice Engine Status Pill with Test Button
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onTestFemaleVoice),
            color = AnimeSurface.copy(alpha = 0.85f),
            border = androidx.compose.foundation.BorderStroke(1.dp, AnimePurplePrimary.copy(alpha = 0.5f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(AnimeMagenta.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AnimePinkGlow,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "🌸 Sweet Female Voice Engine Active",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.5.sp,
                                color = TextPureWhite
                            )
                        )
                        Text(
                            text = "মেয়েদের মিষ্টি রোমান্টিক কণ্ঠে সরাসরি কথা বলবে",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 10.sp,
                                color = AnimePurpleLight
                            )
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AnimeMagenta.copy(alpha = 0.3f))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Test voice",
                        tint = AnimeCyan,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "শুনুন",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPureWhite
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Central Voice Call Trigger Orb: "Tap to Talk" (NO TEXT, ONLY VOICE!)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.clickable(onClick = onStartVoiceCall)
        ) {
            Box(
                modifier = Modifier
                    .size(108.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(
                        Brush.sweepGradient(
                            listOf(AnimeCyan, AnimeMagenta, AnimePurplePrimary, AnimeCyan)
                        )
                    )
                    .padding(3.5.dp)
                    .testTag("home_tap_to_talk_orb"),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(AnimeDeepSpace)
                        .border(2.dp, AnimePurplePrimary.copy(alpha = 0.8f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(AnimeSurfaceVariant, AnimeDeepSpace)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Microphone",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Tap to Talk",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = TextPureWhite
                )
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Dotted audio wave indicator: ···|···|···
            Text(
                text = "······|······|······",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp,
                    color = AnimeCyan
                )
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Voice Trigger Chips (Directly launch voice in sweet female voice)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickVoicePromptChip(label = "🌸 একটি কবিতা বলো") {
                onTriggerVoiceAction("আমাকে একটি সুন্দর রোমান্টিক কবিতা শোনাও")
            }
            QuickVoicePromptChip(label = "💬 কেমন আছো সোনা?") {
                onTriggerVoiceAction("তুমি কেমন আছো সোনা?")
            }
            QuickVoicePromptChip(label = "💖 তোমাকে ভালোবাসি") {
                onTriggerVoiceAction("আমি তোমাকে অনেক ভালোবাসি")
            }
            QuickVoicePromptChip(label = "🔦 ফ্ল্যাশলাইট জ্বালাও") {
                onTriggerVoiceAction("ফ্ল্যাশলাইট জ্বালাও")
            }
            QuickVoicePromptChip(label = "🔊 ভলিউম বাড়াও") {
                onTriggerVoiceAction("ভলিউম বাড়াও")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3 Voice-Centric Action Cards: Voice Calling, Romantic Poem, Phone Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            VoiceFeatureCard(
                icon = Icons.Default.RecordVoiceOver,
                title = "Voice Call",
                subtitle = "লাইভ ভয়েস কথা বলুন",
                iconTint = AnimeCyan,
                onClick = onStartVoiceCall,
                modifier = Modifier.weight(1f)
            )

            VoiceFeatureCard(
                icon = Icons.Default.AutoAwesome,
                title = "Romantic Voice",
                subtitle = "মিষ্টি রোমান্টিক কবিতা",
                iconTint = AnimePinkGlow,
                onClick = {
                    onTriggerVoiceAction("আমাকে একটি সুন্দর রোমান্টিক কবিতা শোনাও")
                },
                modifier = Modifier.weight(1f)
            )

            VoiceFeatureCard(
                icon = Icons.Default.FlashlightOn,
                title = "Device Voice",
                subtitle = "টর্চ ও ভলিউম নিয়ন্ত্রণ",
                iconTint = AnimePurpleLight,
                onClick = {
                    onTriggerVoiceAction("ফ্ল্যাশলাইট জ্বালাও")
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(80.dp)) // Padding for bottom floating bar
    }

    if (showSideMenu) {
        SideMenuSheet(
            activePersonality = activePersonality,
            isFlashlightOn = isFlashlightOn,
            onSelectPersonality = onSelectPersonality,
            onNavigateTab = onNavigateTab,
            onToggleFlashlight = onToggleFlashlight,
            onAdjustVolume = onAdjustVolume,
            onOpenApiKeyDialog = onOpenApiKeyDialog,
            onOpenMemoryVault = onOpenMemoryVault,
            onTestFemaleVoice = onTestFemaleVoice,
            onDismiss = { showSideMenu = false }
        )
    }
}

@Composable
private fun QuickVoicePromptChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(AnimeSurface.copy(alpha = 0.92f))
            .border(1.dp, AnimePurplePrimary.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                color = TextPureWhite
            )
        )
    }
}

@Composable
private fun VoiceFeatureCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .height(108.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = AnimeSurface.copy(alpha = 0.90f),
        border = androidx.compose.foundation.BorderStroke(1.dp, AnimeCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AnimeSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = FontFamily.Cursive,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = TextPureWhite
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    lineHeight = 11.sp
                ),
                color = TextDimmed,
                maxLines = 2
            )
        }
    }
}
