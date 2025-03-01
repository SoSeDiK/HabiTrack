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

    @Query("DELETE FROM habit_categories WHERE id = :id")
    suspend fun delete(id: Long)

}
