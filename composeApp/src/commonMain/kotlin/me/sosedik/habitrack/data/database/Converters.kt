package me.sosedik.habitrack.data.database

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

class Converters {

    @TypeConverter
    fun fromColor(color: Color): Int {
        return color.toArgb()
    }

    @TypeConverter
    fun toColor(color: Int): Color {
        return Color(color)
    }

    @TypeConverter
    fun fromDate(date: Instant): Long {
        return date.toEpochMilliseconds()
    }

    @TypeConverter
    fun toDate(millis: Long): Instant {
        return Instant.fromEpochMilliseconds(millis)
    }

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return if (value == null) null else Json.encodeToString(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return if (value.isNullOrEmpty()) emptyList() else Json.decodeFromString(value)
    }

    @TypeConverter
    fun fromLongList(value: List<Long>?): String? {
        return if (value == null) null else Json.encodeToString(value)
    }

    @TypeConverter
    fun toLongList(value: String?): List<Long>? {
        return if (value.isNullOrEmpty()) emptyList() else Json.decodeFromString(value)
    }

}
