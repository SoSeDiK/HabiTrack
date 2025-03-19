package me.sosedik.habitrack.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import habitrack.composeapp.generated.resources.Res
import habitrack.composeapp.generated.resources.symbols_nerd_font_mono
import org.jetbrains.compose.resources.Font

@Composable
fun NerdSymbolsFontFamily() = FontFamily(
    Font(Res.font.symbols_nerd_font_mono, weight = FontWeight.Light)
)
