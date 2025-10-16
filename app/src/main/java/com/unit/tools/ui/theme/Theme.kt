package com.unit.tools.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Couleurs fixes (fallback) – sobres et accessibles
private val LightColors = lightColorScheme(
    primary = Color(0xFF4F46E5),       // Indigo 600
    onPrimary = Color(0xFFFFFFFF),
    secondary = Color(0xFF64748B),     // Slate 500
    onSecondary = Color(0xFFFFFFFF),
    surface = Color(0xFFF8FAFD),       // très clair
    onSurface = Color(0xFF121417),     // quasi noir
    outline = Color(0xFFE1E6EE),       // dividers
    tertiary = Color(0xFF22C55E),      // accent (success)
    onTertiary = Color(0xFF0B1A10)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF93A7FF),       // Indigo light
    onPrimary = Color(0xFF0B1220),
    secondary = Color(0xFF94A3B8),     // Slate 300
    onSecondary = Color(0xFF0F1216),
    surface = Color(0xFF0F1216),       // très sombre
    onSurface = Color(0xFFE6E9EE),     // quasi blanc
    outline = Color(0xFF273141),       // dividers sombres
    tertiary = Color(0xFF4ADE80),
    onTertiary = Color(0xFF0B1A10)
)

@Composable
fun UnitToolsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // Android 12+
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme =
        if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        } else {
            if (darkTheme) DarkColors else LightColors
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CompactTypography,
        shapes = AppShapes,
        content = content
    )
}
