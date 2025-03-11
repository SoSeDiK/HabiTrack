package me.sosedik.habitrack.data.domain

data class HabitWithCategories(
    val habit: Habit,
    val categories: List<HabitCategory>
)
