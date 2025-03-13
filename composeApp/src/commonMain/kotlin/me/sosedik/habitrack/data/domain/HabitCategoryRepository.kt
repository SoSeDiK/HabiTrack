package me.sosedik.habitrack.data.domain

import kotlinx.coroutines.flow.Flow

interface HabitCategoryRepository {

    suspend fun upsertCategory(entry: HabitCategory): HabitCategory

    suspend fun deleteCategory(entry: HabitCategory)

    fun getHabitCategories(): Flow<List<HabitCategory>>

    fun getCategoriesWithHabits(): Flow<List<HabitCategory>>

}
