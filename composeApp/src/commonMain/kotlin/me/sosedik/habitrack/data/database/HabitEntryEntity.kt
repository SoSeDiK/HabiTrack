package me.sosedik.habitrack.data.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.datetime.Instant

@Entity(
    tableName = "entries"
)
data class HabitEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val habitId: Long,
    val date: Instant,
    val count: Int,
    val limit: Int
)
