package me.sosedik.habitrack.data.domain

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

interface HabitEntryRepository {

    suspend fun addEntry(entry: HabitEntry): HabitEntry

    suspend fun deleteEntry(entry: HabitEntry)

    suspend fun fetchByDate(habit: Habit, date: LocalDate): HabitEntry?

    suspend fun fetchByRange(habit: Habit, startDate: Instant, endDate: Instant): Map<LocalDate, HabitEntry>

}
