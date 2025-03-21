package me.sosedik.habitrack.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import habitrack.composeapp.generated.resources.Res
import habitrack.composeapp.generated.resources.settings_overview_category_archive
import habitrack.composeapp.generated.resources.settings_overview_category_archive_desc
import habitrack.composeapp.generated.resources.settings_overview_category_general
import habitrack.composeapp.generated.resources.settings_overview_category_general_desc
import habitrack.composeapp.generated.resources.settings_overview_header
import habitrack.composeapp.generated.resources.ui_archive_24px
import habitrack.composeapp.generated.resources.ui_settings_24px
import me.sosedik.habitrack.app.Route
import me.sosedik.habitrack.presentation.viewmodel.SettingsAction
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val generalCategoryColor = Color(0xFFDA8DC7)
private val archiveCategoryColor = Color(0xFF90CAF9)

@Composable
fun SettingsScreenRoot(
    onDiscard: () -> Unit,
    onNavigate: (Route) -> Unit
) {
    SettingsScreen(
        onAction = { action ->
            when (action) {
                SettingsAction.Exit -> onDiscard()
                is SettingsAction.PickCategory -> onNavigate(action.categoryRoute)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onAction: (SettingsAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onAction.invoke(SettingsAction.Exit)
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = null
                        )
                    }
                },
                title = {
                    Text(
                        text = stringResource(Res.string.settings_overview_header),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(vertical = 6.dp, horizontal = 12.dp)
                .border(1.dp, MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2F), shape = RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            SettingsCategory(
                name = stringResource(Res.string.settings_overview_category_general),
                description = stringResource(Res.string.settings_overview_category_general_desc),
                color = generalCategoryColor,
                icon = painterResource(Res.drawable.ui_settings_24px),
                onClick = {
                    onAction.invoke(SettingsAction.PickCategory(Route.Settings.General))
                }
            )
            SettingsCategory(
                name = stringResource(Res.string.settings_overview_category_archive),
                description = stringResource(Res.string.settings_overview_category_archive_desc),
                color = archiveCategoryColor,
                icon = painterResource(Res.drawable.ui_archive_24px),
                onClick = {
                    onAction.invoke(SettingsAction.PickCategory(Route.Settings.Archive))
                }
            )
        }
    }
}

@Composable
private fun SettingsCategory(
    name: String,
    description: String,
    color: Color,
    icon: Painter,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(),
        color = Color.Transparent,
        onClick = onClick
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier
                    .background(color, RoundedCornerShape(8.dp))
                    .padding(5.dp),
                painter = icon,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                modifier = Modifier
                    .padding(12.dp),
                text = name,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.weight(1F))
            Icon(
                Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = description,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
