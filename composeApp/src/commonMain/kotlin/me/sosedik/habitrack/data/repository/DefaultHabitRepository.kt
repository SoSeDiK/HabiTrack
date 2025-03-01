package me.sosedik.habitrack.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.sosedik.habitrack.data.database.HabitCategoriesDao
import me.sosedik.habitrack.data.database.HabitsDao
import me.sosedik.habitrack.data.domain.Habit
import me.sosedik.habitrack.data.domain.HabitRepository
import me.sosedik.habitrack.data.mapper.toDomain

class DefaultHabitRepository(
    private val habitCategoriesDao: HabitCategoriesDao,
    private val habitsDao: HabitsDao
): HabitRepository {

    override fun getHabits(): Flow<List<Habit>> {
        return habitsDao.getHabits()
            .map { entities -> entities.map { it.toDomain(habitCategoriesDao) } }
    }

    override suspend fun deleteHabit(habit: Habit) {
        habitsDao.delete(habit.id)
    }

}
