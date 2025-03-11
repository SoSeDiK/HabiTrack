package me.sosedik.habitrack.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitCategoriesDao {

    @Upsert
    suspend fun upsert(habit: HabitCategoryEntity)

    @Query("SELECT * FROM habit_categories WHERE id = :id")
    suspend fun getById(id: Long): HabitCategoryEntity?

    @Query("SELECT * FROM habit_categories")
    fun getHabitCategories(): Flow<List<HabitCategoryEntity>>

    @Query("SELECT * FROM habit_categories_cross")
    fun getHabitCategoryCrossRefs(): Flow<List<HabitCategoryCrossRef>>

    @Query("SELECT COUNT(*) FROM habit_categories_cross WHERE categoryId = :id")
    suspend fun countHabitsForCategory(id: Long): Int

    @Query("DELETE FROM habit_categories WHERE id = :id")
    suspend fun delete(id: Long)

}
