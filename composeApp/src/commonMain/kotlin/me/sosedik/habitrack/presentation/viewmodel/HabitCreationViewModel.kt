package me.sosedik.habitrack.presentation.viewmodel

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
import me.sosedik.habitrack.util.CanNotBeEmptyError
import me.sosedik.habitrack.util.HabiTrackError
import me.sosedik.habitrack.util.PRE_PICKED_COLORS
import me.sosedik.habitrack.util.observeMutableField
import kotlin.math.max
import kotlin.math.min

const val DAILY_LIMIT_MAX = 1_000
private val DEFAULT_CUSTOM_COLOR = Color.White

class HabitCreationViewModel(
    appViewModel: AppViewModel,
    val habitCategoryRepository: HabitCategoryRepository,
    val habitsDao: HabitsDao
) : ViewModel() {

    private val _state = MutableStateFlow(
        appViewModel.cachedHabit?.let { habit ->
            HabitCreationState(
                habitId = habit.id,
                dailyLimit = habit.dailyLimit,
                pickedCategories = habit.categories,
                icon = habit.icon,
                color = habit.color,
                customColor = if (PRE_PICKED_COLORS.contains(habit.color)) DEFAULT_CUSTOM_COLOR else habit.color
            )
        } ?: HabitCreationState()
    )
    val state = _state
        .onStart {
            observeHabitCategories()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000L),
            _state.value
        )

    val nameState: TextFieldState = TextFieldState(initialText = appViewModel.cachedHabit?.name ?: "")
    private val _nameStateError: MutableStateFlow<HabiTrackError?> =
        nameState.observeMutableField(viewModelScope, null) {
            validateName(false)
        }
    val nameStateError: StateFlow<HabiTrackError?> = _nameStateError
    val descriptionState: TextFieldState = TextFieldState(initialText = appViewModel.cachedHabit?.description ?: "")

    private var observeHabitCategoriesJob: Job? = null

    init {
        appViewModel.cachedHabit = null
    }

    fun onAction(action: HabitCreationAction) {
        when (action) {
            HabitCreationAction.IncreaseDailyLimit -> {
                _state.update {
                    it.copy(dailyLimit = min(DAILY_LIMIT_MAX, it.dailyLimit + 1))
                }
            }
            HabitCreationAction.DecreaseDailyLimit -> {
                _state.update {
                    it.copy(dailyLimit = max(0, it.dailyLimit - 1))
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
            is HabitCreationAction.UpdateCustomColor -> {
                _state.update {
                    it.copy(
                        color = action.color,
                        customColor = action.color
                    )
                }
            }
            HabitCreationAction.SaveHabit -> {
                val current = state.value
                val habit = HabitEntity(
                    id = current.habitId ?: 0L,
                    name = nameState.text.trim().toString(),
                    description = descriptionState.text.trim().toString().ifEmpty { null },
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
            HabitCreationAction.ValidateName -> {
                _nameStateError.value = validateName(true)
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

    private fun validateName(finished: Boolean): HabiTrackError? {
        if (finished && nameState.text.isEmpty())
            return CanNotBeEmptyError
        return null
    }

}

sealed interface HabitCreationAction {

    data object IncreaseDailyLimit : HabitCreationAction
    data object DecreaseDailyLimit : HabitCreationAction
    data class UpdateIcon(val icon: HabitIcon) : HabitCreationAction
    data class UpdateColor(val color: Color) : HabitCreationAction
    data class UpdateCustomColor(val color: Color) : HabitCreationAction
    data class ToggleCategory(val category: HabitCategory) : HabitCreationAction
    data object ValidateName : HabitCreationAction
    data object SaveHabit : HabitCreationAction
    data object DismissColor : HabitCreationAction
    data object Discard : HabitCreationAction

}

data class HabitCreationState(
    val savingData: Boolean = false,
    val habitId: Long? = null,
    val dailyLimit: Int = 1,
    val allCategories: List<HabitCategory> = emptyList(),
    val pickedCategories: List<HabitCategory> = emptyList(),
    val icon: HabitIcon = HabitIcon.defaultIcon(),
    val color: Color = PRE_PICKED_COLORS[0],
    val customColor: Color = DEFAULT_CUSTOM_COLOR
) {

    fun isEditing(): Boolean {
        return this.habitId != null
    }
    
}
