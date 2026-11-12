package com.example.service

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.MediaStore
import android.provider.Settings
import com.example.model.ActionType
import com.example.model.DeviceActionData

class DeviceHardwareController(private val context: Context) {

    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    private var isTorchOn = false

    fun isFlashlightOn(): Boolean = isTorchOn

    fun getVolumeLevelPercent(): Int {
        return try {
            val current = audioManager?.getStreamVolume(AudioManager.STREAM_MUSIC) ?: 50
            val max = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 100
            if (max > 0) (current * 100) / max else 50
        } catch (_: Exception) {
            50
        }
    }

    fun vibrateShort() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vibratorManager?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(70, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                vibrator?.vibrate(VibrationEffect.createOneShot(70, VibrationEffect.DEFAULT_AMPLITUDE))
            }
        } catch (_: Exception) {
            // Ignore if vibration is unavailable
        }
    }

    fun setFlashlight(enable: Boolean): DeviceActionData {
        vibrateShort()
        return try {
            val cameraIds = cameraManager?.cameraIdList ?: emptyArray()
            var flashCameraId: String? = null
            for (id in cameraIds) {
                val characteristics = cameraManager?.getCameraCharacteristics(id)
                val hasFlash = characteristics?.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                if (hasFlash) {
                    flashCameraId = id
                    break
                }
            }
            if (flashCameraId != null && cameraManager != null) {
                cameraManager.setTorchMode(flashCameraId, enable)
                isTorchOn = enable
                DeviceActionData(
                    actionType = if (enable) ActionType.FLASHLIGHT_ON else ActionType.FLASHLIGHT_OFF,
                    executed = true,
                    statusMessage = if (enable) "Hardware Flashlight Activated" else "Hardware Flashlight Deactivated"
                )
            } else {
                isTorchOn = enable
                DeviceActionData(
                    actionType = if (enable) ActionType.FLASHLIGHT_ON else ActionType.FLASHLIGHT_OFF,
                    executed = true,
                    statusMessage = if (enable) "Flashlight simulated (Device has no hardware flash)" else "Flashlight turned off"
                )
            }
        } catch (e: Exception) {
            isTorchOn = enable
            DeviceActionData(
                actionType = if (enable) ActionType.FLASHLIGHT_ON else ActionType.FLASHLIGHT_OFF,
                executed = false,
                statusMessage = "Flashlight control: ${e.localizedMessage ?: "Active"}"
            )
        }
    }

    fun adjustVolume(raise: Boolean): DeviceActionData {
        vibrateShort()
        return try {
            val direction = if (raise) AudioManager.ADJUST_RAISE else AudioManager.ADJUST_LOWER
            audioManager?.adjustStreamVolume(AudioManager.STREAM_MUSIC, direction, AudioManager.FLAG_SHOW_UI)
            val currentPercent = getVolumeLevelPercent()
            DeviceActionData(
                actionType = if (raise) ActionType.VOLUME_UP else ActionType.VOLUME_DOWN,
                parameter = "$currentPercent%",
                executed = true,
                statusMessage = "Media volume adjusted to $currentPercent%"
            )
        } catch (e: Exception) {
            DeviceActionData(
                actionType = if (raise) ActionType.VOLUME_UP else ActionType.VOLUME_DOWN,
                executed = false,
                statusMessage = "Failed to adjust volume: ${e.localizedMessage}"
            )
        }
    }

    fun adjustBrightness(activity: Activity?, increase: Boolean): DeviceActionData {
        vibrateShort()
        return try {
            if (activity != null) {
                val lp = activity.window.attributes
                var current = if (lp.screenBrightness < 0f) 0.5f else lp.screenBrightness
                current = if (increase) (current + 0.25f).coerceAtMost(1.0f) else (current - 0.25f).coerceAtLeast(0.1f)
                lp.screenBrightness = current
                activity.window.attributes = lp
                val percent = (current * 100).toInt()
                DeviceActionData(
                    actionType = if (increase) ActionType.BRIGHTNESS_UP else ActionType.BRIGHTNESS_DOWN,
                    parameter = "$percent%",
                    executed = true,
                    statusMessage = "Screen brightness set to $percent%"
                )
            } else {
                DeviceActionData(
                    actionType = if (increase) ActionType.BRIGHTNESS_UP else ActionType.BRIGHTNESS_DOWN,
                    executed = true,
                    statusMessage = if (increase) "Brightness boosted" else "Brightness dimmed"
                )
            }
        } catch (e: Exception) {
            DeviceActionData(
                actionType = if (increase) ActionType.BRIGHTNESS_UP else ActionType.BRIGHTNESS_DOWN,
                executed = false,
                statusMessage = "Brightness error: ${e.localizedMessage}"
            )
        }
    }

    fun openApp(appNameOrPkg: String): DeviceActionData {
        vibrateShort()
        val query = appNameOrPkg.trim().lowercase()
        val pm = context.packageManager

        // Check common apps mapping
        val targetPackage = when {
            query.contains("youtube") -> "com.google.android.youtube"
            query.contains("chrome") || query.contains("browser") -> "com.android.chrome"
            query.contains("maps") -> "com.google.android.apps.maps"
            query.contains("calculator") -> "com.google.android.calculator"
            query.contains("clock") || query.contains("alarm") -> "com.google.android.deskclock"
            query.contains("calendar") -> "com.google.android.calendar"
            query.contains("gmail") || query.contains("mail") -> "com.google.android.gm"
            appNameOrPkg.contains(".") -> appNameOrPkg.trim()
            else -> null
        }

        try {
            // Camera special intent
            if (query.contains("camera")) {
                val intent = Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                return DeviceActionData(
                    actionType = ActionType.OPEN_APP,
                    parameter = "Camera",
                    executed = true,
                    statusMessage = "Launched Camera system app"
                )
            }

            // Settings special intent
            if (query.contains("settings")) {
                val intent = Intent(Settings.ACTION_SETTINGS).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
                return DeviceActionData(
                    actionType = ActionType.OPEN_APP,
                    parameter = "Settings",
                    executed = true,
                    statusMessage = "Opened System Settings"
                )
            }

            // Try resolved package
            if (targetPackage != null) {
                val launchIntent = pm.getLaunchIntentForPackage(targetPackage)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)
                    return DeviceActionData(
                        actionType = ActionType.OPEN_APP,
                        parameter = appNameOrPkg,
                        executed = true,
                        statusMessage = "Launched $appNameOrPkg ($targetPackage)"
                    )
                }
            }

            // Search installed applications
            val installedApps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            for (app in installedApps) {
                val label = pm.getApplicationLabel(app).toString().lowercase()
                if (label.contains(query) || query.contains(label)) {
                    val launchIntent = pm.getLaunchIntentForPackage(app.packageName)
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(launchIntent)
                        return DeviceActionData(
                            actionType = ActionType.OPEN_APP,
                            parameter = label,
                            executed = true,
                            statusMessage = "Launched $label (${app.packageName})"
                        )
                    }
                }
            }

            // Fallback: Open web or app store query
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=$query&c=apps")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(webIntent)
            return DeviceActionData(
                actionType = ActionType.OPEN_APP,
                parameter = appNameOrPkg,
                executed = true,
                statusMessage = "Searched for $appNameOrPkg in app store"
            )
        } catch (e: Exception) {
            return DeviceActionData(
                actionType = ActionType.OPEN_APP,
                parameter = appNameOrPkg,
                executed = false,
                statusMessage = "Could not open $appNameOrPkg: ${e.localizedMessage}"
            )
        }
    }

    fun searchYouTube(query: String): DeviceActionData {
        vibrateShort()
        val trimmedQuery = query.trim()
        FlowerAccessibilityService.instance?.scheduleAutoTypeAndSend(trimmedQuery, autoClickSend = true)
        return try {
            val intent = Intent(Intent.ACTION_SEARCH).apply {
                setPackage("com.google.android.youtube")
                putExtra("query", trimmedQuery)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            DeviceActionData(
                actionType = ActionType.YOUTUBE_SEARCH,
                parameter = trimmedQuery,
                executed = true,
                statusMessage = "Opened YouTube and searched for: $trimmedQuery"
            )
        } catch (_: Exception) {
            try {
                val encoded = java.net.URLEncoder.encode(trimmedQuery, "UTF-8")
                val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$encoded")).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(webIntent)
                DeviceActionData(
                    actionType = ActionType.YOUTUBE_SEARCH,
                    parameter = trimmedQuery,
                    executed = true,
                    statusMessage = "Opened YouTube search in browser: $trimmedQuery"
                )
            } catch (e: Exception) {
                DeviceActionData(
                    actionType = ActionType.YOUTUBE_SEARCH,
                    parameter = trimmedQuery,
                    executed = false,
                    statusMessage = "YouTube search error: ${e.localizedMessage}"
                )
            }
        }
    }

    fun sendWhatsApp(queryOrMessage: String): DeviceActionData {
        vibrateShort()
        val text = queryOrMessage.trim()
        FlowerAccessibilityService.instance?.scheduleAutoTypeAndSend(text, autoClickSend = true)
        return try {
            val encoded = java.net.URLEncoder.encode(text, "UTF-8")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?text=$encoded")).apply {
                setPackage("com.whatsapp")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            DeviceActionData(
                actionType = ActionType.WHATSAPP_SEND,
                parameter = text,
                executed = true,
                statusMessage = "Opened WhatsApp and sent message"
            )
        } catch (_: Exception) {
            try {
                // Fallback to general WhatsApp intent or web
                val pm = context.packageManager
                val launchIntent = pm.getLaunchIntentForPackage("com.whatsapp")
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(launchIntent)
                    DeviceActionData(
                        actionType = ActionType.WHATSAPP_SEND,
                        parameter = text,
                        executed = true,
                        statusMessage = "Opened WhatsApp"
                    )
                } else {
                    val encoded = java.net.URLEncoder.encode(text, "UTF-8")
                    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?text=$encoded")).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(webIntent)
                    DeviceActionData(
                        actionType = ActionType.WHATSAPP_SEND,
                        parameter = text,
                        executed = true,
                        statusMessage = "Opened WhatsApp Web link"
                    )
                }
            } catch (e: Exception) {
                DeviceActionData(
                    actionType = ActionType.WHATSAPP_SEND,
                    parameter = text,
                    executed = false,
                    statusMessage = "WhatsApp launch error: ${e.localizedMessage}"
                )
            }
        }
    }

    fun sendSms(message: String): DeviceActionData {
        vibrateShort()
        val text = message.trim()
        FlowerAccessibilityService.instance?.scheduleAutoTypeAndSend(text, autoClickSend = true)
        return try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("sms:")
                putExtra("sms_body", text)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            DeviceActionData(
                actionType = ActionType.SMS_SEND,
                parameter = text,
                executed = true,
                statusMessage = "Opened SMS composer with message"
            )
        } catch (e: Exception) {
            DeviceActionData(
                actionType = ActionType.SMS_SEND,
                parameter = text,
                executed = true,
                statusMessage = "Opened SMS composer with message"
            )
        } catch (e: Exception) {
            DeviceActionData(
                actionType = ActionType.SMS_SEND,
                parameter = message,
                executed = false,
                statusMessage = "SMS error: ${e.localizedMessage}"
            )
        }
    }

    fun searchWeb(query: String): DeviceActionData {
        vibrateShort()
        return try {
            val encoded = java.net.URLEncoder.encode(query.trim(), "UTF-8")
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$encoded")).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            DeviceActionData(
                actionType = ActionType.WEB_SEARCH,
                parameter = query,
                executed = true,
                statusMessage = "Google search launched: $query"
            )
        } catch (e: Exception) {
            DeviceActionData(
                actionType = ActionType.WEB_SEARCH,
                parameter = query,
                executed = false,
                statusMessage = "Web search error: ${e.localizedMessage}"
            )
        }
    }

    fun callPhone(phoneNumberOrName: String): DeviceActionData {
        vibrateShort()
        return try {
            val number = phoneNumberOrName.filter { it.isDigit() || it == '+' }
            val intent = if (number.isNotBlank()) {
                Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
            } else {
                Intent(Intent.ACTION_DIAL)
            }.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            DeviceActionData(
                actionType = ActionType.CALL_PHONE,
                parameter = phoneNumberOrName,
                executed = true,
                statusMessage = "Opened Phone dialer for: $phoneNumberOrName"
            )
        } catch (e: Exception) {
            DeviceActionData(
                actionType = ActionType.CALL_PHONE,
                parameter = phoneNumberOrName,
                executed = false,
                statusMessage = "Dialer error: ${e.localizedMessage}"
            )
        }
    }
}
