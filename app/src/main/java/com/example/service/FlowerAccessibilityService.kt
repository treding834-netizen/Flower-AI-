package com.example.service

import android.Manifest
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Path
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FlowerAccessibilityService : AccessibilityService() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private val mainHandler = Handler(Looper.getMainLooper())

    companion object {
        private val _currentScreenText = MutableStateFlow("")
        val currentScreenText: StateFlow<String> = _currentScreenText.asStateFlow()

        private val _isServiceActive = MutableStateFlow(false)
        val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()

        var instance: FlowerAccessibilityService? = null
            private set

        private var pendingTextToType: String? = null
        private var pendingClickSend: Boolean = false
        private var pendingTimestamp: Long = 0L
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        _isServiceActive.value = true
        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                FlowerVoiceForegroundService.startService(this)
            }
        } catch (_: Exception) {}
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        try {
            val rootNode = rootInActiveWindow
            if (rootNode != null) {
                val sb = StringBuilder()
                extractTextFromNode(rootNode, sb)
                val text = sb.toString().trim()
                if (text.isNotBlank()) {
                    _currentScreenText.value = text.take(1500)
                }
                rootNode.recycle()
            }
        } catch (_: Exception) {}

        // If there is a pending auto-type/send task within the last 8 seconds, attempt execution on window events
        val text = pendingTextToType
        if (!text.isNullOrBlank() && (System.currentTimeMillis() - pendingTimestamp) < 8000L) {
            executeAutoTypeAndSendNow(text, pendingClickSend)
        }
    }

    private fun extractTextFromNode(node: AccessibilityNodeInfo, sb: StringBuilder) {
        if (node.text != null && node.text.isNotBlank()) {
            sb.append(node.text).append(" ")
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                extractTextFromNode(child, sb)
                child.recycle()
            }
        }
    }

    override fun onInterrupt() {
        _isServiceActive.value = false
        instance = null
    }

    override fun onDestroy() {
        super.onDestroy()
        _isServiceActive.value = false
        instance = null
    }

    fun performHomeAction(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_HOME)
    }

    fun performBackAction(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_BACK)
    }

    fun performRecentsAction(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_RECENTS)
    }

    fun performNotificationsAction(): Boolean {
        return performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
    }

    fun tapAt(x: Float, y: Float): Boolean {
        val path = Path().apply {
            moveTo(x, y)
        }
        val stroke = GestureDescription.StrokeDescription(path, 0, 50)
        val desc = GestureDescription.Builder().addStroke(stroke).build()
        return dispatchGesture(desc, null, null)
    }

    /**
     * Schedule an intelligent auto-fill and auto-send task when an app (WhatsApp, YouTube, SMS) opens
     */
    fun scheduleAutoTypeAndSend(textToType: String, autoClickSend: Boolean = true) {
        pendingTextToType = textToType
        pendingClickSend = autoClickSend
        pendingTimestamp = System.currentTimeMillis()

        // Also copy text to Clipboard as instant fallback paste
        try {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("FlowerAI", textToType)
            clipboard.setPrimaryClip(clip)
        } catch (_: Exception) {}

        // Coroutine loop: attempt to find input field and click send every 300ms for 4 seconds
        serviceScope.launch {
            for (i in 1..12) {
                delay(320)
                val currentPending = pendingTextToType
                if (currentPending.isNullOrBlank()) break
                val success = executeAutoTypeAndSendNow(currentPending, pendingClickSend)
                if (success) {
                    pendingTextToType = null
                    break
                }
            }
        }
    }

    private fun executeAutoTypeAndSendNow(textToType: String, autoClickSend: Boolean): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        return try {
            val targetInput = findEditableInputNode(rootNode)
            if (targetInput != null) {
                targetInput.performAction(AccessibilityNodeInfo.ACTION_FOCUS)

                val args = Bundle().apply {
                    putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, textToType)
                }
                val setSuccess = targetInput.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args)
                targetInput.recycle()

                if (autoClickSend) {
                    mainHandler.postDelayed({
                        clickSendOrSearchBtn()
                    }, 350)
                }
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        } finally {
            rootNode.recycle()
        }
    }

    private fun findEditableInputNode(node: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        if (node.isEditable) return node

        val clsName = node.className?.toString() ?: ""
        if (clsName.contains("EditText", ignoreCase = true) ||
            clsName.contains("AutoCompleteTextView", ignoreCase = true) ||
            clsName.contains("TextField", ignoreCase = true)) {
            return node
        }

        val resId = node.viewIdResourceName?.lowercase() ?: ""
        if (resId.contains("input") || resId.contains("edit") || resId.contains("search") ||
            resId.contains("msg") || resId.contains("message") || resId.contains("entry") || resId.contains("text")) {
            if (node.isFocusable) return node
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                val found = findEditableInputNode(child)
                if (found != null) {
                    child.recycle()
                    return found
                }
                child.recycle()
            }
        }
        return null
    }

    fun clickSendOrSearchBtn(): Boolean {
        val rootNode = rootInActiveWindow ?: return false
        return try {
            val sendKeywords = listOf(
                "send", "Send", "পাঠান", "প্রেরণ", "Search", "search", "খুঁজুন", "submit", "Submit",
                "btn_send", "icon_send", "entry_send", "send_btn", "compose_send"
            )

            // Search by view id / content description / text
            val foundNode = findClickableNodeWithKeywords(rootNode, sendKeywords)
            if (foundNode != null) {
                val clicked = foundNode.performAction(AccessibilityNodeInfo.ACTION_CLICK)
                foundNode.recycle()
                if (clicked) return true
            }

            // Fallback gesture tap on bottom-right screen area (WhatsApp / SMS send button location)
            val displayMetrics = resources.displayMetrics
            val width = displayMetrics.widthPixels.toFloat()
            val height = displayMetrics.heightPixels.toFloat()
            tapAt(width * 0.91f, height * 0.58f) // WhatsApp send button coordinate
            tapAt(width * 0.90f, height * 0.91f) // Keyboard Enter / SMS Send button coordinate
            true
        } finally {
            rootNode.recycle()
        }
    }

    private fun findClickableNodeWithKeywords(node: AccessibilityNodeInfo, keywords: List<String>): AccessibilityNodeInfo? {
        val nodeText = node.text?.toString()?.lowercase() ?: ""
        val nodeDesc = node.contentDescription?.toString()?.lowercase() ?: ""
        val nodeRes = node.viewIdResourceName?.lowercase() ?: ""

        for (kw in keywords) {
            val kwLower = kw.lowercase()
            if (nodeText.contains(kwLower) || nodeDesc.contains(kwLower) || nodeRes.contains(kwLower)) {
                if (node.isClickable) return node
                var parent = node.parent
                while (parent != null) {
                    if (parent.isClickable) return parent
                    parent = parent.parent
                }
                return node
            }
        }

        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                val match = findClickableNodeWithKeywords(child, keywords)
                if (match != null) {
                    child.recycle()
                    return match
                }
                child.recycle()
            }
        }
        return null
    }
}
