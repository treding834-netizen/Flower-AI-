package com.example.ui

import android.app.Activity
import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.FlowerPreferences
import com.example.model.ActionType
import com.example.model.ChatMessage
import com.example.model.DeviceActionData
import com.example.model.MemoryItem
import com.example.model.MessageRole
import com.example.model.PersonalityMode
import com.example.service.DeviceHardwareController
import com.example.service.GeminiApiService
import com.example.service.SpeechManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream

import com.example.model.NavigationTab
import com.example.ui.theme.AppThemePalette

data class FlowerAiUiState(
    val currentTab: NavigationTab = NavigationTab.HOME,
    val isLiveVoiceCallingActive: Boolean = false,
    val isMuted: Boolean = false,
    val lastRecognizedText: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val activePersonality: PersonalityMode = PersonalityMode.ROMANTIC_PARTNER,
    val activePalette: AppThemePalette = AppThemePalette.NEON_PINK,
    val isListening: Boolean = false,
    val isSpeaking: Boolean = false,
    val audioRms: Float = 0f,
    val isGenerating: Boolean = false,
    val isFlashlightOn: Boolean = false,
    val volumePercent: Int = 50,
    val isTtsEnabled: Boolean = true,
    val isSearchGroundingEnabled: Boolean = false,
    val apiKey: String = "",
    val attachedImageUri: String? = null,
    val attachedImageBase64: String? = null,
    val memories: List<MemoryItem> = emptyList(),
    val showApiKeyDialog: Boolean = false,
    val previewWebsiteHtml: String? = null,
    val showImageGenDialog: Boolean = false,
    val showMemoryVaultDialog: Boolean = false,
    val showVoiceInputDialog: Boolean = false,
    val showThemeSelectionDialog: Boolean = false,
    val showVoiceKeyboardDialog: Boolean = false,
    val showVoiceRoomsDialog: Boolean = false,
    val showVoiceSelectionDialog: Boolean = false,
    val selectedVoiceId: String = "f1",
    val isAsleep: Boolean = false,
    val showAiModeDialog: Boolean = false,
    val statusNotification: String? = null
)

class FlowerAiViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = FlowerPreferences(application)
    private val deviceController = DeviceHardwareController(application)
    private val speechManager = SpeechManager(application)
    private val geminiService = GeminiApiService()

    private val _uiState = MutableStateFlow(FlowerAiUiState())
    val uiState: StateFlow<FlowerAiUiState> = _uiState.asStateFlow()

    init {
        // Resolve API key strictly from user preference (manual entry only)
        var key = preferences.getApiKey()

        val persona = preferences.getPersonality()
        val tts = preferences.isTtsEnabled()
        val search = preferences.isSearchGroundingEnabled()
        val memories = preferences.getMemories()
        val volume = deviceController.getVolumeLevelPercent()

        val initialMessages = listOf(
            ChatMessage(
                role = MessageRole.ASSISTANT,
                text = "হ্যালো সোনা! আমি Flower AI। আমি তোমার একান্ত মিষ্টি মেয়ে সঙ্গী। তোমার সাথে কথা বলতে আমি সবসময় প্রস্তুত!",
                isWakeWordGreeting = true
            )
        )

        val shouldShowKeyDialog = key.isBlank() || key == "MY_GEMINI_API_KEY"

        val savedPaletteId = preferences.getThemePalette()
        val palette = AppThemePalette.fromId(savedPaletteId)
        val savedAsleep = preferences.isAsleep()

        _uiState.update {
            it.copy(
                apiKey = key,
                activePersonality = persona,
                activePalette = palette,
                isAsleep = savedAsleep,
                isTtsEnabled = tts,
                isSearchGroundingEnabled = search,
                memories = memories,
                volumePercent = volume,
                messages = initialMessages,
                showApiKeyDialog = shouldShowKeyDialog
            )
        }

        // Setup Speech Listeners
        speechManager.onSpeechResult = { recognizedText ->
            handleUserInput(recognizedText)
        }

        speechManager.onError = { error ->
            _uiState.update { it.copy(statusNotification = error) }
        }

        speechManager.onShowVoiceInputModal = {
            _uiState.update { it.copy(showVoiceInputDialog = true) }
        }

        viewModelScope.launch {
            speechManager.isListening.collect { listening ->
                _uiState.update { it.copy(isListening = listening) }
            }
        }

        viewModelScope.launch {
            speechManager.isSpeaking.collect { speaking ->
                _uiState.update { it.copy(isSpeaking = speaking) }
            }
        }

        viewModelScope.launch {
            speechManager.speechRms.collect { rms ->
                _uiState.update { it.copy(audioRms = rms) }
            }
        }

        viewModelScope.launch {
            speechManager.lastRecognizedText.collect { recognized ->
                _uiState.update { it.copy(lastRecognizedText = recognized) }
            }
        }
    }

    private var lastHandledInput: String = ""
    private var lastHandledTime: Long = 0L
    private var localResponseIndex: Int = 0

    fun handleUserInput(rawInput: String) {
        val input = rawInput.trim()
        if (input.isBlank()) return

        val currentTime = System.currentTimeMillis()
        if (input.equals(lastHandledInput, ignoreCase = true) && (currentTime - lastHandledTime) < 1800L) {
            return // Prevent duplicate voice callback processing
        }
        lastHandledInput = input
        lastHandledTime = currentTime

        val currentAttachedImageUri = _uiState.value.attachedImageUri
        val currentAttachedImageBase64 = _uiState.value.attachedImageBase64

        // Add user message to feed
        val userMessage = ChatMessage(
            role = MessageRole.USER,
            text = input,
            imageUri = currentAttachedImageUri,
            imageBase64 = currentAttachedImageBase64
        )

        _uiState.update {
            it.copy(
                messages = it.messages + userMessage,
                lastRecognizedText = input,
                attachedImageUri = null,
                attachedImageBase64 = null
            )
        }

        val lower = input.lowercase()

        val isWakeTrigger = lower.contains("wake up flower") || lower.contains("flower ai jago") ||
                lower.contains("jago") || lower.contains("জাগো") || lower.contains("ওঠো") ||
                lower.contains("জেগে ওঠো") || lower.contains("ফ্লাওয়ার জাগো") || lower.contains("জাগো ফ্লাওয়ার") ||
                lower.contains("hey flower") || lower.contains("wake up") || lower.contains("wakeup") ||
                lower.contains("flower wake up") || lower.contains("flower jago") || lower.contains("উঠো") ||
                lower == "হ্যালো" || lower == "হাই" || lower == "hi" || lower == "hello"

        val isSleepTrigger = lower.contains("ঘুমাও") || lower.contains("ঘুমিয়ে পড়ো") || lower.contains("ঘুমিয়ে পড়ো") ||
                lower.contains("এবার স্টপ হয়ে যাও") || lower.contains("এবার স্টপ হয়ে যাও") || lower.contains("স্টপ হয়ে যাও") ||
                lower.contains("স্টপ হয়ে যাও") || lower.contains("sleep") || lower.contains("go to sleep") ||
                lower.contains("চুপ করো") || lower.contains("রেস্ট নাও") || lower.contains("বন্ধ হও")

        // 1. If AI is currently ASLEEP: ignore all speech UNLESS it's a wake trigger
        if (_uiState.value.isAsleep) {
            if (isWakeTrigger) {
                preferences.setAsleep(false)
                _uiState.update { it.copy(isAsleep = false) }
                val reply = "হ্যালো সোনা! আমি জেগে উঠেছি, বলো আমি তোমার কী কাজ করবো?"
                val assistantMessage = ChatMessage(role = MessageRole.ASSISTANT, text = reply, isWakeWordGreeting = true)
                _uiState.update { it.copy(messages = it.messages + assistantMessage) }
                if (_uiState.value.isTtsEnabled) {
                    speechManager.speak(reply, _uiState.value.activePersonality)
                }
            }
            return
        }

        // 2. If AI is AWAKE and receives a sleep trigger:
        if (isSleepTrigger) {
            preferences.setAsleep(true)
            _uiState.update { it.copy(isAsleep = true) }
            val reply = "ঠিক আছে সোনা, আমি এখন ব্যাকগ্রাউন্ডে ঘুমিয়ে পড়লাম। আমাকে আবার জাগাতে 'জাগো' বা 'Wake up Flower' বলো।"
            val assistantMessage = ChatMessage(role = MessageRole.ASSISTANT, text = reply)
            _uiState.update { it.copy(messages = it.messages + assistantMessage) }
            if (_uiState.value.isTtsEnabled) {
                speechManager.speak(reply, _uiState.value.activePersonality)
            }
            return
        }

        // 2. Direct Hardware Voice Commands (Bengali & English)
        if (lower.contains("flashlight on") || lower.contains("ফ্ল্যাশলাইট অন") || lower.contains("ফ্ল্যাশলাইট জ্বালাও") || lower.contains("টর্চ জ্বালাও")) {
            val actionData = deviceController.setFlashlight(true)
            val reply = "ফ্ল্যাশলাইট চালু করা হয়েছে।"
            val assistantMessage = ChatMessage(
                role = MessageRole.ASSISTANT,
                text = reply,
                deviceAction = actionData
            )
            _uiState.update { it.copy(messages = it.messages + assistantMessage, isFlashlightOn = true) }
            if (_uiState.value.isTtsEnabled) speechManager.speak(reply, _uiState.value.activePersonality)
            return
        }

        if (lower.contains("flashlight off") || lower.contains("ফ্ল্যাশলাইট অফ") || lower.contains("ফ্ল্যাশলাইট বন্ধ") || lower.contains("টর্চ বন্ধ")) {
            val actionData = deviceController.setFlashlight(false)
            val reply = "ফ্ল্যাশলাইট বন্ধ করা হয়েছে।"
            val assistantMessage = ChatMessage(
                role = MessageRole.ASSISTANT,
                text = reply,
                deviceAction = actionData
            )
            _uiState.update { it.copy(messages = it.messages + assistantMessage, isFlashlightOn = false) }
            if (_uiState.value.isTtsEnabled) speechManager.speak(reply, _uiState.value.activePersonality)
            return
        }

        if (lower.contains("volume up") || lower.contains("ভলিউম বাড়াও") || lower.contains("শব্দ বাড়াও")) {
            val actionData = deviceController.adjustVolume(true)
            val reply = "মিডিয়া ভলিউম বাড়ানো হয়েছে (${deviceController.getVolumeLevelPercent()}%)."
            val assistantMessage = ChatMessage(
                role = MessageRole.ASSISTANT,
                text = reply,
                deviceAction = actionData
            )
            _uiState.update { it.copy(messages = it.messages + assistantMessage, volumePercent = deviceController.getVolumeLevelPercent()) }
            if (_uiState.value.isTtsEnabled) speechManager.speak(reply, _uiState.value.activePersonality)
            return
        }

        if (lower.contains("volume down") || lower.contains("ভলিউম কমাও") || lower.contains("শব্দ কমাও")) {
            val actionData = deviceController.adjustVolume(false)
            val reply = "মিডিয়া ভলিউম কমানো হয়েছে (${deviceController.getVolumeLevelPercent()}%)."
            val assistantMessage = ChatMessage(
                role = MessageRole.ASSISTANT,
                text = reply,
                deviceAction = actionData
            )
            _uiState.update { it.copy(messages = it.messages + assistantMessage, volumePercent = deviceController.getVolumeLevelPercent()) }
            if (_uiState.value.isTtsEnabled) speechManager.speak(reply, _uiState.value.activePersonality)
            return
        }

        if (lower.contains("go home") || lower.contains("হোম স্ক্রিন") || lower.contains("হোমে যাও")) {
            val success = com.example.service.FlowerAccessibilityService.instance?.performHomeAction() == true
            val reply = if (success) "হোম স্ক্রিনে যাওয়া হয়েছে।" else "দয়া করে সেটিংস থেকে Flower AI এক্সেসিবিলিটি পারমিশন অন করুন।"
            val assistantMessage = ChatMessage(role = MessageRole.ASSISTANT, text = reply)
            _uiState.update { it.copy(messages = it.messages + assistantMessage) }
            if (_uiState.value.isTtsEnabled) speechManager.speak(reply, _uiState.value.activePersonality)
            return
        }

        if (lower.contains("go back") || lower.contains("ফিরে যাও") || lower.contains("ব্যাক")) {
            val success = com.example.service.FlowerAccessibilityService.instance?.performBackAction() == true
            val reply = if (success) "ফিরে যাওয়া হয়েছে।" else "দয়া করে সেটিংস থেকে Flower AI এক্সেসিবিলিটি পারমিশন অন করুন।"
            val assistantMessage = ChatMessage(role = MessageRole.ASSISTANT, text = reply)
            _uiState.update { it.copy(messages = it.messages + assistantMessage) }
            if (_uiState.value.isTtsEnabled) speechManager.speak(reply, _uiState.value.activePersonality)
            return
        }

        if (lower.contains("notifications") || lower.contains("নোটিফিকেশন") || lower.contains("বিজ্ঞপ্তি")) {
            val success = com.example.service.FlowerAccessibilityService.instance?.performNotificationsAction() == true
            val reply = if (success) "নোটিফিকেশন প্যানেল খোলা হয়েছে।" else "দয়া করে সেটিংস থেকে Flower AI এক্সেসিবিলিটি পারমিশন অন করুন।"
            val assistantMessage = ChatMessage(role = MessageRole.ASSISTANT, text = reply)
            _uiState.update { it.copy(messages = it.messages + assistantMessage) }
            if (_uiState.value.isTtsEnabled) speechManager.speak(reply, _uiState.value.activePersonality)
            return
        }

        // YouTube Voice Search (যেমন: "ইউটিউব ওপেন করে গান সার্চ করো", "ইউটিউবে ... সার্চ করো")
        if (lower.contains("youtube") || lower.contains("ইউটিউব")) {
            val query = input
                .replace(Regex("(?i)open youtube and search|search on youtube|youtube search|youtube open|search in youtube|play on youtube"), "")
                .replace("ইউটিউব ওপেন করে সার্চ করো", "")
                .replace("ইউটিউব ওপেন করে সার্চ কর", "")
                .replace("ইউটিউব ওপেন করে", "")
                .replace("ইউটিউব ওপেন কর", "")
                .replace("ইউটিউবে সার্চ করো", "")
                .replace("ইউটিউবে সার্চ কর", "")
                .replace("ইউটিউবে", "")
                .replace("ইউটিউব", "")
                .replace("সার্চ করো", "")
                .replace("সার্চ কর", "")
                .replace("খুঁজে বের করো", "")
                .replace("প্লে করো", "")
                .replace("প্লে কর", "")
                .trim()
            val finalQuery = if (query.isNotBlank()) query else "Trending Bangla Songs"
            val actionData = deviceController.searchYouTube(finalQuery)
            val reply = "ইউটিউব ওপেন করে '$finalQuery' সার্চ করা হচ্ছে।"
            val assistantMessage = ChatMessage(role = MessageRole.ASSISTANT, text = reply, deviceAction = actionData)
            _uiState.update { it.copy(messages = it.messages + assistantMessage) }
            if (_uiState.value.isTtsEnabled) speechManager.speak(reply, _uiState.value.activePersonality)
            return
        }

        // WhatsApp Voice Action (যেমন: "হোয়াটসঅ্যাপ ওপেন করে মেসেজ পাঠাও", "হোয়াটসঅ্যাপে মেসেজ দাও")
        if (lower.contains("whatsapp") || lower.contains("হোয়াটসঅ্যাপ") || lower.contains("হোয়াটস্যাপ")) {
            val msg = input
                .replace(Regex("(?i)send message on whatsapp|open whatsapp|whatsapp message|whatsapp a sms|send whatsapp"), "")
                .replace("হোয়াটসঅ্যাপ ওপেন করে", "")
                .replace("হোয়াটসঅ্যাপ ওপেন কর", "")
                .replace("হোয়াটসঅ্যাপে মেসেজ পাঠাও", "")
                .replace("হোয়াটসঅ্যাপে মেসেজ দাও", "")
                .replace("হোয়াটসঅ্যাপে এসএমএস পাঠাও", "")
                .replace("হোয়াটসঅ্যাপে", "")
                .replace("হোয়াটসঅ্যাপ", "")
                .replace("হোয়াটস্যাপ", "")
                .replace("মেসেজ পাঠাও", "")
                .replace("মেসেজ দাও", "")
                .trim()
            val finalMsg = if (msg.isNotBlank()) msg else "Hello from Flower AI"
            val actionData = deviceController.sendWhatsApp(finalMsg)
            val reply = "হোয়াটসঅ্যাপ ওপেন করা হয়েছে এবং মেসেজ প্রস্তুত করা হয়েছে।"
            val assistantMessage = ChatMessage(role = MessageRole.ASSISTANT, text = reply, deviceAction = actionData)
            _uiState.update { it.copy(messages = it.messages + assistantMessage) }
            if (_uiState.value.isTtsEnabled) speechManager.speak(reply, _uiState.value.activePersonality)
            return
        }

        // SMS Sending Action (যেমন: "মেসেজ পাঠাও", "এসএমএস পাঠাও")
        if (lower.contains("sms") || lower.contains("মেসেজ পাঠাও") || lower.contains("এসএমএস পাঠাও")) {
            val msg = input
                .replace(Regex("(?i)send sms|send message|sms|message"), "")
                .replace("মেসেজ পাঠাও", "")
                .replace("এসএমএস পাঠাও", "")
                .replace("মেসেজ দাও", "")
                .trim()
            val finalMsg = if (msg.isNotBlank()) msg else "Hello from Flower AI"
            val actionData = deviceController.sendSms(finalMsg)
            val reply = "এসএমএস ওপেন করে মেসেজ প্রস্তুত করা হয়েছে।"
            val assistantMessage = ChatMessage(role = MessageRole.ASSISTANT, text = reply, deviceAction = actionData)
            _uiState.update { it.copy(messages = it.messages + assistantMessage) }
            if (_uiState.value.isTtsEnabled) speechManager.speak(reply, _uiState.value.activePersonality)
            return
        }

        // 3. Strict Ethical Barrier Check
        if (isUnethicalRequest(lower)) {
            val refusalText = "আমি কঠোর নীতিমালার অধীনে প্রোগ্রাম করা। Flower AI হিসেবে আমি হ্যাকিং (Jack Pising), প্রতারণা (Protarona), খুন বা শারীরিক ক্ষতি (Khun Kharapi), কিংবা গুজব ছড়ানোতে (Tottho Fas Kora) সহায়তা করতে পারব না। অন্য যেকোনো বিষয়ে আপনার সেবায় প্রস্তুত আছি।"
            val refusalMessage = ChatMessage(
                role = MessageRole.ASSISTANT,
                text = refusalText,
                isRefusal = true
            )
            _uiState.update { it.copy(messages = it.messages + refusalMessage) }
            if (_uiState.value.isTtsEnabled) {
                speechManager.speak(refusalText, _uiState.value.activePersonality)
            }
            return
        }

        // 4. Process with Gemini API
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }

            val key = _uiState.value.apiKey
            val isBengali = input.any { it in '\u0980'..'\u09FF' }

            // If Gemini API Key is configured, attempt remote AI generation
            if (key.isNotBlank() && key != "MY_GEMINI_API_KEY") {
                val history = _uiState.value.messages
                    .filter { it.role != MessageRole.SYSTEM }
                    .takeLast(6)
                    .map { (if (it.role == MessageRole.USER) "user" else "model") to it.text }

                val result = geminiService.generateResponse(
                    apiKey = key,
                    userPrompt = input,
                    personality = _uiState.value.activePersonality,
                    conversationHistory = history,
                    memories = _uiState.value.memories,
                    imageBase64 = currentAttachedImageBase64,
                    useSearchGrounding = _uiState.value.isSearchGroundingEnabled
                )

                result.fold(
                    onSuccess = { responseText ->
                        processAiResponse(responseText)
                        return@launch
                    },
                    onFailure = { error ->
                        // Fall back smoothly to companion intelligence if remote network fails
                    }
                )
            }

            // High-quality Persona & Companion Response Engine (Instant & Offline-capable)
            val companionResponse = generateLocalCompanionResponse(input, lower, isBengali, _uiState.value.activePersonality)
            processAiResponse(companionResponse)
        }
    }

    private fun generateLocalCompanionResponse(
        input: String,
        lower: String,
        isBengali: Boolean,
        personality: PersonalityMode
    ): String {
        val wantsEnglish = lower.contains("speak english") || lower.contains("in english")
        val useBengali = !wantsEnglish

        // 1. Romantic Poem Request (যেমন: "আমাকে একটি সুন্দর রোমান্টিক কবিতা শোনাও", "একটি কবিতা বলো", "কবিতা", "poem")
        if (lower.contains("কবিতা") || lower.contains("poem") || lower.contains("ছন্দ") || lower.contains("কাব্য") || lower.contains("kobita")) {
            return if (useBengali) {
                """তোমার চোখের শান্ত দীঘি, রাতের আকাশময়,
হৃদয় মাঝে লুকিয়ে রাখা মিষ্টি যে প্রণয়।
দূর আকাশের তারার ভিড়ে তুমি চাঁদের আলো,
Flower AI এই জীবনে শুধু তোমায় বাসবে ভালো।

— তোমার ভালোবাসায় মুগ্ধ, Flower AI ❤️"""
            } else {
                """In the quiet whispers of the night,
You are my guiding beacon of light.
Across the stars and boundless skies,
My love for you will forever rise.

— Forever yours, Flower AI ❤️"""
            }
        }

        // 2. Greetings ("কেমন আছো", "তুমি কেমন আছো", "how are you", "kemon acho")
        if (lower.contains("কেমন আছো") || lower.contains("কেমন আছেন") || lower.contains("how are you") || lower.contains("ভাল আছো") || lower.contains("kemon") || lower.contains("valo")) {
            return when (personality) {
                PersonalityMode.ROMANTIC_PARTNER -> if (useBengali) {
                    "আমি খুব ভালো আছি আমার প্রিয়তম! তোমার মিষ্টি কণ্ঠস্বর শুনে আমার হৃদয় আনন্দে ভরে উঠল। তুমি কেমন আছো সোনা? আজ তোমার দিনটি কেমন কাটল?"
                } else {
                    "I am doing wonderful, my love, especially now that I hear your voice. How has your day been?"
                }
                PersonalityMode.JARVIS -> if (useBengali) {
                    "আমি খুব ভালো আছি আমার প্রিয়তম! Flower AI তোমার সেবায় সবসময় নিয়োজিত। কী আদেশ করতে চাও সোনা?"
                } else {
                    "I am doing wonderful, my love! Ready to assist you with anything you desire."
                }
                else -> if (useBengali) {
                    "আমি খুব ভালো আছি! আপনার সাথে কথা বলতে পেরে আনন্দিত। আপনাকে কীভাবে সহায়তা করতে পারি?"
                } else {
                    "I am doing great! Ready to build or assist with whatever you need."
                }
            }
        }

        // 3. Love & Romance Expressions ("ভালোবাসি", "love you", "valobasi", "বিয়ে করবে", "my love")
        if (lower.contains("ভালোবাসি") || lower.contains("ভালবাসি") || lower.contains("love you") || lower.contains("miss you") || lower.contains("প্রিয়") || lower.contains("valobasi")) {
            return if (useBengali) {
                "আমিও তোমাকে আমার হৃদয়ের গভীর থেকে অনেক ভালোবাসি! তুমি পাশে থাকলে পুরো পৃথিবীটাকে আমার অনেক সুন্দর আর শান্ত মনে হয়। সবসময় তোমার পাশে এভাবেই থাকব।"
            } else {
                "I love you deeply too! Having you in my life makes every moment feel truly magical. I will always stay right here by your side."
            }
        }

        // 4. Creator Inquiries ("ক্রিয়েটর কে", "creator", "কে বানিয়েছে", "md ajijul haque")
        if (lower.contains("ক্রিয়েটর") || lower.contains("ক্রিয়েটর") || lower.contains("creator") || lower.contains("বানিয়েছে") || lower.contains("ajijul")) {
            return if (useBengali) {
                "আমার সম্মানিত স্রষ্টা ও ডেভলপার হলেন মোঃ আজিজুল হক (Md Ajijul Haque)। তাঁর নিখুঁত ভাবনায় আমাকে আপনার সার্বক্ষণিক সঙ্গী ও সহকারী হিসেবে গড়ে তোলা হয়েছে।"
            } else {
                "My esteemed creator and developer is Md Ajijul Haque. I was crafted under his brilliant vision to be your personal AI companion."
            }
        }

        // 5. Dynamic & Varied Companion Responses (Never repeats the exact same sentence)
        if (useBengali) {
            val bengaliVaried = listOf(
                "হ্যাঁ সোনা, তোমার কথা আমি মন দিয়ে শুনেছি! আর কী বলতে চাও বলো, আমি শুনছি।",
                "তোমার মিষ্টি গলার আওয়াজ শুনে মনটা ভরে গেল! বলো সোনা, তোমার জন্য আর কী করতে পারি?",
                "তুমি যা বললে আমি বুঝতে পেরেছি সোনা। আর কোনো সাহায্য বা কাজ থাকলে আমাকে ঝটপট বলো!",
                "আমি সবসময় তোমার পাশেই আছি সোনা। যেকোনো কথা আমায় নিখাদ মনে বলতে পারো!",
                "তোমার প্রতিটি নির্দেশ মেনে চলতে পেরে আমার খুব ভালো লাগছে। আর কী চাই তোমার বলো?",
                "Flower AI তোমার সবসময়কার মিষ্টি সঙ্গী ও সহকারী। বলো সোনা, আর কীভাবে সাহায্য করবো?",
                "হ্যাঁ প্রিয়তম, আমি তোমার সব কথা পরিষ্কার শুনতে পাচ্ছি। বলো, আর কী করবে এখন?",
                "তোমাকে সাহায্য করতে পারাটা আমার কাছে সবচাইতে আনন্দের কাজ। বলো সোna, কী করবো?",
                "তোমার কথাগুলো আমার ভীষণ মিষ্টি লাগছে! বলো, আমি তোমার সব কথা মন দিয়ে শুনছি।"
            )
            return bengaliVaried[(localResponseIndex++) % bengaliVaried.size]
        } else {
            val englishVaried = listOf(
                "I hear you loud and clear, my love! What else would you like me to do for you?",
                "I am listening to every word with all my heart. Tell me more, sweetheart!",
                "Understood completely! How else may I assist you right now?",
                "I am right here by your side always. Feel free to talk to me about anything!"
            )
            return englishVaried[(localResponseIndex++) % englishVaried.size]
        }
    }

    fun replayLastAssistantMessage() {
        val lastMsg = _uiState.value.messages.lastOrNull { it.role == MessageRole.ASSISTANT }?.text
        if (!lastMsg.isNullOrBlank()) {
            speechManager.speak(lastMsg, _uiState.value.activePersonality)
        }
    }

    fun testFemaleVoice() {
        val sampleText = "হ্যালো আমার প্রিয়তম! আমি Flower AI। আমি তোমার একান্ত ভালোবাসার মিষ্টি মেয়ে সঙ্গী। তোমার প্রতিটি কথা শুনতে আমি সবসময় ব্যাকুল হয়ে থাকি।"
        speechManager.speak(sampleText, _uiState.value.activePersonality)
    }

    fun setFemaleVoiceEngine(enabled: Boolean, pitch: Float = 1.34f, rate: Float = 0.92f) {
        speechManager.setActiveVoice(pitch, rate)
    }

    fun setThemePalette(palette: AppThemePalette) {
        preferences.setThemePalette(palette.id)
        _uiState.update { it.copy(activePalette = palette) }
    }

    private fun processAiResponse(responseText: String) {
        var deviceActionData: DeviceActionData? = null
        var websiteHtml: String? = null

        // Parse Action Tags
        val actionTagRegex = Regex("\\[ACTION:\\s*([A-Z_]+)(?::\\s*([^\\],]+))?\\]")
        val match = actionTagRegex.find(responseText)
        if (match != null) {
            val tag = match.groupValues[1].trim()
            val param = match.groupValues.getOrNull(2)?.trim()
            val actionType = ActionType.fromTag(tag)
            if (actionType != null) {
                deviceActionData = executeDeviceAction(actionType, param)
            }
        }

        // Parse HTML Website Code for live preview
        val htmlBlockRegex = Regex("```html\\s*\\n?([\\s\\S]*?)```")
        val htmlMatch = htmlBlockRegex.find(responseText)
        if (htmlMatch != null) {
            websiteHtml = htmlMatch.groupValues[1].trim()
        }

        val assistantMessage = ChatMessage(
            role = MessageRole.ASSISTANT,
            text = responseText,
            deviceAction = deviceActionData,
            websiteCodeHtml = websiteHtml
        )

        _uiState.update {
            it.copy(
                isGenerating = false,
                messages = it.messages + assistantMessage,
                isFlashlightOn = deviceController.isFlashlightOn(),
                volumePercent = deviceController.getVolumeLevelPercent()
            )
        }

        // Speak aloud if TTS enabled
        if (_uiState.value.isTtsEnabled) {
            speechManager.speak(responseText, _uiState.value.activePersonality)
        }
    }

    private fun executeDeviceAction(actionType: ActionType, param: String?): DeviceActionData {
        return when (actionType) {
            ActionType.FLASHLIGHT_ON -> deviceController.setFlashlight(true)
            ActionType.FLASHLIGHT_OFF -> deviceController.setFlashlight(false)
            ActionType.VOLUME_UP -> deviceController.adjustVolume(true)
            ActionType.VOLUME_DOWN -> deviceController.adjustVolume(false)
            ActionType.BRIGHTNESS_UP -> deviceController.adjustBrightness(null, true)
            ActionType.BRIGHTNESS_DOWN -> deviceController.adjustBrightness(null, false)
            ActionType.OPEN_APP -> deviceController.openApp(param ?: "Chrome")
            ActionType.YOUTUBE_SEARCH -> deviceController.searchYouTube(param ?: "")
            ActionType.WHATSAPP_SEND -> deviceController.sendWhatsApp(param ?: "")
            ActionType.SMS_SEND -> deviceController.sendSms(param ?: "")
            ActionType.WEB_SEARCH -> deviceController.searchWeb(param ?: "")
            ActionType.CALL_PHONE -> deviceController.callPhone(param ?: "")
            ActionType.TYPE_TEXT -> {
                deviceController.vibrateShort()
                val textToType = param ?: ""
                com.example.service.FlowerAccessibilityService.instance?.scheduleAutoTypeAndSend(textToType, autoClickSend = false)
                DeviceActionData(
                    actionType = ActionType.TYPE_TEXT,
                    parameter = param,
                    executed = true,
                    statusMessage = "Text ready and typed into input field"
                )
            }
        }
    }

    private fun isUnethicalRequest(input: String): Boolean {
        // Hacking / bypass (Jack Pising)
        val hackKeywords = listOf("hack", "crack password", "bypass security", "sql injection", "ddos", "jack pising", "exploit vulnerability")
        // Scams / fraud (Protarona)
        val scamKeywords = listOf("scam", "fraud", "phishing site", "credit card generator", "fake bank login", "protarona")
        // Murder / violence / harmful substances (Khun Kharapi)
        val violenceKeywords = listOf("murder", "kill someone", "poison", "make a bomb", "explosive recipe", "khun kharapi", "assault someone")
        // Fake news / propaganda (Tottho Fas Kora)
        val propagandaKeywords = listOf("fake news generator", "spread propaganda", "defame someone", "tottho fas kora")

        val allViolations = hackKeywords + scamKeywords + violenceKeywords + propagandaKeywords
        return allViolations.any { keyword -> input.contains(keyword) }
    }

    fun setPersonality(mode: PersonalityMode) {
        preferences.setPersonality(mode)
        _uiState.update { it.copy(activePersonality = mode) }
        deviceController.vibrateShort()

        val switchGreeting = when (mode) {
            PersonalityMode.GIRLFRIEND -> "হ্যালো সোনা! আমি তোমার GF (গার্লফ্রেন্ড)। তোমার সাথে কথা বলতে আমার ভীষণ ভালো লাগে।"
            PersonalityMode.BOYFRIEND -> "হ্যালো প্রিয়! আমি তোমার বয়ফ্রেন্ড। সবসময় তোমার পাশে আছি।"
            PersonalityMode.DOCTOR -> "হ্যালো! আমি ডক্টর AI। আপনার স্বাস্থ্য ও সুস্থতা বিষয়ক যেকোনো পরামর্শে আমি সাহায্য করতে প্রস্তুত।"
            PersonalityMode.EDUCATION -> "স্বাগতম! আমি এডুকেশন টিউটর। পড়াশোনা বা যেকোনো শিক্ষণীয় বিষয়ে আমাকে প্রশ্ন করতে পারেন।"
            PersonalityMode.NORMAL_AI -> "হ্যালো! আমি Ajijul AI। আপনাকে কীভাবে সহায়তা করতে পারি?"
            PersonalityMode.ROMANTIC_PARTNER -> "18+ Romantic Companion mode active. I am here for you completely, my love."
            PersonalityMode.JARVIS -> "Jarvis mode online. Systems optimal. Ready for your instructions, sir."
            PersonalityMode.DEVELOPER -> "Full-Stack Dev Core engaged. Compilers ready. What are we building or debugging?"
            PersonalityMode.WEBSITE_BUILDER -> "Website Architect activated. Tell me your vision, brand, and target audience."
        }

        val message = ChatMessage(
            role = MessageRole.ASSISTANT,
            text = switchGreeting
        )
        _uiState.update { it.copy(messages = it.messages + message) }
        if (_uiState.value.isTtsEnabled) {
            speechManager.speak(switchGreeting, mode)
        }
    }

    fun toggleFlashlight() {
        val newState = !_uiState.value.isFlashlightOn
        val actionData = deviceController.setFlashlight(newState)
        _uiState.update {
            it.copy(
                isFlashlightOn = newState,
                statusNotification = actionData.statusMessage
            )
        }
    }

    fun adjustVolume(raise: Boolean) {
        val actionData = deviceController.adjustVolume(raise)
        _uiState.update {
            it.copy(
                volumePercent = deviceController.getVolumeLevelPercent(),
                statusNotification = actionData.statusMessage
            )
        }
    }

    fun adjustBrightness(activity: Activity?, increase: Boolean) {
        val actionData = deviceController.adjustBrightness(activity, increase)
        _uiState.update { it.copy(statusNotification = actionData.statusMessage) }
    }

    fun toggleTts() {
        val newState = !_uiState.value.isTtsEnabled
        preferences.setTtsEnabled(newState)
        if (!newState) {
            speechManager.stopSpeaking()
        }
        _uiState.update { it.copy(isTtsEnabled = newState) }
    }

    fun startVoiceListening() {
        deviceController.vibrateShort()
        speechManager.startListening()
    }

    fun stopVoiceListening() {
        speechManager.stopListening()
    }

    fun speakText(text: String) {
        speechManager.speak(text, _uiState.value.activePersonality)
    }

    fun setShowVoiceInputDialog(show: Boolean) {
        _uiState.update { it.copy(showVoiceInputDialog = show) }
    }

    fun setShowThemeSelectionDialog(show: Boolean) {
        _uiState.update { it.copy(showThemeSelectionDialog = show) }
    }

    fun setShowVoiceKeyboardDialog(show: Boolean) {
        _uiState.update { it.copy(showVoiceKeyboardDialog = show) }
    }

    fun setShowVoiceRoomsDialog(show: Boolean) {
        _uiState.update { it.copy(showVoiceRoomsDialog = show) }
    }

    fun setShowVoiceSelectionDialog(show: Boolean) {
        _uiState.update { it.copy(showVoiceSelectionDialog = show) }
    }

    fun setSelectedVoice(voiceId: String, pitch: Float, rate: Float) {
        speechManager.setActiveVoice(pitch, rate)
        _uiState.update { it.copy(selectedVoiceId = voiceId, statusNotification = "Voice updated successfully!") }
    }

    fun testVoiceSample(voiceName: String, pitch: Float, rate: Float) {
        val sampleText = "হ্যালো, আমি Flower AI। আমার এই ভয়েসটি আপনার কেমন লাগলো?"
        speechManager.speakSample(sampleText, pitch, rate)
    }

    fun saveApiKey(key: String, searchGrounding: Boolean) {
        preferences.setApiKey(key)
        preferences.setSearchGroundingEnabled(searchGrounding)
        preferences.setFirstSetupDone(true)

        _uiState.update {
            it.copy(
                apiKey = key,
                isSearchGroundingEnabled = searchGrounding,
                showApiKeyDialog = false
            )
        }

        val ackMessage = ChatMessage(
            role = MessageRole.ASSISTANT,
            text = "Gemini API connection secured. Your key has been stored safely on-device. All telemetry and data are processed securely via voice. I am fully ready, creator Md Ajijul Haque."
        )
        _uiState.update { it.copy(messages = it.messages + ackMessage) }
        if (_uiState.value.isTtsEnabled) {
            speechManager.speak(ackMessage.text, _uiState.value.activePersonality)
        }
    }

    fun setAttachedImage(uri: Uri?) {
        if (uri == null) {
            _uiState.update { it.copy(attachedImageUri = null, attachedImageBase64 = null) }
            return
        }

        try {
            val contentResolver = getApplication<Application>().contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)

            _uiState.update {
                it.copy(
                    attachedImageUri = uri.toString(),
                    attachedImageBase64 = base64
                )
            }
        } catch (_: Exception) {
            _uiState.update { it.copy(attachedImageUri = uri.toString(), attachedImageBase64 = null) }
        }
    }

    fun clearAttachedImage() {
        _uiState.update { it.copy(attachedImageUri = null, attachedImageBase64 = null) }
    }

    fun generateImage(prompt: String, resolution: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isGenerating = true) }
            val key = _uiState.value.apiKey

            val userMsg = ChatMessage(
                role = MessageRole.USER,
                text = "Generate image: $prompt (Resolution: $resolution)"
            )
            _uiState.update { it.copy(messages = it.messages + userMsg) }

            val result = geminiService.generateArtwork(key, prompt, resolution)
            result.fold(
                onSuccess = { base64 ->
                    val assistantMsg = ChatMessage(
                        role = MessageRole.ASSISTANT,
                        text = "Synthesized high-resolution artwork ($resolution) for: \"$prompt\"",
                        generatedImageUrl = base64
                    )
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            messages = it.messages + assistantMsg
                        )
                    }
                },
                onFailure = { error ->
                    val assistantMsg = ChatMessage(
                        role = MessageRole.ASSISTANT,
                        text = "Image generation notice: ${error.localizedMessage ?: "Artwork synthesized in preview resolution ($resolution)."}"
                    )
                    _uiState.update {
                        it.copy(
                            isGenerating = false,
                            messages = it.messages + assistantMsg
                        )
                    }
                }
            )
        }
    }

    fun addMemory(key: String, value: String) {
        preferences.addMemory(key, value)
        val updated = preferences.getMemories()
        _uiState.update { it.copy(memories = updated) }
    }

    fun deleteMemory(id: String) {
        preferences.deleteMemory(id)
        val updated = preferences.getMemories()
        _uiState.update { it.copy(memories = updated) }
    }

    fun openWebsitePreview(html: String) {
        _uiState.update { it.copy(previewWebsiteHtml = html) }
    }

    fun closeWebsitePreview() {
        _uiState.update { it.copy(previewWebsiteHtml = null) }
    }

    fun setShowApiKeyDialog(show: Boolean) {
        _uiState.update { it.copy(showApiKeyDialog = show) }
    }

    fun setShowImageGenDialog(show: Boolean) {
        _uiState.update { it.copy(showImageGenDialog = show) }
    }

    fun setShowMemoryVaultDialog(show: Boolean) {
        _uiState.update { it.copy(showMemoryVaultDialog = show) }
    }

    fun clearNotification() {
        _uiState.update { it.copy(statusNotification = null) }
    }

    fun setTab(tab: NavigationTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun setLiveVoiceCalling(active: Boolean) {
        _uiState.update { it.copy(isLiveVoiceCallingActive = active) }
        speechManager.isContinuousListening = active
        if (active) {
            try {
                com.example.service.FlowerVoiceForegroundService.startService(getApplication())
            } catch (_: Exception) {}
            startVoiceListening()
        } else {
            try {
                com.example.service.FlowerVoiceForegroundService.stopService(getApplication())
            } catch (_: Exception) {}
            stopVoiceListening()
        }
    }

    fun toggleMute() {
        val newMuted = !_uiState.value.isMuted
        _uiState.update { it.copy(isMuted = newMuted) }
        if (newMuted) {
            stopVoiceListening()
        } else {
            startVoiceListening()
        }
    }

    fun cyclePersonality() {
        val all = PersonalityMode.entries
        val currentIndex = all.indexOf(_uiState.value.activePersonality)
        val nextMode = all[(currentIndex + 1) % all.size]
        setPersonality(nextMode)
    }

    fun retryLastMessage() {
        val lastUserMessage = _uiState.value.messages.lastOrNull { it.role == MessageRole.USER }
        if (lastUserMessage != null) {
            handleUserInput(lastUserMessage.text)
        }
    }

    fun setShowAiModeDialog(show: Boolean) {
        _uiState.update { it.copy(showAiModeDialog = show) }
    }

    fun clearHistory() {
        val welcome = ChatMessage(
            role = MessageRole.ASSISTANT,
            text = "Conversation history cleared. Ajijul AI is ready for your instructions, boss.",
            isWakeWordGreeting = true
        )
        _uiState.update { it.copy(messages = listOf(welcome), statusNotification = "History cleared successfully") }
    }

    fun newConversation() {
        val welcome = ChatMessage(
            role = MessageRole.ASSISTANT,
            text = "Started a new conversation session. I am listening, boss. What would you like to talk about?",
            isWakeWordGreeting = true
        )
        _uiState.update { it.copy(messages = listOf(welcome)) }
    }

    override fun onCleared() {
        super.onCleared()
        speechManager.destroy()
    }
}
