package me.sosedik.habitrack.di

import me.sosedik.habitrack.data.createDataStore
import me.sosedik.habitrack.data.database.DatabaseFactory
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single { DatabaseFactory(androidApplication()) }
        single { createDataStore(androidContext()) }
    }
