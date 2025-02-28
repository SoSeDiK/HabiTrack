package me.sosedik.habitrack.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import me.sosedik.habitrack.getPlatform

val DarkColorScheme = darkColorScheme(
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface
)

val LightColorScheme = lightColorScheme()

@Composable
fun HabiTrackTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // TODO dynamic themes
    content: @Composable () -> Unit
) {
    val colorScheme = getPlatform().getColorScheme(dynamicColor, darkTheme)

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
