package me.sosedik.habitrack.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.TypeConverters

@Database(
    entities = [
        HabitCategoryEntity::class,
        HabitEntity::class,
        HabitEntryEntity::class,
        HabitCategoryCrossRef::class
    ],
    version = 1
)
@TypeConverters(Converters::class)
@ConstructedBy(HabiTrackDatabaseConstructor::class)
abstract class HabiTrackDatabase : RoomDatabase() {

    abstract val habitCategoriesDao: HabitCategoriesDao
    abstract val habitsDao: HabitsDao
    abstract val habitEntriesDao: HabitEntriesDao

    companion object {
        const val DB_NAME = "habitrack.db"
    }

}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object HabiTrackDatabaseConstructor : RoomDatabaseConstructor<HabiTrackDatabase> {

    override fun initialize(): HabiTrackDatabase

}
