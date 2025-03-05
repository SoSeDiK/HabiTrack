package me.sosedik.habitrack.presentation.component

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import me.sosedik.habitrack.data.domain.Habit
import me.sosedik.habitrack.data.domain.HabitEntry
import me.sosedik.habitrack.presentation.viewmodel.HabitListAction
import me.sosedik.habitrack.util.calculateColor
import me.sosedik.habitrack.util.getDesaturatedColor
import me.sosedik.habitrack.util.getPriorDayProgress
import me.sosedik.habitrack.util.localDate
import me.sosedik.habitrack.util.locale
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShortListHabit(
    habit: Habit,
    completions: Map<LocalDate, HabitEntry>,
    allowActions: Boolean,
    onAction: (HabitListAction) -> Unit
) {
    val desaturatedColor = remember { getDesaturatedColor(habit.color) }

    Row(
        modifier = Modifier
            .clickable(
                enabled = allowActions,
                onClick = {
                    onAction.invoke(HabitListAction.OnHabitClick(habit))
                }
            )
            .border(1.dp, MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2F), shape = RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(8.dp))
            .padding(8.dp)
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .sizeIn(minHeight = 24.dp, maxHeight = 32.dp)
                    .background(desaturatedColor, shape = RoundedCornerShape(6.dp)),
            ) {
                Icon(
                    modifier = Modifier
                        .padding(3.dp),
                    painter = painterResource(habit.icon.resource),
                    contentDescription = null
                )
            }
            Text(
                text = habit.name,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.weight(1F))

        val lastFiveDays = getLastFiveDays()
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            lastFiveDays.forEachIndexed { index, day ->
                // TODO click to increase day counter
                val date: LocalDate = getPriorDayProgress(4 - index)
                val progress: HabitEntry? = completions[date]
                val color: Color = calculateColor(habit.color, desaturatedColor, progress)
                ShortListDay(
                    modifier = Modifier
                        .combinedClickable(
                            onClick = {
                                if (allowActions) onAction.invoke(HabitListAction.OnHabitProgressClick(habit, date, true))
                            },
                            onLongClick = {
                                if (allowActions) onAction.invoke(HabitListAction.OnHabitProgressClick(habit, date, false))
                            }
                        ),
                    name = day,
                    color = color
                )
            }
        }
    }
}

@Composable
private fun getLastFiveDays(): List<String> {
    val today = localDate()
    val days = mutableListOf<String>()

    for (i in 0..4) {
        val date = today.minus(i, DateTimeUnit.DAY)
        val dayOfWeek = date.dayOfWeek
        val dayName = dayOfWeek.locale()
        days.add(dayName)
    }

    return days.reversed()
}

@Composable
private fun ShortListDay(
    modifier: Modifier = Modifier,
    name: String,
    color: Color
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            style = MaterialTheme.typography.labelSmall
        )
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(color, shape = RoundedCornerShape(6.dp))
        )
    }
}
