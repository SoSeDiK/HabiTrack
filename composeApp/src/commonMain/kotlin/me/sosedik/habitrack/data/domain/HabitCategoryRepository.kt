package me.sosedik.habitrack.data.domain

import kotlinx.coroutines.flow.Flow

interface HabitCategoryRepository {

    fun getHabitCategories(): Flow<List<HabitCategory>>

    fun getCategoriesWithHabits(): Flow<List<HabitCategory>>

}
