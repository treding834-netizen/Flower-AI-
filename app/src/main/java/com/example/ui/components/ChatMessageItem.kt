package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.BrightnessMedium
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.ActionType
import com.example.model.ChatMessage
import com.example.model.DeviceActionData
import com.example.model.MessageRole
import com.example.model.PersonalityMode
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberCardBorder
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.CyberVoid
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonMagenta
import com.example.ui.theme.NeonRose
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    activePersonality: PersonalityMode,
    onSpeakMessage: (String) -> Unit,
    onOpenWebsitePreview: (String) -> Unit,
    onReExecuteAction: (DeviceActionData) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isUser = message.role == MessageRole.USER
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val timeString = remember(message.timestamp) { timeFormat.format(Date(message.timestamp)) }

    val accent = if (isUser) NeonCyan else activePersonality.accentColor

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // Assistant Avatar
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(CyberSurfaceVariant)
                    .border(1.dp, accent, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✿",
                    color = accent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(if (isUser) 0.85f else 0.90f),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Message Surface
            Surface(
                shape = RoundedCornerShape(
                    topStart = 14.dp,
                    topEnd = 14.dp,
                    bottomStart = if (isUser) 14.dp else 2.dp,
                    bottomEnd = if (isUser) 2.dp else 14.dp
                ),
                color = if (isUser) CyberSurfaceVariant.copy(alpha = 0.9f) else CyberSurface.copy(alpha = 0.95f),
                modifier = Modifier
                    .border(
                        width = 1.dp,
                        brush = Brush.horizontalGradient(
                            if (message.isRefusal) listOf(NeonRose, NeonRose.copy(alpha = 0.5f))
                            else listOf(accent.copy(alpha = 0.6f), CyberCardBorder)
                        ),
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isUser) 14.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 14.dp
                        )
                    )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Refusal Warning Banner
                    if (message.isRefusal) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(NeonRose.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Safety Policy",
                                tint = NeonRose,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "STRICT ETHICAL POLICY BARRIER",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeonRose
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // Wake-Word Banner
                    if (message.isWakeWordGreeting) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(NeonCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "⚡ WAKE-WORD INITIALIZATION",
                                style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    // User attached image
                    if (!message.imageUri.isNullOrBlank()) {
                        AsyncImage(
                            model = message.imageUri,
                            contentDescription = "Attached visual",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Generated Artwork Image
                    if (!message.generatedImageUrl.isNullOrBlank()) {
                        val base64 = message.generatedImageUrl
                        val bitmap = remember(base64) {
                            try {
                                val bytes = Base64.decode(base64, Base64.DEFAULT)
                                BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                            } catch (_: Exception) {
                                null
                            }
                        }
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = "Generated Artwork",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(240.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .border(1.dp, NeonMagenta, RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Formatted Body Text (Split code blocks if any)
                    RenderMessageBody(
                        fullText = message.text,
                        accentColor = accent,
                        onOpenWebsitePreview = onOpenWebsitePreview
                    )

                    // Hardware Action Execution Card
                    if (message.deviceAction != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        DeviceActionHUDCard(
                            action = message.deviceAction,
                            accentColor = accent,
                            onExecute = { onReExecuteAction(message.deviceAction) }
                        )
                    }

                    // Website preview button if website code present and not already displayed
                    if (!message.websiteCodeHtml.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        FilledTonalButton(
                            onClick = { onOpenWebsitePreview(message.websiteCodeHtml) },
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = NeonAmber.copy(alpha = 0.2f),
                                contentColor = NeonAmber
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Web,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Launch Live Website Preview",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    // Footer: Timestamp & Speak button
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeString,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )

                        if (!isUser) {
                            IconButton(
                                onClick = { onSpeakMessage(message.text) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "Speak Aloud",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RenderMessageBody(
    fullText: String,
    accentColor: Color,
    onOpenWebsitePreview: (String) -> Unit
) {
    val context = LocalContext.current
    // Regex to detect markdown code blocks: ```lang\ncode\n```
    val codeBlockRegex = Regex("```([a-zA-Z0-9_-]*)\\s*\\n?([\\s\\S]*?)```")
    val matches = codeBlockRegex.findAll(fullText).toList()

    if (matches.isEmpty()) {
        Text(
            text = fullText,
            style = MaterialTheme.typography.bodyLarge,
            color = TextPrimary
        )
    } else {
        var lastIndex = 0
        matches.forEach { match ->
            // Text before code block
            val textBefore = fullText.substring(lastIndex, match.range.first).trim()
            if (textBefore.isNotEmpty()) {
                Text(
                    text = textBefore,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(6.dp))
            }

            val lang = match.groupValues[1].ifBlank { "code" }
            val code = match.groupValues[2].trim()

            // Code Snippet Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(CyberVoid)
                    .border(1.dp, CyberCardBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = NeonGreen,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = lang.uppercase(),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NeonGreen,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Row {
                            if (lang.equals("html", ignoreCase = true)) {
                                Text(
                                    text = "PREVIEW",
                                    style = MaterialTheme.typography.labelSmall.copy(color = NeonAmber),
                                    modifier = Modifier
                                        .clickable { onOpenWebsitePreview(code) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                            }

                            Text(
                                text = "COPY",
                                style = MaterialTheme.typography.labelSmall.copy(color = NeonCyan),
                                modifier = Modifier
                                    .clickable {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("code", code))
                                        Toast.makeText(context, "Code copied", Toast.LENGTH_SHORT).show()
                                    }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = code,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = TextPrimary
                        ),
                        modifier = Modifier.padding(4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            lastIndex = match.range.last + 1
        }

        // Remaining text after last code block
        if (lastIndex < fullText.length) {
            val remainingText = fullText.substring(lastIndex).trim()
            if (remainingText.isNotEmpty()) {
                Text(
                    text = remainingText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextPrimary
                )
            }
        }
    }
}

@Composable
fun DeviceActionHUDCard(
    action: DeviceActionData,
    accentColor: Color,
    onExecute: () -> Unit,
    modifier: Modifier = Modifier
) {
    val icon = when (action.actionType) {
        ActionType.FLASHLIGHT_ON, ActionType.FLASHLIGHT_OFF -> Icons.Default.FlashlightOn
        ActionType.VOLUME_UP, ActionType.VOLUME_DOWN -> Icons.Default.VolumeUp
        ActionType.BRIGHTNESS_UP, ActionType.BRIGHTNESS_DOWN -> Icons.Default.BrightnessMedium
        ActionType.OPEN_APP, ActionType.YOUTUBE_SEARCH, ActionType.WEB_SEARCH -> Icons.Default.Apps
        ActionType.TYPE_TEXT, ActionType.WHATSAPP_SEND, ActionType.SMS_SEND, ActionType.CALL_PHONE -> Icons.Default.Code
        else -> Icons.Default.Apps
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CyberVoid)
            .border(1.dp, accentColor.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = "DEVICE ACTION EXECUTED",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeonCyan,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = action.statusMessage,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            IconButton(
                onClick = onExecute,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Executed",
                    tint = NeonGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
