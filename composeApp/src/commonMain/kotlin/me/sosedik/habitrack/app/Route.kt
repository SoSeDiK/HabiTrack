package me.sosedik.habitrack.app

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    object Home {
        @Serializable
        data object Overview : Route
        @Serializable
        data object HabitCreation : Route
    }
    @Serializable
    object Settings {
        @Serializable
        data object Overview : Route
        @Serializable
        data object General : Route
        @Serializable
        data object Archive : Route
    }

}
