package me.sosedik.habitrack.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import habitrack.composeapp.generated.resources.Res
import habitrack.composeapp.generated.resources.settings_archive_action_delete
import habitrack.composeapp.generated.resources.settings_archive_action_delete_cancel
import habitrack.composeapp.generated.resources.settings_archive_action_delete_description
import habitrack.composeapp.generated.resources.settings_archive_action_delete_title
import habitrack.composeapp.generated.resources.settings_archive_action_restore
import habitrack.composeapp.generated.resources.settings_archive_action_restore_cancel
import habitrack.composeapp.generated.resources.settings_archive_action_restore_description
import habitrack.composeapp.generated.resources.settings_archive_action_restore_title
import habitrack.composeapp.generated.resources.settings_overview_category_archive
import me.sosedik.habitrack.data.domain.Habit
import me.sosedik.habitrack.presentation.component.HabitIcon
import me.sosedik.habitrack.presentation.component.PLACEHOLDER_COLOR
import me.sosedik.habitrack.presentation.theme.IconCache
import me.sosedik.habitrack.presentation.viewmodel.ArchivedHabitsAction
import me.sosedik.habitrack.presentation.viewmodel.ArchivedHabitsState
import me.sosedik.habitrack.presentation.viewmodel.ArchivedHabitsViewModel
import me.sosedik.habitrack.util.getDesaturatedColor
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ArchivedHabitsScreenRoot(
    viewModel: ArchivedHabitsViewModel = koinViewModel(),
    iconCache: IconCache,
    onDiscard: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val habits: LazyPagingItems<Habit> = viewModel.archivedHabits.collectAsLazyPagingItems()

    ArchivedHabitsScreen(
        state = state,
        iconCache = iconCache,
        habits = habits,
        onAction = { action ->
            when (action) {
                ArchivedHabitsAction.OnExit -> onDiscard()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArchivedHabitsScreen(
    state: ArchivedHabitsState,
    iconCache: IconCache,
    habits: LazyPagingItems<Habit>,
    onAction: (ArchivedHabitsAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onAction.invoke(ArchivedHabitsAction.OnExit)
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
                        text = stringResource(Res.string.settings_overview_category_archive),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(vertical = 6.dp, horizontal = 12.dp)
        ) {
            items(
                count = habits.itemCount,
                key = habits.itemKey { it.id }
            ) { index ->
                val habit: Habit? = habits[index]
                ArchivedHabit(
                    state = state,
                    iconCache = iconCache,
                    habit = habit,
                    onAction = onAction
                )
            }
        }
    }
}

@Composable
private fun ArchivedHabit(
    state: ArchivedHabitsState,
    iconCache: IconCache,
    habit: Habit?,
    onAction: (ArchivedHabitsAction) -> Unit
) {
    var showDeletionDialogue by remember { mutableStateOf(false) }
    var showRestorationDialogue by remember { mutableStateOf(false) }

    var desaturatedColor by remember { mutableStateOf(if (habit == null) PLACEHOLDER_COLOR else getDesaturatedColor(habit.color)) }

    LaunchedEffect(habit) {
        if (habit != null) desaturatedColor = getDesaturatedColor(habit.color)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .sizeIn(minHeight = 24.dp, maxHeight = 32.dp)
                    .background(desaturatedColor, shape = RoundedCornerShape(6.dp)),
            ) {
                if (habit != null) {
                    HabitIcon(
                        iconCache = iconCache,
                        modifier = Modifier
                            .padding(3.dp),
                        id = habit.icon
                    )
                }
            }
            Column {
                Text(
                    text = habit?.name ?: "",
                    style = MaterialTheme.typography.bodyLarge
                )
                if (habit?.description != null) {
                    Text(
                        text = habit.description,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.End),
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                enabled = !state.updatingData,
                onClick = {
                    showDeletionDialogue = true
                },
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(text = stringResource(Res.string.settings_archive_action_delete))
            }
            Button(
                enabled = !state.updatingData,
                onClick = {
                    showRestorationDialogue = true
                }
            ) {
                Text(text = stringResource(Res.string.settings_archive_action_restore))
            }
        }
    }

    if (showDeletionDialogue) {
        AlertDialog(
            modifier = Modifier
                .padding(16.dp),
            onDismissRequest = {
                showDeletionDialogue = false
            },
            title = {
                Text(text = stringResource(Res.string.settings_archive_action_delete_title))
            },
            text = {
                Text(text = stringResource(Res.string.settings_archive_action_delete_description))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeletionDialogue = false
                    }
                ) {
                    Text(text = stringResource(Res.string.settings_archive_action_delete_cancel))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeletionDialogue = false
                        if (habit != null) onAction.invoke(ArchivedHabitsAction.OnDelete(habit))
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(text = stringResource(Res.string.settings_archive_action_delete))
                }
            }
        )
    }

    if (showRestorationDialogue) {
        AlertDialog(
            modifier = Modifier
                .padding(16.dp),
            onDismissRequest = {
                showRestorationDialogue = false
            },
            title = {
                Text(text = stringResource(Res.string.settings_archive_action_restore_title))
            },
            text = {
                Text(text = stringResource(Res.string.settings_archive_action_restore_description))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showRestorationDialogue = false
                        if (habit != null) onAction.invoke(ArchivedHabitsAction.OnRestore(habit))
                    }
                ) {
                    Text(text = stringResource(Res.string.settings_archive_action_restore))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showRestorationDialogue = false
                    }
                ) {
                    Text(text = stringResource(Res.string.settings_archive_action_restore_cancel))
                }
            }
        )
    }
}
