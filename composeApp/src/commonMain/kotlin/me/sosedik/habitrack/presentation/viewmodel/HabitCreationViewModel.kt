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
import me.sosedik.habitrack.data.domain.Habit
import me.sosedik.habitrack.data.domain.HabitCategory
import me.sosedik.habitrack.data.domain.HabitCategoryRepository
import me.sosedik.habitrack.data.domain.HabitRepository
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
    private val habitCategoryRepository: HabitCategoryRepository,
    private val habitRepository: HabitRepository
) : ViewModel() {

    private val _state = MutableStateFlow(
        appViewModel.cachedHabit?.let { habit ->
            HabitCreationState(
                updatingData = true,
                habitId = habit.id,
                dailyLimit = habit.dailyLimit,
                icon = habit.icon,
                color = habit.color,
                customColor = if (PRE_PICKED_COLORS.contains(habit.color)) DEFAULT_CUSTOM_COLOR else habit.color,
                order = habit.order,
                archived = habit.archived
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
        appViewModel.cachedHabit?.let { habit ->
            appViewModel.cachedHabit = null

            viewModelScope.launch {
                val categories = habitRepository.getCategoriesForHabit(habit)
                _state.update {
                    it.copy(
                        updatingData = false,
                        pickedCategories = categories
                    )
                }
            }
        }
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
            is HabitCreationAction.UpdateCustomIcon -> {
                _state.update {
                    it.copy(
                        icon = action.icon,
                        customIcon = action.icon
                    )
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
                _state.update {
                    it.copy(updatingData = true)
                }
                viewModelScope.launch {
                    var habit = Habit(
                        id = current.habitId ?: 0L,
                        name = nameState.text.trim().toString(),
                        description = descriptionState.text.trim().toString().ifEmpty { null },
                        dailyLimit = current.dailyLimit,
                        icon = current.icon,
                        color = current.color,
                        order = current.order ?: (habitRepository.getMaxOrder() + 1),
                        archived = current.archived
                    )
                    habit = habitRepository.upsert(habit)
                    habitRepository.updateHabitCategories(habit, current.pickedCategories)
                    _state.update {
                        it.copy(updatingData = false)
                    }
                }
            }
            is HabitCreationAction.DeleteCategory -> {
                _state.update {
                    val allCategories = it.allCategories.toMutableList()
                    val pickedCategories = it.pickedCategories.toMutableList()
                    allCategories.remove(action.category)
                    pickedCategories.remove(action.category)
                    it.copy(
                        allCategories = allCategories.toList(),
                        pickedCategories = pickedCategories.toList()
                    )
                }
                viewModelScope.launch {
                    habitCategoryRepository.deleteCategory(action.category)
                }
            }
            is HabitCreationAction.UpdateCategory -> {
                viewModelScope.launch {
                    habitCategoryRepository.upsertCategory(action.category)
                }
            }
            is HabitCreationAction.SaveCategories -> {
                _state.update {
                    it.copy(
                        pickedCategories = action.categories.toList()
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
                _state.update { state ->
                    val pickedCategoryIds = state.pickedCategories.map { it.id }.toSet()
                    val pickedCategories = habitCategories.filter { it.id in pickedCategoryIds }
                    state.copy(
                        allCategories = habitCategories,
                        pickedCategories = pickedCategories
                    )
                }
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
    data class UpdateIcon(val icon: String) : HabitCreationAction
    data class UpdateCustomIcon(val icon: String) : HabitCreationAction
    data class UpdateColor(val color: Color) : HabitCreationAction
    data class UpdateCustomColor(val color: Color) : HabitCreationAction
    data object EditCategories : HabitCreationAction
    data class EditCategory(val category: HabitCategory) : HabitCreationAction
    data class DeleteCategory(val category: HabitCategory) : HabitCreationAction
    data class UpdateCategory(val category: HabitCategory) : HabitCreationAction
    data object ValidateName : HabitCreationAction
    data object SaveHabit : HabitCreationAction
    data class SaveCategories(val categories: List<HabitCategory>) : HabitCreationAction
    data object AddCategory : HabitCreationAction
    data object Discard : HabitCreationAction

}

data class HabitCreationState(
    val updatingData: Boolean = false,
    val habitId: Long? = null,
    val dailyLimit: Int = 1,
    val allCategories: List<HabitCategory> = emptyList(),
    val pickedCategories: List<HabitCategory> = emptyList(),
    val icon: String = "",
    val customIcon: String = "",
    val color: Color = PRE_PICKED_COLORS[0],
    val customColor: Color = DEFAULT_CUSTOM_COLOR,
    val order: Int? = null,
    val archived: Boolean = false
) {

    fun hasDailyLimit(): Boolean {
        return dailyLimit > 0
    }

    fun isEditing(): Boolean {
        return this.habitId != null
    }
    
}
