package me.sosedik.habitrack.data.mapper

import me.sosedik.habitrack.data.database.HabitEntity
import me.sosedik.habitrack.data.domain.Habit

fun HabitEntity.toDomain(): Habit {
    return Habit(
        id = this.id,
        name = this.name,
        description = this.description,
        dailyLimit = this.dailyLimit,
        icon = this.icon,
        color = this.color,
        order = this.order
    )
}

fun Habit.toEntity(): HabitEntity {
    return HabitEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        dailyLimit = this.dailyLimit,
        icon = this.icon,
        color = this.color,
        order = this.order
    )
}
