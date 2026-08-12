package com.ledscreen.editor.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = LEDRed,
    secondary = LEDRed,
    tertiary = LEDRed,
    background = DarkBackground,
    surface = SurfaceDark,
    onBackground = TextLight,
    onSurface = TextLight,
)

@Composable
fun LEDScreenEditorTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
