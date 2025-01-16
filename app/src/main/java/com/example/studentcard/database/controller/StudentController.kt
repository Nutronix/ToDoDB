package com.example.studentcard.database.controller

import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.util.Log
import com.example.studentcard.database.DbHelper
import com.example.studentcard.database.dataclass.StudentDataClass
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class StudentController(context: Context) {
    private val dbHelper = DbHelper(context)

    fun insertStudent(student: StudentDataClass): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("firstname", student.firstname)
                put("lastname", student.lastname)
                put("matrikelnummer", student.matnumber)
                put("email", student.email)
            }
            val result = db.insert("Student", null, values)
            result != -1L
        } catch (e: Exception) {
            android.util.Log.e("StudentController", "Insert failed", e)
            false
        } finally {
            db.close()
        }
    }

    fun updateStudent(student: StudentDataClass): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val values = ContentValues().apply {
                put("firstname", student.firstname)
                put("lastname", student.lastname)
                put("matrikelnummer", student.matnumber)
                put("email", student.email)
            }
            val result = db.update("Student", values, "id = ?", arrayOf(student.id.toString()))
            Log.d("StudentController", "Update result: $result, Student ID: ${student.id}")
            result > 0
        } catch (e: Exception) {
            Log.e("StudentController", "Update failed", e)
            false
        } finally {
            db.close()
        }
    }

    fun deleteStudent(studentId: Int): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            val result = db.delete("Student", "id = ?", arrayOf(studentId.toString()))
            result > 0
        } catch (e: Exception) {
            Log.e("StudentController", "Delete failed", e)
            false
        } finally {
            db.close()
        }
    }

    fun getAllStudents(): List<StudentDataClass> {
        val db = dbHelper.readableDatabase
        val students = mutableListOf<StudentDataClass>()
        val cursor = db.rawQuery("SELECT * FROM Student", null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    val student = StudentDataClass(
                        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
                        firstname = cursor.getString(cursor.getColumnIndexOrThrow("firstname")),
                        lastname = cursor.getString(cursor.getColumnIndexOrThrow("lastname")),
                        matnumber = cursor.getString(cursor.getColumnIndexOrThrow("matrikelnummer")),
                        email = cursor.getString(cursor.getColumnIndexOrThrow("email"))
                    )
                    students.add(student)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            Log.e("StudentController", "Fetching students failed", e)
        } finally {
            cursor.close()
            db.close()
        }
        return students
    }

}