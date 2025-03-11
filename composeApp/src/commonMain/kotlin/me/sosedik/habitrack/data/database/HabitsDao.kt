package me.sosedik.habitrack.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitsDao {

    @Upsert
    suspend fun upsert(habit: HabitEntity): Long

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT MAX(`order`) FROM habits")
    suspend fun getMaxOrder(): Int?

    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getHabitById(habitId: Long): Flow<HabitEntity?>

    @Query("SELECT * FROM habits ORDER BY `order` ASC")
    fun getAllHabits(): PagingSource<Int, HabitEntity>

    @Query("SELECT * FROM habits WHERE id IN (SELECT habitId FROM habit_categories_cross WHERE categoryId = :categoryId) ORDER BY `order` ASC")
    fun getHabitsByCategory(categoryId: Long): PagingSource<Int, HabitEntity>

    @Transaction
    @Query("SELECT * FROM habit_categories WHERE id IN (SELECT categoryId FROM habit_categories_cross WHERE habitId = :id)")
    suspend fun getCategoriesForHabit(id: Long): List<HabitCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitCategoryCrossRef(crossRef: HabitCategoryCrossRef)

    @Delete
    suspend fun deleteHabitCategoryCrossRef(crossRef: HabitCategoryCrossRef)

    @Transaction
    suspend fun updateHabitCategories(habitId: Long, newCategoryIds: List<Long>) {
        val existingCategories = getCategoryIdsForHabit(habitId)
        val categoriesToRemove = existingCategories.filter { it !in newCategoryIds }
        categoriesToRemove.forEach { categoryId ->
            deleteHabitCategoryCrossRef(HabitCategoryCrossRef(habitId, categoryId))
        }

        newCategoryIds.forEach { categoryId ->
            if (categoryId !in existingCategories) {
                insertHabitCategoryCrossRef(HabitCategoryCrossRef(habitId, categoryId))
            }
        }
    }

    @Query("SELECT categoryId FROM habit_categories_cross WHERE habitId = :id")
    suspend fun getCategoryIdsForHabit(id: Long): List<Long>

}
