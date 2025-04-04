package me.sosedik.habitrack.presentation.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import habitrack.composeapp.generated.resources.Res
import habitrack.composeapp.generated.resources.habit_details_action_archive_cancel
import habitrack.composeapp.generated.resources.habit_details_action_archive_confirm
import habitrack.composeapp.generated.resources.habit_details_action_archive_delete
import habitrack.composeapp.generated.resources.habit_details_action_archive_description
import habitrack.composeapp.generated.resources.habit_details_action_archive_title
import habitrack.composeapp.generated.resources.habit_details_action_desc_archive
import habitrack.composeapp.generated.resources.habit_details_action_desc_calendar
import habitrack.composeapp.generated.resources.habit_details_action_desc_edit
import habitrack.composeapp.generated.resources.habit_details_action_desc_share
import habitrack.composeapp.generated.resources.habit_details_desc_streak
import habitrack.composeapp.generated.resources.habit_details_progress_completed
import habitrack.composeapp.generated.resources.habit_details_progress_in_progress
import habitrack.composeapp.generated.resources.habit_details_progress_not_started
import habitrack.composeapp.generated.resources.habits_header_first_half
import habitrack.composeapp.generated.resources.habits_header_second_half
import habitrack.composeapp.generated.resources.ui_add_circle_24px
import habitrack.composeapp.generated.resources.ui_archive_24px
import habitrack.composeapp.generated.resources.ui_bar_chart_24px
import habitrack.composeapp.generated.resources.ui_calendar_month_24px
import habitrack.composeapp.generated.resources.ui_circle_24px
import habitrack.composeapp.generated.resources.ui_desc_add_habit
import habitrack.composeapp.generated.resources.ui_desc_settings
import habitrack.composeapp.generated.resources.ui_desc_stats
import habitrack.composeapp.generated.resources.ui_pending_24px
import habitrack.composeapp.generated.resources.ui_rocket_24px
import habitrack.composeapp.generated.resources.ui_settings_24px
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import me.sosedik.habitrack.data.domain.Habit
import me.sosedik.habitrack.data.domain.HabitEntry
import me.sosedik.habitrack.presentation.component.FilterCategoryChip
import me.sosedik.habitrack.presentation.component.HabitCalendarProgressions
import me.sosedik.habitrack.presentation.component.HabitIcon
import me.sosedik.habitrack.presentation.component.ShortListHabit
import me.sosedik.habitrack.presentation.theme.IconCache
import me.sosedik.habitrack.presentation.viewmodel.HabitListAction
import me.sosedik.habitrack.presentation.viewmodel.HabitListState
import me.sosedik.habitrack.presentation.viewmodel.HabitListViewModel
import me.sosedik.habitrack.util.calculateColor
import me.sosedik.habitrack.util.getCurrentDayOfWeek
import me.sosedik.habitrack.util.getDesaturatedColor
import me.sosedik.habitrack.util.localDate
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import sh.calvin.reorderable.ReorderableItem
import sh.calvin.reorderable.rememberReorderableLazyListState

@Composable
fun HabitListScreenRoot(
    viewModel: HabitListViewModel = koinViewModel(),
    iconCache: IconCache,
    onSettings: () -> Unit,
    onNewHabitCreation: () -> Unit,
    onHabitEdit: (Habit) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val habits: LazyPagingItems<Habit> = viewModel.habits.collectAsLazyPagingItems()

    HabitListScreen(
        iconCache = iconCache,
        state = state,
        habits = habits,
        onOrderUpdate = {
            viewModel.updateHabitOrder(it)
        },
        onAction = { action ->
            when (action) {
                HabitListAction.OnOpenSettings -> onSettings()
                HabitListAction.OnNewHabitAdd -> onNewHabitCreation()
                is HabitListAction.OnHabitEdit -> onHabitEdit(action.habit)
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun HabitListScreen(
    iconCache: IconCache,
    state: HabitListState,
    habits: LazyPagingItems<Habit>,
    onOrderUpdate: suspend (HabitListAction.OnOrderUpdate) -> Unit,
    onAction: (HabitListAction) -> Unit
) {
    val blurValue by animateFloatAsState(targetValue = if (state.focusedHabit != null) 20F else 0F)
    var showCalendar by remember { mutableStateOf(false) }

    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    BackHandler(enabled = state.focusedHabit != null) {
        onAction.invoke(HabitListAction.OnFocusCancel)
    }

    Surface(
        modifier = Modifier
            .blur(blurValue.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp)
        ) {
            Header(
                state = state,
                onAction = onAction
            )

            CategoryFilters(
                iconCache = iconCache,
                state = state,
                onAction = onAction
            )

            Spacer(modifier = Modifier.height(10.dp))

            HabitsList(
                iconCache = iconCache,
                state = state,
                habits = habits,
                onOrderUpdate = onOrderUpdate,
                onAction = onAction
            )
        }
    }

    state.focusedHabit?.let { focusedHabit ->
        val completions = state.habitProgressions[focusedHabit.id] ?: emptyMap()

        Box(
            modifier = Modifier
                .clickable(
                    onClick = {
                        onAction.invoke(HabitListAction.OnFocusCancel)
                    },
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                )
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .clickable(
                        onClick = { /* Prevent parent clicks */ },
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ),
                color = Color.Transparent,
                contentColor = contentColorFor(MaterialTheme.colorScheme.surface)
            ) {
                FocusedHabit(
                    iconCache = iconCache,
                    habit = focusedHabit,
                    completions = completions,
                    allowActions = !state.updatingData,
                    onAction = { action ->
                        when (action) {
                            HabitListAction.OnCalendarActionClick -> {
                                showCalendar = true
                            }
                            else -> Unit
                        }
                        onAction(action)
                    }
                )
            }
        }

        if (showCalendar) {
            ModalBottomSheet(
                sheetState = bottomSheetState,
                onDismissRequest = {
                    showCalendar = false
                }
            ) {
                HabitCalendarProgressions(
                    habit = focusedHabit,
                    completions = completions,
                    firstDayOfWeek = state.firstDayOfWeek,
                    allowActions = !state.updatingData,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun Header(
    state: HabitListState,
    onAction: (HabitListAction) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        HeaderButton(
            icon = painterResource(Res.drawable.ui_settings_24px),
            contentDescription = stringResource(Res.string.ui_desc_settings),
            onClick = {
                onAction.invoke(HabitListAction.OnOpenSettings)
            }
        )

        Spacer(modifier = Modifier.width(16.dp))

        HeaderText(
            text = stringResource(Res.string.habits_header_first_half),
            color = MaterialTheme.colorScheme.onSurface
        )
        HeaderText(
            text = stringResource(Res.string.habits_header_second_half),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.weight(1F))

        HeaderButton(
            icon = painterResource(Res.drawable.ui_bar_chart_24px),
            contentDescription = stringResource(Res.string.ui_desc_stats),
            onClick = {
                // TODO Open stats
            }
        )
        HeaderButton(
            icon = painterResource(Res.drawable.ui_add_circle_24px),
            contentDescription = stringResource(Res.string.ui_desc_add_habit),
            onClick = {
                if (!state.updatingData) onAction.invoke(HabitListAction.OnNewHabitAdd)
            }
        )
    }
}

@Composable
private fun HeaderText(
    text: String,
    color: Color
) {
    Text(
        text = text,
        color = color,
        style = MaterialTheme.typography.headlineSmall
    )
}

@Composable
private fun HeaderButton(
    icon: Painter,
    contentDescription: String?,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .size(40.dp),
        onClick = onClick
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription
        )
    }
}

@Composable
private fun CategoryFilters(
    iconCache: IconCache,
    state: HabitListState,
    onAction: (HabitListAction) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(
            items = state.categories,
            key = { it.id }
        ) { category ->
            FilterCategoryChip(
                iconCache = iconCache,
                habitCategory = category,
                selected = category == state.filteredCategory,
                allowActions = !state.updatingData,
                onClick = {
                    onAction.invoke(HabitListAction.OnHabitCategoryClick(category))
                }
            )
        }
    }
}

@Composable
private fun HabitsList(
    iconCache: IconCache,
    state: HabitListState,
    habits: LazyPagingItems<Habit>,
    onOrderUpdate: suspend (HabitListAction.OnOrderUpdate) -> Unit,
    onAction: (HabitListAction) -> Unit
) {
    val listState = rememberLazyListState()
    val reorderableLazyListState = rememberReorderableLazyListState(listState) { from, to ->
        onOrderUpdate(HabitListAction.OnOrderUpdate(habits, from.index, to.index))
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        items(
            count = habits.itemCount,
            key = habits.itemKey { it.id }
        ) { index ->
            val habit: Habit? = habits[index]
            if (habit == null) {
                ShortListHabit(
                    iconCache = iconCache,
                    habit = null,
                    completions = emptyMap(),
                    allowActions = false,
                    onAction = onAction
                )
            } else {
                ReorderableItem(reorderableLazyListState, key = habit.id) { isDragging ->
                    val elevation by animateDpAsState(if (isDragging) 4.dp else 0.dp)

                    Surface(shadowElevation = elevation) {
                        ShortListHabit( // TODO more views
                            modifier = Modifier
                                .longPressDraggableHandle(),
                            iconCache = iconCache,
                            habit = habit,
                            completions = state.habitProgressions[habit.id] ?: emptyMap(),
                            allowActions = !state.updatingData,
                            onAction = onAction
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FocusedHabit(
    iconCache: IconCache,
    habit: Habit,
    completions: Map<LocalDate, HabitEntry>,
    allowActions: Boolean,
    onAction: (HabitListAction) -> Unit
) {
    var showDeletionConfirmation by remember { mutableStateOf(false) }

    var desaturatedColor by remember { mutableStateOf(getDesaturatedColor(habit.color)) }

    var lastCalculatedDate by remember { mutableStateOf(localDate()) }
    var streak by remember { mutableIntStateOf(
        calculateStreak(
            startDate = lastCalculatedDate,
            completions = completions
        )
    ) }

    LaunchedEffect(habit) {
        desaturatedColor = getDesaturatedColor(habit.color)
    }

    val dayCompletions: Int = completions[lastCalculatedDate]?.count ?: 0

    // TODO Streak counter
    // Recalculate the streak if the day has changed
    val currentDate = localDate()
    LaunchedEffect(currentDate, habit) {
        if (currentDate != lastCalculatedDate) {
            streak = calculateStreak(
                    startDate = currentDate,
                    completions = completions
                )
            lastCalculatedDate = currentDate
        }
    }
    LaunchedEffect(completions, habit) {
        streak = calculateStreak(
                startDate = currentDate,
                completions = completions
            )
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
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
                HabitIcon(
                    iconCache = iconCache,
                    modifier = Modifier
                        .padding(3.dp),
                    id = habit.icon
                )
            }
            Text(
                text = habit.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.weight(1F))
            IconButton(
                onClick = {
                    onAction.invoke(HabitListAction.OnFocusCancel)
                }
            ) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = null
                )
            }
        }

        habit.description?.let { description ->
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        CalendarGrid(
            activeColor = habit.color,
            inactiveColor = desaturatedColor,
            completions = completions
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .combinedClickable(
                        onClick = {
                            if (allowActions) onAction.invoke(HabitListAction.OnHabitProgressClick(habit, currentDate, true))
                        },
                        onLongClick = {
                            if (allowActions) onAction.invoke(HabitListAction.OnHabitProgressClick(habit, currentDate, false))
                        }
                    )
                    .weight(1F)
                    .background(habit.color, RoundedCornerShape(8.dp))
                    .padding(5.dp)
            ) {
                val total = habit.dailyLimit

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter =
                            if (dayCompletions == 0)
                                painterResource(Res.drawable.ui_circle_24px)
                            else if (dayCompletions < total)
                                painterResource(Res.drawable.ui_pending_24px)
                            else
                                rememberVectorPainter(Icons.Outlined.CheckCircle),
                        contentDescription = stringResource(Res.string.habit_details_desc_streak)
                    )
                    Text(
                        text =
                            (stringResource(
                                if (dayCompletions == 0)
                                    Res.string.habit_details_progress_not_started
                                else if (dayCompletions < total)
                                    Res.string.habit_details_progress_in_progress
                                else
                                    Res.string.habit_details_progress_completed
                            )) + (
                                if (habit.hasDailyLimit()) "  $dayCompletions / $total"
                                else (if (dayCompletions == 0) "" else "  ×$dayCompletions")
                            ),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Box(
                modifier = Modifier
                    .background(desaturatedColor, RoundedCornerShape(8.dp))
                    .padding(5.dp)
            ) {
                Text(
                    text = "Daily", // TODO Streak counter
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Box(
                modifier = Modifier
                    .background(desaturatedColor, RoundedCornerShape(8.dp))
                    .padding(5.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        modifier = Modifier
                            .size(20.dp),
                        painter = painterResource(Res.drawable.ui_rocket_24px),
                        contentDescription = stringResource(Res.string.habit_details_desc_streak)
                    )
                    Text(
                        text = streak.toString(),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FocusedHabitAction(
                icon = painterResource(Res.drawable.ui_calendar_month_24px),
                contentDescription = stringResource(Res.string.habit_details_action_desc_calendar),
                onClick = {
                    if (allowActions) onAction.invoke(HabitListAction.OnCalendarActionClick)
                }
            )
            FocusedHabitAction(
                icon = rememberVectorPainter(Icons.Default.Edit),
                contentDescription = stringResource(Res.string.habit_details_action_desc_edit),
                onClick = {
                    if (allowActions) onAction.invoke(HabitListAction.OnHabitEdit(habit))
                }
            )
            FocusedHabitAction(
                icon = rememberVectorPainter(Icons.Default.Delete),
                contentDescription = stringResource(Res.string.habit_details_action_desc_archive),
                onClick = {
                    if (allowActions) showDeletionConfirmation = true
                }
            )
            FocusedHabitAction(
                icon = rememberVectorPainter(Icons.Default.Share),
                contentDescription = stringResource(Res.string.habit_details_action_desc_share),
                onClick = {
                    if (allowActions) {}
                    // TODO Share habit
                }
            )
        }
    }

    if (showDeletionConfirmation) {
        HabitArchivalDialogue(
            habit = habit,
            onDismissRequest = { showDeletionConfirmation = false },
            onAction = onAction
        )
    }
}

private fun calculateStreak(
    startDate: LocalDate,
    completions: Map<LocalDate, HabitEntry>
): Int {
    var streak = 0
    var streakDay = startDate
    if (completions[streakDay] == null) streakDay = streakDay.minus(1, DateTimeUnit.DAY)
    while (completions[streakDay] != null && completions[streakDay]!!.isCompleted()) {
        streak++
        streakDay = streakDay.minus(1, DateTimeUnit.DAY)
    }
    return streak
}

@Composable
fun HabitArchivalDialogue(
    habit: Habit,
    onDismissRequest: () -> Unit,
    onAction: (HabitListAction) -> Unit
) {
    AlertDialog(
        modifier = Modifier
            .padding(16.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(Res.string.habit_details_action_archive_title))
        },
        text = {
            Text(text = stringResource(Res.string.habit_details_action_archive_description))
        },
        confirmButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        onDismissRequest()
                        onAction.invoke(HabitListAction.OnHabitDelete(habit))
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(Res.string.habit_details_action_archive_delete))
                }
                TextButton(
                    onClick = {
                        onDismissRequest()
                        onAction.invoke(HabitListAction.OnHabitArchival(habit))
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ui_archive_24px),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = stringResource(Res.string.habit_details_action_archive_confirm))
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(text = stringResource(Res.string.habit_details_action_archive_cancel))
            }
        }
    )
}

@Composable
private fun CalendarGrid(
    activeColor: Color,
    inactiveColor: Color,
    completions: Map<LocalDate, HabitEntry>
) { // TODO Tracking progress
    val weeksInYear = 53 // weeks to render
    val today: LocalDate = localDate()
    val currentDayOfTheWeek = getCurrentDayOfWeek()

    LazyRow(
        state = rememberLazyListState(initialFirstVisibleItemIndex = weeksInYear - 1),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(weeksInYear) { week ->
            val currentWeek = week == weeksInYear - 1
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                repeat(7) { day ->
                    val date: LocalDate = today.minus(weeksInYear - week - 1, DateTimeUnit.WEEK).plus(day - currentDayOfTheWeek + 1, DateTimeUnit.DAY)
                    val progress: HabitEntry? = completions[date]
                    val color: Color = calculateColor(activeColor, inactiveColor, progress)
                    val currentDay = currentWeek && day == currentDayOfTheWeek - 1
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(color, RoundedCornerShape(4.dp))
                            .let {
                                if (currentDay)
                                    it.border(1.dp, Color.White, RoundedCornerShape(4.dp)) // TODO Proper color
                                else
                                    it
                            }
                    )
                }
            }
        }
    }
}

@Composable
private fun FocusedHabitAction(
    icon: Painter,
    contentDescription: String?,
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .size(40.dp),
        onClick = onClick
    ) {
        Icon(
            painter = icon,
            contentDescription = contentDescription
        )
    }
}
