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
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.minus
import me.sosedik.habitrack.data.domain.Habit
import me.sosedik.habitrack.data.domain.HabitCategory
import me.sosedik.habitrack.data.domain.HabitEntry
import me.sosedik.habitrack.presentation.component.HabitCalendarProgressions
import me.sosedik.habitrack.presentation.screen.CategoriesPicker
import me.sosedik.habitrack.presentation.screen.ColorPicker
import me.sosedik.habitrack.presentation.screen.FocusedHabit
import me.sosedik.habitrack.presentation.screen.GeneralSettingsScreen
import me.sosedik.habitrack.presentation.screen.HabitCreationScreen
import me.sosedik.habitrack.presentation.screen.HabitListScreen
import me.sosedik.habitrack.presentation.screen.PRE_PICKED_ICONS
import me.sosedik.habitrack.presentation.screen.SettingsScreen
import me.sosedik.habitrack.presentation.theme.HabiTrackTheme
import me.sosedik.habitrack.presentation.theme.IconCache
import me.sosedik.habitrack.presentation.viewmodel.GeneralSettingsState
import me.sosedik.habitrack.presentation.viewmodel.HabitCreationState
import me.sosedik.habitrack.presentation.viewmodel.HabitListState
import me.sosedik.habitrack.util.PRE_PICKED_COLORS
import me.sosedik.habitrack.util.localDate

private val iconCache = IconCache(
    mappings = mapOf(
        "default" to "*",
        PRE_PICKED_ICONS[0] to "^",
        PRE_PICKED_ICONS[1] to "0",
        PRE_PICKED_ICONS[2] to "@",
        PRE_PICKED_ICONS[3] to "_",
        PRE_PICKED_ICONS[4] to "$",
        PRE_PICKED_ICONS[5] to "3"
    ),
    fontFamily = null
)

private val categories = (1..10).map {
    HabitCategory(
        id = it.toLong(),
        name = "Cat $it",
        icon = PRE_PICKED_ICONS[it % PRE_PICKED_ICONS.size]
    )
}
private val habits = (1..10).map {
    Habit(
        id = it.toLong(),
        name = "Habit $it",
        description = "This is habit $it",
        dailyLimit = 1,
        icon = PRE_PICKED_ICONS[it % PRE_PICKED_ICONS.size],
        color = PRE_PICKED_COLORS[it % PRE_PICKED_COLORS.size],
        order = it
    )
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun HomePreview() {
    var list by remember { mutableStateOf(habits) }

    HabiTrackTheme {
        Surface {
            HabitListScreen(
                iconCache = iconCache,
                state = HabitListState(
                    categories = categories
                ),
                habits = flowOf(PagingData.from(list)).collectAsLazyPagingItems(),
                onOrderUpdate = {
                    list = list.toMutableList().apply {
                        add(it.toIndex, removeAt(it.fromIndex))
                    }
                },
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
                iconCache = iconCache,
                state = HabitCreationState(
                    allCategories = categories,
                    pickedCategories = categories.takeLast(8)
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
fun CategoriesPickerPreview() {
    var pickedCategories by remember { mutableStateOf(categories.takeLast(3)) }

    HabiTrackTheme {
        Surface {
            CategoriesPicker(
                iconCache = iconCache,
                state = HabitCreationState(
                    allCategories = categories,
                    pickedCategories = categories.takeLast(3)
                ),
                pickedCategories = pickedCategories,
                onCategoryClick = { category ->
                    val categories = pickedCategories.toMutableList()
                    if (!categories.remove(category))
                        categories.add(category)
                    pickedCategories = categories.toList()
                },
                onAction = {}
            )
        }
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun ColorPickerPreview() {
    HabiTrackTheme {
        Surface {
            ColorPicker(
                state = HabitCreationState(
                    allCategories = categories
                ),
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
                iconCache = iconCache,
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
