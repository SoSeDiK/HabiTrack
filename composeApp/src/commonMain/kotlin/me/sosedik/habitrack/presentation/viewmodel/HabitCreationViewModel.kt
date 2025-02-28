package me.sosedik.habitrack.presentation.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.sosedik.habitrack.data.database.HabitEntity
import me.sosedik.habitrack.data.database.HabitsDao
import me.sosedik.habitrack.data.domain.HabitCategory
import me.sosedik.habitrack.data.domain.HabitIcon
import kotlin.math.min

class HabitCreationViewModel(
    val habitsDao: HabitsDao
) : ViewModel() {

    private val _state = MutableStateFlow(HabitCreationState())
    val state = _state.asStateFlow()

    val nameState: TextFieldState = TextFieldState()
    val descriptionState: TextFieldState = TextFieldState()

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
//                    categories = current.categories, // TODO saving categories
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
            else -> Unit
        }
    }

}

sealed interface HabitCreationAction {

    data object IncreaseDailyLimit : HabitCreationAction
    data object DecreaseDailyLimit : HabitCreationAction
    data class UpdateIcon(val icon: HabitIcon) : HabitCreationAction
    data class UpdateColor(val color: Color) : HabitCreationAction
    data object SaveHabit : HabitCreationAction
    data object Discard : HabitCreationAction

}

data class HabitCreationState(
    val savingData: Boolean = false,
    val dailyLimit: Int = 1,
    val categories: List<HabitCategory> = emptyList(),
    val icon: HabitIcon = HabitIcon.defaultIcon(),
    val color: Color = Color.Red
)
