package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
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
import com.example.ui.theme.AnimeDeepSpace
import com.example.ui.theme.AnimePinkGlow
import com.example.ui.theme.AnimePinkLight
import com.example.ui.theme.AnimePinkPrimary
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.RainbowColorSpectrum
import com.example.ui.theme.TextDimmed
import com.example.ui.theme.TextMutedLilac
import com.example.ui.theme.TextPureWhite

@Composable
fun HomeScreen(
    apiKey: String,
    activePersonality: PersonalityMode,
    isFlashlightOn: Boolean,
    isAsleep: Boolean = false,
    isListening: Boolean = false,
    isSpeaking: Boolean = false,
    lastRecognizedText: String = "",
    activePalette: AppThemePalette = AppThemePalette.NEON_PINK,
    onSelectPersonality: (PersonalityMode) -> Unit,
    onSelectPalette: (AppThemePalette) -> Unit = {},
    onToggleFlashlight: () -> Unit,
    onAdjustVolume: (Boolean) -> Unit,
    onOpenApiKeyDialog: () -> Unit,
    onOpenAiModeDialog: () -> Unit,
    onOpenMemoryVault: () -> Unit,
    onNavigateTab: (NavigationTab) -> Unit,
    onStartVoiceCall: () -> Unit,
    onTriggerVoiceAction: (String) -> Unit,
    onTestFemaleVoice: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSideMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val needsApiKey = apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY"

    // 3D Breathing & Perspective Tilt Transitions
    val infiniteTransition = rememberInfiniteTransition(label = "3d_home_animations")

    val tiltX by infiniteTransition.animateFloat(
        initialValue = -2.5f,
        targetValue = 2.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt_x"
    )

    val tiltY by infiniteTransition.animateFloat(
        initialValue = 3.0f,
        targetValue = -3.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "tilt_y"
    )

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val density = LocalDensity.current.density
    val rainbowBrush = Brush.sweepGradient(colors = RainbowColorSpectrum)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top 3D Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 3-Dot Menu Button with 3D Glowing Rainbow Lighting
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(rainbowBrush)
                    .padding(1.8.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(AnimeSurface)
                    .clickable { showSideMenu = true }
                    .testTag("three_dot_menu_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Menu",
                    tint = TextPureWhite,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Center Brand: "AJIJUL AI" & Developer
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "AJIJUL AI",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp,
                        fontSize = 20.sp
                    ),
                    color = AnimePinkPrimary
                )
                Text(
                    text = "BY MD. AJIJUL HAQUE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = FontFamily.SansSerif,
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp
                    ),
                    color = AnimePinkLight
                )
            }

            // Settings Button with 3D Glowing Rainbow Lighting
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(rainbowBrush)
                    .padding(1.8.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(AnimeSurface)
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

        Spacer(modifier = Modifier.height(8.dp))

        // 🌟 First Launch API Key Setup Card (if API key is missing)
        if (needsApiKey) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(rainbowBrush)
                    .padding(2.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(AnimeSurface)
                    .clickable(onClick = onOpenApiKeyDialog)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(AnimePinkPrimary.copy(alpha = 0.3f))
                                .border(1.dp, AnimePinkPrimary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Key,
                                contentDescription = null,
                                tint = AnimePinkPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "🔑 Set Your Gemini API Key",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = TextPureWhite
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Tap here to enter free API key & unlock Ajijul AI",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                color = AnimePinkLight
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = AnimePinkPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
        }

        // 🎭 AI Mode Selector Bar Button ("mode botton a tap korle show korbe ar silect kora jabe")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(rainbowBrush)
                .padding(1.8.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AnimeSurfaceVariant)
                .clickable(onClick = onOpenAiModeDialog)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = AnimePinkPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Active Mode: ${activePersonality.title}",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = TextPureWhite
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AnimePinkPrimary.copy(alpha = 0.25f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Change Mode ▾",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        ),
                        color = AnimePinkLight
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 🌟 3D Floating Hero Anime Companion Section with Large 3D Circular Orb ("borok ak sarkel jetai ai kotha sunle akrokom abar kotha bolar somoi ak rokom")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
                .graphicsLayer {
                    rotationX = tiltX
                    rotationY = tiltY
                    cameraDistance = 18f * density
                    shadowElevation = 20.dp.toPx()
                    shape = RoundedCornerShape(26.dp)
                    clip = true
                }
                .shadow(16.dp, RoundedCornerShape(26.dp), spotColor = AnimePinkPrimary)
                .background(
                    Brush.verticalGradient(
                        listOf(AnimeSurfaceVariant.copy(alpha = 0.85f), AnimeDeepSpace)
                    )
                )
                .border(2.dp, rainbowBrush, RoundedCornerShape(26.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.flower_anime_hero),
                contentDescription = "Ajijul AI Companion Portrait",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(26.dp)),
                contentScale = ContentScale.Crop
            )

            // 3D Glassmorphism Gradient Depth Overlay
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

            // Center Large 3D Circular Orb with Particle Aura ("3d sarcel ar moto akare fota fora kore boro ak sarkel")
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(130.dp)
                    .scale(if (isListening || isSpeaking) pulseScale else 1f)
                    .clip(CircleShape)
                    .background(rainbowBrush)
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(AnimeDeepSpace)
                        .border(2.dp, AnimePinkPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        if (isListening) Color(0xFFFF2E93) else if (isSpeaking) Color(0xFFFF85C2) else AnimePinkPrimary,
                                        Color(0xFF2A1926)
                                    )
                                )
                            )
                            .border(1.5.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Mic Orb",
                            tint = Color.White,
                            modifier = Modifier.size(42.dp)
                        )
                    }
                }
            }

            // Bottom 3D Live Transcript Area ("voice a ja bola hobe sona hobe sob niche kekha asbe")
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(AnimeSurface.copy(alpha = 0.90f))
                    .border(1.2.dp, rainbowBrush, RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isListening) AnimePinkPrimary else if (isSpeaking) Color(0xFF77DD77) else AnimePinkLight)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isListening) "🎙️ Listening to your voice..." else if (isSpeaking) "🌸 Ajijul AI is speaking..." else "✨ Ready & Listening (Tap Orb to Talk)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = TextPureWhite
                            )
                        )
                    }
                    if (lastRecognizedText.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "\"$lastRecognizedText\"",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                fontSize = 12.sp
                            ),
                            color = AnimePinkLight,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 🚀 Action Cards with 3D Glowing Rainbow Lighting ("botton gulor chati pashe lite jolbe")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Home3DRainbowCard(
                icon = Icons.Default.RecordVoiceOver,
                title = "Voice Call",
                subtitle = "লাইভ ৩ডি কল",
                iconTint = AnimePinkGlow,
                onClick = onStartVoiceCall,
                modifier = Modifier.weight(1f)
            )

            Home3DRainbowCard(
                icon = Icons.Default.Star,
                title = "AI Modes",
                subtitle = "GF, BF, Doctor",
                iconTint = Color(0xFFFF4081),
                onClick = onOpenAiModeDialog,
                modifier = Modifier.weight(1f)
            )

            Home3DRainbowCard(
                icon = Icons.Default.PlayArrow,
                title = "YouTube Search",
                subtitle = "ইউটিউব গান",
                iconTint = Color(0xFFFF0055),
                onClick = {
                    onTriggerVoiceAction("ইউটিউব ওপেন করে গান সার্চ করো")
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Home3DRainbowCard(
                icon = Icons.AutoMirrored.Filled.VolumeUp,
                title = "Volume",
                subtitle = "ভলিউম নিয়ন্ত্রণ",
                iconTint = Color(0xFFFF9900),
                onClick = { onAdjustVolume(true) },
                modifier = Modifier.weight(1f)
            )

            Home3DRainbowCard(
                icon = Icons.Default.FlashlightOn,
                title = "Flashlight",
                subtitle = if (isFlashlightOn) "টর্চ অন" else "টর্চ অফ",
                iconTint = if (isFlashlightOn) Color(0xFFFFDD00) else AnimePinkLight,
                onClick = onToggleFlashlight,
                modifier = Modifier.weight(1f)
            )

            Home3DRainbowCard(
                icon = Icons.Default.Send,
                title = "WhatsApp / SMS",
                subtitle = "অটো মেসেজ",
                iconTint = Color(0xFF77DD77),
                onClick = {
                    onTriggerVoiceAction("হোয়াটসঅ্যাপ ওপেন করে মেসেজ পাঠাও")
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(84.dp))
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

/**
 * 3D Rainbow Button Card component with 3D elevation and RGB Rainbow Glowing Lighting
 */
@Composable
private fun Home3DRainbowCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    iconTint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rainbowBrush = Brush.sweepGradient(colors = RainbowColorSpectrum)

    Box(
        modifier = modifier
            .height(110.dp)
            .shadow(8.dp, RoundedCornerShape(18.dp), spotColor = AnimePinkPrimary)
            .clip(RoundedCornerShape(18.dp))
            .background(rainbowBrush)
            .padding(1.8.dp) // Glowing rainbow lighting border
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        AnimeSurfaceVariant.copy(alpha = 0.95f),
                        AnimeDeepSpace
                    )
                )
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AnimeSurface)
                    .border(1.dp, iconTint.copy(alpha = 0.6f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                ),
                color = TextPureWhite,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    lineHeight = 11.sp
                ),
                color = TextDimmed,
                maxLines = 1
            )
        }
    }
}
