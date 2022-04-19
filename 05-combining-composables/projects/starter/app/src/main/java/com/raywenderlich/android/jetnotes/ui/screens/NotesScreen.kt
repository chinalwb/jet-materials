package com.raywenderlich.android.jetnotes.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.tooling.preview.Preview
import com.raywenderlich.android.jetnotes.domain.model.NoteModel
import com.raywenderlich.android.jetnotes.routing.Screen
import com.raywenderlich.android.jetnotes.theme.JetNotesTheme
import com.raywenderlich.android.jetnotes.ui.components.AppDrawer
import com.raywenderlich.android.jetnotes.ui.components.Note
import com.raywenderlich.android.jetnotes.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun NotesScreen(viewModel: MainViewModel) {

    val notes: List<NoteModel> by viewModel
        .notesNotInTrash
        .observeAsState(listOf())

    val scaffoldState: ScaffoldState = rememberScaffoldState()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "JetNotes", color = MaterialTheme.colors.onPrimary)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        coroutineScope.launch { scaffoldState.drawerState.open() }
                    }) {
                        Icon(
                            imageVector = Icons.Filled.List,
                            contentDescription = "Drawer Button"
                        )
                    }
                }
            )
        },
        scaffoldState = scaffoldState,
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Notes,
                closeDrawerAction = {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onCreateNewNoteClick() },
                contentColor = MaterialTheme.colors.background,
                content = {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Add Note Button"
                    )
                }
            )
        },
        content = {
            if (notes.isNotEmpty()) {
                NotesList(
                    notes = notes,
                    onNoteClick = { viewModel.onNoteClick(it) },
                    onNoteCheckedChange = { viewModel.onNoteCheckedChange(it) }
                )
            }
        }
    )

//    Column {
//        TopAppBar(title = "JetNotes", icon = Icons.Filled.List, onIconClick = {})
//        NotesList(
//            notes,
//            onNoteCheckedChange = { viewModel.onNoteCheckedChange(it) },
//            onNoteClick = { viewModel.onNoteClick(it) }
//        )
//    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NotesList(
    notes: List<NoteModel>,
    onNoteClick: (NoteModel) -> Unit,
    onNoteCheckedChange: (NoteModel) -> Unit
) {
    LazyColumn {
        items(count = notes.size) { noteIndex ->
            val note = notes[noteIndex]
            Note(
                note = note,
                onNoteClick = onNoteClick,
                onNoteCheckedChange = onNoteCheckedChange,
                isSelected = false,
            )
        }
    }
}

@Preview
@Composable
fun NotesListPreview() {
    JetNotesTheme {
        NotesList(
            notes = listOf(
                NoteModel(1, "Note 1", "Content 1", true),
                NoteModel(2, "Note 2", "Content 2", null),
                NoteModel(3, "Note 3", "Content 3", false),
            ),
            onNoteClick = {},
            onNoteCheckedChange = {}
        )
    }
}