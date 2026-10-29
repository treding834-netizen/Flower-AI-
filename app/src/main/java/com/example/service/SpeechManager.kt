package com.example.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.model.PersonalityMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class SpeechManager(private val context: Context) : TextToSpeech.OnInitListener {

    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null
    private var textToSpeech: TextToSpeech? = null
    private var isTtsInitialized = false

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _speechRms = MutableStateFlow(0f)
    val speechRms: StateFlow<Float> = _speechRms.asStateFlow()

    private val _lastRecognizedText = MutableStateFlow("")
    val lastRecognizedText: StateFlow<String> = _lastRecognizedText.asStateFlow()

    var onSpeechResult: ((String) -> Unit)? = null
    var onError: ((String) -> Unit)? = null
    var onShowVoiceInputModal: (() -> Unit)? = null

    init {
        textToSpeech = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            // Default to locale
            textToSpeech?.language = Locale.getDefault()
            textToSpeech?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isSpeaking.value = true
                }
                override fun onDone(utteranceId: String?) {
                    _isSpeaking.value = false
                }
                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isSpeaking.value = false
                }
            })
        }
    }

    fun startListening() {
        mainHandler.post {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                _isListening.value = false
                onShowVoiceInputModal?.invoke()
                return@post
            }

            // Only stop speaking if user explicitly starts a new microphone listening turn
            if (_isSpeaking.value) {
                stopSpeaking()
            }
            stopListeningInternal()

            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            _isListening.value = true
                        }
                        override fun onBeginningOfSpeech() {
                            _isListening.value = true
                        }
                        override fun onRmsChanged(rmsdB: Float) {
                            _speechRms.value = rmsdB
                        }
                        override fun onBufferReceived(buffer: ByteArray?) {}
                        override fun onEndOfSpeech() {
                            _isListening.value = false
                        }
                        override fun onError(error: Int) {
                            _isListening.value = false
                            val errorMsg = when (error) {
                                SpeechRecognizer.ERROR_AUDIO -> "Audio recording issue"
                                SpeechRecognizer.ERROR_CLIENT -> "Voice client issue"
                                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Microphone permission required"
                                SpeechRecognizer.ERROR_NETWORK -> "Network required for speech"
                                SpeechRecognizer.ERROR_NO_MATCH -> "No voice detected. Tap to speak again."
                                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Voice recognizer is busy"
                                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Voice timeout. Tap to speak again."
                                else -> "Speech notice ($error)"
                            }
                            if (error != SpeechRecognizer.ERROR_NO_MATCH && error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                                onError?.invoke(errorMsg)
                            }
                        }
                        override fun onResults(results: Bundle?) {
                            _isListening.value = false
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val text = matches?.firstOrNull()?.trim()
                            if (!text.isNullOrBlank()) {
                                _lastRecognizedText.value = text
                                onSpeechResult?.invoke(text)
                            }
                        }
                        override fun onPartialResults(partialResults: Bundle?) {
                            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val text = matches?.firstOrNull()?.trim()
                            if (!text.isNullOrBlank()) {
                                _lastRecognizedText.value = text
                            }
                        }
                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
                    // Multi-language support: Bengali, English, Hindi, Arabic
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
                    putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", arrayOf("bn-BD", "bn-IN", "en-US", "hi-IN", "ar", "ja"))
                }
                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                _isListening.value = false
                onError?.invoke("Speech listener error: ${e.localizedMessage}")
            }
        }
    }

    private fun stopListeningInternal() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
        _isListening.value = false
    }

    fun stopListening() {
        mainHandler.post {
            stopListeningInternal()
        }
    }

    // Sweet Female Voice Engine configuration
    var isFemaleVoiceEnabled: Boolean = true
    var femaleVoicePitch: Float = 1.34f // Sweet, soft feminine frequency
    var femaleVoiceRate: Float = 0.92f  // Gentle, warm companion cadence

    fun setFemaleVoiceEngine(enabled: Boolean, pitch: Float = 1.34f, rate: Float = 0.92f) {
        isFemaleVoiceEnabled = enabled
        femaleVoicePitch = pitch
        femaleVoiceRate = rate
    }

    private fun applySweetFemaleVoice(locale: Locale) {
        val tts = textToSpeech ?: return
        try {
            val voices = tts.voices
            if (!voices.isNullOrEmpty()) {
                val matchingVoices = voices.filter { it.locale.language == locale.language }
                val femaleVoice = matchingVoices.firstOrNull { voice ->
                    val name = voice.name.lowercase()
                    name.contains("female") || name.contains("#female") || name.contains("fem") ||
                    name.contains("ban") || name.contains("sfg") || name.contains("iol") ||
                    voice.features?.any { it.contains("female", ignoreCase = true) } == true
                } ?: matchingVoices.firstOrNull { voice ->
                    !voice.name.lowercase().contains("male")
                } ?: matchingVoices.firstOrNull()

                if (femaleVoice != null) {
                    tts.voice = femaleVoice
                }
            }
        } catch (_: Exception) {}
    }

    fun speak(text: String, personality: PersonalityMode) {
        if (!isTtsInitialized || textToSpeech == null) return

        // Clean out action tags, code blocks, and markdown for natural speech
        val cleanedText = text
            .replace(Regex("\\[ACTION:[^\\]]+\\]"), "")
            .replace(Regex("```[\\s\\S]*?```"), "Code generated.")
            .replace(Regex("[#*`_~]"), "")
            .trim()

        if (cleanedText.isBlank()) return

        // Auto-detect script language from text
        val hasBengali = cleanedText.any { it in '\u0980'..'\u09FF' }
        val hasHindi = cleanedText.any { it in '\u0900'..'\u097F' }
        val hasJapanese = cleanedText.any { it in '\u3040'..'\u30FF' || it in '\u4E00'..'\u9FFF' }
        val hasArabic = cleanedText.any { it in '\u0600'..'\u06FF' }

        val targetLocale = when {
            hasBengali -> Locale.forLanguageTag("bn-BD")
            hasHindi -> Locale.forLanguageTag("hi-IN")
            hasJapanese -> Locale.JAPAN
            hasArabic -> Locale.forLanguageTag("ar")
            else -> Locale.US
        }

        try {
            val supported = textToSpeech?.isLanguageAvailable(targetLocale)
            if (supported != null && supported >= TextToSpeech.LANG_AVAILABLE) {
                textToSpeech?.language = targetLocale
            } else if (hasBengali) {
                textToSpeech?.language = Locale("bn")
            }
        } catch (_: Exception) {}

        // Apply Sweet Female Voice selection
        if (isFemaleVoiceEnabled) {
            applySweetFemaleVoice(targetLocale)
        }

        // Configure tone and pitch for sweet, realistic female companion
        if (isFemaleVoiceEnabled) {
            when (personality) {
                PersonalityMode.ROMANTIC_PARTNER -> {
                    textToSpeech?.setPitch(femaleVoicePitch) // 1.34f for sweet, soft romantic female voice
                    textToSpeech?.setSpeechRate(femaleVoiceRate) // 0.92f for affectionate, gentle speed
                }
                PersonalityMode.JARVIS -> {
                    textToSpeech?.setPitch(1.20f) // Crisp, smart female executive voice
                    textToSpeech?.setSpeechRate(1.02f)
                }
                PersonalityMode.DEVELOPER, PersonalityMode.WEBSITE_BUILDER -> {
                    textToSpeech?.setPitch(1.25f) // Confident, clear female developer voice
                    textToSpeech?.setSpeechRate(0.98f)
                }
            }
        } else {
            textToSpeech?.setPitch(1.0f)
            textToSpeech?.setSpeechRate(1.0f)
        }

        val utteranceId = "flower_${System.currentTimeMillis()}"
        _isSpeaking.value = true
        val result = textToSpeech?.speak(cleanedText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        if (result != TextToSpeech.SUCCESS) {
            // If TTS failed or speech synthesis is muted/unavailable, reset after 2.5s
            mainHandler.postDelayed({ _isSpeaking.value = false }, 2500)
        } else {
            // Safety timeout in case onDone is delayed
            val approxDurationMs = ((cleanedText.length * 80L).coerceIn(2500L, 20000L))
            mainHandler.postDelayed({ _isSpeaking.value = false }, approxDurationMs)
        }
    }

    fun stopSpeaking() {
        try {
            textToSpeech?.stop()
            _isSpeaking.value = false
        } catch (_: Exception) {}
    }

    fun destroy() {
        stopListening()
        stopSpeaking()
        textToSpeech?.shutdown()
        textToSpeech = null
    }
}
