package me.sosedik.habitrack.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface HabitEntriesDao {

    @Upsert
    suspend fun upsert(habitEntryEntity: HabitEntryEntity): Long

    @Query("DELETE FROM entries WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("SELECT * FROM entries WHERE habitId = :habitId AND date = :date")
    suspend fun findByDate(habitId: Long, date: Long): HabitEntryEntity?

    @Query("SELECT * FROM entries WHERE habitId = :habitId AND date BETWEEN :startDate AND :endDate")
    suspend fun findByDateRange(
        habitId: Long,
        startDate: Long,
        endDate: Long
    ): List<HabitEntryEntity>

}
