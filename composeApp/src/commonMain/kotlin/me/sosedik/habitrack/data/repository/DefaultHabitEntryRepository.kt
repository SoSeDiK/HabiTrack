package me.sosedik.habitrack.data.repository

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.toLocalDateTime
import me.sosedik.habitrack.data.database.HabitEntriesDao
import me.sosedik.habitrack.data.database.HabitEntryEntity
import me.sosedik.habitrack.data.domain.Habit
import me.sosedik.habitrack.data.domain.HabitEntry
import me.sosedik.habitrack.data.domain.HabitEntryRepository
import me.sosedik.habitrack.data.mapper.toDomain
import me.sosedik.habitrack.data.mapper.toEntity
import me.sosedik.habitrack.util.localTimeZone

class DefaultHabitEntryRepository(
    private val habitEntriesDao: HabitEntriesDao
): HabitEntryRepository {

    override suspend fun addEntry(entry: HabitEntry): HabitEntry {
        val newId = habitEntriesDao.upsert(entry.toEntity())
        return if (newId == -1L) entry else entry.copy(id = newId)
    }

    override suspend fun deleteEntry(entry: HabitEntry) {
        habitEntriesDao.delete(entry.id)
    }

    override suspend fun fetchByDate(
        habit: Habit,
        date: LocalDate
    ): HabitEntry? {
        return habitEntriesDao.findByDate(habit.id, date.atStartOfDayIn(TimeZone.UTC).toEpochMilliseconds())?.toDomain()
    }

    override suspend fun fetchByRange(
        habit: Habit,
        startDate: Instant,
        endDate: Instant
    ): Map<LocalDate, HabitEntry> {
        val entries: List<HabitEntryEntity> = habitEntriesDao.findByDateRange(habit.id, startDate.toEpochMilliseconds(), endDate.toEpochMilliseconds())
        if (entries.isEmpty())
            return emptyMap()

        val timeZone: TimeZone = localTimeZone()
        return entries.associate { entry ->
            val localDate = entry.date.toLocalDateTime(timeZone).date
            localDate to entry.toDomain()
        }
    }

}
