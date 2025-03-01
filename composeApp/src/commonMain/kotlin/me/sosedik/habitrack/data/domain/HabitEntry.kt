package me.sosedik.habitrack.data.domain

import kotlinx.datetime.Instant

data class HabitEntry(
    val id: Long,
    val habitId: Long,
    val date: Instant,
    val count: Int,
    val limit: Int
)
