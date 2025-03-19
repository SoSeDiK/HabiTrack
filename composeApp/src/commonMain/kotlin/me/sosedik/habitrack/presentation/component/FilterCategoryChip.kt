package me.sosedik.habitrack.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import me.sosedik.habitrack.data.domain.HabitCategory
import me.sosedik.habitrack.presentation.theme.IconCache

@Composable
fun FilterCategoryChip(
    iconCache: IconCache,
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
            HabitIcon(
                iconCache = iconCache,
                modifier = Modifier.size(FilterChipDefaults.IconSize),
                id = habitCategory.icon
            )
        }
    )
}

@Composable
fun FilterCategory(
    modifier: Modifier = Modifier,
    iconCache: IconCache,
    habitCategory: HabitCategory,
    selected: Boolean,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer
) {
    Row(
        modifier = modifier
            .border(1.dp,
                color = if (selected) MaterialTheme.colorScheme.inverseSurface else MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2F),
                shape = RoundedCornerShape(8.dp)
            )
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .padding(5.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HabitIcon(
            iconCache = iconCache,
            modifier = Modifier.size(FilterChipDefaults.IconSize),
            id = habitCategory.icon,
        )
        Text(
            text = habitCategory.name
        )
    }
}
