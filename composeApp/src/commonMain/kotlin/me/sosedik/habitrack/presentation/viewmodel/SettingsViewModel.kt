package me.sosedik.habitrack.presentation.viewmodel

import me.sosedik.habitrack.app.Route

sealed interface SettingsAction {

    data object Exit : SettingsAction
    data class PickCategory(val categoryRoute: Route) : SettingsAction

}
