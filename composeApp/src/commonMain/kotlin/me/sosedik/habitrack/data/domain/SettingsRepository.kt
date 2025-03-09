package me.sosedik.habitrack.data.domain

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun getStartWeekOnSunday(): Flow<Boolean>
    suspend fun isStartWeekOnSunday(): Boolean
    suspend fun setStartWeekOnSunday(startOnSunday: Boolean)

}
