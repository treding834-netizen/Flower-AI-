package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.ChatMessage
import com.example.model.MessageRole
import com.example.model.PersonalityMode
import com.example.ui.components.ChatMessageItem
import com.example.ui.components.StarryBackground
import com.example.ui.theme.AnimeCardBorder
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeMagenta
import com.example.ui.theme.AnimePinkGlow
import com.example.ui.theme.AnimePurpleLight
import com.example.ui.theme.AnimePurplePrimary
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.BubbleAiBackground
import com.example.ui.theme.BubbleBorder
import com.example.ui.theme.BubbleUserBackground
import com.example.ui.theme.TextDimmed
import com.example.ui.theme.TextMutedLilac
import com.example.ui.theme.TextPureWhite

@Composable
fun ChatScreen(
    messages: List<ChatMessage>,
    activePersonality: PersonalityMode,
    isGenerating: Boolean,
    onSendMessage: (String) -> Unit,
    onSpeakMessage: (String) -> Unit,
    onRetryLastMessage: () -> Unit,
    onStartVoiceInput: () -> Unit,
    onBackToHome: () -> Unit,
    onNewConversation: () -> Unit,
    onOpenWebsitePreview: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val listState = rememberLazyListState()
    var inputQuery by remember { mutableStateOf("") }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        StarryBackground()

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = AnimeSurface.copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AnimeCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onBackToHome,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPureWhite,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Glowing Anime Avatar Circle
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, AnimePurplePrimary, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.flower_anime_hero),
                                contentDescription = "Flower Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Text(
                                text = "Flower",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 17.sp
                                ),
                                color = TextPureWhite
                            )
                            Text(
                                text = "Here to help",
                                style = MaterialTheme.typography.labelSmall,
                                color = AnimePurpleLight
                            )
                        }
                    }

                    // Plus Button (New Chat)
                    IconButton(
                        onClick = onNewConversation,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New chat",
                            tint = TextPureWhite,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            // Message Stream
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("chat_screen_message_stream"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(messages, key = { it.id }) { message ->
                    AnimeChatBubble(
                        message = message,
                        onSpeak = { onSpeakMessage(message.text) },
                        onRetry = onRetryLastMessage,
                        onOpenWebsitePreview = onOpenWebsitePreview
                    )
                }

                if (isGenerating) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = AnimePurpleLight,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Flower is thinking...",
                                style = MaterialTheme.typography.labelSmall.copy(color = AnimePurpleLight)
                            )
                        }
                    }
                }

                // Space for floating bottom bar
                item {
                    Spacer(modifier = Modifier.height(72.dp))
                }
            }

            // Bottom Voice Interaction Bar (NO TEXT INPUT, PURELY VOICE-TO-VOICE)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .padding(bottom = 76.dp), // stays above bottom nav pill
                shape = RoundedCornerShape(28.dp),
                color = AnimeSurface.copy(alpha = 0.95f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AnimePurplePrimary.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onStartVoiceInput)
                        .padding(horizontal = 18.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AnimeMagenta.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = "Voice input",
                                tint = AnimeCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "🌸 Pure Voice-to-Voice Mode",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = TextPureWhite
                                )
                            )
                            Text(
                                text = "কথা বলতে মাইক্রোফোনে ট্যাপ করুন (মেয়ে কণ্ঠে উত্তর)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.5.sp,
                                    color = AnimePurpleLight
                                )
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(AnimePurplePrimary, AnimeMagenta)
                                )
                            )
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tap to Talk",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.5.sp,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimeChatBubble(
    message: ChatMessage,
    onSpeak: () -> Unit,
    onRetry: () -> Unit,
    onOpenWebsitePreview: (String) -> Unit
) {
    val context = LocalContext.current
    val isUser = message.role == MessageRole.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) BubbleUserBackground else BubbleAiBackground,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (isUser) Color(0xFF6B5330) else BubbleBorder),
            modifier = Modifier.fillMaxWidth(if (isUser) 0.82f else 0.88f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 15.sp),
                    color = TextPureWhite
                )

                // Action Bar below AI response: Copy, Retry, Speaker
                if (!isUser) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Copy
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy message",
                            tint = TextMutedLilac,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Flower AI", message.text))
                                    Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
                                }
                        )

                        // Retry
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Regenerate",
                            tint = TextMutedLilac,
                            modifier = Modifier
                                .size(17.dp)
                                .clickable(onClick = onRetry)
                        )

                        // Speaker
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Speak Aloud",
                            tint = TextMutedLilac,
                            modifier = Modifier
                                .size(18.dp)
                                .clickable(onClick = onSpeak)
                        )
                    }
                }
            }
        }
    }
}
