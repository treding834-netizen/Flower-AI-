package com.example.ui

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.core.content.ContextCompat
import com.example.model.NavigationTab
import com.example.ui.components.AnimeBottomNavBar
import com.example.ui.dialogs.AiModeSelectionDialog
import com.example.ui.dialogs.ApiKeyDialog
import com.example.ui.dialogs.ImageGenerationDialog
import com.example.ui.dialogs.MemoryVaultDialog
import com.example.ui.dialogs.ThemeSelectionDialog
import com.example.ui.dialogs.VoiceInputDialog
import com.example.ui.dialogs.VoiceKeyboardDialog
import com.example.ui.dialogs.VoiceRoomsDialog
import com.example.ui.dialogs.VoiceSelectionDialog
import com.example.ui.dialogs.WebsitePreviewDialog
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveVoiceCallingScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AnimeDeepSpace

@Composable
fun FlowerAiScreen(
    viewModel: FlowerAiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Multi-permission launcher for Record Audio & Post Notifications
    val permissionsLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isAudioGranted = permissions[Manifest.permission.RECORD_AUDIO] == true
        if (isAudioGranted) {
            viewModel.setLiveVoiceCalling(true)
        } else {
            Toast.makeText(context, "Microphone permission is required for voice calling", Toast.LENGTH_SHORT).show()
        }
    }

    // Auto-request microphone & notification permissions on first launch
    LaunchedEffect(Unit) {
        val hasAudioPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasAudioPermission) {
            val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
            if (android.os.Build.VERSION.SDK_INT >= 33) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
            permissionsLauncher.launch(permissions.toTypedArray())
        }
    }

    // Handle toast notification
    LaunchedEffect(uiState.statusNotification) {
        val note = uiState.statusNotification
        if (!note.isNullOrBlank()) {
            Toast.makeText(context, note, Toast.LENGTH_SHORT).show()
            viewModel.clearNotification()
        }
    }

    // Back handler
    BackHandler(
        enabled = uiState.isLiveVoiceCallingActive ||
                uiState.previewWebsiteHtml != null ||
                uiState.showApiKeyDialog ||
                uiState.showImageGenDialog ||
                uiState.showMemoryVaultDialog ||
                uiState.currentTab != NavigationTab.HOME
    ) {
        when {
            uiState.isLiveVoiceCallingActive -> viewModel.setLiveVoiceCalling(false)
            uiState.previewWebsiteHtml != null -> viewModel.closeWebsitePreview()
            uiState.showApiKeyDialog -> viewModel.setShowApiKeyDialog(false)
            uiState.showImageGenDialog -> viewModel.setShowImageGenDialog(false)
            uiState.showMemoryVaultDialog -> viewModel.setShowMemoryVaultDialog(false)
            uiState.currentTab != NavigationTab.HOME -> viewModel.setTab(NavigationTab.HOME)
        }
    }

    // Full-Screen Live Voice Calling View
    if (uiState.isLiveVoiceCallingActive) {
        val lastAssistantMessage = uiState.messages.lastOrNull { it.role == com.example.model.MessageRole.ASSISTANT }?.text
        LiveVoiceCallingScreen(
            isListening = uiState.isListening,
            isSpeaking = uiState.isSpeaking,
            isGenerating = uiState.isGenerating,
            audioRms = uiState.audioRms,
            isMuted = uiState.isMuted,
            lastSpeechText = uiState.lastRecognizedText,
            latestAssistantResponse = lastAssistantMessage,
            onToggleMute = { viewModel.toggleMute() },
            onTapToSpeak = { viewModel.startVoiceListening() },
            onQuickVoicePrompt = { prompt -> viewModel.handleUserInput(prompt) },
            onReplaySpeech = { viewModel.replayLastAssistantMessage() },
            onEndCall = { viewModel.setLiveVoiceCalling(false) },
            onMinimize = { viewModel.setLiveVoiceCalling(false) },
            modifier = Modifier.testTag("live_voice_calling_screen")
        )
    } else {
        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .statusBarsPadding()
                .testTag("flower_ai_main_screen"),
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = AnimeDeepSpace,
            bottomBar = {
                AnimeBottomNavBar(
                    selectedTab = uiState.currentTab,
                    onTabSelected = { tab -> viewModel.setTab(tab) },
                    onCenterOrbClick = {
                        val hasAudioPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.RECORD_AUDIO
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasAudioPermission) {
                            viewModel.setLiveVoiceCalling(true)
                        } else {
                            permissionsLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                        }
                    },
                    isVoiceActive = uiState.isListening || uiState.isSpeaking
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AnimeDeepSpace)
            ) {
                Crossfade(
                    targetState = uiState.currentTab,
                    label = "tab_crossfade"
                ) { tab ->
                    when (tab) {
                        NavigationTab.HOME -> {
                            HomeScreen(
                                apiKey = uiState.apiKey,
                                activePersonality = uiState.activePersonality,
                                isFlashlightOn = uiState.isFlashlightOn,
                                isAsleep = uiState.isAsleep,
                                isListening = uiState.isListening,
                                isSpeaking = uiState.isSpeaking,
                                lastRecognizedText = uiState.lastRecognizedText,
                                activePalette = uiState.activePalette,
                                onSelectPersonality = { mode -> viewModel.setPersonality(mode) },
                                onSelectPalette = { palette -> viewModel.setThemePalette(palette) },
                                onToggleFlashlight = { viewModel.toggleFlashlight() },
                                onAdjustVolume = { raise -> viewModel.adjustVolume(raise) },
                                onOpenApiKeyDialog = { viewModel.setShowApiKeyDialog(true) },
                                onOpenAiModeDialog = { viewModel.setShowAiModeDialog(true) },
                                onOpenMemoryVault = { viewModel.setShowMemoryVaultDialog(true) },
                                onNavigateTab = { targetTab -> viewModel.setTab(targetTab) },
                                onStartVoiceCall = {
                                    val hasAudioPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (hasAudioPermission) {
                                        viewModel.setLiveVoiceCalling(true)
                                    } else {
                                        permissionsLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                                    }
                                },
                                onTriggerVoiceAction = { prompt ->
                                    val hasAudioPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (!hasAudioPermission) {
                                        permissionsLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                                    }
                                    viewModel.setLiveVoiceCalling(true)
                                    viewModel.handleUserInput(prompt)
                                },
                                onTestFemaleVoice = { viewModel.testFemaleVoice() },
                                onOpenSettings = { viewModel.setTab(NavigationTab.SETTINGS) }
                            )
                        }
                        NavigationTab.CHAT -> {
                            ChatScreen(
                                messages = uiState.messages,
                                activePersonality = uiState.activePersonality,
                                isGenerating = uiState.isGenerating,
                                onSendMessage = { text -> viewModel.handleUserInput(text) },
                                onSpeakMessage = { text -> viewModel.speakText(text) },
                                onRetryLastMessage = { viewModel.retryLastMessage() },
                                onStartVoiceInput = {
                                    val hasAudioPermission = ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                    if (hasAudioPermission) {
                                        viewModel.setLiveVoiceCalling(true)
                                    } else {
                                        permissionsLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                                    }
                                },
                                onBackToHome = { viewModel.setTab(NavigationTab.HOME) },
                                onNewConversation = { viewModel.newConversation() },
                                onOpenWebsitePreview = { html -> viewModel.openWebsitePreview(html) }
                            )
                        }
                        NavigationTab.HISTORY -> {
                            HistoryScreen(
                                messages = uiState.messages,
                                onSelectConversation = {
                                    viewModel.setTab(NavigationTab.CHAT)
                                }
                            )
                        }
                        NavigationTab.SETTINGS -> {
                            SettingsScreen(
                                apiKey = uiState.apiKey,
                                activePersonality = uiState.activePersonality,
                                isFlashlightOn = uiState.isFlashlightOn,
                                onOpenApiKeyDialog = { viewModel.setShowApiKeyDialog(true) },
                                onOpenMemoryVault = { viewModel.setShowMemoryVaultDialog(true) },
                                onOpenAiModeDialog = { viewModel.setShowAiModeDialog(true) },
                                onClearHistory = { viewModel.clearHistory() },
                                onCyclePersonality = { viewModel.cyclePersonality() },
                                onToggleFlashlight = { viewModel.toggleFlashlight() },
                                onAdjustVolume = { raise -> viewModel.adjustVolume(raise) },
                                onTestFemaleVoice = { viewModel.testFemaleVoice() },
                                onOpenVoiceSelection = { viewModel.setShowVoiceSelectionDialog(true) },
                                onOpenThemeSelection = { viewModel.setShowThemeSelectionDialog(true) },
                                onOpenVoiceKeyboard = { viewModel.setShowVoiceKeyboardDialog(true) },
                                onOpenVoiceRooms = { viewModel.setShowVoiceRoomsDialog(true) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Dialogs
    if (uiState.showApiKeyDialog) {
        ApiKeyDialog(
            initialApiKey = uiState.apiKey,
            initialSearchGrounding = uiState.isSearchGroundingEnabled,
            onSave = { key, searchGrounding ->
                viewModel.saveApiKey(key, searchGrounding)
            },
            onDismiss = { viewModel.setShowApiKeyDialog(false) }
        )
    }

    if (uiState.previewWebsiteHtml != null) {
        WebsitePreviewDialog(
            htmlCode = uiState.previewWebsiteHtml!!,
            onDismiss = { viewModel.closeWebsitePreview() }
        )
    }

    if (uiState.showImageGenDialog) {
        ImageGenerationDialog(
            onGenerate = { prompt, res ->
                viewModel.generateImage(prompt, res)
            },
            onDismiss = { viewModel.setShowImageGenDialog(false) }
        )
    }

    if (uiState.showMemoryVaultDialog) {
        MemoryVaultDialog(
            memories = uiState.memories,
            onAddMemory = { key, value ->
                viewModel.addMemory(key, value)
            },
            onDeleteMemory = { id ->
                viewModel.deleteMemory(id)
            },
            onDismiss = { viewModel.setShowMemoryVaultDialog(false) }
        )
    }

    if (uiState.showVoiceInputDialog) {
        VoiceInputDialog(
            onDismiss = { viewModel.setShowVoiceInputDialog(false) },
            onSubmitVoice = { voiceText ->
                viewModel.handleUserInput(voiceText)
            }
        )
    }

    if (uiState.showThemeSelectionDialog) {
        ThemeSelectionDialog(
            currentPersonality = uiState.activePersonality,
            activePalette = uiState.activePalette,
            onSelectPersonality = { mode -> viewModel.setPersonality(mode) },
            onSelectPalette = { palette -> viewModel.setThemePalette(palette) },
            onDismiss = { viewModel.setShowThemeSelectionDialog(false) }
        )
    }

    if (uiState.showVoiceKeyboardDialog) {
        VoiceKeyboardDialog(
            onDismiss = { viewModel.setShowVoiceKeyboardDialog(false) },
            onTestVoiceKeyboard = { viewModel.testFemaleVoice() }
        )
    }

    if (uiState.showVoiceSelectionDialog) {
        VoiceSelectionDialog(
            selectedVoiceId = uiState.selectedVoiceId,
            onSelectVoice = { voice ->
                viewModel.setSelectedVoice(voice.id, voice.pitch, voice.rate)
            },
            onTestVoice = { voice ->
                viewModel.testVoiceSample(voice.name, voice.pitch, voice.rate)
            },
            onDismiss = { viewModel.setShowVoiceSelectionDialog(false) }
        )
    }

    if (uiState.showAiModeDialog) {
        AiModeSelectionDialog(
            currentMode = uiState.activePersonality,
            onSelectMode = { mode -> viewModel.setPersonality(mode) },
            onDismiss = { viewModel.setShowAiModeDialog(false) }
        )
    }
}
