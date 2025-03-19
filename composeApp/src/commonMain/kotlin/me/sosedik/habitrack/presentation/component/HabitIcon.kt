package me.sosedik.habitrack.presentation.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import me.sosedik.habitrack.presentation.theme.IconCache

@Composable
fun HabitIcon(
    iconCache: IconCache,
    modifier: Modifier = Modifier,
    id: String?,
    fontSize: TextUnit = TextUnit.Unspecified,
    lineHeight: TextUnit = 20.sp
) {
    val icon: String = remember(id) { iconCache.getById(id) }

    Box(
        modifier = modifier
            .aspectRatio(1F),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = icon,
            fontSize = fontSize,
            lineHeight = lineHeight,
            fontFamily = iconCache.fontFamily
        )
    }
}

