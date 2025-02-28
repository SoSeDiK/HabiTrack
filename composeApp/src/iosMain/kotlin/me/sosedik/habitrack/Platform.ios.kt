package me.sosedik.habitrack

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import me.sosedik.habitrack.presentation.theme.DarkColorScheme
import me.sosedik.habitrack.presentation.theme.LightColorScheme
import platform.UIKit.UIDevice

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

    @Composable
    override fun getColorScheme(
        dynamicColor: Boolean,
        darkTheme: Boolean
    ): ColorScheme {
        return if (darkTheme) DarkColorScheme else LightColorScheme
    }
}

actual fun getPlatform(): Platform = IOSPlatform()
