package com.example.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeMagenta
import com.example.ui.theme.AnimePurplePrimary
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.TextMutedLilac
import com.example.ui.theme.TextPureWhite

@Composable
fun VoiceInputDialog(
    onDismiss: () -> Unit,
    onSubmitVoice: (String) -> Unit
) {
    var spokenText by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp)),
            color = AnimeSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, AnimePurplePrimary.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Mic Icon Header
                Surface(
                    modifier = Modifier.size(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = AnimeMagenta.copy(alpha = 0.2f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Input",
                            tint = AnimeCyan,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Flower AI Voice Input",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = TextPureWhite
                    )
                )

                Text(
                    text = "Speak or enter your voice query below",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = TextMutedLilac
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = spokenText,
                    onValueChange = { spokenText = it },
                    placeholder = { Text("e.g. কেমন আছো Flower?", color = TextMutedLilac.copy(alpha = 0.6f)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeCyan,
                        unfocusedBorderColor = AnimePurplePrimary,
                        focusedTextColor = TextPureWhite,
                        unfocusedTextColor = TextPureWhite,
                        cursorColor = AnimeCyan
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = TextMutedLilac)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (spokenText.isNotBlank()) {
                                onSubmitVoice(spokenText)
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan)
                    ) {
                        Text("Send Voice", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
