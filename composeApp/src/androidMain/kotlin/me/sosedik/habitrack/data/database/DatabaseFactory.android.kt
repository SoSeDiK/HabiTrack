package me.sosedik.habitrack.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

actual class DatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<HabiTrackDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(HabiTrackDatabase.DB_NAME)

        return Room.databaseBuilder<HabiTrackDatabase>(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}
