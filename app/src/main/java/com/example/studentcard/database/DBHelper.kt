package com.example.studentcard.database

import ToDoClass
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.FileOutputStream


/**
 * Die Klasse verwaltet die Datenbankverbindung und -operationen für die ToDo-Daten.
 */
class DbHelper(val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    /**
     * Wird aufgerufen, wenn die Datenbank zum ersten Mal erstellt wird.
     * Die Methode bleibt leer, da wir eine bestehende Datenbank aus den assets verwenden.
     */
    override fun onCreate(db: SQLiteDatabase?) {
    }

    /**
     * Wird aufgerufen, wenn die Datenbank aktualisiert werden muss.
     * Löscht die bestehende Datenbank und kopiert sie erneut aus den Assets.
     *
     * @param db Die zu aktualisierende SQLite-Datenbank.
     * @param oldVersion Die alte Version der Datenbank.
     * @param newVersion Die neue Version der Datenbank.
     */
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        context.deleteDatabase(DATABASE_NAME)
        copyDatabaseFromAssets()
    }

    /**
     * Gibt eine lesbare Datenbank zurück und stellt sicher, dass die ToDo-Tabelle vorhanden und korrekt ist.
     *
     * @return Eine lesbare SQLite-Datenbank.
     */
    override fun getReadableDatabase(): SQLiteDatabase {
        copyDatabaseFromAssets()
        val db = super.getReadableDatabase()
        validateTodosTable(db)
        createTodosTableIfNotExists(db)
        return db
    }

    /**
     * Gibt eine schreibbare Datenbank zurück und stellt sicher, dass die ToDo-Tabelle vorhanden und korrekt ist.
     *
     * @return Eine schreibbare SQLite-Datenbank.
     */
    override fun getWritableDatabase(): SQLiteDatabase {
        copyDatabaseFromAssets()
        val db = super.getWritableDatabase()
        validateTodosTable(db)
        createTodosTableIfNotExists(db)
        return db
    }

    /**
     * Kopiert die Datenbank aus den Assets in den internen Speicher, wenn sie noch nicht existiert.
     */
    fun copyDatabaseFromAssets() {
        val dbPath = context.getDatabasePath(DATABASE_NAME)
        if (!dbPath.exists()) {
            try {
                context.assets.open(DATABASE_NAME).use { inputStream ->
                    FileOutputStream(dbPath).use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                Log.d("DbHelper", "Database copied successfully to: ${dbPath.absolutePath}")
            } catch (e: Exception) {
                Log.e("DbHelper", "Error copying database", e)
            }
        }
    }

    /**
     * Überprüft, ob die ToDo-Tabelle die notwendigen Spalten enthält und fügt fehlende Spalten hinzu.
     *
     * @param db Die SQLite-Datenbank, die überprüft wird.
     */
    private fun validateTodosTable(db: SQLiteDatabase) {
        val cursor = db.rawQuery("PRAGMA table_info(todos);", null)
        val columns = mutableSetOf<String>()
        while (cursor.moveToNext()) {
            val columnName = cursor.getString(cursor.getColumnIndexOrThrow("name"))
            columns.add(columnName)
        }
        cursor.close()

        if (!columns.contains("description")) {
            db.execSQL("ALTER TABLE todos ADD COLUMN description TEXT;")
        }
        if (!columns.contains("isCompleted")) {
            db.execSQL("ALTER TABLE todos ADD COLUMN isCompleted INTEGER NOT NULL DEFAULT 0;")
        }
    }

    /**
     * Erstellt die ToDo-Tabelle, wenn sie noch nicht existiert.
     *
     * @param db Die SQLite-Datenbank, in der die Tabelle erstellt werden soll.
     */
    private fun createTodosTableIfNotExists(db: SQLiteDatabase) {
        val createTableQuery = """
        CREATE TABLE IF NOT EXISTS todos (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            name TEXT NOT NULL,
            priority INTEGER NOT NULL,
            endDate INTEGER NOT NULL,
            description TEXT,
            isCompleted INTEGER NOT NULL
        );
    """
        try {
            db.execSQL(createTableQuery)
            Log.d("DbHelper", "Table 'todos' is ready to use.")
        } catch (e: Exception) {
            Log.e("DbHelper", "Error checking/creating table 'todos'", e)
        }
    }

    /**
     * Ruft alle aktiven ToDos aus der Datenbank ab.
     *
     * @return Eine Liste von ToDoClass-Objekten, die die aktiven ToDos darstellen.
     */
    fun getActiveToDos(): List<ToDoClass> {
        val dbHelper = DbHelper(context)
        val activeToDoClasses = mutableListOf<ToDoClass>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM todos WHERE isCompleted = 0", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val priority = cursor.getInt(cursor.getColumnIndexOrThrow("priority"))
                val endDate = cursor.getLong(cursor.getColumnIndexOrThrow("endDate"))
                val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
                val isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("isCompleted")) == 1
                activeToDoClasses.add(
                    ToDoClass(
                        id,
                        name,
                        priority,
                        endDate,
                        description,
                        isCompleted
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return activeToDoClasses
    }

    companion object {
        private const val DATABASE_NAME = "datenbank.db"
        private const val DATABASE_VERSION = 1
    }
}