package me.sosedik.habitrack

import android.content.res.Configuration
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.minus
import me.sosedik.habitrack.data.domain.Habit
import me.sosedik.habitrack.data.domain.HabitCategory
import me.sosedik.habitrack.data.domain.HabitEntry
import me.sosedik.habitrack.data.domain.HabitIcon
import me.sosedik.habitrack.presentation.component.HabitCalendarProgressions
import me.sosedik.habitrack.presentation.screen.ColorPicker
import me.sosedik.habitrack.presentation.screen.FocusedHabit
import me.sosedik.habitrack.presentation.screen.GeneralSettingsScreen
import me.sosedik.habitrack.presentation.screen.HabitCreationScreen
import me.sosedik.habitrack.presentation.screen.HabitListScreen
import me.sosedik.habitrack.presentation.screen.SettingsScreen
import me.sosedik.habitrack.presentation.theme.HabiTrackTheme
import me.sosedik.habitrack.presentation.viewmodel.GeneralSettingsState
import me.sosedik.habitrack.presentation.viewmodel.HabitCreationAction
import me.sosedik.habitrack.presentation.viewmodel.HabitCreationState
import me.sosedik.habitrack.presentation.viewmodel.HabitListState
import me.sosedik.habitrack.util.PRE_PICKED_COLORS
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
        description = "This is habit $it",
        dailyLimit = 1,
        categories = emptyList(),
        icon = HabitIcon.getById("star"),
        color = PRE_PICKED_COLORS[0]
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
                nameState = rememberTextFieldState(initialText = "Sample habit"),
                descriptionState = rememberTextFieldState(initialText = "This is a sample habit"),
                onAction = {}
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ColorPickerPreview() {
    var state by remember { mutableStateOf(
        HabitCreationState(
            allCategories = categories
        )
    ) }

    HabiTrackTheme {
        Surface {
            ColorPicker(
                state = state,
                onAction = { action ->
                    when (action) {
                        is HabitCreationAction.UpdateCustomColor -> {
                            state = state.copy(
                                customColor = action.color
                            )
                        }
                        else -> Unit
                    }
                }
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HabitCalendarProgressionsPreview() {
    HabiTrackTheme {
        Surface {
            HabitCalendarProgressions(
                habit = habits[0],
                completions = mapOf(
                    localDate().minus(0, DateTimeUnit.WEEK).minus(1, DateTimeUnit.DAY) to HabitEntry(1, 1, Clock.System.now(), 1, 1),
                    localDate().minus(0, DateTimeUnit.WEEK).minus(2, DateTimeUnit.DAY) to HabitEntry(2, 1, Clock.System.now(), 2, 2),
                    localDate().minus(0, DateTimeUnit.WEEK).minus(3, DateTimeUnit.DAY) to HabitEntry(3, 1, Clock.System.now(), 3, 3),
                    localDate().minus(0, DateTimeUnit.WEEK).minus(4, DateTimeUnit.DAY) to HabitEntry(4, 1, Clock.System.now(), 4, 4),
                    localDate().minus(0, DateTimeUnit.WEEK).minus(5, DateTimeUnit.DAY) to HabitEntry(5, 1, Clock.System.now(), 5, 5),
                    localDate().minus(0, DateTimeUnit.WEEK).minus(6, DateTimeUnit.DAY) to HabitEntry(6, 1, Clock.System.now(), 6, 6),
                    localDate().minus(1, DateTimeUnit.WEEK).minus(2, DateTimeUnit.DAY) to HabitEntry(3, 1, Clock.System.now(), 1, 1),
                    localDate() to HabitEntry(4, 1, Clock.System.now(), 18, 20),
                ),
                firstDayOfWeek = DayOfWeek.MONDAY,
                allowActions = true,
                onAction = {}
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SettingsPreview() {
    HabiTrackTheme {
        Surface {
            SettingsScreen(
                onAction = {}
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun GeneralSettingsPreview() {
    HabiTrackTheme {
        Surface {
            GeneralSettingsScreen(
                state = GeneralSettingsState(
                    loadingData = false,
                    weekOnSunday = false
                ),
                onAction = {}
            )
        }
    }
}
