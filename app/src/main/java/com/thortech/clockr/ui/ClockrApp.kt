package com.thortech.clockr.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.thortech.clockr.data.AppDatabase

@Composable
fun ClockrApp() {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val viewModel: TimeTrackerViewModel = viewModel(
        factory = TimeTrackerViewModelFactory(database.timeEntryDao())
    )
    val navController = rememberNavController()

    Scaffold { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(viewModel = viewModel)
            }
            // Future screens can be added here
        }
    }
}
