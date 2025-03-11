package me.sosedik.habitrack.data.mapper

import me.sosedik.habitrack.data.database.HabitWithCategoriesEntities
import me.sosedik.habitrack.data.domain.HabitWithCategories

fun HabitWithCategoriesEntities.toDomain(): HabitWithCategories {
    return HabitWithCategories(
        habit = this.habit.toDomain(),
        categories = this.categories.map { it.toDomain() }
    )
}
