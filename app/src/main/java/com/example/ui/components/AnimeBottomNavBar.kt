package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
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
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeMagenta
import com.example.ui.theme.AnimePurplePrimary
import com.example.ui.theme.BottomNavPillBackground
import com.example.ui.theme.BottomNavPillBorder
import com.example.ui.theme.TextDimmed
import com.example.ui.theme.TextMutedLilac

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

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Floating pill container
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp),
            shape = RoundedCornerShape(34.dp),
            color = BottomNavPillBackground.copy(alpha = 0.96f),
            border = androidx.compose.foundation.BorderStroke(1.dp, BottomNavPillBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
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
                Spacer(modifier = Modifier.width(56.dp))

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

        // Center Floating Raised Voice Orb
        Box(
            modifier = Modifier
                .offset(y = (-14).dp)
                .size(66.dp)
                .scale(if (isVoiceActive) pulseScale else 1f)
                .clip(CircleShape)
                .background(
                    Brush.sweepGradient(
                        listOf(AnimeCyan, AnimeMagenta, AnimePurplePrimary, AnimeCyan)
                    )
                )
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
                    .border(2.dp, AnimeMagenta, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Inner concentric glowing ring
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(AnimeMagenta.copy(alpha = 0.85f), AnimePurplePrimary.copy(alpha = 0.5f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Call",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
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
    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) AnimeAmber else TextDimmed,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp
            ),
            color = if (isSelected) AnimeAmber else TextDimmed
        )
        if (isSelected) {
            Spacer(modifier = Modifier.height(3.dp))
            Box(
                modifier = Modifier
                    .width(16.dp)
                    .height(2.dp)
                    .clip(RoundedCornerShape(1.dp))
                    .background(AnimeAmber)
            )
        } else {
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}
