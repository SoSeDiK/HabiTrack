package me.sosedik.habitrack.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.isoDayNumber
import me.sosedik.habitrack.data.domain.SettingsRepository

class DefaultSettingsRepository(
    val prefs: DataStore<Preferences>
): SettingsRepository {

    override fun getStartWeekOnSunday(): Flow<Boolean> {
        return prefs.data.map { preferences ->
            preferences[intPreferencesKey(FIRST_DAY_OF_THE_WEEK_KEY)] == DayOfWeek.SUNDAY.isoDayNumber
        }
    }

    override suspend fun isStartWeekOnSunday(): Boolean {
        return getStartWeekOnSunday().firstOrNull() == true
    }

    override suspend fun setStartWeekOnSunday(startOnSunday: Boolean) {
        prefs.edit { preferences ->
            preferences[intPreferencesKey(FIRST_DAY_OF_THE_WEEK_KEY)] =
                if (startOnSunday) DayOfWeek.SUNDAY.isoDayNumber
                else DayOfWeek.MONDAY.isoDayNumber
        }
    }

}

private const val FIRST_DAY_OF_THE_WEEK_KEY = "first_week_day"
