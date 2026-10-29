package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val AnimeFlowerColorScheme = darkColorScheme(
    primary = AnimePurplePrimary,
    onPrimary = TextPureWhite,
    primaryContainer = AnimeSurfaceVariant,
    onPrimaryContainer = AnimePurpleLight,
    secondary = AnimeMagenta,
    onSecondary = TextPureWhite,
    secondaryContainer = AnimeSurface,
    onSecondaryContainer = AnimePinkGlow,
    tertiary = AnimeCyan,
    background = AnimeDeepSpace,
    onBackground = TextPureWhite,
    surface = AnimeSurface,
    onSurface = TextPureWhite,
    surfaceVariant = AnimeSurfaceVariant,
    onSurfaceVariant = TextMutedLilac,
    outline = AnimeCardBorder
)

@Composable
fun FlowerAiTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = AnimeDeepSpace.toArgb()
                window.navigationBarColor = AnimeDeepSpace.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = AnimeFlowerColorScheme,
        typography = Typography,
        content = content
    )
}
