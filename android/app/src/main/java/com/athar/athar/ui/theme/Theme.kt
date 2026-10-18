package com.athar.athar.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = AtharPrimary,
    onPrimary = AtharOnPrimary,
    primaryContainer = AtharPrimaryContainer,
    secondary = AtharAccent,
    background = AtharBackground,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7FD3C0),
    onPrimary = Color(0xFF00382F),
    secondary = AtharAccent,
)

@Composable
fun AtharTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
