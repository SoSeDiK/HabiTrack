package me.sosedik.habitrack.util

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import me.sosedik.habitrack.data.domain.HabitEntry

val PRE_PICKED_COLORS = listOf(
    Color(0xFFDF675B),
    Color(0xFFE17D59),
    Color(0xFFEBAE54),
    Color(0xFFDEC55E),
    Color(0xFF60CE55),
    Color(0xFF8ED858),
    Color(0xFF78D6A1),
    Color(0xFFA5D784),
    Color(0xFFC6D77A),
    Color(0xFF5B69DC),
    Color(0xFF5CC5DD),
    Color(0xFFA15CDD),
    Color(0xFFAC94E0),
    Color(0xFF8DA6D8),
    Color(0xFFDD5CD2),
    Color(0xFFDF88CA),
    Color(0xFFDC5B8A),
    Color(0xFFBEA69A),
    Color(0xFF8D9898),
    Color(0xFFA3B3C4)
)

fun Color.toHex(): String {
    val argb = this.toArgb()
    val red = (argb shr 16) and 0xFF
    val green = (argb shr 8) and 0xFF
    val blue = argb and 0xFF

    return "#${red.toString(16).padStart(2, '0')}${green.toString(16).padStart(2, '0')}${blue.toString(16).padStart(2, '0')}".uppercase()
}

fun hexToColor(hex: String): Color? {
    val cleanedHex = hex.trimStart('#')

    return when (cleanedHex.length) {
        6 -> {
            val r = cleanedHex.substring(0, 2).toIntOrNull(16) ?: return null
            val g = cleanedHex.substring(2, 4).toIntOrNull(16) ?: return null
            val b = cleanedHex.substring(4, 6).toIntOrNull(16) ?: return null
            Color(red = r, green = g, blue = b)
        }
        8 -> {
            val r = cleanedHex.substring(2, 4).toIntOrNull(16) ?: return null
            val g = cleanedHex.substring(4, 6).toIntOrNull(16) ?: return null
            val b = cleanedHex.substring(6, 8).toIntOrNull(16) ?: return null
            Color(red = r, green = g, blue = b)
        }
        else -> null
    }
}

fun getDesaturatedColor(
    color: Color
): Color {
    return color.copy(alpha = 0.3F)
}

fun calculateColor( // TODO Better color management
    activeColor: Color,
    inactiveColor: Color,
    progress: HabitEntry?
): Color {
    if (progress == null || progress.count <= 0)
        return inactiveColor

    if (progress.count >= progress.limit)
        return activeColor

    val scaled = (0.3F + (0.7F * progress.count / progress.limit)).coerceIn(0F, 1F)
    return activeColor.copy(alpha = 0.3F + scaled)
}
