package me.sosedik.habitrack.data.repository

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import me.sosedik.habitrack.data.database.HabitCategoriesDao
import me.sosedik.habitrack.data.domain.HabitCategory
import me.sosedik.habitrack.data.domain.HabitCategoryRepository
import me.sosedik.habitrack.data.mapper.toDomain
import me.sosedik.habitrack.data.mapper.toEntity

class DefaultHabitCategoryRepository(
    private val habitCategoriesDao: HabitCategoriesDao
): HabitCategoryRepository {

    override suspend fun upsertCategory(entry: HabitCategory): HabitCategory {
        val newId = habitCategoriesDao.upsert(entry.toEntity())
        return if (newId == -1L) entry else entry.copy(id = newId)
    }

    override suspend fun deleteCategory(entry: HabitCategory) {
        habitCategoriesDao.delete(entry.id)
    }

    override fun getHabitCategories(): Flow<List<HabitCategory>> {
        return habitCategoriesDao.getHabitCategories()
            .map { entities -> entities.map { it.toDomain() } }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCategoriesWithHabits(): Flow<List<HabitCategory>> {
        return habitCategoriesDao.getHabitCategories()
            .combine(habitCategoriesDao.getHabitCategoryCrossRefs()) { categories, crossRefs ->
                val categoryIdsWithHabits = crossRefs.map { it.categoryId }.toSet()

                categories.filter { category ->
                    category.id in categoryIdsWithHabits
                }.map { it.toDomain() }
            }
    }


}