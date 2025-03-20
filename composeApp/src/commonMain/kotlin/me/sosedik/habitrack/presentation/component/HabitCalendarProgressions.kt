package me.sosedik.habitrack.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.ExperimentalCalendarApi
import com.kizitonwose.calendar.core.OutDateStyle
import com.kizitonwose.calendar.core.YearMonth
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.minusMonths
import com.kizitonwose.calendar.core.plusMonths
import habitrack.composeapp.generated.resources.Res
import habitrack.composeapp.generated.resources.habit_calendar_footer
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import me.sosedik.habitrack.data.domain.Habit
import me.sosedik.habitrack.data.domain.HabitEntry
import me.sosedik.habitrack.presentation.viewmodel.HabitListAction
import me.sosedik.habitrack.util.localDate
import me.sosedik.habitrack.util.locale
import org.jetbrains.compose.resources.stringResource

@Composable
fun HabitCalendarProgressions(
    habit: Habit,
    completions: Map<LocalDate, HabitEntry>,
    firstDayOfWeek: DayOfWeek,
    allowActions: Boolean,
    onAction: (HabitListAction) -> Unit
) {
    val today by remember { mutableStateOf(localDate()) } // TODO check this, update on day change?
    val daysOfWeek by remember { mutableStateOf(daysOfWeek(firstDayOfWeek)) }

    val state = rememberCalendarState(
        startMonth = YearMonth(0, 1),
        endMonth = YearMonth.now(),
        firstVisibleMonth = YearMonth.now(),
        firstDayOfWeek = firstDayOfWeek,
        outDateStyle = OutDateStyle.EndOfGrid
    )

    LaunchedEffect(state.firstVisibleMonth) {
        onAction.invoke(HabitListAction.OnCalendarMonthLoad(state.firstVisibleMonth.yearMonth))
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalCalendar(
            state = state,
            dayContent = { day ->
                Day(
                    day = day,
                    today = today,
                    habit = habit,
                    completion = completions[day.date],
                    allowActions = allowActions,
                    onAction = onAction
                )
            },
            monthHeader = { month ->
                Column {
                    MonthHeader(
                        month = month,
                        state = state
                    )
                    DaysOfWeekTitle(daysOfWeek = daysOfWeek)
                }
            }
        )
        Text(
            text = stringResource(Res.string.habit_calendar_footer)
        )
    }
}

@Composable
private fun MonthHeader(
    month: CalendarMonth,
    state: CalendarState
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            enabled = state.canScrollBackward,
            onClick = {
                coroutineScope.launch {
                    state.animateScrollToMonth(month.yearMonth.minusMonths(1))
                }
            }
        ) {
            Icon(
                Icons.AutoMirrored.Default.KeyboardArrowLeft,
                contentDescription = null
            )
        }
        Text(
            text = month.yearMonth.month.locale() + " " + month.yearMonth.year
        )
        IconButton(
            enabled = state.canScrollForward,
            onClick = {
                coroutineScope.launch {
                    state.animateScrollToMonth(month.yearMonth.plusMonths(1))
                }
            }
        ) {
            Icon(
                Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(modifier = Modifier.fillMaxWidth()) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1F),
                textAlign = TextAlign.Center,
                text = dayOfWeek.locale(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3F)
            )
        }
    }
}

@Composable
private fun Day(
    day: CalendarDay,
    today: LocalDate,
    habit: Habit,
    completion: HabitEntry?,
    allowActions: Boolean,
    onAction: (HabitListAction) -> Unit
) {
    Column(
        modifier = Modifier
            .aspectRatio(1F)
            .combinedClickable(
                onClick = {
                    if (allowActions) onAction.invoke(HabitListAction.OnHabitProgressClick(habit, day.date, true))
                },
                onLongClick = {
                    if (allowActions) onAction.invoke(HabitListAction.OnHabitProgressClick(habit, day.date, false))
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .let {
                    if (day.date == today)
                        it
                            .border(1.dp, MaterialTheme.colorScheme.onSurface, RoundedCornerShape(8.dp))
                    else
                        it
                }
                .padding(vertical = 3.dp, horizontal = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color =
                if (day.position == DayPosition.MonthDate)
                    if (day.date.dayOfMonth <= today.dayOfMonth)
                        MaterialTheme.colorScheme.onSurface
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75F)
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6F)
            )
        }
        Row(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .height(20.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // TODO Progress display
            completion?.count?.takeIf { it > 0 }?.let { count ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (count > 5) {
                        ProgressCircle(habit = habit)
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = count.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    } else {
                        repeat(count) {
                            ProgressCircle(habit = habit)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProgressCircle(
    habit: Habit
) {
    Box(
        modifier = Modifier
            .size(6.dp)
            .background(habit.color, CircleShape)
    )
}
