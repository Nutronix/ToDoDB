package com.example.studentcard.database.controller

import ToDoClass
import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.studentcard.database.DbHelper

/**
 * In der Klasse werden ToDos erstellt, gelesen, geupdeted und gelöscht.
 */
class ToDoController(context: Context) {

    private val dbHelper = DbHelper(context)

    /**
     * Aktualisiert ein bestehendes ToDo in der Datenbank.
     *
     * @param toDoClass Die ToDo mit den aktualisierten Informationen.
     * @return Ein Boolean-Wert, der anzeigt, ob die Aktualisierung erfolgreich war.
     */
    fun updateToDo(toDoClass: ToDoClass): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("name", toDoClass.name)
                put("priority", toDoClass.priority)
                put("endDate", toDoClass.endDate)
                put("description", toDoClass.description)
                put("isCompleted", if (toDoClass.isCompleted) 1 else 0)
            }
            val result = db.update("todos", values, "id = ?", arrayOf(toDoClass.id.toString()))
            Log.d("ToDoController", "Update result: $result, ToDo ID: ${toDoClass.id}")
            result > 0
        } catch (e: Exception) {
            Log.e("ToDoController", "Update failed", e)
            false
        } finally {
            db.close()
        }
    }

    /**
     * Löscht ein ToDo aus der Datenbank.
     *
     * @param toDoId Die ID des ToDos, welches gelöscht werden soll.
     * @return Ein Boolean-Wert, der anzeigt, ob das Löschen erfolgreich war.
     */
    fun deleteToDo(toDoId: Int): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val result = db.delete("todos", "id = ?", arrayOf(toDoId.toString()))
            result > 0
        } catch (e: Exception) {
            Log.e("ToDoController", "Delete failed", e)
            false
        } finally {
            db.close()
        }
    }

    /**
     * Ruft alle aktiven ToDos aus der Datenbank ab.
     *
     * @return Eine Liste von ToDoClass-Objekten, die die aktiven ToDos darstellen.
     */
    fun getActiveToDos(): List<ToDoClass> {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM todos WHERE isCompleted = 0", null)
        val toDoList = mutableListOf<ToDoClass>()

        if (cursor.moveToFirst()) {
            do {
                val toDo = ToDoClass(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                    name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    priority = cursor.getInt(cursor.getColumnIndexOrThrow("priority")),
                    endDate = cursor.getLong(cursor.getColumnIndexOrThrow("endDate")),
                    description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                    isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("isCompleted")) == 1
                )
                toDoList.add(toDo)
            } while (cursor.moveToNext())
        }

        cursor.close()
        return toDoList
    }

    /**
     * Ruft alle erledigten ToDos aus der Datenbank ab.
     *
     * @return Eine Liste von ToDoClass-Objekten, die die erledigten ToDos darstellen.
     */
    fun getCompletedToDos(): List<ToDoClass> {
        val db = dbHelper.readableDatabase
        val completedToDoClasses = mutableListOf<ToDoClass>()
        val cursor = db.rawQuery("SELECT * FROM todos WHERE isCompleted = 1", null)

        try {
            if (cursor.moveToFirst()) {
                do {
                    val toDoClass = ToDoClass(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
                        priority = cursor.getInt(cursor.getColumnIndexOrThrow("priority")),
                        endDate = cursor.getLong(cursor.getColumnIndexOrThrow("endDate")),
                        description = cursor.getString(cursor.getColumnIndexOrThrow("description")),
                        isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow("isCompleted")) == 1
                    )
                    completedToDoClasses.add(toDoClass)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("ToDoController", "Fetching completed ToDos failed", e)
        } finally {
            cursor.close()
            db.close()
        }
        return completedToDoClasses
    }
}