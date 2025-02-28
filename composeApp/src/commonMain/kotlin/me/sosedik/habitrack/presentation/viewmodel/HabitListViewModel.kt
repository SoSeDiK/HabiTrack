package me.sosedik.habitrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import me.sosedik.habitrack.data.database.HabitEntryEntity
import me.sosedik.habitrack.data.domain.Habit
import me.sosedik.habitrack.data.domain.HabitCategory
import me.sosedik.habitrack.data.domain.HabitCategoryRepository
import me.sosedik.habitrack.data.domain.HabitEntry
import me.sosedik.habitrack.data.domain.HabitEntryRepository
import me.sosedik.habitrack.data.domain.HabitRepository
import me.sosedik.habitrack.data.mapper.toDomain
import me.sosedik.habitrack.util.getCurrentDayOfWeek
import me.sosedik.habitrack.util.getPriorDaysRangeUTC
import me.sosedik.habitrack.util.getStartOfDayInUTC
import me.sosedik.habitrack.util.localDate

class HabitListViewModel(
    val habitCategoryRepository: HabitCategoryRepository,
    val habitRepository: HabitRepository,
    val habitEntryRepository: HabitEntryRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HabitListState())
    val state = _state
        .onStart {
            observeHabitCategories()
            observeHabits()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    private var observeHabitCategoriesJob: Job? = null
    private var observeHabitsJob: Job? = null

    fun onAction(action: HabitListAction) {
        when (action) {
            is HabitListAction.OnHabitCategoryClick -> onHabitCategoryClick(action.category)
            is HabitListAction.OnHabitClick -> {
                _state.update {
                    it.copy(
                        updatingData = true,
                        focusedHabit = action.habit
                    )
                }
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
                    habitRepository.deleteHabit(action.habit)
                    _state.update {
                        it.copy(
                            updatingData = false,
                            focusedHabit = null
                        )
                    }
                }
            }
            is HabitListAction.OnFocusedHabitProgressClick -> {
                val habit: Habit = _state.value.focusedHabit ?: return
                _state.update {
                    it.copy(updatingData = true)
                }

                val allCompletions = _state.value.habitProgressions.toMutableMap()
                val completions: MutableMap<LocalDate, HabitEntry> = allCompletions[habit.id]?.toMutableMap() ?: hashMapOf()

                val today = localDate()
                var entry: HabitEntry? = completions[today]

                var count = (entry?.count ?: 0) + 1
                if (count > habit.dailyLimit) count = 0
                if (count > 0) {
                    entry = if (entry == null) {
                        HabitEntryEntity(
                            habitId = habit.id,
                            date = getStartOfDayInUTC(),
                            count = count
                        ).toDomain()
                    } else {
                        entry.copy(
                            count = count
                        )
                    }
                }

                viewModelScope.launch {
                    if (entry != null) {
                        if (count > 0) {
                            entry = entry!!.copy(
                                id = habitEntryRepository.addEntry(entry!!)
                            )
                            completions.put(today, entry)
                        } else {
                            habitEntryRepository.deleteEntry(entry)
                            completions.remove(today)
                        }
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
            HabitListAction.OnFocusCancel -> {
                _state.update { it.copy(
                    focusedHabit = null
                ) }
            }
            else -> Unit
        }
    }

    private fun onHabitCategoryClick(category: HabitCategory) {
        val filteredCategory: HabitCategory? = if (_state.value.filteredCategory == category) null else category
        _state.update {
            it.copy(updatingData = true)
        }
        viewModelScope.launch {
            val habits: List<Habit> = habitRepository.getHabits().last()
            _state.update {
                it.copy(
                    updatingData = false,
                    filteredCategory = filteredCategory,
                    habits = if (filteredCategory == null) habits else habits.filter { habit ->
                        habit.categories.contains(filteredCategory)
                    }
                )
            }
        }
    }

    private fun observeHabitCategories() {
        observeHabitCategoriesJob?.cancel()
        observeHabitCategoriesJob = habitCategoryRepository
            .getHabitCategories()
            .onEach { habitCategories ->
                _state.update { it.copy(
                    categories = habitCategories
                ) }
            }
            .launchIn(viewModelScope)
    }

    private fun observeHabits() {
        observeHabitsJob?.cancel()
        observeHabitsJob = habitRepository
            .getHabits()
            .onEach { habits ->
                val filteredCategory: HabitCategory? = _state.value.filteredCategory
                val filteredHabits = if (filteredCategory == null) habits else habits.filter { habit ->
                        habit.categories.contains(filteredCategory)
                    }

                val weekRange = getPriorDaysRangeUTC(dayOffset = 5)
                val habitProgressions: MutableMap<Long, Map<LocalDate, HabitEntry>> = hashMapOf()
                filteredHabits.forEach { habit ->
                    val completions: Map<LocalDate, HabitEntry> = habitEntryRepository.fetchByRange(habit, weekRange.first, weekRange.second)
                    if (completions.isNotEmpty())
                        habitProgressions.put(habit.id, completions)
                }

                _state.update { it.copy(
                    habits = filteredHabits,
                    habitProgressions = habitProgressions
                ) }
            }
            .launchIn(viewModelScope)
    }

}

sealed interface HabitListAction {

    data object OnNewHabitAdd : HabitListAction
    data class OnHabitCategoryClick(val category: HabitCategory) : HabitListAction
    data class OnHabitClick(val habit: Habit) : HabitListAction
    data class OnHabitDelete(val habit: Habit) : HabitListAction
    data class OnFocusedHabitProgressClick(val habit: Habit) : HabitListAction
    data object OnFocusCancel : HabitListAction

}

data class HabitListState(
    val updatingData: Boolean = false,
    val filteredCategory: HabitCategory? = null,
    val focusedHabit: Habit? = null,
    val categories: List<HabitCategory> = emptyList(),
    val habits: List<Habit> = emptyList(),
    val habitProgressions: Map<Long, Map<LocalDate, HabitEntry>> = emptyMap()
)
