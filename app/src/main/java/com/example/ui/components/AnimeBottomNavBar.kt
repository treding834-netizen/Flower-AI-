package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.NavigationTab
import com.example.ui.theme.AnimeAmber
import com.example.ui.theme.AnimePinkGlow
import com.example.ui.theme.AnimePinkLight
import com.example.ui.theme.AnimePinkPrimary
import com.example.ui.theme.BottomNavPillBackground
import com.example.ui.theme.RainbowColorSpectrum
import com.example.ui.theme.TextDimmed

@Composable
fun AnimeBottomNavBar(
    selectedTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    onCenterOrbClick: () -> Unit,
    isVoiceActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orb_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val rainbowAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rainbow_rotate"
    )

    val rainbowBrush = Brush.sweepGradient(colors = RainbowColorSpectrum)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating pill container with Rainbow Border Lighting
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .clip(RoundedCornerShape(34.dp))
                .border(1.5.dp, rainbowBrush, RoundedCornerShape(34.dp)),
            shape = RoundedCornerShape(34.dp),
            color = BottomNavPillBackground.copy(alpha = 0.96f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Home Tab
                NavTabItem(
                    label = "Home",
                    icon = Icons.Default.Home,
                    isSelected = selectedTab == NavigationTab.HOME,
                    onClick = { onTabSelected(NavigationTab.HOME) },
                    modifier = Modifier.testTag("nav_tab_home")
                )

                // Chat Tab
                NavTabItem(
                    label = "Chat",
                    icon = Icons.AutoMirrored.Filled.Chat,
                    isSelected = selectedTab == NavigationTab.CHAT,
                    onClick = { onTabSelected(NavigationTab.CHAT) },
                    modifier = Modifier.testTag("nav_tab_chat")
                )

                // Space for Center Floating Orb
                Spacer(modifier = Modifier.width(58.dp))

                // History Tab
                NavTabItem(
                    label = "History",
                    icon = Icons.Default.Schedule,
                    isSelected = selectedTab == NavigationTab.HISTORY,
                    onClick = { onTabSelected(NavigationTab.HISTORY) },
                    modifier = Modifier.testTag("nav_tab_history")
                )

                // Settings Tab
                NavTabItem(
                    label = "Settings",
                    icon = Icons.Default.Settings,
                    isSelected = selectedTab == NavigationTab.SETTINGS,
                    onClick = { onTabSelected(NavigationTab.SETTINGS) },
                    modifier = Modifier.testTag("nav_tab_settings")
                )
            }
        }

        // Center Floating Raised Voice Orb with RGB Rainbow Lighting
        Box(
            modifier = Modifier
                .offset(y = (-16).dp)
                .size(68.dp)
                .scale(if (isVoiceActive) pulseScale else 1f)
                .clip(CircleShape)
                .background(rainbowBrush)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onCenterOrbClick
                )
                .padding(3.dp)
                .testTag("center_floating_voice_orb"),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .background(BottomNavPillBackground)
                    .border(1.5.dp, AnimePinkPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Inner concentric glowing 3D pink ring
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(AnimePinkPrimary.copy(alpha = 0.95f), Color(0xFF4A0A35))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Call",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rainbowBrush = Brush.sweepGradient(colors = RainbowColorSpectrum)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .then(
                if (isSelected) {
                    Modifier
                        .background(
                            Brush.verticalGradient(
                                listOf(AnimePinkPrimary.copy(alpha = 0.22f), Color.Transparent)
                            )
                        )
                        .border(1.2.dp, rainbowBrush, RoundedCornerShape(20.dp))
                } else Modifier
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 10.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) AnimePinkPrimary else TextDimmed,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                ),
                color = if (isSelected) AnimePinkLight else TextDimmed
            )
        }
    }
}
