package com.example.studentcard.ui.screen

import ToDoClass
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.studentcard.database.controller.ToDoController
import java.text.SimpleDateFormat
import java.util.Calendar.*
import java.util.Date
import java.util.Locale

/**
 * Bietet eine Übersicht von ToDos und unterteilt in aktive und erledigte Aufgaben.
 * Ermöglicht die Anzeige, Bearbeitung und Löschung von Aufgaben sowie das Hinzufügen neuer Aufgaben.
 * Die ToDos können nach Priorität kategorisiert und aktualisiert werden.
 */
@Composable
fun Dashboard() {
    val context = LocalContext.current
    val toDoController = ToDoController(context)

    var showActiveList by remember { mutableStateOf(true) }
    var showCompletedList by remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    var selectedToDo by remember { mutableStateOf<ToDoClass?>(null) }

    val activeToDoClasses = remember { mutableStateListOf<ToDoClass>() }
    val completedToDoClasses = remember { mutableStateListOf<ToDoClass>() }

    LaunchedEffect(Unit) {
        activeToDoClasses.addAll(toDoController.getActiveToDos())
        completedToDoClasses.addAll(toDoController.getCompletedToDos())
    }

    var editingToDo by remember { mutableStateOf<ToDoClass?>(null) }

    val priorityColors = listOf(Color(0xFFDFF0D8), Color(0xFFFFF4C2), Color(0xFFF8D7DA)) // Grün, Gelb, Rot

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 72.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showActiveList = !showActiveList },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Aktive ToDos", style = MaterialTheme.typography.titleLarge)
                Icon(
                    imageVector = if (showActiveList) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Toggle Active List"
                )
            }

            AnimatedVisibility(visible = showActiveList) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(activeToDoClasses) { toDo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedToDo = toDo
                                    showInfoDialog = true
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = priorityColors[toDo.priority - 1])
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = toDo.isCompleted,
                                    onCheckedChange = { isChecked ->
                                        val updatedToDo = toDo.copy(isCompleted = isChecked)
                                        if (toDoController.updateToDo(updatedToDo)) {
                                            if (isChecked) {
                                                activeToDoClasses.remove(toDo)
                                                completedToDoClasses.add(updatedToDo)
                                            } else {
                                                completedToDoClasses.remove(toDo)
                                                activeToDoClasses.add(updatedToDo)
                                            }
                                        }
                                    }
                                )
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = toDo.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Priorität: ${toDo.priority}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        editingToDo = toDo
                                        showDialog = true
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
                                        contentDescription = "Edit ToDo"
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        if (toDoController.deleteToDo(toDo.id)) {
                                            activeToDoClasses.remove(toDo)
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete ToDo"
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showCompletedList = !showCompletedList },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Erledigte ToDos", style = MaterialTheme.typography.titleLarge)
                Icon(
                    imageVector = if (showCompletedList) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Toggle Completed List"
                )
            }

            AnimatedVisibility(visible = showCompletedList) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(completedToDoClasses) { toDo ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedToDo = toDo
                                    showInfoDialog = true
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = priorityColors[toDo.priority - 1])
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = toDo.isCompleted,
                                    onCheckedChange = { isChecked ->
                                        val updatedToDo = toDo.copy(isCompleted = isChecked)
                                        if (toDoController.updateToDo(updatedToDo)) {
                                            if (isChecked) {
                                                activeToDoClasses.remove(toDo)
                                                completedToDoClasses.add(updatedToDo)
                                            } else {
                                                completedToDoClasses.remove(toDo)
                                                activeToDoClasses.add(updatedToDo)
                                            }
                                        }
                                    }
                                )
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = toDo.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        maxLines = 1
                                    )
                                    Text(
                                        text = "Priorität: ${toDo.priority}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                IconButton(
                                    onClick = {
                                        if (toDoController.deleteToDo(toDo.id)) {
                                            completedToDoClasses.remove(toDo) // Entferne auch aus der erledigten Liste
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = "Delete ToDo"
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        FloatingActionButton(
            onClick = {
                editingToDo = null
                showDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add ToDo")
        }

        if (showDialog) {
            OpenAddToDoDialog(
                toDo = editingToDo,
                onDismiss = { showDialog = false },
                onToDoUpdated = { updatedToDo ->
                    if (editingToDo == null) {
                        activeToDoClasses.add(updatedToDo)
                    } else {
                        activeToDoClasses.remove(editingToDo)
                        activeToDoClasses.add(updatedToDo)
                    }
                },
                context = context
            )
        }

        if (showInfoDialog && selectedToDo != null) {
            InfoDialog(
                toDo = selectedToDo!!,
                onDismiss = { showInfoDialog = false }
            )
        }
    }
}


/**
 * Dialog zum Hinzufügen und Bearbeiten von ToDos. Zeigt Eingabefelder für Name, Priorität, Beschreibung
 * und Enddatum eines ToDos an und ermöglicht das Speichern der Änderungen.
 *
 * @param toDo Optionales ToDo-Objekt, welches bearbeitet oder `null` wird, wenn ein neues ToDo erstellt wird.
 * @param onDismiss Funktion, die aufgerufen wird, wenn der Dialog geschlossen wird.
 * @param onToDoUpdated Funktion, die aufgerufen wird, wenn das ToDo erfolgreich hinzugefügt oder bearbeitet wurde.
 * @param context Kontext der aktuellen Anwendung, um Ressourcen oder Datenbankzugriffe zu ermöglichen.
 */
@Composable
fun OpenAddToDoDialog(
    toDo: ToDoClass? = null,
    onDismiss: () -> Unit,
    onToDoUpdated: (ToDoClass) -> Unit,
    context: Context
) {
    var name by remember { mutableStateOf(toDo?.name ?: "") }
    var priority by remember { mutableIntStateOf(toDo?.priority ?: 1) }
    var description by remember { mutableStateOf(toDo?.description ?: "") }
    var endDate by remember { mutableLongStateOf(toDo?.endDate ?: System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }

    val priorities = listOf("1 - Niedrig", "2 - Mittel", "3 - Hoch")

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(if (toDo != null) "ToDo bearbeiten" else "Neues ToDo hinzufügen") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Name des ToDos
                Text("Name des ToDos:")
                TextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (showError && it.isNotBlank()) showError = false // Fehler zurücksetzen
                    },
                    isError = showError,
                    modifier = Modifier.fillMaxWidth()
                )
                if (showError) {
                    Text(
                        text = "Name darf nicht leer sein!",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Priorität auswählen:")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Priorität: ${priorities[priority - 1]}")
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    priorities.forEachIndexed { index, priorityText ->
                        DropdownMenuItem(
                            text = { Text(priorityText) },
                            onClick = {
                                priority = index + 1
                                expanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("Beschreibung:")
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Enddatum:")
                Button(onClick = { showDatePicker = true }) {
                    Text("Datum auswählen")
                }
                Text(text = "Ausgewähltes Datum: ${formatDate(endDate)}")

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        onDateSelected = { year, month, dayOfMonth ->
                            val calendar = getInstance()
                            calendar.set(year, month, dayOfMonth)
                            endDate = calendar.timeInMillis
                            showDatePicker = false
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (name.isBlank()) {
                    showError = true
                } else {
                    val updatedToDo = toDo?.copy(
                        name = name,
                        priority = priority,
                        description = description,
                        endDate = endDate
                    ) ?: ToDoClass(
                        id = 0,
                        name = name,
                        priority = priority,
                        description = description,
                        isCompleted = false,
                        endDate = endDate
                    )
                    onToDoUpdated(updatedToDo)
                    onDismiss()
                }
            }) {
                Text("Speichern")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Abbrechen")
            }
        }
    )
}

/**
 * Formatierung der Zeitstempel in das Format "dd.MM.yyyy".
 *
 * @param timestamp Der Zeitstempel, der formatiert werden soll.
 * @return Das formatierte Datum als String im Format "dd.MM.yyyy".
 */
fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

/**
 * Dialog, der einen DatePicker anzeigt, um ein Datum auszuwählen.
 *
 * @param onDismissRequest Funktion, die aufgerufen wird, wenn der Dialog geschlossen wird.
 * @param onDateSelected Funktion, die das ausgewählte Datum als Jahr, Monat und Tag übergibt.
 */
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (year: Int, month: Int, dayOfMonth: Int) -> Unit
) {
    val calendar = getInstance()
    val year = calendar.get(YEAR)
    val month = calendar.get(MONTH)
    val dayOfMonth = calendar.get(DAY_OF_MONTH)

    val minDate = calendar.timeInMillis

    AndroidView(
        factory = { context ->
            android.widget.DatePicker(context).apply {
                init(
                    year,
                    month,
                    dayOfMonth
                ) { _, selectedYear, selectedMonth, selectedDayOfMonth ->
                    onDateSelected(selectedYear, selectedMonth, selectedDayOfMonth)
                }

                this.minDate = minDate
            }
        },
        modifier = Modifier.padding(16.dp)
    )
}

/**
 * Dialog, der Details zu einem bestimmten ToDo anzeigt, einschließlich Name, Priorität, Beschreibung,
 * Enddatum und Status.
 *
 * @param toDo Das ToDo-Objekt, dessen Details angezeigt werden sollen.
 * @param onDismiss Funktion, die aufgerufen wird, wenn der Dialog geschlossen wird.
 */
@Composable
fun InfoDialog(toDo: ToDoClass, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("ToDo-Details") },
        text = {
            Column {
                Text("Name: ${toDo.name}")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Priorität: ${toDo.priority}")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Beschreibung: ${toDo.description}")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Enddatum: ${formatDate(toDo.endDate)}")
                Spacer(modifier = Modifier.height(8.dp))
                Text("Status: ${if (toDo.isCompleted) "Erledigt" else "Aktiv"}")
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Schließen")
            }
        }
    )
}