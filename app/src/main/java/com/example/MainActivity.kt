package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.service.FlowerVoiceForegroundService
import com.example.ui.FlowerAiScreen
import com.example.ui.FlowerAiViewModel
import com.example.ui.theme.AnimeDeepSpace
import com.example.ui.theme.FlowerAiTheme

class MainActivity : ComponentActivity() {

    private val viewModel: FlowerAiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        try {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                FlowerVoiceForegroundService.startService(this)
                viewModel.setLiveVoiceCalling(true)
            }
        } catch (_: Exception) {}

        setContent {
            FlowerAiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AnimeDeepSpace
                ) {
                    FlowerAiScreen(viewModel = viewModel)
                }
            }
        }
    }
}

