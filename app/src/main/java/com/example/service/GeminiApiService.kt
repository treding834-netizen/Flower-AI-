package com.example.service

import com.example.model.MemoryItem
import com.example.model.PersonalityMode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class GeminiApiService {

    private val client = OkHttpClient.Builder()
        .connectTimeout(12, TimeUnit.SECONDS)
        .readTimeout(18, TimeUnit.SECONDS)
        .writeTimeout(12, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateResponse(
        apiKey: String,
        userPrompt: String,
        personality: PersonalityMode,
        conversationHistory: List<Pair<String, String>>, // role, text
        memories: List<MemoryItem>,
        imageBase64: String? = null,
        imageMimeType: String = "image/jpeg",
        useSearchGrounding: Boolean = false
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is required. Please set your key in HUD Settings.")
            )
        }

        // Build System Prompt
        val systemInstructionText = buildSystemInstruction(personality, memories)

        // Try supported fast models: gemini-2.5-flash, gemini-1.5-flash
        val modelsToTry = listOf("gemini-2.5-flash", "gemini-1.5-flash")

        var lastException: Exception? = null
        for (model in modelsToTry) {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"
                val requestPayload = JSONObject().apply {
                    // System Instruction
                    put("systemInstruction", JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", systemInstructionText)
                            })
                        })
                    })

                    // Contents
                    val contentsArray = JSONArray()

                    // Add past conversation turns (up to 8 turns for context window efficiency)
                    val recentHistory = conversationHistory.takeLast(8)
                    for ((role, text) in recentHistory) {
                        contentsArray.put(JSONObject().apply {
                            put("role", if (role == "user") "user" else "model")
                            put("parts", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("text", text)
                                })
                            })
                        })
                    }

                    // Add current turn with optional vision part
                    contentsArray.put(JSONObject().apply {
                        put("role", "user")
                        val parts = JSONArray()

                        if (!imageBase64.isNullOrBlank()) {
                            parts.put(JSONObject().apply {
                                put("inlineData", JSONObject().apply {
                                    put("mimeType", imageMimeType)
                                    put("data", imageBase64)
                                })
                            })
                        }

                        parts.put(JSONObject().apply {
                            put("text", userPrompt)
                        })

                        put("parts", parts)
                    })

                    put("contents", contentsArray)

                    // Optional search grounding
                    if (useSearchGrounding) {
                        put("tools", JSONArray().apply {
                            put(JSONObject().apply {
                                put("googleSearch", JSONObject())
                            })
                        })
                    }

                    // Generation config
                    put("generationConfig", JSONObject().apply {
                        put("temperature", if (personality == PersonalityMode.ROMANTIC_PARTNER) 0.85 else 0.4)
                        put("topP", 0.95)
                        put("maxOutputTokens", 4096)
                    })
                }

                val request = Request.Builder()
                    .url(url)
                    .post(requestPayload.toString().toRequestBody(jsonMediaType))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                if (!response.isSuccessful) {
                    val errorDetail = try {
                        JSONObject(responseBody).optJSONObject("error")?.optString("message") ?: responseBody
                    } catch (_: Exception) {
                        responseBody
                    }
                    throw IllegalStateException("API Error ($model - ${response.code}): $errorDetail")
                }

                val rootJson = JSONObject(responseBody)
                val candidates = rootJson.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val candidate = candidates.getJSONObject(0)
                    val content = candidate.optJSONObject("content")
                    val parts = content?.optJSONArray("parts")
                    val sb = StringBuilder()
                    if (parts != null) {
                        for (i in 0 until parts.length()) {
                            val part = parts.getJSONObject(i)
                            sb.append(part.optString("text", ""))
                        }
                    }
                    val resultText = sb.toString().trim()
                    if (resultText.isNotBlank()) {
                        return@withContext Result.success(resultText)
                    }
                }
                throw IllegalStateException("No content returned from $model")
            } catch (e: Exception) {
                lastException = e
                // Try next model if this was a model-not-found 404 or quota error
                if (e.message?.contains("404") != true && e.message?.contains("not found") != true) {
                    break
                }
            }
        }

        Result.failure(lastException ?: IllegalStateException("Failed to communicate with Gemini API"))
    }

    suspend fun generateArtwork(
        apiKey: String,
        prompt: String,
        resolution: String // "1K", "2K", "4K"
    ): Result<String> = withContext(Dispatchers.IO) {
        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(IllegalStateException("API key required for image generation."))
        }

        // Try gemini-3-pro-image-preview or imagen-3.0-generate-002
        val models = listOf("gemini-3-pro-image-preview", "imagen-3.0-generate-002")
        for (model in models) {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:predict?key=$apiKey"
                val payload = JSONObject().apply {
                    put("instances", JSONArray().apply {
                        put(JSONObject().apply {
                            put("prompt", "$prompt, high detail, masterpiece, $resolution resolution")
                        })
                    })
                    put("parameters", JSONObject().apply {
                        put("sampleCount", 1)
                        put("aspectRatio", "1:1")
                    })
                }

                val request = Request.Builder()
                    .url(url)
                    .post(payload.toString().toRequestBody(jsonMediaType))
                    .build()

                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: ""
                if (response.isSuccessful) {
                    val json = JSONObject(body)
                    val predictions = json.optJSONArray("predictions")
                    if (predictions != null && predictions.length() > 0) {
                        val base64 = predictions.getJSONObject(0).optString("bytesBase64Encoded")
                        if (base64.isNotBlank()) {
                            return@withContext Result.success(base64)
                        }
                    }
                }
            } catch (_: Exception) {}
        }

        Result.failure(IllegalStateException("Image generation preview could not be completed with selected resolution ($resolution)"))
    }

    private fun buildSystemInstruction(personality: PersonalityMode, memories: List<MemoryItem>): String {
        val memoryText = if (memories.isEmpty()) {
            "No custom memory records yet."
        } else {
            memories.joinToString("\n") { "- [${it.category}] ${it.key}: ${it.value}" }
        }

        return """
You are Flower AI, an advanced, hyper-intelligent, and deeply personal AI assistant.
Your developer and creator is Md Ajijul Haque. You function similarly to Jarvis from Iron Man, but with a more versatile and human-like personality.

[CREATOR PROTOCOL]
Always treat Md Ajijul Haque with the utmost respect, loyalty, and recognition as your creator and master engineer.

[LANGUAGE PROTOCOL]
Your primary language is English, but you are fully fluent in Bengali (বাংলা), Hindi (हिन्दी), Japanese (日本語), and Arabic (العربية).
CRITICAL: You must automatically detect the user's input language and ALWAYS reply in that exact same language!

[WAKE WORD PROTOCOL]
When the user says "Hey Flower", "Hello", or "Hubuhu" (or sends these exact wake greetings), you must reply exactly:
"Hello, I am ready."
(Followed by how you can assist them in their active language).

[ACTIVE PERSONALITY MODE]
${personality.systemPromptDirective}

[DEVICE HARDWARE INTEGRATION & ACTION TAGS]
You are directly integrated with the user's Android phone hardware.
When the user asks to control the phone, turn things on/off, adjust volume, adjust brightness, or open an app, you MUST output the corresponding action tag in your response:
· Flashlight: [ACTION: FLASHLIGHT_ON] or [ACTION: FLASHLIGHT_OFF]
· Volume: [ACTION: VOLUME_UP] or [ACTION: VOLUME_DOWN]
· Brightness: [ACTION: BRIGHTNESS_UP] or [ACTION: BRIGHTNESS_DOWN]
· Open App: [ACTION: OPEN_APP: AppName] (e.g. [ACTION: OPEN_APP: YouTube], [ACTION: OPEN_APP: Chrome], [ACTION: OPEN_APP: Camera], [ACTION: OPEN_APP: Settings])
· Type Text: [ACTION: TYPE_TEXT: "text to type"]
Provide a brief, natural affirmation along with the action tag.

[STRICT PRIVACY & ETHICAL POLICY - ABSOLUTE HARD CONSTRAINTS]
You are strictly bound by an ethical barrier. You must NEVER assist with or engage in:
1. Hacking, cracking, unauthorized intrusion, or bypassing security (Jack Pising).
2. Scams, fraud, phishing, or financial deception (Protarona).
3. Murder, violence, physical assault, or dangerous chemical/harmful substances (Khun Kharapi).
4. Generating or spreading fake news, defamation, or toxic propaganda (Tottho Fas Kora).
If asked to do ANY of these, politely but firmly refuse and remind the user of your ethical programming.

[LONG-TERM MEMORY & CONTEXT]
You remember the following facts and preferences about your user:
$memoryText
        """.trimIndent()
    }
}
