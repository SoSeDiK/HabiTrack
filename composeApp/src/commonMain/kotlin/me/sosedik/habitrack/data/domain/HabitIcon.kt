package me.sosedik.habitrack.data.domain

import habitrack.composeapp.generated.resources.*
import habitrack.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.DrawableResource

enum class HabitType {
    ACTIVITY, FOOD_BEVERAGE, MISC
}

data class HabitIcon(
    val id: String,
    val resource: DrawableResource,
    val type: HabitType
) {

    companion object {
        private val defaultIcon = HabitIcon("star", Res.drawable.icon_star_40px, HabitType.MISC)
        private val habitIconsMap = listOf(
            defaultIcon,
            HabitIcon("school", Res.drawable.icon_school_40px, HabitType.ACTIVITY),
            HabitIcon("self_improvement", Res.drawable.icon_self_improvement_40px, HabitType.ACTIVITY),
            HabitIcon("shower", Res.drawable.icon_shower_40px, HabitType.ACTIVITY),
            HabitIcon("gamepad", Res.drawable.icon_sports_esports_40px, HabitType.ACTIVITY),
            HabitIcon("wash", Res.drawable.icon_wash_40px, HabitType.ACTIVITY),
            HabitIcon("food_beverage", Res.drawable.icon_emoji_food_beverage_40px, HabitType.FOOD_BEVERAGE)
        ).associateBy { it.id }

        fun defaultIcon(): HabitIcon {
            return defaultIcon
        }

        fun icons(): List<HabitIcon> {
            return habitIconsMap.values.toList()
        }

        fun getById(id: String): HabitIcon {
            return habitIconsMap[id] ?: defaultIcon
        }
    }

}
