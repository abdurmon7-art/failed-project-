package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = FlamePrimary,
    onPrimary = TextDark,
    primaryContainer = FlameDark,
    onPrimaryContainer = TextWhite,
    secondary = CyanAccent,
    onSecondary = TextDark,
    secondaryContainer = Color(0xFF003D52),
    onSecondaryContainer = Color(0xFFB8E8FF),
    tertiary = CrimsonAccent,
    onTertiary = TextWhite,
    tertiaryContainer = CrimsonDark,
    onTertiaryContainer = Color(0xFFFFDADA),
    background = DarkBg,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = DarkOutline,
    outlineVariant = DarkOutlineHighlight
)

private val LightColorScheme = darkColorScheme( // Keep dedicated gaming dark atmosphere by default
    primary = FlamePrimary,
    onPrimary = TextDark,
    primaryContainer = FlamePrimaryLight,
    onPrimaryContainer = TextDark,
    secondary = CyanAccent,
    onSecondary = TextDark,
    secondaryContainer = Color(0xFF003D52),
    onSecondaryContainer = Color(0xFFB8E8FF),
    tertiary = CrimsonAccent,
    onTertiary = TextWhite,
    background = DarkBg,
    onBackground = TextWhite,
    surface = DarkSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextMuted,
    outline = DarkOutline,
    outlineVariant = DarkOutlineHighlight
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep consistent esports flame branding
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = DarkBg.toArgb()
                window.navigationBarColor = DarkBg.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
