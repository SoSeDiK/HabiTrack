package me.sosedik.habitrack

import android.content.res.Configuration
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.minus
import me.sosedik.habitrack.data.domain.Habit
import me.sosedik.habitrack.data.domain.HabitCategory
import me.sosedik.habitrack.data.domain.HabitEntry
import me.sosedik.habitrack.data.domain.HabitIcon
import me.sosedik.habitrack.presentation.component.HabitCalendarProgressions
import me.sosedik.habitrack.presentation.screen.FocusedHabit
import me.sosedik.habitrack.presentation.screen.HabitCreationScreen
import me.sosedik.habitrack.presentation.screen.HabitListScreen
import me.sosedik.habitrack.presentation.theme.HabiTrackTheme
import me.sosedik.habitrack.presentation.viewmodel.HabitCreationState
import me.sosedik.habitrack.presentation.viewmodel.HabitListState
import me.sosedik.habitrack.util.localDate

private val categories = (1..10).map {
    HabitCategory(
        id = it.toLong(),
        name = "Cat $it",
        icon = HabitIcon.getById("star")
    )
}
private val habits = (1..10).map {
    Habit(
        id = it.toLong(),
        name = "Habit $it",
        description = null,
        dailyLimit = 1,
        categories = emptyList(),
        icon = HabitIcon.getById("star"),
        color = Color.Red
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomePreview() {
    HabiTrackTheme {
        Surface {
            HabitListScreen(
                state = HabitListState(
                    categories = categories,
                    filteredHabits = habits
                ),
                onAction = {}
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HabitCreationPreview() {
    HabiTrackTheme {
        Surface {
            HabitCreationScreen(
                state = HabitCreationState(
                    allCategories = categories
                ),
                nameState = rememberTextFieldState(),
                descriptionState = rememberTextFieldState(),
                onAction = {}
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun FocusedHabitPreview() {
    HabiTrackTheme {
        Surface {
            FocusedHabit(
                habit = habits[0],
                completions = mapOf(
                    localDate().minus(0, DateTimeUnit.WEEK).minus(1, DateTimeUnit.DAY) to HabitEntry(1, 1, Clock.System.now(), 1, 1),
                    localDate().minus(1, DateTimeUnit.WEEK).minus(3, DateTimeUnit.DAY) to HabitEntry(2, 1, Clock.System.now(), 1, 1),
                    localDate().minus(1, DateTimeUnit.WEEK).minus(2, DateTimeUnit.DAY) to HabitEntry(3, 1, Clock.System.now(), 1, 1)
                ),
                allowActions = true,
                onAction = {}
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HabitCalendarProgressionsPreview() {
    HabiTrackTheme {
        Surface {
            HabitCalendarProgressions(
                habit = habits[0],
                completions = mapOf(
                    localDate().minus(0, DateTimeUnit.WEEK).minus(1, DateTimeUnit.DAY) to HabitEntry(1, 1, Clock.System.now(), 1, 1),
                    localDate().minus(1, DateTimeUnit.WEEK).minus(3, DateTimeUnit.DAY) to HabitEntry(2, 1, Clock.System.now(), 1, 1),
                    localDate().minus(1, DateTimeUnit.WEEK).minus(2, DateTimeUnit.DAY) to HabitEntry(3, 1, Clock.System.now(), 1, 1)
                ),
                allowActions = true,
                onAction = {}
            )
        }
    }
}
