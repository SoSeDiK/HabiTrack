package me.sosedik.habitrack.data.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation

@Entity(
    tableName = "habit_categories_cross",
    primaryKeys = ["habitId", "categoryId"]
)
data class HabitCategoryCrossRef(
    val habitId: Long,
    val categoryId: Long
)

data class HabitWithCategoriesEntities(
    @Embedded val habit: HabitEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(HabitCategoryCrossRef::class)
    )
    val categories: List<HabitCategoryEntity>
)
