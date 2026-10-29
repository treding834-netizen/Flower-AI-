package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.MessageRole
import com.example.ui.components.StarryBackground
import com.example.ui.theme.AnimeCardBorder
import com.example.ui.theme.AnimePurpleLight
import com.example.ui.theme.AnimePurplePrimary
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextDimmed
import com.example.ui.theme.TextMutedLilac
import com.example.ui.theme.TextPureWhite
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ConversationSummary(
    val id: String,
    val title: String,
    val preview: String,
    val timeLabel: String
)

@Composable
fun HistoryScreen(
    messages: List<ChatMessage>,
    onSelectConversation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }

    // Generate summaries ONLY from real user interactions
    val summaries = remember(messages) {
        val userMessages = messages.filter { it.role == MessageRole.USER }
        userMessages.mapIndexed { index, userMsg ->
            val userMsgIndex = messages.indexOf(userMsg)
            val assistantMsg = if (userMsgIndex >= 0 && userMsgIndex + 1 < messages.size) {
                messages[userMsgIndex + 1]
            } else null

            ConversationSummary(
                id = userMsg.id,
                title = "Voice - " + userMsg.text.take(40) + if (userMsg.text.length > 40) "..." else "",
                preview = assistantMsg?.text?.take(60) ?: "Voice response saved.",
                timeLabel = timeFormat.format(Date(userMsg.timestamp))
            )
        }.reversed()
    }

    val filteredSummaries = remember(searchQuery, summaries) {
        if (searchQuery.isBlank()) summaries
        else summaries.filter {
            it.title.contains(searchQuery, ignoreCase = true) || it.preview.contains(searchQuery, ignoreCase = true)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        StarryBackground()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Title: "Conversations"
            Text(
                text = "Conversations",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp
                ),
                color = TextPureWhite
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar: "Search conversations"
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                color = AnimeSurface.copy(alpha = 0.85f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AnimeCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = TextDimmed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                text = "Search conversations",
                                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                                color = TextDimmed
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("history_search_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = TextPureWhite,
                            unfocusedTextColor = TextPureWhite
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Check if there are real conversations
            if (filteredSummaries.isEmpty()) {
                // Clean Empty State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(68.dp)
                                .clip(CircleShape)
                                .background(AnimeSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = AnimePurpleLight,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (searchQuery.isNotBlank()) "No matching conversations" else "No conversations yet",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            ),
                            color = TextPureWhite
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (searchQuery.isNotBlank()) "Try a different search word" else "Speak to Flower AI or start a chat. Your conversations will automatically appear here.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                            ),
                            color = TextMutedLilac
                        )
                    }
                }
            } else {
                // Real Conversations List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredSummaries, key = { it.id }) { item ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectConversation(item.id) },
                            shape = RoundedCornerShape(20.dp),
                            color = AnimeSurface.copy(alpha = 0.90f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, AnimeCardBorder)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(18.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Text(
                                        text = item.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        ),
                                        color = TextPureWhite,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(
                                        text = item.timeLabel,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 12.sp),
                                        color = TextDimmed
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = item.preview,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                                    color = TextMutedLilac,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }
}
