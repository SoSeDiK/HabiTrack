package me.sosedik.habitrack.data.database

import androidx.compose.ui.graphics.Color
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "habits"
)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val description: String?,
    val dailyLimit: Int,
    val icon: String,
    val color: Color,
    val order: Int,
    val archived: Boolean
)
