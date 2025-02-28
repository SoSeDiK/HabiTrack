package me.sosedik.habitrack.data.mapper

import me.sosedik.habitrack.data.database.HabitEntryEntity
import me.sosedik.habitrack.data.domain.HabitEntry

fun HabitEntryEntity.toDomain(): HabitEntry {
    return HabitEntry(
        id = this.id,
        habitId = this.habitId,
        date = this.date,
        count = count
    )
}

fun HabitEntry.toEntity(): HabitEntryEntity {
    return HabitEntryEntity(
        id = this.id,
        habitId = this.habitId,
        date = this.date,
        count = count
    )
}
