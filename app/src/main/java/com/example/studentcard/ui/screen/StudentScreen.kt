package com.example.studentcard.ui.screen

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.studentcard.database.controller.StudentController
import com.example.studentcard.database.dataclass.StudentDataClass

@Composable
fun StudentScreen(
    context: Context,
    navController: NavHostController = rememberNavController()
) {
    val studentController = StudentController(context)
    var students by remember { mutableStateOf(studentController.getAllStudents()) }
    var showEditDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<StudentDataClass?>(null) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.navigate("dashboard") {
                popUpTo("dashboard") { inclusive = true }
            } }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück zum Dashboard"
                )
            }
            Text(
                text = "Studierendenregister",
                style = MaterialTheme.typography.titleLarge
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(students) { student ->
                ExpandableStudentCard(
                    student = student,
                    onEditClick = {
                        selectedStudent = student
                        showEditDialog = true
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                selectedStudent = null
                showEditDialog = true
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Eintrag anlegen",
                style = MaterialTheme.typography.titleLarge)
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showEditDialog) {
        EditStudentDialog(
            student = selectedStudent,
            onDismiss = { showEditDialog = false },
            onSave = { student ->
                if (student.id == 0) {
                    studentController.insertStudent(student)
                } else {
                    studentController.updateStudent(student)
                }
                students = studentController.getAllStudents()
                showEditDialog = false
            },
            onDelete = { student ->
                studentController.deleteStudent(student.id)
                students = studentController.getAllStudents()
                showEditDialog = false
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpandableStudentCard(
    student: StudentDataClass,
    onEditClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (!expanded) {
                    expanded = true
                }
            }
            .combinedClickable(
                onClick = { expanded = !expanded },
                onLongClick = {
                    if (expanded) {
                        onEditClick()
                    }
                }
            ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${student.matnumber}",
                    style = MaterialTheme.typography.titleLarge
                )
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = if (expanded) "Einklappen" else "Ausklappen"
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Text("${student.firstname} ${student.lastname}",
                        style = MaterialTheme.typography.titleLarge)
                    Text("${student.email}",
                        style = MaterialTheme.typography.titleLarge)

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Lang drücken zum Bearbeiten",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
fun EditStudentDialog(
    student: StudentDataClass?,
    onDismiss: () -> Unit,
    onSave: (StudentDataClass) -> Unit,
    onDelete: (StudentDataClass) -> Unit
) {
    var firstname by remember { mutableStateOf(student?.firstname ?: "") }
    var lastname by remember { mutableStateOf(student?.lastname ?: "") }
    var matnumber by remember { mutableStateOf(student?.matnumber ?: "") }
    var email by remember { mutableStateOf(student?.email ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = if (student == null) "Neuen Eintrag anlegen" else "Eintrag bearbeiten")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = firstname,
                    onValueChange = { firstname = it },
                    label = { Text("Vorname") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = lastname,
                    onValueChange = { lastname = it },
                    label = { Text("Nachname") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = matnumber,
                    onValueChange = { matnumber = it },
                    label = { Text("Matrikelnummer") },
                    modifier = Modifier.fillMaxWidth()
                )
                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val updatedStudent = StudentDataClass(
                    id = student?.id ?: 0,
                    firstname = firstname,
                    lastname = lastname,
                    matnumber = matnumber,
                    email = email
                )
                onSave(updatedStudent)
            }) {
                Text("Speichern")
            }
        },
        dismissButton = {
            if (student != null) {
                Button(onClick = { onDelete(student) }) {
                    Text("Löschen")
                }
            }
        }
    )
}