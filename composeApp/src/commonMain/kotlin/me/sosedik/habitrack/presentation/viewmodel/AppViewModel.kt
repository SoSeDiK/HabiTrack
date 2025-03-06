package me.sosedik.habitrack.presentation.viewmodel

import androidx.lifecycle.ViewModel
import me.sosedik.habitrack.data.domain.Habit

class AppViewModel : ViewModel() {

    var cachedHabit: Habit? = null

}
