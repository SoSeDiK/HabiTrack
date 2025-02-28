package me.sosedik.habitrack.data.domain

import kotlinx.coroutines.flow.Flow

interface HabitRepository {

    fun getHabits(): Flow<List<Habit>>

    suspend fun deleteHabit(habit: Habit)

}
