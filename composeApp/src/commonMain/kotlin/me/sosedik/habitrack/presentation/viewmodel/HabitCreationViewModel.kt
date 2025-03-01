package me.sosedik.habitrack.presentation.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.sosedik.habitrack.data.database.HabitEntity
import me.sosedik.habitrack.data.database.HabitsDao
import me.sosedik.habitrack.data.domain.HabitCategory
import me.sosedik.habitrack.data.domain.HabitCategoryRepository
import me.sosedik.habitrack.data.domain.HabitIcon
import kotlin.math.min

class HabitCreationViewModel(
    val habitCategoryRepository: HabitCategoryRepository,
    val habitsDao: HabitsDao
) : ViewModel() {

    private val _state = MutableStateFlow(HabitCreationState())
    val state = _state
        .onStart {
            observeHabitCategories()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    val nameState: TextFieldState = TextFieldState()
    val descriptionState: TextFieldState = TextFieldState()

    private var observeHabitCategoriesJob: Job? = null

    fun onAction(action: HabitCreationAction) {
        when (action) {
            HabitCreationAction.IncreaseDailyLimit -> {
                _state.update {
                    it.copy(dailyLimit = it.dailyLimit + 1)
                }
            }
            HabitCreationAction.DecreaseDailyLimit -> {
                _state.update {
                    it.copy(dailyLimit = min(0, it.dailyLimit - 1))
                }
            }
            is HabitCreationAction.UpdateIcon -> {
                _state.update {
                    it.copy(icon = action.icon)
                }
            }
            is HabitCreationAction.UpdateColor -> {
                _state.update {
                    it.copy(color = action.color)
                }
            }
            HabitCreationAction.SaveHabit -> {
                val current = state.value
                val habit = HabitEntity(
                    name = nameState.text.toString(),
                    description = descriptionState.text.toString().ifBlank { null },
                    dailyLimit = current.dailyLimit,
                    categories = current.pickedCategories.map { it.id },
                    icon = current.icon.id,
                    color = current.color
                )
                _state.update {
                    it.copy(savingData = true)
                }
                viewModelScope.launch {
                    habitsDao.upsert(habit)
                    _state.update {
                        it.copy(savingData = false)
                    }
                }
            }
            is HabitCreationAction.ToggleCategory -> {
                val categories = _state.value.pickedCategories.toMutableList()
                if (!categories.remove(action.category))
                    categories.add(action.category)
                _state.update {
                    it.copy(
                        pickedCategories = categories.toList()
                    )
                }
            }
            else -> Unit
        }
    }

    private fun observeHabitCategories() {
        observeHabitCategoriesJob?.cancel()
        observeHabitCategoriesJob = habitCategoryRepository
            .getHabitCategories()
            .onEach { habitCategories ->
                _state.update { it.copy(
                    allCategories = habitCategories
                ) }
            }
            .launchIn(viewModelScope)
    }

}

sealed interface HabitCreationAction {

    data object IncreaseDailyLimit : HabitCreationAction
    data object DecreaseDailyLimit : HabitCreationAction
    data class UpdateIcon(val icon: HabitIcon) : HabitCreationAction
    data class UpdateColor(val color: Color) : HabitCreationAction
    data class ToggleCategory(val category: HabitCategory) : HabitCreationAction
    data object SaveHabit : HabitCreationAction
    data object Discard : HabitCreationAction

}

data class HabitCreationState(
    val savingData: Boolean = false,
    val dailyLimit: Int = 1,
    val allCategories: List<HabitCategory> = emptyList(),
    val pickedCategories: List<HabitCategory> = emptyList(),
    val icon: HabitIcon = HabitIcon.defaultIcon(),
    val color: Color = Color.Red
)
