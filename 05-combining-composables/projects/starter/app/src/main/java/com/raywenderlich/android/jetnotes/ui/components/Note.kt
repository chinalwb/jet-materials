package com.raywenderlich.android.jetnotes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raywenderlich.android.jetnotes.domain.model.NoteModel
import com.raywenderlich.android.jetnotes.theme.rwGreen
import com.raywenderlich.android.jetnotes.util.dp
import com.raywenderlich.android.jetnotes.util.fromHex


@ExperimentalMaterialApi
@Composable
fun Note(
    modifier: Modifier = Modifier,
    note: NoteModel,
    onNoteClick: (NoteModel) -> Unit = {},
    onNoteCheckedChange: (NoteModel) -> Unit = {},
    isSelected: Boolean
) {
    val background = if (isSelected)
        Color.LightGray
    else
        MaterialTheme.colors.surface

    Card(
        shape = RoundedCornerShape(4.dp),
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        backgroundColor = background
    ) {
        ListItem(
            text = { Text(text = note.title, maxLines = 1) },
            secondaryText = {
                Text(text = note.content, maxLines = 1)
            },
            icon = {
                NoteColor(
                    color = Color.fromHex(note.color.hex),
                    size = 40.dp,
                    border = 1.dp
                )
            },
            trailing = {
                if (note.isCheckedOff != null) {
                    Checkbox(
                        checked = note.isCheckedOff,
                        onCheckedChange = { isChecked ->
                            val newNote = note.copy(isCheckedOff = isChecked)
                            onNoteCheckedChange.invoke(newNote)
                        },
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            },
            modifier = Modifier.clickable {
                onNoteClick.invoke(note)
            }
        )
    }
}

@ExperimentalMaterialApi
@Preview
@Composable
private fun NotePreview() {
    Note(note = NoteModel(1, "Note 1", "Content 1", null), isSelected = false)
}
//
//@Composable
//fun Note(
//    note: NoteModel,
//    onNoteClick: (NoteModel) -> Unit = {},
//    onNoteCheckedChange: (NoteModel) -> Unit = {}
//) {
//
//    val backgroundShape: Shape = RoundedCornerShape(dp(4))
//    Row(
//        modifier = Modifier
//            .padding(dp(8))
//            .shadow(dp(1), backgroundShape)
//            .fillMaxWidth()
//            .heightIn(min = dp(64))
//            .background(Color.White, backgroundShape)
//            .clickable(onClick = { onNoteClick(note) })
//    ) {
//        NoteColor(
//            modifier = Modifier
//                .align(Alignment.CenterVertically)
//                .padding(start = dp(16), end = dp(16)),
//            color = Color.fromHex(note.color.hex),
//            size = dp(40),
//            border = dp(1)
//        )
//
//        Column(
//            modifier = Modifier
//                .weight(1f)
//                .align(Alignment.CenterVertically)
//        ) {
//            Text(
//                text = note.title,
//                color = Color.Black,
//                maxLines = 1,
//                style = TextStyle(
//                    fontWeight = FontWeight.Normal,
//                    fontSize = 16.sp,
//                    letterSpacing = .15.sp
//                )
//            )
//            Text(
//                text = note.content,
//                color = Color.Black.copy(alpha = 0.75f),
//                maxLines = 1,
//                style = TextStyle(
//                    fontWeight = FontWeight.Normal,
//                    fontSize = 14.sp,
//                    letterSpacing = .25.sp
//                )
//            )
//        }
//
//        if (note.isCheckedOff != null) {
//            Checkbox(
//                checked = note.isCheckedOff,
//                onCheckedChange = { isChecked ->
//                    val newNote = note.copy(isCheckedOff = isChecked)
//                    onNoteCheckedChange(newNote)
//                },
//                modifier = Modifier
//                    .padding(16.dp)
//                    .align(Alignment.CenterVertically)
//            )
//        }
//    }
//}
//
//@Preview
//@Composable
//private fun NotePreview() {
//    Note(
//        NoteModel(1, "Note 1", "Content 1", null)
//    )
//}