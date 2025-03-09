package me.sosedik.habitrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.sosedik.habitrack.data.domain.SettingsRepository

class GeneralSettingsViewModel(
    val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(GeneralSettingsState())
    val state = _state.asStateFlow()

    init {
        loadInitialSettings()
    }

    fun onAction(action: GeneralSettingsAction) {
        when (action) {
            is GeneralSettingsAction.ToggleWeekOnSunday -> {
                _state.update {
                    it.copy(weekOnSunday = action.state)
                }
                viewModelScope.launch {
                    settingsRepository.setStartWeekOnSunday(action.state)
                }
            }
            else -> Unit
        }
    }

    private fun loadInitialSettings() {
        viewModelScope.launch {
            val weekOnSunday = settingsRepository.isStartWeekOnSunday()
            _state.value = _state.value.copy(
                loadingData = false,
                weekOnSunday = weekOnSunday
            )
        }
    }

}

sealed interface GeneralSettingsAction {

    data object Exit : GeneralSettingsAction
    data class ToggleWeekOnSunday(val state: Boolean) : GeneralSettingsAction

}

data class GeneralSettingsState(
    val loadingData: Boolean = true,
    val weekOnSunday: Boolean = false
)
