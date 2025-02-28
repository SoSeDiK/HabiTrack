package me.sosedik.habitrack.app

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    object Home {
        @Serializable
        object Overview
        @Serializable
        object HabitCreation
    }

}
