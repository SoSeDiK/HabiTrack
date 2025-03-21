package me.sosedik.habitrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.LazyPagingItems
import androidx.paging.map
import com.kizitonwose.calendar.core.YearMonth
import com.kizitonwose.calendar.core.minusMonths
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import me.sosedik.habitrack.data.domain.Habit
import me.sosedik.habitrack.data.domain.HabitCategory
import me.sosedik.habitrack.data.domain.HabitCategoryRepository
import me.sosedik.habitrack.data.domain.HabitEntry
import me.sosedik.habitrack.data.domain.HabitEntryRepository
import me.sosedik.habitrack.data.domain.HabitRepository
import me.sosedik.habitrack.data.domain.SettingsRepository
import me.sosedik.habitrack.util.getCurrentDayOfWeek
import me.sosedik.habitrack.util.getMonthRange
import me.sosedik.habitrack.util.getPriorDaysRangeUTC
import me.sosedik.habitrack.util.getStartOfDayInUTC
import me.sosedik.habitrack.util.localDate

class HabitListViewModel(
    private val settingsRepository: SettingsRepository,
    private val habitCategoryRepository: HabitCategoryRepository,
    private val habitRepository: HabitRepository,
    private val habitEntryRepository: HabitEntryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HabitListState())
    val state = _state
        .onStart {
            observeHabitCategories()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val habits: StateFlow<PagingData<Habit>> = _state
        .map { it.filteredCategory }
        .distinctUntilChanged()
        .flatMapLatest { filteredCategory ->
            val habitsFlow = filteredCategory?.let { category ->
                habitRepository.getActiveHabitsByCategory(category)
            } ?: habitRepository.getAllActiveHabits()

            habitsFlow.map { pagingData -> // TODO remove this monstrosity
                pagingData.map { habit ->
                    val weekRange = getPriorDaysRangeUTC(dayOffset = 5)
                    val habitProgressions: MutableMap<Long, Map<LocalDate, HabitEntry>> = _state.value.habitProgressions.toMutableMap()
                    val completions: Map<LocalDate, HabitEntry> = habitEntryRepository.fetchByRange(habit, weekRange.first, weekRange.second)
                    if (completions.isNotEmpty()) {
                        val comp = habitProgressions.getOrElse(habit.id) { emptyMap() }.toMutableMap()
                        comp.putAll(completions)
                        habitProgressions[habit.id] = comp.toMap()
                    }
                    _state.update { it.copy(
                        habitProgressions = habitProgressions.toMap()
                    ) }
                    habit
                }
            }
        }
        .cachedIn(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

    private var observeWeekStartJob: Job? = null
    private var observeHabitCategoriesJob: Job? = null
    private var observeFocusedHabitJob: Job? = null

    init {
        observeWeekStart()
        observeHabitCategories()
    }

    private fun observeWeekStart() {
        observeWeekStartJob?.cancel()
        observeWeekStartJob = settingsRepository.getStartWeekOnSunday().onEach {
            refreshPreference { state ->
                state.copy(
                    firstDayOfWeek = if (it) DayOfWeek.SUNDAY else DayOfWeek.MONDAY
                )
            }
        }.launchIn(viewModelScope)
    }

    fun onAction(action: HabitListAction) {
        when (action) {
            is HabitListAction.OnHabitCategoryClick -> onHabitCategoryClick(action.category)
            is HabitListAction.OnHabitClick -> {
                _state.update {
                    it.copy(
                        updatingData = true
                    )
                }
                listenForFocusedHabitUpdates(action.habit)
                val offset = 53 * 7 - getCurrentDayOfWeek() - 1
                val range = getPriorDaysRangeUTC(dayOffset = offset)
                val habitProgressions: MutableMap<Long, Map<LocalDate, HabitEntry>> = _state.value.habitProgressions.toMutableMap()
                viewModelScope.launch {
                    val completions: Map<LocalDate, HabitEntry> = habitEntryRepository.fetchByRange(action.habit, range.first, range.second)
                    habitProgressions[action.habit.id] = completions
                    _state.update {
                        it.copy(
                            updatingData = false,
                            habitProgressions = habitProgressions.toMap()
                        )
                    }
                }
            }
            is HabitListAction.OnHabitDelete -> {
                _state.update {
                    it.copy(updatingData = true)
                }
                viewModelScope.launch {
                    listenForFocusedHabitUpdates(null)
                    habitRepository.deleteHabit(action.habit)
                    _state.update {
                        it.copy(updatingData = false)
                    }
                }
            }
            is HabitListAction.OnHabitArchival -> {
                _state.update {
                    it.copy(updatingData = true)
                }
                viewModelScope.launch {
                    listenForFocusedHabitUpdates(null)
                    habitRepository.updateArchivedState(action.habit, true)
                    _state.update {
                        it.copy(updatingData = false)
                    }
                }
            }
            is HabitListAction.OnHabitProgressClick -> {
                val date = action.date
                val today = localDate()
                if (date.year > today.year) return
                if (date.year == today.year) {
                    if (date.monthNumber > today.monthNumber) return
                    if (date.monthNumber == today.monthNumber && date.dayOfMonth > today.dayOfMonth) return
                }

                updateHabitProgress(action.habit, date, action.increase)
            }
            HabitListAction.OnFocusCancel -> {
                listenForFocusedHabitUpdates(null)
            }
            is HabitListAction.OnCalendarMonthLoad -> {
                val habit = _state.value.focusedHabit ?: return
                
                _state.update {
                    it.copy(
                        updatingData = true
                    )
                }

                val yearMonth = action.yearMonth.minusMonths(3)
                val range = getMonthRange(yearMonth)
                val habitProgressions: MutableMap<Long, Map<LocalDate, HabitEntry>> = _state.value.habitProgressions.toMutableMap()
                viewModelScope.launch {
                    val currentCompletions = habitProgressions[habit.id]?.toMutableMap() ?: hashMapOf()
                    val completions: Map<LocalDate, HabitEntry> = habitEntryRepository.fetchByRange(habit, range.first, range.second)
                    currentCompletions.putAll(completions)
                    habitProgressions[habit.id] = currentCompletions.toMap()
                    _state.update {
                        it.copy(
                            updatingData = false,
                            habitProgressions = habitProgressions.toMap()
                        )
                    }
                }
            }
            else -> Unit
        }
    }

    suspend fun updateHabitOrder(action: HabitListAction.OnOrderUpdate) {
        val fromIndex = action.fromIndex
        val toIndex = action.toIndex
        if (fromIndex == toIndex) return

        val currentHabits = action.habits.itemSnapshotList.items.toMutableList()
        val habitToMove = currentHabits[fromIndex]
        val newOrder = currentHabits[toIndex].order

        val step = if (toIndex < fromIndex) 1 else -1
        val range = if (toIndex < fromIndex) toIndex ..< fromIndex else fromIndex + 1..toIndex

        for (i in range) {
            currentHabits[i] = currentHabits[i].copy(order = currentHabits[i].order + step)
        }

        currentHabits.removeAt(fromIndex)
        currentHabits.add(toIndex, habitToMove.copy(order = newOrder))

        habitRepository.update(currentHabits)
    }

    private fun updateHabitProgress(
        habit: Habit,
        date: LocalDate,
        increase: Boolean
    ) {
        _state.update {
            it.copy(updatingData = true)
        }

        val allCompletions = _state.value.habitProgressions.toMutableMap()
        val completions: MutableMap<LocalDate, HabitEntry> = allCompletions[habit.id]?.toMutableMap() ?: hashMapOf()

        var entry: HabitEntry? = completions[date]

        var count = entry?.count ?: 0
        if (increase)
            count++
        else
            count--

        count =
            if (habit.hasDailyLimit() && count > habit.dailyLimit) 0
            else count.coerceIn(0, DAILY_LIMIT_MAX)

        entry = entry?.copy(
            count = count
        )
            ?: HabitEntry(
                id = 0L,
                habitId = habit.id,
                date = getStartOfDayInUTC(date),
                count = count,
                limit = habit.dailyLimit
            )

        viewModelScope.launch {
            if (entry != null) {
                entry = habitEntryRepository.addEntry(entry!!)
                if (entry!!.id != 0L)
                    completions[date] = entry!!
                allCompletions[habit.id] = completions.toMap()
            }

            _state.update {
                it.copy(
                    updatingData = false,
                    habitProgressions = allCompletions.toMap()
                )
            }
        }
    }

    private fun onHabitCategoryClick(category: HabitCategory) {
        val filteredCategory: HabitCategory? = if (_state.value.filteredCategory == category) null else category
        _state.update {
            it.copy(
                filteredCategory = filteredCategory
            )
        }
    }

    private fun listenForFocusedHabitUpdates(habit: Habit?) {
        observeFocusedHabitJob?.cancel()
        _state.update {
            it.copy(focusedHabit = habit)
        }
        if (habit == null) {
            observeFocusedHabitJob = null
            return
        }

        observeFocusedHabitJob = viewModelScope.launch {
            habitRepository.getHabitUpdates(habit).collect { updatedHabit ->
                if (updatedHabit == null || updatedHabit.archived) {
                    listenForFocusedHabitUpdates(null)
                } else {
                    if (_state.value.focusedHabit?.id == updatedHabit.id) {
                        _state.update {
                            it.copy(focusedHabit = updatedHabit)
                        }
                    }
                }
            }
        }
    }

    private fun observeHabitCategories() {
        observeHabitCategoriesJob?.cancel()
        observeHabitCategoriesJob = habitCategoryRepository
            .getCategoriesWithHabits()
            .onEach { habitCategories ->
                _state.update { it.copy(
                    categories = habitCategories
                ) }
            }
            .launchIn(viewModelScope)
    }

    private fun refreshPreference(action: (HabitListState) -> HabitListState) {
        viewModelScope.launch {
            _state.update {
                action(it)
            }
        }
    }

}

sealed interface HabitListAction {

    data object OnOpenSettings : HabitListAction
    data object OnNewHabitAdd : HabitListAction
    data class OnHabitCategoryClick(val category: HabitCategory) : HabitListAction
    data class OnHabitClick(val habit: Habit) : HabitListAction
    data class OnHabitEdit(val habit: Habit) : HabitListAction
    data class OnHabitDelete(val habit: Habit) : HabitListAction
    data class OnHabitArchival(val habit: Habit) : HabitListAction
    data class OnHabitProgressClick(val habit: Habit, val date: LocalDate, val increase: Boolean) : HabitListAction
    data object OnFocusCancel : HabitListAction
    data object OnCalendarActionClick : HabitListAction
    data class OnCalendarMonthLoad(val yearMonth: YearMonth) : HabitListAction
    data class OnOrderUpdate(val habits: LazyPagingItems<Habit>, val fromIndex: Int, val toIndex: Int) : HabitListAction

}

data class HabitListState(
    val updatingData: Boolean = false,
    val firstDayOfWeek: DayOfWeek = DayOfWeek.MONDAY,
    val filteredCategory: HabitCategory? = null,
    val focusedHabit: Habit? = null,
    val categories: List<HabitCategory> = emptyList(),
    val habitProgressions: Map<Long, Map<LocalDate, HabitEntry>> = emptyMap()
)
