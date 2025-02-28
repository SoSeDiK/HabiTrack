package me.sosedik.habitrack

import android.os.Build
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import me.sosedik.habitrack.presentation.theme.DarkColorScheme
import me.sosedik.habitrack.presentation.theme.LightColorScheme

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    @Composable
    override fun getColorScheme(
        dynamicColor: Boolean,
        darkTheme: Boolean
    ): ColorScheme {
        return when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(
                    context
                )
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
    }
}

actual fun getPlatform(): Platform = AndroidPlatform()
