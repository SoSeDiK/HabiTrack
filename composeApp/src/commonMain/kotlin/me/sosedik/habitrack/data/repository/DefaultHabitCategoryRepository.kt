package me.sosedik.habitrack.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.sosedik.habitrack.data.database.HabitCategoriesDao
import me.sosedik.habitrack.data.domain.HabitCategory
import me.sosedik.habitrack.data.domain.HabitCategoryRepository
import me.sosedik.habitrack.data.mapper.toDomain

class DefaultHabitCategoryRepository(
    private val habitCategoriesDao: HabitCategoriesDao
): HabitCategoryRepository {

    override fun getHabitCategories(): Flow<List<HabitCategory>> {
        return habitCategoriesDao.getHabitCategories()
            .map { entities -> entities.map { it.toDomain() } }
    }

}