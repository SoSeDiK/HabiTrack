package me.sosedik.habitrack.app

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    object Home {
        @Serializable
        object Overview : Route
        @Serializable
        object HabitCreation : Route
    }
    @Serializable
    object Settings {
        @Serializable
        object Overview : Route
        @Serializable
        object General : Route
    }

}
