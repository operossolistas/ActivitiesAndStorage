package com.example.activities.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.activities.model.Note

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "NotesDB"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NOTES = "notes"
        private const val KEY_ID = "id"
        private const val KEY_NAME = "name"
        private const val KEY_CONTENT = "content"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NOTES (
                $KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $KEY_NAME TEXT NOT NULL,
                $KEY_CONTENT TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }

    fun addNote(note: Note): Long {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(KEY_NAME, note.name)
            put(KEY_CONTENT, note.content)
        }
        return db.insert(TABLE_NOTES, null, values)
    }

    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val db = this.readableDatabase
        val cursor = db.query(TABLE_NOTES, null, null, null, null, null, null)

        with(cursor) {
            while (moveToNext()) {
                val note = Note(
                    id = getLong(getColumnIndexOrThrow(KEY_ID)),
                    name = getString(getColumnIndexOrThrow(KEY_NAME)),
                    content = getString(getColumnIndexOrThrow(KEY_CONTENT))
                )
                notes.add(note)
            }
        }
        cursor.close()
        return notes
    }

    fun deleteNote(noteId: Long): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NOTES, "$KEY_ID = ?", arrayOf(noteId.toString()))
    }
} 