package me.sosedik.habitrack.presentation.screen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.skydoves.colorpicker.compose.AlphaTile
import com.github.skydoves.colorpicker.compose.BrightnessSlider
import com.github.skydoves.colorpicker.compose.ColorEnvelope
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.drawColorIndicator
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import habitrack.composeapp.generated.resources.Res
import habitrack.composeapp.generated.resources.color_picker_action_copy
import habitrack.composeapp.generated.resources.color_picker_action_done
import habitrack.composeapp.generated.resources.color_picker_action_paste
import habitrack.composeapp.generated.resources.habit_creation_action_color_picker
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
import habitrack.composeapp.generated.resources.habit_creation_header_editor
import habitrack.composeapp.generated.resources.habit_creation_icon
import habitrack.composeapp.generated.resources.habit_creation_name
import habitrack.composeapp.generated.resources.ui_add_24px
import habitrack.composeapp.generated.resources.ui_content_copy_24px
import habitrack.composeapp.generated.resources.ui_content_paste_24px
import habitrack.composeapp.generated.resources.ui_palette_24px
import habitrack.composeapp.generated.resources.ui_remove_24px
import me.sosedik.habitrack.data.domain.HabitIcon
import me.sosedik.habitrack.presentation.component.FilterCategory
import me.sosedik.habitrack.presentation.component.SimpleTextField
import me.sosedik.habitrack.presentation.viewmodel.DAILY_LIMIT_MAX
import me.sosedik.habitrack.presentation.viewmodel.HabitCreationAction
import me.sosedik.habitrack.presentation.viewmodel.HabitCreationState
import me.sosedik.habitrack.presentation.viewmodel.HabitCreationViewModel
import me.sosedik.habitrack.util.HabiTrackError
import me.sosedik.habitrack.util.PRE_PICKED_COLORS
import me.sosedik.habitrack.util.hexToColor
import me.sosedik.habitrack.util.message
import me.sosedik.habitrack.util.toHex
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

val BOX_SIZE = 34.dp
val GRID_SPACING = 15.dp

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
        nameStateError = viewModel.nameStateError.collectAsState().value,
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
    nameStateError: HabiTrackError? = null,
    descriptionState: TextFieldState,
    onAction: (HabitCreationAction) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val nameInputFocusRequester = remember { FocusRequester() }
    val descriptionInputFocusRequester = remember { FocusRequester() }
    var showColorPicker by remember { mutableStateOf(false) }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    if (!state.isEditing()) {
        LaunchedEffect(Unit) {
            nameInputFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

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
                        text = stringResource(if (state.isEditing()) Res.string.habit_creation_header_editor else Res.string.habit_creation_header),
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
                        .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(12.dp))
                        .widthIn(min = 300.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = nameState.text.isNotBlank(),
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
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            CategoryLabel(name = stringResource(Res.string.habit_creation_name))
            SimpleTextField(
                modifier = Modifier
                    .focusRequester(nameInputFocusRequester),
                state = nameState,
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                onKeyboardAction = {
                    onAction.invoke(HabitCreationAction.ValidateName)
                    descriptionInputFocusRequester.requestFocus()
                },
                isError = nameStateError != null,
                errorMessage = nameStateError?.message()
            )

            CategoryLabel(name = stringResource(Res.string.habit_creation_description))
            SimpleTextField(
                modifier = Modifier
                    .focusRequester(descriptionInputFocusRequester),
                state = descriptionState,
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                onKeyboardAction = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            )

            CategoryLabel(name = stringResource(Res.string.habit_creation_categories))
            LazyVerticalGrid(
                modifier = Modifier
                    .height(((state.allCategories.size / 4) * 70).dp),
                columns = GridCells.Fixed(4)
            ) {
                items(
                    items = state.allCategories
                ) { category ->
                    FilterCategory(
                        habitCategory = category,
                        selected = state.pickedCategories.contains(category),
                        allowActions = true,
                        onClick = {
                            onAction.invoke(HabitCreationAction.ToggleCategory(category))
                        }
                    )
                }
            }

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
                    enabled = state.dailyLimit < DAILY_LIMIT_MAX,
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
                columns = GridCells.Adaptive(minSize = BOX_SIZE),
                modifier = Modifier
                    .heightIn(max = 1000.dp),
                horizontalArrangement = Arrangement.spacedBy(GRID_SPACING),
                verticalArrangement = Arrangement.spacedBy(GRID_SPACING)
            ) {
                items(
                    items = HabitIcon.icons(),
                    key = { it.id }
                ) { icon ->
                    IconButton(
                        modifier = Modifier
                            .aspectRatio(1F)
                            .size(BOX_SIZE)
                            .let {
                                if (icon == state.icon)
                                    it.border(1.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(8.dp))
                                else
                                    it.border(1.dp, MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2F), shape = RoundedCornerShape(8.dp))
                            }
                            .background(MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(8.dp))
                            .padding(5.dp),
                        onClick = {
                            onAction.invoke(HabitCreationAction.UpdateIcon(icon))
                        }
                    ) {
                        Icon(
                            painter = painterResource(icon.resource),
                            contentDescription = null
                        )
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
            LazyVerticalGrid(
                modifier = Modifier
                    .heightIn(max = 1000.dp),
                columns = GridCells.Adaptive(BOX_SIZE),
                horizontalArrangement = Arrangement.spacedBy(GRID_SPACING),
                verticalArrangement = Arrangement.spacedBy(GRID_SPACING)
            ) {
                items(PRE_PICKED_COLORS) { color ->
                    ColorBox(
                        color = color,
                        picked = state.color == color,
                        onAction = onAction
                    )
                }
                item {
                    ColorBox(
                        color = state.customColor,
                        picked = state.color == state.customColor && !PRE_PICKED_COLORS.contains(state.customColor),
                        onAction = onAction
                    )
                }
                item {
                    ColorPickerButton(
                        onClick = { showColorPicker = true }
                    )
                }
            }
        }
    }

    if (showColorPicker) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = {
                showColorPicker = false
            }
        ) {
            ColorPicker(
                state = state,
                onAction = { action ->
                    when (action) {
                        HabitCreationAction.DismissColor -> {
                            showColorPicker = false
                        }
                        else -> Unit
                    }
                    onAction(action)
                }
            )
        }
    }
}

private val MAX_COLOR_PICKER_SIZE = 450.dp
@Composable
fun ColorPicker(
    state: HabitCreationState,
    onAction: (HabitCreationAction) -> Unit
) {
    val controller = rememberColorPickerController()
    val clipboardManager = LocalClipboardManager.current // TODO migrate once https://youtrack.jetbrains.com/issue/CMP-7624 is done

    Column(
        modifier = Modifier
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AlphaTile(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    controller = controller
                )
                Text(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                        .padding(5.dp),
                    text = state.customColor.toHex()
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        clipboardManager.setText(buildAnnotatedString {
                            append(text = state.customColor.toHex())
                        })
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ui_content_copy_24px),
                        contentDescription = stringResource(Res.string.color_picker_action_copy)
                    )
                }
                IconButton(
                    onClick = {
                        clipboardManager.getText()?.text?.let {
                            val color: Color = hexToColor(it) ?: return@let
                            controller.selectByColor(color = color, fromUser = true)
                        }
                    }
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ui_content_paste_24px),
                        contentDescription = stringResource(Res.string.color_picker_action_paste)
                    )
                }
            }
        }

        HsvColorPicker(
            modifier = Modifier
                .aspectRatio(1F)
                .fillMaxWidth()
                .widthIn(max = MAX_COLOR_PICKER_SIZE),
            initialColor = state.customColor,
            controller = controller,
            onColorChanged = { colorEnvelope: ColorEnvelope ->
                onAction.invoke(HabitCreationAction.UpdateCustomColor(colorEnvelope.color))
            },
            drawOnPosSelected = {
                drawColorIndicator(controller.selectedPoint.value, controller.selectedColor.value)
            }
        )

        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = MAX_COLOR_PICKER_SIZE)
                .height(35.dp),
            controller = controller
        )

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
                    onAction.invoke(HabitCreationAction.DismissColor)
                }
            ) {
                Text(
                    text = stringResource(Res.string.color_picker_action_done),
                    style = MaterialTheme.typography.headlineSmall
                )
            }
        }
    }
}

@Composable
private fun ColorBox(
    color: Color,
    picked: Boolean,
    onAction: (HabitCreationAction) -> Unit
) {
    val pickedSquareSize by animateDpAsState(
        targetValue = if (picked) BOX_SIZE / 2 else 0.dp,
        animationSpec = tween(durationMillis = 300)
    )

    Box(
        modifier = Modifier
            .aspectRatio(1F)
            .size(BOX_SIZE)
            .background(color, RoundedCornerShape(10.dp))
            .clickable(
                onClick = {
                    if (!picked) onAction.invoke(HabitCreationAction.UpdateColor(color))
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        if (pickedSquareSize > 0.dp) {
            Box(
                modifier = Modifier
                    .background(Color.Black, RoundedCornerShape(4.dp)) // TODO not black if black is picked
                    .size(pickedSquareSize)
            )
        }
    }
}

@Composable
private fun ColorPickerButton(
    onClick: () -> Unit
) {
    IconButton(
        modifier = Modifier
            .aspectRatio(1F)
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(10.dp))
            .border(1.dp, MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2F), shape = RoundedCornerShape(8.dp))
            .size(BOX_SIZE),
        onClick = onClick
    ) {
        Icon(
            painter = painterResource(Res.drawable.ui_palette_24px),
            contentDescription = stringResource(Res.string.habit_creation_action_color_picker)
        )
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
