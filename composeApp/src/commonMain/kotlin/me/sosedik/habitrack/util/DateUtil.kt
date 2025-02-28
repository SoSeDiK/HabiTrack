package me.sosedik.habitrack.util

import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime

private val dbTimeZone = TimeZone.UTC

fun localTimeZone(): TimeZone {
    return TimeZone.currentSystemDefault()
}

fun localTimeUTC(timeZone: TimeZone = dbTimeZone): LocalDateTime {
    return Clock.System.now().toLocalDateTime(timeZone) // Track against +0 timezone internally
}

fun localDate(): LocalDate {
    return localTimeUTC(localTimeZone()).date
}

fun localDateUTC(): LocalDate {
    return localTimeUTC().date
}

fun getPriorDayProgress(dayOffset: Int): LocalDate {
    val timeZone = localTimeZone()
    return Clock.System.now().minus(dayOffset, DateTimeUnit.DAY, timeZone).toLocalDateTime(timeZone).date
}

fun getPriorDaysRangeUTC(dayOffset: Int): Pair<Instant, Instant> {
    val endDate: Instant = Clock.System.now()
    val startDate = endDate.minus(dayOffset, DateTimeUnit.DAY, dbTimeZone)
    return Pair(startDate, endDate)
}

fun getStartOfDayInUTC(): Instant {
    return Clock.System.now().toLocalDateTime(localTimeZone()).date.atStartOfDayIn(dbTimeZone)
}

fun getCurrentDayOfWeek(): Int {
    val currentDate = Clock.System.now().toLocalDateTime(localTimeZone()).date
    val dayOfWeek = currentDate.dayOfWeek
    return when (dayOfWeek) {
        DayOfWeek.MONDAY -> 1
        DayOfWeek.TUESDAY -> 2
        DayOfWeek.WEDNESDAY -> 3
        DayOfWeek.THURSDAY -> 4
        DayOfWeek.FRIDAY -> 5
        DayOfWeek.SATURDAY -> 6
        DayOfWeek.SUNDAY -> 7
        else -> throw IllegalArgumentException("Unknown day of the week: $dayOfWeek")
    }
}
