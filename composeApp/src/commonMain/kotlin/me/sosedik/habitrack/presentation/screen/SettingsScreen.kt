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
import androidx.compose.ui.unit.dp
import habitrack.composeapp.generated.resources.Res
import habitrack.composeapp.generated.resources.settings_overview_category_general
import habitrack.composeapp.generated.resources.settings_overview_category_general_desc
import habitrack.composeapp.generated.resources.settings_overview_header
import habitrack.composeapp.generated.resources.ui_settings_24px
import me.sosedik.habitrack.app.Route
import me.sosedik.habitrack.presentation.viewmodel.SettingsAction
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

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
                onClick = {
                    onAction.invoke(SettingsAction.PickCategory(Route.Settings.General))
                }
            )
        }
    }
}

private val generalCategoryColor = Color(0xFFDA8DC7)
@Composable
private fun SettingsCategory(
    name: String,
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
                    .background(generalCategoryColor, RoundedCornerShape(8.dp))
                    .padding(5.dp),
                painter = painterResource(Res.drawable.ui_settings_24px),
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
                contentDescription = stringResource(Res.string.settings_overview_category_general_desc),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
