package com.example.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.activities.ui.theme.ActivitiesTheme
import com.example.activities.data.DatabaseHelper
import com.example.activities.model.Note
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver

class MainActivity : ComponentActivity() {
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelper(this)

        setContent {
            ActivitiesTheme {
                MainScreenContent(dbHelper)
            }
        }
    }
}

@Composable
fun MainScreenContent(dbHelper: DatabaseHelper) {
    var notes by remember { mutableStateOf(dbHelper.getAllNotes()) }
    val context = LocalContext.current

    // Refresh notes when composition is first created
    LaunchedEffect(Unit) {
        notes = dbHelper.getAllNotes()
    }

    // Create a key that changes when we want to refresh the notes
    var refreshKey by remember { mutableStateOf(0) }

    // Refresh notes whenever the key changes
    LaunchedEffect(refreshKey) {
        notes = dbHelper.getAllNotes()
    }

    // Observe activity lifecycle to refresh notes
    DisposableEffect(Unit) {
        val activity = context as ComponentActivity
        val lifecycleObserver = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                refreshKey++ // Increment key to trigger refresh
            }
        }
        activity.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            activity.lifecycle.removeObserver(lifecycleObserver)
        }
    }

    MainScreen(
        notes = notes,
        onAddNoteClick = {
            context.startActivity(Intent(context, AddNoteActivity::class.java))
        },
        onDeleteNoteClick = {
            context.startActivity(Intent(context, DeleteNoteActivity::class.java))
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    notes: List<Note>,
    onAddNoteClick: () -> Unit,
    onDeleteNoteClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notes App") },
                actions = {
                    Button(
                        onClick = onAddNoteClick,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text("Add Note")
                    }
                    Button(
                        onClick = onDeleteNoteClick,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text("Delete Note")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            items(notes) { note ->
                NoteItem(note = note)
            }
        }
    }
}

@Composable
fun NoteItem(note: Note) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = note.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = note.content,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}