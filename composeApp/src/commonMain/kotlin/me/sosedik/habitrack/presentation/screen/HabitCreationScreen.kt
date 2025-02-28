package me.sosedik.habitrack.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import habitrack.composeapp.generated.resources.Res
import habitrack.composeapp.generated.resources.habit_creation_action_more_icons
import habitrack.composeapp.generated.resources.habit_creation_action_save
import habitrack.composeapp.generated.resources.habit_creation_categories
import habitrack.composeapp.generated.resources.habit_creation_color
import habitrack.composeapp.generated.resources.habit_creation_daily_limit
import habitrack.composeapp.generated.resources.habit_creation_daily_limit_day
import habitrack.composeapp.generated.resources.habit_creation_daily_limit_decrease
import habitrack.composeapp.generated.resources.habit_creation_daily_limit_increase
import habitrack.composeapp.generated.resources.habit_creation_description
import habitrack.composeapp.generated.resources.habit_creation_header
import habitrack.composeapp.generated.resources.habit_creation_icon
import habitrack.composeapp.generated.resources.habit_creation_name
import habitrack.composeapp.generated.resources.ui_add_24px
import habitrack.composeapp.generated.resources.ui_remove_24px
import me.sosedik.habitrack.data.domain.HabitIcon
import me.sosedik.habitrack.presentation.viewmodel.HabitCreationAction
import me.sosedik.habitrack.presentation.viewmodel.HabitCreationState
import me.sosedik.habitrack.presentation.viewmodel.HabitCreationViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.math.ceil

@Composable
fun HabitCreationScreenRoot(
    viewModel: HabitCreationViewModel = koinViewModel(),
    onDiscard: () -> Unit,
    onSave: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HabitCreationScreen(
        state = state,
        nameState = viewModel.nameState,
        descriptionState = viewModel.descriptionState,
        onAction = { action ->
            when (action) {
                HabitCreationAction.Discard -> onDiscard()
                HabitCreationAction.SaveHabit -> onSave()
                else -> Unit
            }
            viewModel.onAction(action)
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitCreationScreen(
    state: HabitCreationState,
    nameState: TextFieldState,
    descriptionState: TextFieldState,
    onAction: (HabitCreationAction) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            onAction.invoke(HabitCreationAction.Discard)
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
                        text = stringResource(Res.string.habit_creation_header),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    modifier = Modifier
                        .widthIn(min = 300.dp),
                    shape = RoundedCornerShape(12.dp),
                    onClick = {
                        onAction.invoke(HabitCreationAction.SaveHabit)
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.habit_creation_action_save),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .verticalScroll(state = rememberScrollState())
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            CategoryLabel(name = stringResource(Res.string.habit_creation_name))
            BasicTextField(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                state = nameState,
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )

            CategoryLabel(name = stringResource(Res.string.habit_creation_description))
            BasicTextField(
                modifier = Modifier
                    .height(40.dp)
                    .fillMaxWidth(),
                state = descriptionState,
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                )
            )

            CategoryLabel(name = stringResource(Res.string.habit_creation_categories))

            CategoryLabel(name = stringResource(Res.string.habit_creation_daily_limit))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1F)
                        .border(1.dp, MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2F), shape = RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                        .padding(5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text =
                            if (state.dailyLimit > 0)
                                state.dailyLimit.toString()
                            else
                                "∞",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Text(
                            text = " / " + stringResource(Res.string.habit_creation_daily_limit_day),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                IconButton(
                    enabled = state.dailyLimit > 0,
                    onClick = {
                        onAction.invoke(HabitCreationAction.DecreaseDailyLimit)
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ui_remove_24px),
                        contentDescription = stringResource(Res.string.habit_creation_daily_limit_decrease)
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                IconButton(
                    enabled = state.dailyLimit < 1_000,
                    onClick = {
                        onAction.invoke(HabitCreationAction.IncreaseDailyLimit)
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ui_add_24px),
                        contentDescription = stringResource(Res.string.habit_creation_daily_limit_increase)
                    )
                }
            }

            CategoryLabel(name = stringResource(Res.string.habit_creation_icon))
            LazyVerticalGrid(
                columns = GridCells.Fixed(7),
                modifier = Modifier
                    .height(((ceil(HabitIcon.icons().size / 8.0)) * 50).dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = HabitIcon.icons(),
                    key = { it.id }
                ) { icon ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .let {
                                if (icon == state.icon)
                                    it.border(1.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(8.dp))
                                else
                                    it.border(1.dp, MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2F), shape = RoundedCornerShape(8.dp))
                            }
                            .background(MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(8.dp))
                    ) {
                        IconButton(
                            modifier = Modifier
                                .fillMaxSize(),
                            onClick = {
                                onAction.invoke(HabitCreationAction.UpdateIcon(icon))
                            }
                        ) {
                            Icon(
                                modifier = Modifier
                                    .size(30.dp),
                                painter = painterResource(icon.resource),
                                contentDescription = null
                            )
                        }
                    }
                }
            }
            Button(
                modifier = Modifier
                    .align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                onClick = {
                    // TODO More icons
                }
            ) {
                Text(
                    text = stringResource(Res.string.habit_creation_action_more_icons)
                )
            }

            CategoryLabel(name = stringResource(Res.string.habit_creation_color))
            Box(
                modifier = Modifier
                    .size(30.dp)
                    .background(state.color, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(Color.Black, RoundedCornerShape(4.dp)) // TODO Shouldn't be black if black is chosen
                )
            }
        }
    }
}

@Composable
private fun CategoryLabel(
    name: String
) {
    Text(
        text = name,
        style = MaterialTheme.typography.labelMedium
    )
}
