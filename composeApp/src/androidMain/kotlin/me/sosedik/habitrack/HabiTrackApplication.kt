package me.sosedik.habitrack

import android.app.Application
import me.sosedik.habitrack.di.initKoin
import org.koin.android.ext.koin.androidContext

class HabiTrackApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        initKoin {
            androidContext(this@HabiTrackApplication)
        }
    }

}
