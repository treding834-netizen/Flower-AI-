package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.Palette
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.PersonalityMode
import com.example.ui.components.Rainbow3DButton
import com.example.ui.theme.AnimeCardBorder
import com.example.ui.theme.AnimeDeepSpace
import com.example.ui.theme.AnimePinkGlow
import com.example.ui.theme.AnimePinkPrimary
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AppThemePalette
import com.example.ui.theme.RainbowColorSpectrum
import com.example.ui.theme.TextMutedLilac
import com.example.ui.theme.TextPureWhite

@Composable
fun ThemeSelectionDialog(
    currentPersonality: PersonalityMode,
    activePalette: AppThemePalette = AppThemePalette.NEON_PINK,
    onSelectPersonality: (PersonalityMode) -> Unit,
    onSelectPalette: (AppThemePalette) -> Unit = {},
    onDismiss: () -> Unit
) {
    val rainbowBrush = Brush.sweepGradient(colors = RainbowColorSpectrum)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .border(1.5.dp, rainbowBrush, RoundedCornerShape(24.dp)),
            color = AnimeDeepSpace
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    color = AnimePinkPrimary.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AnimePinkPrimary)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = AnimePinkGlow,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "App Color & Theme Style",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPureWhite,
                        fontSize = 18.sp
                    )
                )

                Text(
                    text = "আপনার পছন্দের কালার ও থিম নির্বাচন করুন",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMutedLilac
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Section 1: Color Palettes (এপ নানা কালোর অ্যাড করো)
                Text(
                    text = "🌈 COLOR PALETTES (রঙের থিমসমূহ)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = AnimePinkPrimary
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AppThemePalette.values().forEach { palette ->
                        val isSelected = activePalette == palette
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    1.2.dp,
                                    if (isSelected) rainbowBrush else Brush.linearGradient(listOf(AnimeCardBorder, AnimeCardBorder)),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { onSelectPalette(palette) },
                            color = if (isSelected) palette.primaryColor.copy(alpha = 0.25f) else AnimeSurface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(CircleShape)
                                            .background(palette.primaryColor)
                                            .border(1.5.dp, Color.White, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = palette.displayName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) Color.White else TextPureWhite
                                        )
                                    )
                                }
                                if (isSelected) {
                                    Text(
                                        text = "ACTIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            fontSize = 10.sp,
                                            color = palette.primaryColor
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section 2: AI Personalities
                Text(
                    text = "🌸 AI PERSONALITY (ব্যক্তিত্ব মোড)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = AnimePinkPrimary
                    ),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(8.dp))

                val modes = listOf(
                    PersonalityMode.ROMANTIC_PARTNER to "🌸 Romantic Partner (প্রেমময় সঙ্গী)",
                    PersonalityMode.JARVIS to "⚡ Jarvis Executive (স্মার্ট জার্ভিস)",
                    PersonalityMode.DEVELOPER to "💻 Full-Stack Coder (ডেভেলপার)",
                    PersonalityMode.WEBSITE_BUILDER to "🌐 Website Builder (ওয়েব ক্রিয়েটর)"
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    modes.forEach { (mode, title) ->
                        val isSelected = currentPersonality == mode
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .clickable {
                                    onSelectPersonality(mode)
                                },
                            color = if (isSelected) AnimePinkPrimary.copy(alpha = 0.25f) else AnimeSurface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) AnimePinkPrimary else AnimeCardBorder
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = title,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) AnimePinkGlow else TextPureWhite
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                Rainbow3DButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "হয়ে গেছে / Done",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}
