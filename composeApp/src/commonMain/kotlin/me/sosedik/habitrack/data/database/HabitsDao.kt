package me.sosedik.habitrack.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitsDao {

    @Upsert
    suspend fun upsert(habit: HabitEntity)

    @Query("SELECT * FROM habits")
    fun getHabits(): Flow<List<HabitEntity>>

    @Query("DELETE FROM habits WHERE id = :id")
    suspend fun delete(id: Long)

}
