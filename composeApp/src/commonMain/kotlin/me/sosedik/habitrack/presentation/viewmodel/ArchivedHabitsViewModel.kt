package me.sosedik.habitrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.sosedik.habitrack.data.domain.Habit
import me.sosedik.habitrack.data.domain.HabitRepository

class ArchivedHabitsViewModel(
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ArchivedHabitsState())
    val state = _state.asStateFlow()

    val archivedHabits: Flow<PagingData<Habit>> = habitRepository.getAllArchivedHabits()
        .cachedIn(viewModelScope)

    fun onAction(action: ArchivedHabitsAction) {
        when (action) {
            is ArchivedHabitsAction.OnDelete -> {
                _state.update {
                    it.copy(updatingData = true)
                }
                viewModelScope.launch {
                    habitRepository.deleteHabit(action.habit)
                    _state.update {
                        it.copy(updatingData = false)
                    }
                }
            }
            is ArchivedHabitsAction.OnRestore -> {
                _state.update {
                    it.copy(updatingData = true)
                }
                viewModelScope.launch {
                    habitRepository.updateArchivedState(action.habit, false)
                    _state.update {
                        it.copy(updatingData = false)
                    }
                }
            }
        }
    }

}

interface ArchivedHabitsAction {

    data object OnExit : ArchivedHabitsAction
    data class OnDelete(val habit: Habit) : ArchivedHabitsAction
    data class OnRestore(val habit: Habit) : ArchivedHabitsAction

}

data class ArchivedHabitsState(
    val updatingData: Boolean = false
)
