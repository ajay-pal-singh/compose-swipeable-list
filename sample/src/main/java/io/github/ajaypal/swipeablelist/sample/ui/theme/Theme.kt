package io.github.ajaypal.swipeablelist.sample.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = SlateBlue,
    background = Cream,
    surface = ColorWhite,
    surfaceVariant = SoftBlue,
    onPrimary = ColorWhite,
    onBackground = Ink,
    onSurface = Ink,
    onSurfaceVariant = Ink,
)

private val DarkColors = darkColorScheme(
    primary = SoftBlue,
    background = Ink,
    surface = SlateBlue,
    surfaceVariant = Color(0xFF1E293B),
    onPrimary = Ink,
    onBackground = ColorWhite,
    onSurface = ColorWhite,
    onSurfaceVariant = ColorWhite,
)

@Composable
fun SampleTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (isSystemInDarkTheme()) DarkColors else LightColors,
        typography = AppTypography,
        content = content,
    )
}
