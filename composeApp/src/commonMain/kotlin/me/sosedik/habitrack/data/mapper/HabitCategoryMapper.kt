package me.sosedik.habitrack.data.mapper

import me.sosedik.habitrack.data.database.HabitCategoryEntity
import me.sosedik.habitrack.data.domain.HabitCategory

fun HabitCategoryEntity.toDomain(): HabitCategory {
    return HabitCategory(
        id = this.id,
        name = this.name,
        icon = this.icon
    )
}

fun HabitCategory.toEntity(): HabitCategoryEntity {
    return HabitCategoryEntity(
        id = this.id,
        name = this.name,
        icon = this.icon
    )
}
