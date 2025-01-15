package com.example.studentcard.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.studentcard.database.controller.StudentController


@Composable
fun Dashboard() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") {
            val context = LocalContext.current
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Button(onClick = { navController.navigate("student_screen") }) {
                    Text("Studierenden anlegen")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    StudentController(context).exportDatabase()
                }) {
                    Text("Datenbank exportieren")
                }
            }
        }
        composable("student_screen") {
            val context = LocalContext.current
            StudentScreen(
                context = context,
                navController = navController
            )
        }
    }
}