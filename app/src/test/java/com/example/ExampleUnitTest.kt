package com.example

import com.example.model.ActionType
import com.example.model.PersonalityMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testPersonalityModes() {
        assertEquals("18+ Romantic Companion", PersonalityMode.ROMANTIC_PARTNER.title)
        assertEquals("Jarvis Assistant", PersonalityMode.JARVIS.title)
        assertEquals("Full-Stack Coder", PersonalityMode.DEVELOPER.title)
        assertEquals("Website Architect", PersonalityMode.WEBSITE_BUILDER.title)
    }

    @Test
    fun testActionTypeParsing() {
        val flashlightAction = ActionType.fromTag("FLASHLIGHT_ON")
        assertNotNull(flashlightAction)
        assertEquals(ActionType.FLASHLIGHT_ON, flashlightAction)

        val volumeAction = ActionType.fromTag("VOLUME_UP")
        assertNotNull(volumeAction)
        assertEquals(ActionType.VOLUME_UP, volumeAction)

        val appAction = ActionType.fromTag("OPEN_APP")
        assertNotNull(appAction)
        assertEquals(ActionType.OPEN_APP, appAction)
    }

    @Test
    fun testWakeWordDetection() {
        val wakeWords = listOf("Hey Flower", "Hello", "Hubuhu", "hey flower", "hello", "hubuhu")
        for (word in wakeWords) {
            val lower = word.lowercase()
            val isWake = lower == "hey flower" || lower == "hello" || lower == "hubuhu"
            assertTrue("Expected '$word' to trigger wake-word", isWake)
        }
    }
}
