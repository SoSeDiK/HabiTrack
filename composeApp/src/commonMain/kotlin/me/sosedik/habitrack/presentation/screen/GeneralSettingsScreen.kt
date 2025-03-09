package me.sosedik.habitrack.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import habitrack.composeapp.generated.resources.Res
import habitrack.composeapp.generated.resources.settings_cat_general_week_on_sunday
import habitrack.composeapp.generated.resources.settings_overview_category_general
import me.sosedik.habitrack.presentation.component.SettingsSwitchOption
import me.sosedik.habitrack.presentation.viewmodel.GeneralSettingsAction
import me.sosedik.habitrack.presentation.viewmodel.GeneralSettingsState
import me.sosedik.habitrack.presentation.viewmodel.GeneralSettingsViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun GeneralSettingsScreenRoot(
    viewModel: GeneralSettingsViewModel = koinViewModel(),
    onDiscard: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    GeneralSettingsScreen(
        state = state,
        onAction = { action ->
            when (action) {
                GeneralSettingsAction.Exit -> onDiscard()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralSettingsScreen(
    state: GeneralSettingsState,
    onAction: (GeneralSettingsAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onAction.invoke(GeneralSettingsAction.Exit)
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
                        text = stringResource(Res.string.settings_overview_category_general),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { paddingValues ->
        if (state.loadingData) return@Scaffold

        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(vertical = 6.dp, horizontal = 12.dp)
                .border(1.dp, MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2F), shape = RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                .padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            SettingsSwitchOption(
                name = stringResource(Res.string.settings_cat_general_week_on_sunday),
                state = state.weekOnSunday,
                onClick = {
                    onAction.invoke(GeneralSettingsAction.ToggleWeekOnSunday(it))
                }
            )
        }
    }
}
