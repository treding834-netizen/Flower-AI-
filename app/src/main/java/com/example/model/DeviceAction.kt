package com.example.model

enum class ActionType(val tag: String, val displayName: String, val iconName: String) {
    FLASHLIGHT_ON("FLASHLIGHT_ON", "Flashlight Turned ON", "FlashlightOn"),
    FLASHLIGHT_OFF("FLASHLIGHT_OFF", "Flashlight Turned OFF", "FlashlightOff"),
    VOLUME_UP("VOLUME_UP", "Media Volume Increased", "VolumeUp"),
    VOLUME_DOWN("VOLUME_DOWN", "Media Volume Decreased", "VolumeDown"),
    BRIGHTNESS_UP("BRIGHTNESS_UP", "Brightness Boosted", "BrightnessHigh"),
    BRIGHTNESS_DOWN("BRIGHTNESS_DOWN", "Brightness Dimmed", "BrightnessLow"),
    OPEN_APP("OPEN_APP", "Opened Application", "Apps"),
    TYPE_TEXT("TYPE_TEXT", "Text Copied to Input", "Keyboard"),
    YOUTUBE_SEARCH("YOUTUBE_SEARCH", "YouTube Search", "PlayArrow"),
    WHATSAPP_SEND("WHATSAPP_SEND", "WhatsApp Message", "Send"),
    SMS_SEND("SMS_SEND", "SMS Sent", "Email"),
    WEB_SEARCH("WEB_SEARCH", "Web Search", "Search"),
    CALL_PHONE("CALL_PHONE", "Phone Call", "Phone");

    companion object {
        fun fromTag(tag: String): ActionType? {
            return entries.firstOrNull { it.tag.equals(tag.trim(), ignoreCase = true) }
        }
    }
}

data class DeviceActionData(
    val actionType: ActionType,
    val parameter: String? = null,
    val executed: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
    val statusMessage: String = "${actionType.displayName} ${if (parameter != null) ": $parameter" else ""}"
)
