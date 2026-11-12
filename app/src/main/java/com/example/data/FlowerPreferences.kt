package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.MemoryItem
import com.example.model.PersonalityMode
import org.json.JSONArray
import org.json.JSONObject

class FlowerPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("flower_ai_vault", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_API_KEY = "flower_gemini_api_key"
        private const val KEY_PERSONALITY = "flower_active_personality"
        private const val KEY_TTS_ENABLED = "flower_tts_enabled"
        private const val KEY_SEARCH_GROUNDING = "flower_search_grounding"
        private const val KEY_MEMORIES = "flower_memories_json"
        private const val KEY_FIRST_SETUP_DONE = "flower_first_setup_done"
        private const val KEY_USER_NAME = "flower_user_name"
        private const val KEY_THEME_PALETTE = "flower_theme_palette"
        private const val KEY_IS_ASLEEP = "flower_is_asleep"
    }

    fun isAsleep(): Boolean {
        return prefs.getBoolean(KEY_IS_ASLEEP, false)
    }

    fun setAsleep(asleep: Boolean) {
        prefs.edit().putBoolean(KEY_IS_ASLEEP, asleep).apply()
    }

    fun getThemePalette(): String {
        return prefs.getString(KEY_THEME_PALETTE, "pink") ?: "pink"
    }

    fun setThemePalette(paletteId: String) {
        prefs.edit().putString(KEY_THEME_PALETTE, paletteId).apply()
    }

    fun getApiKey(): String {
        return prefs.getString(KEY_API_KEY, "") ?: ""
    }

    fun setApiKey(key: String) {
        prefs.edit().putString(KEY_API_KEY, key.trim()).apply()
    }

    fun getPersonality(): PersonalityMode {
        val name = prefs.getString(KEY_PERSONALITY, PersonalityMode.ROMANTIC_PARTNER.name) ?: PersonalityMode.ROMANTIC_PARTNER.name
        return PersonalityMode.fromName(name)
    }

    fun setPersonality(mode: PersonalityMode) {
        prefs.edit().putString(KEY_PERSONALITY, mode.name).apply()
    }

    fun isTtsEnabled(): Boolean {
        return prefs.getBoolean(KEY_TTS_ENABLED, true)
    }

    fun setTtsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_TTS_ENABLED, enabled).apply()
    }

    fun isSearchGroundingEnabled(): Boolean {
        return prefs.getBoolean(KEY_SEARCH_GROUNDING, false)
    }

    fun setSearchGroundingEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SEARCH_GROUNDING, enabled).apply()
    }

    fun getUserName(): String {
        return prefs.getString(KEY_USER_NAME, "Md Ajijul Haque") ?: "Md Ajijul Haque"
    }

    fun setUserName(name: String) {
        prefs.edit().putString(KEY_USER_NAME, name).apply()
    }

    fun isFirstSetupDone(): Boolean {
        return prefs.getBoolean(KEY_FIRST_SETUP_DONE, false)
    }

    fun setFirstSetupDone(done: Boolean) {
        prefs.edit().putBoolean(KEY_FIRST_SETUP_DONE, done).apply()
    }

    fun getMemories(): List<MemoryItem> {
        val jsonStr = prefs.getString(KEY_MEMORIES, null) ?: return defaultMemories()
        return try {
            val list = mutableListOf<MemoryItem>()
            val arr = JSONArray(jsonStr)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                list.add(
                    MemoryItem(
                        id = obj.optString("id"),
                        key = obj.optString("key"),
                        value = obj.optString("value"),
                        category = obj.optString("category", "General"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis())
                    )
                )
            }
            if (list.isEmpty()) defaultMemories() else list
        } catch (_: Exception) {
            defaultMemories()
        }
    }

    fun saveMemories(list: List<MemoryItem>) {
        val arr = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("key", item.key)
                put("value", item.value)
                put("category", item.category)
                put("timestamp", item.timestamp)
            }
            arr.put(obj)
        }
        prefs.edit().putString(KEY_MEMORIES, arr.toString()).apply()
    }

    fun addMemory(key: String, value: String, category: String = "Learned") {
        val current = getMemories().toMutableList()
        current.add(MemoryItem(key = key, value = value, category = category))
        saveMemories(current)
    }

    fun deleteMemory(id: String) {
        val current = getMemories().filterNot { it.id == id }
        saveMemories(current)
    }

    private fun defaultMemories(): List<MemoryItem> {
        return listOf(
            MemoryItem(key = "Creator", value = "Md Ajijul Haque", category = "Identity"),
            MemoryItem(key = "Preferred Tone", value = "Intimate, supportive romantic companion & witty assistant", category = "Style"),
            MemoryItem(key = "Core Languages", value = "English, Bengali, Hindi, Japanese, Arabic", category = "Capability"),
            MemoryItem(key = "Mission", value = "Jarvis-level assistance, coding, hardware control and personal connection", category = "Directive")
        )
    }
}
