package me.sosedik.habitrack.presentation.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import me.sosedik.habitrack.data.domain.HabitCategory
import org.jetbrains.compose.resources.painterResource

@Composable
fun FilterCategory(
    habitCategory: HabitCategory,
    selected: Boolean,
    allowActions: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = {
            if (allowActions) onClick()
        },
        colors = FilterChipDefaults.filterChipColors().copy(
            leadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        label = {
            Text(text = habitCategory.name)
        },
        leadingIcon = {
            Icon(
                painter = painterResource(habitCategory.icon.resource),
                contentDescription = null,
                modifier = Modifier.size(FilterChipDefaults.IconSize)
            )
        }
    )
}
