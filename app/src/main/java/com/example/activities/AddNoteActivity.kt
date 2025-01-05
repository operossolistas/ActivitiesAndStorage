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

class AddNoteActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)

        setContent {
            MaterialTheme {
                AddNoteScreen(
                    onSaveNote = { name, content ->
                        if (saveNote(name, content)) {
                            finish()
                        }
                    }
                )
            }
        }
    }

    private fun saveNote(name: String, content: String): Boolean {
        if (name.isBlank() || content.isBlank()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return false
        }

        val note = Note(name = name, content = content)
        val result = dbHelper.addNote(note)
        
        if (result > 0) {
            Toast.makeText(this, "Note saved successfully", Toast.LENGTH_SHORT).show()
            return true
        }
        
        Toast.makeText(this, "Failed to save note", Toast.LENGTH_SHORT).show()
        return false
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteScreen(onSaveNote: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Note Title") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        )

        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Note Content") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 8.dp)
        )

        Button(
            onClick = { onSaveNote(name, content) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Save Note")
        }
    }
} 