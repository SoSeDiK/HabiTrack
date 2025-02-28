package me.sosedik.habitrack

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

interface Platform {
    val name: String

    @Composable
    fun getColorScheme(dynamicColor: Boolean, darkTheme: Boolean): ColorScheme
}

expect fun getPlatform(): Platform
