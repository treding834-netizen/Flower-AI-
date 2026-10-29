package com.example.model

import java.util.UUID

enum class MessageRole {
    USER,
    ASSISTANT,
    SYSTEM
}

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: MessageRole,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val detectedLanguage: String? = null,
    val imageUri: String? = null,
    val imageBase64: String? = null,
    val deviceAction: DeviceActionData? = null,
    val websiteCodeHtml: String? = null,
    val isRefusal: Boolean = false,
    val isWakeWordGreeting: Boolean = false,
    val generatedImageUrl: String? = null
)

data class MemoryItem(
    val id: String = UUID.randomUUID().toString(),
    val key: String,
    val value: String,
    val category: String = "Preference",
    val timestamp: Long = System.currentTimeMillis()
)
