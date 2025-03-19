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
            return listOf( // TODO should be localized
                HabitCategoryEntity(name = "Art", icon = "nf-md-palette_outline"),
                HabitCategoryEntity(name = "Finances", icon = "nf-fa-money_bill_1"),
                HabitCategoryEntity(name = "Fitness", icon = "nf-md-bicycle"),
                HabitCategoryEntity(name = "Health", icon = "nf-oct-heart"),
                HabitCategoryEntity(name = "Nutrition", icon = "nf-md-silverware_fork_knife"),
                HabitCategoryEntity(name = "Social", icon = "nf-fa-comments_o"),
                HabitCategoryEntity(name = "Study", icon = "nf-md-school_outline"),
                HabitCategoryEntity(name = "Work", icon = "nf-cod-briefcase"),
                HabitCategoryEntity(name = "Other", icon = "nf-oct-diamond"),
                HabitCategoryEntity(name = "Morning", icon = "nf-weather-sunrise"),
                HabitCategoryEntity(name = "Day", icon = "nf-fa-sun"),
                HabitCategoryEntity(name = "Evening", icon = "nf-fa-moon")
            )
        }

    }

}
