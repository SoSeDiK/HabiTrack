package me.sosedik.habitrack.presentation.screen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.github.skydoves.colorpicker.compose.drawColorIndicator
import com.github.skydoves.colorpicker.compose.rememberColorPickerController
import habitrack.composeapp.generated.resources.Res
import habitrack.composeapp.generated.resources.categories_editor_action_abort_desc
import habitrack.composeapp.generated.resources.categories_editor_action_create
import habitrack.composeapp.generated.resources.categories_editor_action_edit
import habitrack.composeapp.generated.resources.categories_editor_edit_header
import habitrack.composeapp.generated.resources.categories_editor_new_header
import habitrack.composeapp.generated.resources.categories_picker_action_create
import habitrack.composeapp.generated.resources.categories_picker_action_delete
import habitrack.composeapp.generated.resources.categories_picker_action_edit
import habitrack.composeapp.generated.resources.categories_picker_action_save
import habitrack.composeapp.generated.resources.categories_picker_description
import habitrack.composeapp.generated.resources.categories_picker_header
import habitrack.composeapp.generated.resources.color_picker_action_copy
import habitrack.composeapp.generated.resources.color_picker_action_done
import habitrack.composeapp.generated.resources.color_picker_action_paste
import habitrack.composeapp.generated.resources.habit_creation_action_color_picker
import habitrack.composeapp.generated.resources.habit_creation_action_more_icons
import habitrack.composeapp.generated.resources.habit_creation_action_save
import habitrack.composeapp.generated.resources.habit_creation_categories
import habitrack.composeapp.generated.resources.habit_creation_categories_none_picked
import habitrack.composeapp.generated.resources.habit_creation_categories_pick
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
import habitrack.composeapp.generated.resources.icon_picker_action_abort_desc
import habitrack.composeapp.generated.resources.icon_picker_action_done
import habitrack.composeapp.generated.resources.icon_picker_header
import habitrack.composeapp.generated.resources.ui_add_24px
import habitrack.composeapp.generated.resources.ui_content_copy_24px
import habitrack.composeapp.generated.resources.ui_content_paste_24px
import habitrack.composeapp.generated.resources.ui_palette_24px
import habitrack.composeapp.generated.resources.ui_remove_24px
import kotlinx.coroutines.launch
import me.sosedik.habitrack.data.domain.HabitCategory
import me.sosedik.habitrack.presentation.component.FilterCategory
import me.sosedik.habitrack.presentation.component.HabitIcon
import me.sosedik.habitrack.presentation.component.SimpleTextField
import me.sosedik.habitrack.presentation.theme.IconCache
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

private val BOX_SIZE = 34.dp
private val GRID_SPACING = 15.dp
val PRE_PICKED_ICONS = listOf(
    "nf-cod-pulse",
    "nf-md-alarm",
    "nf-fae-apple_fruit",
    "nf-md-bed_outline",
    "nf-fa-wallet",
    "nf-md-hand_heart",
    "nf-fa-dumbbell",
    "nf-fa-book",
    "nf-fa-terminal",
    "nf-fa-laptop_code",
    "nf-md-palette_outline",
    "nf-fa-yin_yang",
    "nf-cod-music",
    "nf-fa-shower",
    "nf-fa-bar_chart",
    "nf-md-coffee_outline",
    "nf-fa-dollar",
    "nf-oct-heart",
    "nf-fa-leaf",
    "nf-md-spa_outline",
    "nf-md-gamepad_variant_outline",
    "nf-md-bicycle",
    "nf-fa-person_running",
)

private enum class CategoryPage {
    Overview, Edit, IconPicker
}

@Composable
fun HabitCreationScreenRoot(
    viewModel: HabitCreationViewModel = koinViewModel(),
    iconCache: IconCache,
    onDiscard: () -> Unit,
    onSave: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HabitCreationScreen(
        iconCache = iconCache,
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
    iconCache: IconCache,
    state: HabitCreationState,
    nameState: TextFieldState,
    nameStateError: HabiTrackError? = null,
    descriptionState: TextFieldState,
    onAction: (HabitCreationAction) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { newState ->
            newState != SheetValue.Hidden
        }
    )
    val nameInputFocusRequester = remember { FocusRequester() }
    val descriptionInputFocusRequester = remember { FocusRequester() }
    var showCategoriesPicker by remember { mutableStateOf(false) }
    var showIconPicker by remember { mutableStateOf(false) }
    var showColorPicker by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    if (!state.isEditing()) {
        LaunchedEffect(Unit) {
            nameInputFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    if (state.icon.isEmpty()) {
        LaunchedEffect(Unit) {
            onAction.invoke(HabitCreationAction.UpdateIcon(iconCache.defaultIconKey))
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
            CategoriesOverview(
                iconCache = iconCache,
                state = state,
                onAction = { action ->
                    if (action == HabitCreationAction.EditCategories)
                        showCategoriesPicker = true
                    onAction(action)
                }
            )

            CategoryLabel(name = stringResource(Res.string.habit_creation_daily_limit))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1F)
                        .border(1.dp, MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2F), shape = RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text =
                                if (state.hasDailyLimit())
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
            IconsOverview(
                iconCache = iconCache,
                state = state,
                onAction = onAction
            )
            Button(
                modifier = Modifier
                    .align(Alignment.End),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                onClick = {
                    showIconPicker = true
                }
            ) {
                Text(
                    text = stringResource(Res.string.habit_creation_action_more_icons)
                )
            }

            CategoryLabel(name = stringResource(Res.string.habit_creation_color))
            LazyVerticalGrid(
                modifier = Modifier
                    .heightIn(max = 1_000.dp),
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

    if (showCategoriesPicker) {
        var currentPage by remember { mutableStateOf(CategoryPage.Overview) }

        var editingCategory by remember { mutableStateOf<HabitCategory?>(null) }
        val categoryNameState = rememberTextFieldState()
        var pickedIcon by remember { mutableStateOf<String?>(null) }
        var pickedCategories by remember { mutableStateOf(state.pickedCategories) }

        LaunchedEffect(state.allCategories) {
            val categories = pickedCategories.toMutableList()
            categories.removeAll { !state.allCategories.contains(it) }
            pickedCategories = categories.toList()
        }

        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = {
                if (currentPage.ordinal > 0) {
                    currentPage = CategoryPage.entries[currentPage.ordinal - 1]
                    if (currentPage == CategoryPage.Overview)
                        editingCategory = null
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                } else {
                    showCategoriesPicker = false
                }
            }
        ) {
            Crossfade(targetState = currentPage) { page ->
                when (page) {
                    CategoryPage.Overview -> {
                        CategoriesPicker(
                            iconCache = iconCache,
                            state = state,
                            pickedCategories = pickedCategories,
                            onCategoryClick = { category ->
                                val categories = pickedCategories.toMutableList()
                                if (!categories.remove(category))
                                    categories.add(category)
                                pickedCategories = categories.toList()
                            },
                            onAction = { action ->
                                when (action) {
                                    is HabitCreationAction.SaveCategories -> showCategoriesPicker = false
                                    HabitCreationAction.AddCategory -> {
                                        editingCategory = null
                                        categoryNameState.clearText()
                                        pickedIcon = null
                                        currentPage = CategoryPage.Edit
                                    }
                                    is HabitCreationAction.EditCategory -> {
                                        editingCategory = action.category
                                        categoryNameState.setTextAndPlaceCursorAtEnd(action.category.name)
                                        pickedIcon = action.category.icon
                                        currentPage = CategoryPage.Edit
                                    }
                                    else -> Unit
                                }
                                onAction(action)
                            }
                        )
                    }
                    CategoryPage.Edit -> {
                        CategoryEdit(
                            iconCache = iconCache,
                            category = editingCategory,
                            nameState = categoryNameState,
                            icon = pickedIcon,
                            onDismiss = {
                                currentPage = CategoryPage.Overview
                                editingCategory = null
                                categoryNameState.clearText()
                                pickedIcon = null
                            },
                            onSave = { category ->
                                onAction.invoke(HabitCreationAction.UpdateCategory(category))
                                currentPage = CategoryPage.Overview
                                editingCategory = null
                                categoryNameState.clearText()
                                pickedIcon = null
                            },
                            onIconPick = {
                                currentPage = CategoryPage.IconPicker
                            }
                        )
                    }
                    CategoryPage.IconPicker ->  {
                        IconPicker(
                            iconCache = iconCache,
                            icon = pickedIcon,
                            onDismiss = {
                                currentPage = CategoryPage.Edit
                            },
                            onIconPick = { icon ->
                                pickedIcon = icon
                                currentPage = CategoryPage.Edit
                            }
                        )
                    }
                }
            }
        }
    }

    if (showIconPicker) {
        ModalBottomSheet(
            sheetState = bottomSheetState,
            onDismissRequest = {
                showIconPicker = false
            }
        ) {
            IconPicker(
                iconCache = iconCache,
                icon = state.icon.ifEmpty { null },
                onDismiss = {
                    showIconPicker = false
                },
                onIconPick = { icon ->
                    onAction.invoke(HabitCreationAction.UpdateCustomIcon(icon))
                    showIconPicker = false
                }
            )
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
                    if (action is HabitCreationAction.UpdateCustomColor)
                        showColorPicker = false
                    onAction(action)
                }
            )
        }
    }
}

@Composable
private fun IconsOverview(
    iconCache: IconCache,
    state: HabitCreationState,
    onAction: (HabitCreationAction) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = BOX_SIZE),
        modifier = Modifier
            .heightIn(max = 400.dp),
        horizontalArrangement = Arrangement.spacedBy(GRID_SPACING),
        verticalArrangement = Arrangement.spacedBy(GRID_SPACING)
    ) {
        item {
            IconItem(
                icon =
                    if (PRE_PICKED_ICONS.contains(state.customIcon))
                        iconCache.defaultIconKey
                    else
                        state.customIcon.ifEmpty { iconCache.defaultIconKey },
                iconCache = iconCache,
                state = state,
                onAction = onAction
            )
        }
        items(
            items = PRE_PICKED_ICONS,
            key = { it }
        ) { icon ->
            IconItem(
                icon = icon,
                iconCache = iconCache,
                state = state,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun IconItem(
    icon: String,
    iconCache: IconCache,
    state: HabitCreationState,
    onAction: (HabitCreationAction) -> Unit
) {
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
        HabitIcon(
            iconCache = iconCache,
            id = icon
        )
    }
}

@Composable
private fun CategoriesOverview(
    iconCache: IconCache,
    state: HabitCreationState,
    onAction: (HabitCreationAction) -> Unit
) {
    Box(
        modifier = Modifier
            .clickable {
                onAction.invoke(HabitCreationAction.EditCategories)
            }
            .border(1.dp, MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2F), shape = RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (state.pickedCategories.isEmpty()) {
                Text(
                    text = stringResource(Res.string.habit_creation_categories_none_picked),
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.weight(1F))
            } else {
                Box(
                    modifier = Modifier
                        .weight(1F)
                        .padding(vertical = 5.dp)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        state.pickedCategories.forEach { category ->
                            FilterCategory(
                                iconCache = iconCache,
                                habitCategory = category,
                                selected = false,
                                backgroundColor = MaterialTheme.colorScheme.surfaceContainerLow
                            )
                        }
                    }
                }
            }
            Icon(
                Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = stringResource(Res.string.habit_creation_categories_pick)
            )
        }
    }
}

@Composable
fun CategoriesPicker(
    iconCache: IconCache,
    state: HabitCreationState,
    pickedCategories: List<HabitCategory>,
    onCategoryClick: (HabitCategory) -> Unit,
    onAction: (HabitCreationAction) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = stringResource(Res.string.categories_picker_header),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            text = stringResource(Res.string.categories_picker_description),
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(5.dp))

        FlowRow(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            state.allCategories.forEach { category ->
                var showMenu by remember { mutableStateOf(false) }
                
                Box {
                    FilterCategory(
                        iconCache = iconCache,
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    onCategoryClick(category)
                                },
                                onLongClick = {
                                    showMenu = true
                                }
                            ),
                        habitCategory = category,
                        selected = pickedCategories.contains(category)
                    )
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = {
                            showMenu = false
                        }
                    ) {
                        DropdownMenuItem(
                            onClick = {
                                showMenu = false
                                onAction.invoke(HabitCreationAction.EditCategory(category))
                            },
                            text = {
                                Text(
                                    text = stringResource(Res.string.categories_picker_action_edit)
                                )
                            }
                        )
                        DropdownMenuItem(
                            onClick = {
                                showMenu = false
                                // TODO confirmation dialogue
                                onAction.invoke(HabitCreationAction.DeleteCategory(category))
                            },
                            text = {
                                Text(
                                    text = stringResource(Res.string.categories_picker_action_delete)
                                )
                            }
                        )
                    }
                }
            }
        }
        TextButton(
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 5.dp),
            onClick = {
                onAction.invoke(HabitCreationAction.AddCategory)
            }
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ui_add_24px),
                    contentDescription = null
                )
                Text(
                    text = stringResource(Res.string.categories_picker_action_create),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        PickerAction(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            text = stringResource(Res.string.categories_picker_action_save),
            onClick = {
                onAction.invoke(HabitCreationAction.SaveCategories(pickedCategories))
            }
        )
    }
}

@Composable
private fun CategoryEdit(
    iconCache: IconCache,
    category: HabitCategory?,
    nameState: TextFieldState,
    icon: String?,
    onDismiss: () -> Unit,
    onSave: (HabitCategory) -> Unit,
    onIconPick: () -> Unit
) {
    val nameInputFocusRequester = remember { FocusRequester() }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    if (category == null && nameState.text.isEmpty()) {
        LaunchedEffect(Unit) {
            nameInputFocusRequester.requestFocus()
            keyboardController?.show()
        }
    }

    Column(
        modifier = Modifier
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = stringResource(if (category == null) Res.string.categories_editor_new_header else Res.string.categories_editor_edit_header),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onIconPick
            ) {
                HabitIcon(
                    iconCache = iconCache,
                    id = icon
                )
            }
            SimpleTextField(
                modifier = Modifier
                    .focusRequester(nameInputFocusRequester),
                state = nameState,
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Words,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Done
                ),
                onKeyboardAction = {
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss
            ) {
                Icon(
                    Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    contentDescription = stringResource(Res.string.categories_editor_action_abort_desc)
                )
            }
            PickerAction(
                text = stringResource(if (category == null) Res.string.categories_editor_action_create else Res.string.categories_editor_action_edit),
                enabled = nameState.text.isNotBlank(),
                onClick = {
                    onSave.invoke(HabitCategory(
                        id = category?.id ?: 0L,
                        name = nameState.text.toString(),
                        icon = icon ?: iconCache.defaultIconKey
                    ))
                }
            )
        }
    }
}

@Composable
private fun IconPicker(
    iconCache: IconCache,
    icon: String?,
    onDismiss: () -> Unit,
    onIconPick: (String) -> Unit
) {
    val iconNameFilter = rememberTextFieldState()
    var pickedIcon by remember { mutableStateOf(icon ?: iconCache.defaultIconKey) }

    Column(
        modifier = Modifier
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = stringResource(Res.string.icon_picker_header),
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HabitIcon(
                modifier = Modifier.size(BOX_SIZE),
                iconCache = iconCache,
                id = pickedIcon
            )
            SimpleTextField(
                state = iconNameFilter,
                lineLimits = TextFieldLineLimits.SingleLine,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    autoCorrectEnabled = true,
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search
                )
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = BOX_SIZE),
            modifier = Modifier
                .heightIn(max = 400.dp),
            horizontalArrangement = Arrangement.spacedBy(GRID_SPACING),
            verticalArrangement = Arrangement.spacedBy(GRID_SPACING)
        ) {
            items(
                items =
                    if (iconNameFilter.text.isBlank())
                        iconCache.mappings.keys.toList()
                    else
                        iconCache.mappings.keys.filter { key ->
                            for (text in iconNameFilter.text.split(" ")) {
                                if (!key.contains(text, ignoreCase = true))
                                    return@filter false
                            }
                            return@filter true
                        }.toList(),
                key = { it }
            ) { icon ->
                IconButton(
                    modifier = Modifier
                        .aspectRatio(1F)
                        .size(BOX_SIZE)
                        .let {
                            if (icon == pickedIcon)
                                it.border(1.dp, MaterialTheme.colorScheme.inverseSurface, RoundedCornerShape(8.dp))
                            else
                                it.border(1.dp, MaterialTheme.colorScheme.inverseSurface.copy(alpha = 0.2F), shape = RoundedCornerShape(8.dp))
                        }
                        .background(MaterialTheme.colorScheme.surfaceContainer, shape = RoundedCornerShape(8.dp))
                        .padding(5.dp),
                    onClick = {
                        pickedIcon = icon
                    }
                ) {
                    HabitIcon(
                        iconCache = iconCache,
                        id = icon
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onDismiss
            ) {
                Icon(
                    Icons.AutoMirrored.Default.KeyboardArrowLeft,
                    contentDescription = stringResource(Res.string.icon_picker_action_abort_desc)
                )
            }
            PickerAction(
                text = stringResource(Res.string.icon_picker_action_done),
                onClick = {
                    onIconPick(pickedIcon)
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
                    text = controller.selectedColor.value.toHex()
                )
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        clipboardManager.setText(buildAnnotatedString {
                            append(text = controller.selectedColor.value.toHex())
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

        PickerAction(
            text = stringResource(Res.string.color_picker_action_done),
            onClick = {
                onAction.invoke(HabitCreationAction.UpdateCustomColor(controller.selectedColor.value))
            }
        )
    }
}

@Composable
private fun PickerAction(
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Button(
            modifier = Modifier
                .widthIn(min = 300.dp),
            shape = RoundedCornerShape(12.dp),
            enabled = enabled,
            onClick = onClick
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall
            )
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
