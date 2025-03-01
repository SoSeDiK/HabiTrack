package me.sosedik.habitrack.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_categories"
)
data class HabitCategoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String,
    val icon: String
) {

    companion object {

        fun getDefaultCategories(): List<HabitCategoryEntity> {
            return listOf( // TODO should be localized // TODO icons
                HabitCategoryEntity(name = "Art", icon = "art"),
                HabitCategoryEntity(name = "Finances", icon = "finance"),
                HabitCategoryEntity(name = "Fitness", icon = "fitness"),
                HabitCategoryEntity(name = "Health", icon = "heart"),
                HabitCategoryEntity(name = "Nutrition", icon = "nutrition"),
                HabitCategoryEntity(name = "Social", icon = "social"),
                HabitCategoryEntity(name = "Study", icon = "study"),
                HabitCategoryEntity(name = "Work", icon = "work"),
                HabitCategoryEntity(name = "Other", icon = "other"),
                HabitCategoryEntity(name = "Morning", icon = "morning"),
                HabitCategoryEntity(name = "Day", icon = "day"),
                HabitCategoryEntity(name = "Evening", icon = "evening"),
            )
        }

    }

}
