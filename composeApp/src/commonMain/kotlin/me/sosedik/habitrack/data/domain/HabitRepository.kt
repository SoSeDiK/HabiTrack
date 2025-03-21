package me.sosedik.habitrack.data.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface HabitRepository {

    suspend fun upsert(habit: Habit): Habit

    suspend fun update(habits: List<Habit>)

    suspend fun getMaxOrder(): Int

    fun getHabitUpdates(habit: Habit): Flow<Habit?>

    fun getAllActiveHabits(): Flow<PagingData<Habit>>

    fun getAllArchivedHabits(): Flow<PagingData<Habit>>

    fun getActiveHabitsByCategory(category: HabitCategory): Flow<PagingData<Habit>>

    suspend fun deleteHabit(habit: Habit)

    suspend fun updateArchivedState(habit: Habit, archived: Boolean)

    suspend fun updateHabitCategories(habit: Habit, newCategoryIds: List<HabitCategory>)

    suspend fun getCategoriesForHabit(habit: Habit): List<HabitCategory>

}
