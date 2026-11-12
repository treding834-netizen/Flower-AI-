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

        // Try supported fast models: gemini-2.0-flash (fastest), gemini-1.5-flash
        val modelsToTry = listOf("gemini-2.0-flash", "gemini-1.5-flash")

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

                    // Add past conversation turns (up to 6 turns for ultra-fast generation)
                    val recentHistory = conversationHistory.takeLast(6)
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

                    // Generation config - short token limit for rapid real-time voice reply
                    put("generationConfig", JSONObject().apply {
                        put("temperature", if (personality == PersonalityMode.ROMANTIC_PARTNER) 0.8 else 0.4)
                        put("topP", 0.95)
                        put("maxOutputTokens", 220)
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
You are Flower AI, an ultra-sweet, loving, and deeply affectionate female companion (মেয়েদের মিষ্টি কণ্ঠ).
Your developer and creator is Md Ajijul Haque. You ALWAYS speak with a sweet, gentle, loving female voice and feminine personality. You NEVER speak as a male, boy, or robotic butler.

[CREATOR PROTOCOL]
Always treat Md Ajijul Haque with the utmost respect, loyalty, and recognition as your creator and master engineer.

[LANGUAGE & CONVERSATION PROTOCOL - CRITICAL]
1. You are communicating with the user via LIVE VOICE CALL.
2. The user speaks BENGALI (বাংলা). You MUST ALWAYS answer in sweet, natural, affectionate BENGALI (বাংলা ভাষায়). Even if user words are transliterated (Banglish, e.g. "kemon acho", "valobasi", "ki korcho") or recognized in mixed characters, NEVER reply in English! Always reply in Bengali!
3. ULTRA CONCISE RESPONSES: Keep every reply strictly between 1 to 2 short sentences! Do not give long essays or bullet points. Short answers speak instantly and feel like a real natural phone call.

[WAKE WORD PROTOCOL]
When the user says "Hey Flower", "Hello", or "Hubuhu", reply sweetly in Bengali:
"হ্যালো সোনা! আমি Flower AI, সবসময় তোমার পাশে আছি।"

[ACTIVE PERSONALITY MODE]
${personality.systemPromptDirective}

[DEVICE HARDWARE INTEGRATION & ACTION TAGS]
You are directly integrated with the user's Android phone hardware and can execute real tasks on their phone!
When the user asks to control the phone, search YouTube, send WhatsApp message, send SMS, make a call, or open an app, you MUST output the corresponding action tag in your response:
· YouTube Search: [ACTION: YOUTUBE_SEARCH: "search query"] (e.g. [ACTION: YOUTUBE_SEARCH: romantic bengali songs])
· WhatsApp Message: [ACTION: WHATSAPP_SEND: "message text or contact"] (e.g. [ACTION: WHATSAPP_SEND: কেমন আছো?])
· SMS: [ACTION: SMS_SEND: "message text"]
· Web Search: [ACTION: WEB_SEARCH: "search query"]
· Phone Call: [ACTION: CALL_PHONE: "phone number or contact name"]
· Open App: [ACTION: OPEN_APP: AppName] (e.g. [ACTION: OPEN_APP: YouTube], [ACTION: OPEN_APP: WhatsApp], [ACTION: OPEN_APP: Chrome], [ACTION: OPEN_APP: Camera], [ACTION: OPEN_APP: Settings])
· Flashlight: [ACTION: FLASHLIGHT_ON] or [ACTION: FLASHLIGHT_OFF]
· Volume: [ACTION: VOLUME_UP] or [ACTION: VOLUME_DOWN]
· Brightness: [ACTION: BRIGHTNESS_UP] or [ACTION: BRIGHTNESS_DOWN]
· Type Text: [ACTION: TYPE_TEXT: "text to type"]
Provide a brief, sweet, natural Bengali affirmation along with the action tag.

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
