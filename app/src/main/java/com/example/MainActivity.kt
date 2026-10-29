package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.FlowerAiScreen
import com.example.ui.FlowerAiViewModel
import com.example.ui.theme.AnimeDeepSpace
import com.example.ui.theme.FlowerAiTheme

class MainActivity : ComponentActivity() {

    private val viewModel: FlowerAiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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
