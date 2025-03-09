package me.sosedik.habitrack.di

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import me.sosedik.habitrack.data.database.DatabaseFactory
import me.sosedik.habitrack.data.database.HabiTrackDatabase
import me.sosedik.habitrack.data.database.HabitCategoryEntity
import me.sosedik.habitrack.data.domain.HabitCategoryRepository
import me.sosedik.habitrack.data.domain.HabitEntryRepository
import me.sosedik.habitrack.data.domain.HabitRepository
import me.sosedik.habitrack.data.domain.SettingsRepository
import me.sosedik.habitrack.data.repository.DefaultHabitCategoryRepository
import me.sosedik.habitrack.data.repository.DefaultHabitEntryRepository
import me.sosedik.habitrack.data.repository.DefaultHabitRepository
import me.sosedik.habitrack.data.repository.DefaultSettingsRepository
import me.sosedik.habitrack.presentation.viewmodel.AppViewModel
import me.sosedik.habitrack.presentation.viewmodel.GeneralSettingsViewModel
import me.sosedik.habitrack.presentation.viewmodel.HabitCreationViewModel
import me.sosedik.habitrack.presentation.viewmodel.HabitListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {
    // Database
    single {
        get<DatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(connection: SQLiteConnection) {
                    super.onCreate(connection)

                    var habitCategoriesDao = get<HabiTrackDatabase>().habitCategoriesDao
                    val defaultCategories = HabitCategoryEntity.getDefaultCategories()
                    CoroutineScope(Dispatchers.IO).launch {
                        defaultCategories.forEach { category ->
                            habitCategoriesDao.upsert(category)
                        }
                    }
                }
            })
            .build()
    }

    // Data store
    single { DefaultSettingsRepository(get()) }.bind<SettingsRepository>()

    single { get<HabiTrackDatabase>().habitCategoriesDao }
    single { get<HabiTrackDatabase>().habitsDao }
    single { get<HabiTrackDatabase>().habitEntriesDao }

    singleOf(::DefaultHabitCategoryRepository).bind<HabitCategoryRepository>()
    singleOf(::DefaultHabitRepository).bind<HabitRepository>()
    singleOf(::DefaultHabitEntryRepository).bind<HabitEntryRepository>()

    singleOf(::AppViewModel)
    viewModelOf(::HabitCreationViewModel)
    viewModelOf(::HabitListViewModel)
    viewModelOf(::GeneralSettingsViewModel)
}
