package com.driftingleaf.seshasya.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily

// Dark palette: deep forest night, sage leaf, serpent gold.
// All text/background pairs meet WCAG AA contrast (>= 4.5:1 for body text).
val Sage = Color(0xFF9CC283)
val Gold = Color(0xFFD9BF7C)

private val SeshasyaDark = darkColorScheme(
    primary = Sage,
    onPrimary = Color(0xFF0F1A12),
    primaryContainer = Color(0xFF26402C),
    onPrimaryContainer = Color(0xFFCBE5B6),
    secondary = Gold,
    onSecondary = Color(0xFF241B05),
    secondaryContainer = Color(0xFF3A3018),
    onSecondaryContainer = Color(0xFFF2E3B8),
    tertiary = Color(0xFF8CC0B4),
    onTertiary = Color(0xFF0B1F1A),
    background = Color(0xFF0B1310),
    onBackground = Color(0xFFE4EAE2),
    surface = Color(0xFF0B1310),
    onSurface = Color(0xFFE4EAE2),
    surfaceVariant = Color(0xFF1C2A23),
    onSurfaceVariant = Color(0xFFADBBB1),
    outline = Color(0xFF52665A),
    outlineVariant = Color(0xFF26332C),
    surfaceContainerLowest = Color(0xFF080E0B),
    surfaceContainerLow = Color(0xFF111B16),
    surfaceContainer = Color(0xFF15201A),
    surfaceContainerHigh = Color(0xFF1B2821),
    surfaceContainerHighest = Color(0xFF22312A),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

private val base = Typography()

// Serif for headings (calm, scriptural feel); default sans for body (legibility).
private val SeshasyaType = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = FontFamily.Serif),
    displayMedium = base.displayMedium.copy(fontFamily = FontFamily.Serif),
    displaySmall = base.displaySmall.copy(fontFamily = FontFamily.Serif),
    headlineLarge = base.headlineLarge.copy(fontFamily = FontFamily.Serif),
    headlineMedium = base.headlineMedium.copy(fontFamily = FontFamily.Serif),
    headlineSmall = base.headlineSmall.copy(fontFamily = FontFamily.Serif),
    titleLarge = base.titleLarge.copy(fontFamily = FontFamily.Serif),
)

@Composable
fun SeshasyaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SeshasyaDark,
        typography = SeshasyaType,
        content = content,
    )
}
