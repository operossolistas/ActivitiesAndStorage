package com.example.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.activities.data.DatabaseHelper
import com.example.activities.model.Note

class DeleteNoteActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)

        setContent {
            MaterialTheme {
                DeleteNoteScreen(
                    notes = dbHelper.getAllNotes(),
                    onDeleteNote = { noteId ->
                        if (deleteNote(noteId)) {
                            finish()
                        }
                    }
                )
            }
        }
    }

    private fun deleteNote(noteId: Long): Boolean {
        val result = dbHelper.deleteNote(noteId)
        if (result > 0) {
            Toast.makeText(this, "Note deleted successfully", Toast.LENGTH_SHORT).show()
            return true
        }
        Toast.makeText(this, "Failed to delete note", Toast.LENGTH_SHORT).show()
        return false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeleteNoteScreen(notes: List<Note>, onDeleteNote: (Long) -> Unit) {
    var selectedNote by remember { mutableStateOf<Note?>(null) }
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            TextField(
                value = selectedNote?.name ?: "Select a note to delete",
                onValueChange = { },
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                notes.forEach { note ->
                    DropdownMenuItem(
                        text = { 
                            Column {
                                Text(
                                    text = note.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = note.content,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1
                                )
                            }
                        },
                        onClick = {
                            selectedNote = note
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { selectedNote?.let { onDeleteNote(it.id) } },
            enabled = selectedNote != null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError
            )
        ) {
            Text("Delete Selected Note")
        }
    }
}