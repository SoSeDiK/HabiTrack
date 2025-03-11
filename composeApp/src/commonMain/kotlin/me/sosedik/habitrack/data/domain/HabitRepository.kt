package me.sosedik.habitrack.data.domain

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow

interface HabitRepository {

    suspend fun upsert(habit: Habit): Habit

    suspend fun getMaxOrder(): Int

    fun getHabitUpdates(habit: Habit): Flow<Habit?>

    fun getAllHabits(): Flow<PagingData<Habit>>

    fun getHabitsByCategory(category: HabitCategory): Flow<PagingData<Habit>>

    suspend fun deleteHabit(habit: Habit)

    suspend fun updateHabitCategories(habit: Habit, newCategoryIds: List<HabitCategory>)

    suspend fun getCategoriesForHabit(habit: Habit): List<HabitCategory>

}
