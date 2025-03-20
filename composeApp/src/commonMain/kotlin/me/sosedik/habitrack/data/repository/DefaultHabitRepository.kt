package me.sosedik.habitrack.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.sosedik.habitrack.data.database.HabitsDao
import me.sosedik.habitrack.data.domain.Habit
import me.sosedik.habitrack.data.domain.HabitCategory
import me.sosedik.habitrack.data.domain.HabitRepository
import me.sosedik.habitrack.data.mapper.toDomain
import me.sosedik.habitrack.data.mapper.toEntity

class DefaultHabitRepository(
    private val habitsDao: HabitsDao
): HabitRepository {

    override suspend fun upsert(habit: Habit): Habit {
        val newId = habitsDao.upsert(habit.toEntity())
        return if (newId == -1L) habit else habit.copy(id = newId)
    }

    override suspend fun update(habits: List<Habit>) {
        habitsDao.updateHabits(habits.map { it.toEntity() })
    }

    override suspend fun getMaxOrder(): Int {
        return habitsDao.getMaxOrder() ?: -1
    }

    override fun getHabitUpdates(habit: Habit): Flow<Habit?> {
        return habitsDao.getHabitById(habit.id)
            .map { it?.toDomain() }
    }

    override fun getAllHabits(): Flow<PagingData<Habit>> {
        return Pager(
            config = PagingConfig(pageSize = 30),
            pagingSourceFactory = { habitsDao.getAllHabits() }
        ).flow
            .map { pagingData ->
                pagingData.map { it.toDomain() }
            }
    }

    override fun getHabitsByCategory(category: HabitCategory): Flow<PagingData<Habit>>{
        return Pager(
            config = PagingConfig(pageSize = 30),
            pagingSourceFactory = { habitsDao.getHabitsByCategory(category.id) }
        ).flow
            .map { pagingData ->
                pagingData.map { it.toDomain() }
            }
    }

    override suspend fun deleteHabit(habit: Habit) {
        habitsDao.delete(habit.id)
    }

    override suspend fun updateHabitCategories(
        habit: Habit,
        newCategoryIds: List<HabitCategory>
    ) {
        habitsDao.updateHabitCategories(habit.id, newCategoryIds.map { it.id })
    }

    override suspend fun getCategoriesForHabit(habit: Habit): List<HabitCategory> {
        return habitsDao.getCategoriesForHabit(habit.id).map { it.toDomain() }
    }

}
