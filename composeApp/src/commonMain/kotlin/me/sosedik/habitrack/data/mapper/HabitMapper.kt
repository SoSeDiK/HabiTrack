package me.sosedik.habitrack.data.mapper

import me.sosedik.habitrack.data.database.HabitCategoriesDao
import me.sosedik.habitrack.data.database.HabitEntity
import me.sosedik.habitrack.data.domain.Habit
import me.sosedik.habitrack.data.domain.HabitIcon

suspend fun HabitEntity.toDomain(
    categoriesDao: HabitCategoriesDao
): Habit {
    return Habit(
        id = this.id,
        name = this.name,
        description = this.description,
        dailyLimit = this.dailyLimit,
        categories = this.categories.mapNotNull { categoriesDao.getById(it)?.toDomain() },
        icon = HabitIcon.getById(this.icon),
        color = this.color
    )
}

fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        dailyLimit = this.dailyLimit,
        categories = this.categories.map { it.id },
        icon = this.icon.id,
        color = this.color
    )
}
