package com.raywenderlich.android.jetnotes.ui.components

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raywenderlich.android.jetnotes.domain.model.ColorModel
import com.raywenderlich.android.jetnotes.domain.model.NEW_NOTE_ID
import com.raywenderlich.android.jetnotes.domain.model.NoteModel
import com.raywenderlich.android.jetnotes.routing.JetNotesRouter
import com.raywenderlich.android.jetnotes.routing.Screen
import com.raywenderlich.android.jetnotes.util.composeFillMaxWidth
import com.raywenderlich.android.jetnotes.util.dp
import com.raywenderlich.android.jetnotes.util.fromHex
import com.raywenderlich.android.jetnotes.util.sp
import com.raywenderlich.android.jetnotes.viewmodel.MainViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


@Composable
fun ColorItem(
    color: ColorModel,
    onColorSelect: (ColorModel) -> Unit
) {
    Row(
        modifier = composeFillMaxWidth()
            .clickable { onColorSelect(color) }
    ) {
        NoteColor(
            modifier = Modifier.padding(dp(16)),
            color = Color.fromHex(color.hex),
            size = dp(80),
            border = dp(2)
        )

        Text(
            text = color.name,
            fontSize = 22.sp,
            modifier = Modifier
                .padding(horizontal = dp(16))
                .align(Alignment.CenterVertically)
                .weight(1f)
        )
    }
}

@Composable
private fun ColorPicker(
    colors: List<ColorModel>,
    onColorSelect: (ColorModel) -> Unit
) {
    Column(modifier = composeFillMaxWidth()) {
        Text(
            text = "Color Picker",
            fontSize = sp(18),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(dp(8))
        )

        LazyColumn {
            items(colors.size) { itemIndex ->
                val color = colors[itemIndex]
                ColorItem(color = color, onColorSelect = onColorSelect)
            }
        }
    }
}


@ExperimentalMaterialApi
@Composable
fun SaveNoteScreen(viewModel: MainViewModel) {

    val noteEntry: NoteModel by viewModel.noteEntry
        .observeAsState(NoteModel())

    val colors: List<ColorModel> by viewModel.colors
        .observeAsState(listOf())

    val bottomDrawerState: BottomDrawerState =
        rememberBottomDrawerState(initialValue = BottomDrawerValue.Closed)

    val coroutineScope = rememberCoroutineScope()

    val moveNoteToTrashDialogShownState: MutableState<Boolean> = rememberSaveable {
        mutableStateOf(false)
    }

    BackHandler(onBack = {
        if (bottomDrawerState.isOpen) {
            coroutineScope.launch {
                bottomDrawerState.close()
            }
        } else {
            JetNotesRouter.navigateTo(Screen.Notes)
        }
    })

    Scaffold(
        topBar = {
            val isEditingMode = noteEntry.id != NEW_NOTE_ID
            SaveNoteTopAppBar(
                isEditingMode,
                onBackClick = {
                    JetNotesRouter.navigateTo(Screen.Notes)
                },
                onSaveNoteClick = {
                    viewModel.saveNote(noteEntry)
                },
                onOpenColorPickerClick = {
                    coroutineScope.launch {
                        bottomDrawerState.open()
                    }
                },
                onDeleteNoteClick = {
                    moveNoteToTrashDialogShownState.value = true
                }
            )
        },
        content = {
            BottomDrawer(
                drawerState = bottomDrawerState,
                drawerContent = {
                    ColorPicker(colors = colors, onColorSelect = { color ->
                        val newNoteEntry = noteEntry.copy(color = color)
                        viewModel.onNoteEntryChange(newNoteEntry)
                        coroutineScope.launch {
                            bottomDrawerState.close()
                        }
                    })
                },
                content = {
                    SaveNoteContent(
                        note = noteEntry,
                        onNoteChange = {
                            viewModel.onNoteEntryChange(it)
                        },
                        onPickedColorClicked = {
                            coroutineScope.launch {
                                bottomDrawerState.open()
                            }
                        }
                    )
                }
            )

            if (moveNoteToTrashDialogShownState.value) {
                AlertDialog(
                    onDismissRequest = {
                        moveNoteToTrashDialogShownState.value = false
                    },
                    title = {
                        Text(text = "Move note to the trash?")
                    },
                    text = {
                        Text(text = "Are you sure you want to move this note to the trash?")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            viewModel.moveNoteToTrash(noteEntry)
                        }) {
                            Text(text = "Confirm")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            moveNoteToTrashDialogShownState.value = false
                        }) {
                            Text(text = "Dismiss")
                        }
                    }
                )
            }
        }
    )
}

@Composable
fun SaveNoteTopAppBar(
    isEditingMode: Boolean,
    onBackClick: () -> Unit,
    onSaveNoteClick: () -> Unit,
    onOpenColorPickerClick: () -> Unit,
    onDeleteNoteClick: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = "Save Note",
                color = MaterialTheme.colors.onPrimary
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Go Back Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        },
        actions = {
            IconButton(onClick = onSaveNoteClick) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Save Note",
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            IconButton(onClick = onOpenColorPickerClick) {
                Icon(
                    painter = painterResource(id = com.raywenderlich.android.jetnotes.R.drawable.ic_baseline_color_lens_24),
                    contentDescription = "Open Color Picker Button",
                    tint = MaterialTheme.colors.onPrimary
                )
            }

            // Delete action icon (show only in editing mode)
            if (isEditingMode) {
                IconButton(onClick = onDeleteNoteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Note Button",
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        }
    )
}

@Composable
private fun SaveNoteContent(
    note: NoteModel,
    onNoteChange: (NoteModel) -> Unit,
    onPickedColorClicked: (() -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxSize()) {
        ContentTextField(
            label = "Title",
            text = note.title,
            onTextChange = { newTitle ->
                onNoteChange(note.copy(title = newTitle))
            }
        )

        ContentTextField(
            modifier = Modifier
                .heightIn(max = 240.dp)
                .padding(top = 16.dp),
            label = "Body",
            text = note.content,
            onTextChange = { newContent ->
                onNoteChange.invoke(note.copy(content = newContent))
            }
        )

        val canBeCheckedOff: Boolean = note.isCheckedOff != null

        NoteCheckOption(
            isChecked = canBeCheckedOff,
            onCheckedChange = { canBeCheckedOffNewValue ->
                val isCheckedOff: Boolean? = if (canBeCheckedOffNewValue) false else null
                onNoteChange.invoke(note.copy(isCheckedOff = isCheckedOff))
            }
        )

        PickedColor(color = note.color, onPickedColorClicked)

    }
}


@Composable
private fun ContentTextField(
    modifier: Modifier = Modifier,
    label: String,
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}


@Composable
private fun NoteCheckOption(
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
    ) {
        Text(
            text = "Can note be checked off?",
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}


@Composable
private fun PickedColor(
    color: ColorModel,
    onClick: (() -> Unit)? = null
) {
    Row(
        Modifier
            .padding(8.dp)
            .padding(top = 16.dp)
            .clickable {
                onClick?.invoke()
            }
    ) {
        Text(
            text = "Picked color",
            modifier = Modifier
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        NoteColor(
            color = Color.fromHex(color.hex),
            size = 40.dp,
            border = 1.dp,
            modifier = Modifier.padding(4.dp)
        )
    }
}


@Preview
@Composable
fun ColorItemPreview() {
    ColorItem(ColorModel.DEFAULT) { }
}

@Preview
@Composable
fun ColorPickerPreview() {
    ColorPicker(
        colors = listOf(
            ColorModel.DEFAULT,
            ColorModel.DEFAULT,
            ColorModel.DEFAULT
        )
    ) {}
}

@Preview
@Composable
fun SaveNoteTopAppBarPreview() {
    SaveNoteTopAppBar(
        isEditingMode = true,
        onBackClick = { /*TODO*/ },
        onSaveNoteClick = { /*TODO*/ },
        onOpenColorPickerClick = { /*TODO*/ }) {

    }
}

@Preview
@Composable
fun PickedColorPreview() {
    PickedColor(color = ColorModel.DEFAULT) {}
}

@Preview
@Composable
fun NoteCheckOptionPreview() {
    NoteCheckOption(true) {}
}

@Preview
@Composable
fun ContentTextFieldPreview() {
    ContentTextField(
        label = "Title",
        text = "",
        onTextChange = {}
    )
}

@Preview
@Composable
fun SaveNoteContentPreview() {
    SaveNoteContent(
        note = NoteModel(title = "Title", content = "content"),
        onNoteChange = {}
    )
}

