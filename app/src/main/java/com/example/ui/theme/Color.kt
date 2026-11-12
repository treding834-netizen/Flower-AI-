package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// ==========================================
// 🌸 FLOWER AI PINK NOIR & 3D PALETTE
// Changed from Blue to Stunning Cyber Pink as requested
// ==========================================

// ==========================================
// 🌸 AJIJUL AI SOFT PASTEL PINK 3D PALETTE
// Extremely soft light pink aesthetic as requested
// ==========================================

// Soft Pastel Rose & Gentle Pink Cosmic Backgrounds
val AnimeDeepSpace = Color(0xFF1D111A)
val AnimeBackground = Color(0xFF261823)
val AnimeSurface = Color(0xFF32202E)
val AnimeSurfaceVariant = Color(0xFF422C3D)
val AnimeCardBorder = Color(0xFFD67AB1)

// Soft Glowing Light Pink & Rose Gold Accent Colors
val AnimePinkPrimary = Color(0xFFFF75B5) // Soft Light Pink
val AnimePinkLight = Color(0xFFFFB6C1)   // Light Pastel Pink
val AnimePinkGlow = Color(0xFFFF99C8)    // Soft Rose Glow
val AnimeMagenta = Color(0xFFFA709A)     // Soft Rose Pink
val AnimePurplePrimary = Color(0xFFFF75B5)
val AnimePurpleLight = Color(0xFFFFD1DC)
val AnimeAmber = Color(0xFFFFB347)       // Soft Warm Amber
val AnimeGreen = Color(0xFF77DD77)       // Soft Pastel Green
val AnimeRedEnd = Color(0xFFFF6961)      // Soft Coral Red

val AnimeCyan = Color(0xFFFF85C2)        // Soft Pink replacing blue
val AnimeCyanGlow = Color(0x66FF75B5)

// Chat Bubbles
val BubbleAiBackground = Color(0xFF3A2436)
val BubbleUserBackground = Color(0xFF523048)
val BubbleBorder = Color(0xFFD67AB1)

// Bottom Nav Pill
val BottomNavPillBackground = Color(0xFF2A1926)
val BottomNavPillBorder = Color(0xFFFFB6C1)

// Text Tokens
val TextPureWhite = Color(0xFFFFF0F5) // Lavender Blush white
val TextMutedLilac = Color(0xFFF3E8FF)
val TextSubtleNavy = Color(0xFFFFB6C1)
val TextDimmed = Color(0xFFD4B2CB)

// ==========================================
// 🌈 FULL RGB RAINBOW LIGHTING SPECTRUM
// Used for animated Rainbow Lighting on every button
// ==========================================
val RainbowColorSpectrum = listOf(
    Color(0xFFFF0055), // Hot Pink
    Color(0xFFFF5500), // Electric Orange
    Color(0xFFFFDD00), // Vivid Gold
    Color(0xFF00FF88), // Neon Spring
    Color(0xFF00E5FF), // Cyber Cyan
    Color(0xFF8800FF), // Deep Violet
    Color(0xFFFF00AA), // Neon Magenta
    Color(0xFFFF0055)  // Return to Hot Pink
)

// ==========================================
// 🎨 MULTI-THEME COLOR PALETTES
// Allows user to pick multiple vibrant color themes
// ==========================================
enum class AppThemePalette(
    val id: String,
    val displayName: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val surfaceColor: Color,
    val borderColor: Color
) {
    NEON_PINK(
        id = "pink",
        displayName = "🌸 Neon Cyber Pink (পিংক)",
        primaryColor = Color(0xFFFF2E93),
        secondaryColor = Color(0xFFFF85C2),
        surfaceColor = Color(0xFF27092E),
        borderColor = Color(0xFF6E1856)
    ),
    ROSE_GOLD(
        id = "rose",
        displayName = "✨ Rose Gold Sakura (রোজ গোল্ড)",
        primaryColor = Color(0xFFFB7185),
        secondaryColor = Color(0xFFFECDD3),
        surfaceColor = Color(0xFF290E22),
        borderColor = Color(0xFF7C2356)
    ),
    RAINBOW_GLOW(
        id = "rainbow",
        displayName = "🌈 Rainbow Spectrum (রেইনবো)",
        primaryColor = Color(0xFFFF007F),
        secondaryColor = Color(0xFF00E5FF),
        surfaceColor = Color(0xFF220828),
        borderColor = Color(0xFFFF00AA)
    ),
    ELECTRIC_VIOLET(
        id = "violet",
        displayName = "💜 Electric Amethyst (ভায়োলেট)",
        primaryColor = Color(0xFFA855F7),
        secondaryColor = Color(0xFFE879F9),
        surfaceColor = Color(0xFF1E0E38),
        borderColor = Color(0xFF5B21B6)
    ),
    SUNSET_AMBER(
        id = "sunset",
        displayName = "🔥 Sunset Neon Amber (সানসেট)",
        primaryColor = Color(0xFFF59E0B),
        secondaryColor = Color(0xFFF43F5E),
        surfaceColor = Color(0xFF281308),
        borderColor = Color(0xFF92400E)
    ),
    CYBER_CYAN(
        id = "cyan",
        displayName = "💎 Cyber Aqua (সায়ান)",
        primaryColor = Color(0xFF06B6D4),
        secondaryColor = Color(0xFF38BDF8),
        surfaceColor = Color(0xFF091E2C),
        borderColor = Color(0xFF0E7490)
    );

    companion object {
        fun fromId(id: String): AppThemePalette {
            return values().firstOrNull { it.id.equals(id, ignoreCase = true) } ?: NEON_PINK
        }
    }
}

// Aliases for Dialogs and Components compatibility
val CyberVoid = AnimeDeepSpace
val CyberBackground = AnimeBackground
val CyberSurface = AnimeSurface
val CyberSurfaceVariant = AnimeSurfaceVariant
val CyberCardBorder = AnimeCardBorder
val NeonCyan = AnimePinkPrimary // Cyan aliased to hot pink
val NeonMagenta = AnimePinkGlow
val NeonAmber = AnimeAmber
val NeonGreen = AnimeGreen
val NeonRose = AnimeRedEnd
val TextPrimary = TextPureWhite
val TextSecondary = TextMutedLilac
val TextTertiary = TextDimmed
