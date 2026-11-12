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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.components.Rainbow3DButton
import com.example.ui.theme.AnimeCardBorder
import com.example.ui.theme.AnimeDeepSpace
import com.example.ui.theme.AnimePinkGlow
import com.example.ui.theme.AnimePinkLight
import com.example.ui.theme.AnimePinkPrimary
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.RainbowColorSpectrum
import com.example.ui.theme.TextDimmed
import com.example.ui.theme.TextMutedLilac
import com.example.ui.theme.TextPureWhite

data class VoiceOption(
    val id: String,
    val name: String,
    val description: String,
    val isFemale: Boolean,
    val pitch: Float,
    val rate: Float
)

// Exclusively Female Voices - Male voices strictly removed
val availableVoices = listOf(
    VoiceOption("f1", "🌸 Sweet Romantic (মিষ্টি রোমান্টিক)", "Soft, affectionate and warm girl companion tone", true, 1.36f, 0.92f),
    VoiceOption("f2", "✨ Energetic Girl (উজ্জ্বল তরুণী)", "Bright, cheerful, lively young female cadence", true, 1.32f, 1.00f),
    VoiceOption("f3", "🌙 Gentle Whisper (স্নিগ্ধ মিষ্টি কণ্ঠ)", "Calm, soothing, loving feminine voice", true, 1.38f, 0.88f),
    VoiceOption("f4", "💎 Executive Lady (স্মার্ট নারী)", "Confident, clear, sweet and intelligent female tone", true, 1.28f, 0.96f),
    VoiceOption("f5", "🎶 Cute Anime Sakura (মিষ্টি সাকুরা)", "Ultra-cute, lovely high sweet frequency anime girl", true, 1.44f, 0.95f)
)

@Composable
fun VoiceSelectionDialog(
    selectedVoiceId: String,
    onSelectVoice: (VoiceOption) -> Unit,
    onTestVoice: (VoiceOption) -> Unit,
    onDismiss: () -> Unit
) {
    var activeHoverVoiceId by remember { mutableStateOf(selectedVoiceId) }
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
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
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
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = AnimePinkGlow,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Voice Select (মেয়েদের মিষ্টি কণ্ঠ)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPureWhite,
                        fontSize = 18.sp
                    )
                )

                Text(
                    text = "শুধুমাত্র মেয়েদের মিষ্টি কণ্ঠ active থাকবে (ছেলেদের ভয়েস রিমুভ করা হয়েছে)",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMutedLilac,
                        fontSize = 12.sp
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Female Voice List Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🌸 SWEET FEMALE VOICES (মিষ্টি কণ্ঠসমূহ):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = AnimePinkPrimary,
                            letterSpacing = 1.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Voice List
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(290.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableVoices.forEach { voice ->
                        val isSelected = activeHoverVoiceId == voice.id
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .border(
                                    1.2.dp,
                                    if (isSelected) rainbowBrush else Brush.linearGradient(listOf(AnimeCardBorder, AnimeCardBorder)),
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { activeHoverVoiceId = voice.id },
                            color = if (isSelected) AnimePinkPrimary.copy(alpha = 0.22f) else AnimeSurface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = voice.name,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (isSelected) AnimePinkGlow else TextPureWhite
                                            )
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Selected",
                                                tint = AnimePinkPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = voice.description,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 11.sp,
                                            color = TextDimmed
                                        )
                                    )
                                }

                                // Listen Preview Button
                                Surface(
                                    modifier = Modifier
                                        .height(34.dp)
                                        .clip(RoundedCornerShape(17.dp))
                                        .clickable { onTestVoice(voice) },
                                    shape = RoundedCornerShape(17.dp),
                                    color = AnimePinkPrimary.copy(alpha = 0.25f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, AnimePinkPrimary)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = "Test voice",
                                            tint = AnimePinkLight,
                                            modifier = Modifier.size(15.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "শুনুন",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = Color.White
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Confirm and Apply Voice Button with RGB Rainbow Lighting
                val currentChosenVoice = availableVoices.firstOrNull { it.id == activeHoverVoiceId } ?: availableVoices.first()

                Rainbow3DButton(
                    onClick = {
                        onSelectVoice(currentChosenVoice)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text(
                        text = "ভয়েস সিলেক্ট করুন ও Confirm করুন",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
