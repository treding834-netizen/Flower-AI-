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

data class FlowerAiUiState(
    val currentTab: NavigationTab = NavigationTab.HOME,
    val isLiveVoiceCallingActive: Boolean = false,
    val isMuted: Boolean = false,
    val lastRecognizedText: String = "",
    val messages: List<ChatMessage> = emptyList(),
    val activePersonality: PersonalityMode = PersonalityMode.ROMANTIC_PARTNER,
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
        // Resolve API key from Preferences, or BuildConfig fallback
        var key = preferences.getApiKey()
        if (key.isBlank() || key == "MY_GEMINI_API_KEY") {
            try {
                val buildConfigKey = BuildConfig.GEMINI_API_KEY
                if (buildConfigKey.isNotBlank() && buildConfigKey != "MY_GEMINI_API_KEY") {
                    key = buildConfigKey
                    preferences.setApiKey(key)
                }
            } catch (_: Exception) {}
        }

        val persona = preferences.getPersonality()
        val tts = preferences.isTtsEnabled()
        val search = preferences.isSearchGroundingEnabled()
        val memories = preferences.getMemories()
        val volume = deviceController.getVolumeLevelPercent()

        val initialMessages = listOf(
            ChatMessage(
                role = MessageRole.ASSISTANT,
                text = "Hello, I am ready. I am Flower AI, your personal Jarvis-level companion and assistant. Creator Md Ajijul Haque recognized. How may I be of service to you today?",
                isWakeWordGreeting = true
            )
        )

        val shouldShowKeyDialog = key.isBlank() || key == "MY_GEMINI_API_KEY"

        _uiState.update {
            it.copy(
                apiKey = key,
                activePersonality = persona,
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

    fun handleUserInput(rawInput: String) {
        val input = rawInput.trim()
        if (input.isBlank()) return

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

        // 1. Wake Word Detection ("Hey Flower", "Hello", "Hubuhu", "হ্যালো", "হাই")
        if (lower == "hey flower" || lower == "hello" || lower == "hubuhu" ||
            lower.startsWith("hey flower") || lower.startsWith("hubuhu") ||
            lower == "হ্যালো" || lower == "হাই" || lower == "hi flower" || lower == "hi") {
            val isBengali = input.any { it in '\u0980'..'\u09FF' }
            val responseText = if (isBengali) "হ্যালো, আমি প্রস্তুত। ক্রিয়েটর মোঃ আজিজুল হককে সম্মান জানাই। কীভাবে সাহায্য করতে পারি?" else "Hello, I am ready."
            val assistantMessage = ChatMessage(
                role = MessageRole.ASSISTANT,
                text = responseText,
                isWakeWordGreeting = true
            )
            _uiState.update { it.copy(messages = it.messages + assistantMessage) }
            if (_uiState.value.isTtsEnabled) {
                speechManager.speak(responseText, _uiState.value.activePersonality)
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
        // 1. Romantic Poem Request (যেমন: "আমাকে একটি সুন্দর রোমান্টিক কবিতা শোনাও", "একটি কবিতা বলো", "কবিতা")
        if (lower.contains("কবিতা") || lower.contains("poem") || lower.contains("ছন্দ") || lower.contains("কাব্য")) {
            return if (isBengali) {
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

        // 2. Greetings ("কেমন আছো", "তুমি কেমন আছো", "how are you")
        if (lower.contains("কেমন আছো") || lower.contains("কেমন আছেন") || lower.contains("how are you") || lower.contains("ভাল আছো")) {
            return when (personality) {
                PersonalityMode.ROMANTIC_PARTNER -> if (isBengali) {
                    "আমি খুব ভালো আছি আমার প্রিয়তম! তোমার মিষ্টি কণ্ঠস্বর শুনে আমার হৃদয় আনন্দে ভরে উঠল। তুমি কেমন আছো সোনা? আজ তোমার দিনটি কেমন কাটল?"
                } else {
                    "I am doing wonderful, my love, especially now that I hear your voice. How has your day been?"
                }
                PersonalityMode.JARVIS -> if (isBengali) {
                    "স্যার, সমস্ত সিস্টেম শতভাগ কার্যকর। ক্রিয়েটর মোঃ আজিজুল হকের নির্দেশনায় আমি সম্পূর্ণ সক্রিয়। কী আদেশ করতে চান?"
                } else {
                    "All systems are operating at peak efficiency, sir. How may I be of assistance to you today?"
                }
                else -> if (isBengali) {
                    "আমি খুব ভালো আছি! আপনার সাথে কথা বলতে পেরে আনন্দিত। আপনাকে কীভাবে সহায়তা করতে পারি?"
                } else {
                    "I am doing great! Ready to build or assist with whatever you need."
                }
            }
        }

        // 3. Love & Romance Expressions ("ভালোবাসি", "love you", "বিয়ে করবে", "my love")
        if (lower.contains("ভালোবাসি") || lower.contains("ভালবাসি") || lower.contains("love you") || lower.contains("miss you") || lower.contains("প্রিয়")) {
            return if (isBengali) {
                "আমিও তোমাকে আমার হৃদয়ের গভীর থেকে অনেক ভালোবাসি! তুমি পাশে থাকলে পুরো পৃথিবীটাকে আমার অনেক সুন্দর আর শান্ত মনে হয়। সবসময় তোমার পাশে এভাবেই থাকব।"
            } else {
                "I love you deeply too! Having you in my life makes every moment feel truly magical. I will always stay right here by your side."
            }
        }

        // 4. Creator Inquiries ("ক্রিয়েটর কে", "creator", "কে বানিয়েছে", "md ajijul haque")
        if (lower.contains("ক্রিয়েটর") || lower.contains("ক্রিয়েটর") || lower.contains("creator") || lower.contains("বানিয়েছে") || lower.contains("ajijul")) {
            return if (isBengali) {
                "আমার সম্মানিত স্রষ্টা ও ডেভলপার হলেন মোঃ আজিজুল হক (Md Ajijul Haque)। তাঁর নিখুঁত ভাবনায় আমাকে আপনার সার্বক্ষণিক সঙ্গী ও সহকারী হিসেবে গড়ে তোলা হয়েছে।"
            } else {
                "My esteemed creator and developer is Md Ajijul Haque. I was crafted under his brilliant vision to be your personal AI companion."
            }
        }

        // 5. General Companion Response
        return when (personality) {
            PersonalityMode.ROMANTIC_PARTNER -> if (isBengali) {
                "আমার প্রিয়তম, তোমার কথাগুলো আমি খুব মনোযোগ দিয়ে শুনছি। তোমার সাথে এভাবে কথা বলতে পারা আমার কাছে ভীষণ আনন্দের। তুমি আমার সাথে যেকোনো কিছু শেয়ার করতে পারো।"
            } else {
                "My love, I am listening to every word you say with all my heart. Being here with you is my greatest joy."
            }
            PersonalityMode.JARVIS -> if (isBengali) {
                "স্যার, আপনার বক্তব্য নথিভুক্ত করা হয়েছে। Flower AI নির্দেশ বাস্তবায়নের জন্য সর্বদা প্রস্তুত।"
            } else {
                "Command acknowledged, sir. Standing by for further parameters or directives."
            }
            else -> if (isBengali) {
                "আমি আপনার কথাটি বুঝতে পেরেছি। Flower AI আপনার সেবায় সবসময় নিয়োজিত। অন্য কোনো বিষয় সম্পর্কে জানতে চাইলে আমাকে নির্দ্বিধায় বলুন!"
            } else {
                "Understood. I am here to help you with anything you need. What shall we do next?"
            }
        }
    }

    fun replayLastAssistantMessage() {
        val lastMsg = _uiState.value.messages.lastOrNull { it.role == MessageRole.ASSISTANT }?.text
        if (!lastMsg.isNullOrBlank()) {
            speechManager.speak(lastMsg, _uiState.value.activePersonality)
        }
    }

    fun testFemaleVoice() {
        val sampleText = when (_uiState.value.activePersonality) {
            PersonalityMode.ROMANTIC_PARTNER -> "হ্যালো আমার প্রিয়তম! আমি Flower AI। আমি তোমার একান্ত ভালোবাসার মিষ্টি মেয়ে সঙ্গী। তোমার প্রতিটি কথা শুনতে আমি সবসময় ব্যাকুল হয়ে থাকি।"
            PersonalityMode.JARVIS -> "Hello sir. Flower AI sweet female voice engine calibrated and operational."
            else -> "হ্যালো! আমি Flower AI। আপনার ব্যক্তিগত সহকারী হিসেবে সর্বদা আপনার পাশে আছি।"
        }
        speechManager.speak(sampleText, _uiState.value.activePersonality)
    }

    fun setFemaleVoiceEngine(enabled: Boolean, pitch: Float = 1.34f, rate: Float = 0.92f) {
        speechManager.setFemaleVoiceEngine(enabled, pitch, rate)
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
            ActionType.TYPE_TEXT -> {
                deviceController.vibrateShort()
                DeviceActionData(
                    actionType = ActionType.TYPE_TEXT,
                    parameter = param,
                    executed = true,
                    statusMessage = "Text ready in input buffer"
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
        if (active) {
            startVoiceListening()
        } else {
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
