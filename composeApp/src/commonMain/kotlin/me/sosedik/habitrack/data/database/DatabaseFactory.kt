package me.sosedik.habitrack.data.database

import androidx.room.RoomDatabase

expect class DatabaseFactory {

    fun create(): RoomDatabase.Builder<HabiTrackDatabase>

}
