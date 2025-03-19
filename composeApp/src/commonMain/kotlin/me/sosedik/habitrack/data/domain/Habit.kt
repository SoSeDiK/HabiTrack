package me.sosedik.habitrack.data.domain

import androidx.compose.ui.graphics.Color

data class Habit(
    val id: Long,
    val name: String,
    val description: String?,
    val dailyLimit: Int,
    val icon: String,
    val color: Color,
    val order: Int
    // TODO streak goal (none, daily, weekly, monthly)
    // TODO remainder
)
