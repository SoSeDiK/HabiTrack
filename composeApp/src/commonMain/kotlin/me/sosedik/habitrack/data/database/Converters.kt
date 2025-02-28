package me.sosedik.habitrack.data.database

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.room.TypeConverter
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate

fun LocalDate.asDMYString(): String {
    val day = this.dayOfMonth.toString().padStart(2, '0')
    val month = this.monthNumber.toString().padStart(2, '0')
    val year = this.year.toString()
    return "$day.$month.$year"
}

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

}
