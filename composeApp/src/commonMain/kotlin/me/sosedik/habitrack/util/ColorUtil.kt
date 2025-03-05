package me.sosedik.habitrack.util

import androidx.compose.ui.graphics.Color
import me.sosedik.habitrack.data.domain.HabitEntry

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
