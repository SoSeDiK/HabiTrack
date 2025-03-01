package me.sosedik.habitrack.util

import androidx.compose.runtime.Composable
import com.kizitonwose.calendar.core.YearMonth
import com.kizitonwose.calendar.core.lengthOfMonth
import habitrack.composeapp.generated.resources.Res
import habitrack.composeapp.generated.resources.date_april
import habitrack.composeapp.generated.resources.date_august
import habitrack.composeapp.generated.resources.date_december
import habitrack.composeapp.generated.resources.date_february
import habitrack.composeapp.generated.resources.date_friday_short
import habitrack.composeapp.generated.resources.date_january
import habitrack.composeapp.generated.resources.date_july
import habitrack.composeapp.generated.resources.date_june
import habitrack.composeapp.generated.resources.date_march
import habitrack.composeapp.generated.resources.date_may
import habitrack.composeapp.generated.resources.date_monday_short
import habitrack.composeapp.generated.resources.date_november
import habitrack.composeapp.generated.resources.date_october
import habitrack.composeapp.generated.resources.date_saturday_short
import habitrack.composeapp.generated.resources.date_september
import habitrack.composeapp.generated.resources.date_sunday_short
import habitrack.composeapp.generated.resources.date_thursday_short
import habitrack.composeapp.generated.resources.date_tuesday_short
import habitrack.composeapp.generated.resources.date_wednesday_short
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.stringResource

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

fun getPriorDaysRangeUTC(
    dayOffset: Int,
    endDate: Instant = Clock.System.now()
): Pair<Instant, Instant> {
    val startDate = endDate.minus(dayOffset, DateTimeUnit.DAY, dbTimeZone)
    return Pair(startDate, endDate)
}

fun getMonthRange(
    yearMonth: YearMonth
): Pair<Instant, Instant> {
    val startDate = LocalDateTime(yearMonth.year, yearMonth.monthNumber, 1, 0, 0, 0)
    val endDate = LocalDateTime(yearMonth.year, yearMonth.monthNumber, yearMonth.lengthOfMonth(), 23, 59, 59)
    return Pair(startDate.toInstant(dbTimeZone), endDate.toInstant(dbTimeZone))
}

fun getStartOfDayInUTC(
    date: LocalDate = Clock.System.now().toLocalDateTime(localTimeZone()).date
): Instant {
    return date.atStartOfDayIn(dbTimeZone)
}

fun getCurrentDayOfWeek(): Int {
    val currentDate = Clock.System.now().toLocalDateTime(localTimeZone()).date
    return currentDate.dayOfWeek.isoDayNumber
}

@Composable
fun DayOfWeek.locale(): String {
    return when (this) {
        DayOfWeek.MONDAY -> stringResource(Res.string.date_monday_short)
        DayOfWeek.TUESDAY -> stringResource(Res.string.date_tuesday_short)
        DayOfWeek.WEDNESDAY -> stringResource(Res.string.date_wednesday_short)
        DayOfWeek.THURSDAY -> stringResource(Res.string.date_thursday_short)
        DayOfWeek.FRIDAY -> stringResource(Res.string.date_friday_short)
        DayOfWeek.SATURDAY -> stringResource(Res.string.date_saturday_short)
        DayOfWeek.SUNDAY -> stringResource(Res.string.date_sunday_short)
        else -> throw IllegalArgumentException("Invalid day of the week: $this")
    }
}

@Composable
fun Month.locale(): String {
    return when (this) {
        Month.JANUARY -> stringResource(Res.string.date_january)
        Month.FEBRUARY -> stringResource(Res.string.date_february)
        Month.MARCH -> stringResource(Res.string.date_march)
        Month.APRIL -> stringResource(Res.string.date_april)
        Month.MAY -> stringResource(Res.string.date_may)
        Month.JUNE -> stringResource(Res.string.date_june)
        Month.JULY -> stringResource(Res.string.date_july)
        Month.AUGUST -> stringResource(Res.string.date_august)
        Month.SEPTEMBER -> stringResource(Res.string.date_september)
        Month.OCTOBER -> stringResource(Res.string.date_october)
        Month.NOVEMBER -> stringResource(Res.string.date_november)
        Month.DECEMBER -> stringResource(Res.string.date_december)
        else -> throw IllegalArgumentException("Invalid month: $this")
    }
}
