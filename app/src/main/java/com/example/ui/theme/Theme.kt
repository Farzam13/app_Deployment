package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Burgundy800,
    onPrimary = Color.White,
    primaryContainer = Burgundy100,
    onPrimaryContainer = Burgundy950,
    secondary = Gold500,
    onSecondary = Color.White,
    background = Cream25,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Cream50,
    onSurfaceVariant = InkSoft,
    outline = BorderColor
)

@Composable
fun DrKAssessmentTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
