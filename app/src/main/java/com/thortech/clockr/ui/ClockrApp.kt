package com.thortech.clockr.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.thortech.clockr.data.AppDatabase
import com.thortech.clockr.data.AuthRepository
import com.thortech.clockr.data.FirestoreRepository
import com.thortech.clockr.data.SettingsRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClockrApp() {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val settingsRepository = SettingsRepository(context)
    val authRepository = AuthRepository()
    val firestoreRepository = FirestoreRepository()
    
    val timeViewModel: TimeTrackerViewModel = viewModel(
        factory = TimeTrackerViewModelFactory(
            database.timeEntryDao(), 
            settingsRepository, 
            authRepository, 
            firestoreRepository
        )
    )
    val settingsViewModel: SettingsViewModel = viewModel(
        factory = SettingsViewModelFactory(settingsRepository)
    )
    
    val currentUser by timeViewModel.currentUser.collectAsState()
    
    // Automatically sign in anonymously if not signed in
    LaunchedEffect(currentUser) {
        if (currentUser == null) {
            timeViewModel.signIn()
        }
    }

    if (currentUser == null) {
        // Loading screen or Sign-in screen
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        Scaffold(
            topBar = {
                if (currentRoute == "home") {
                    TopAppBar(
                        title = { Text("Clockr") },
                        actions = {
                            IconButton(onClick = { navController.navigate("stats") }) {
                                Icon(Icons.Default.Info, contentDescription = "Stats")
                            }
                            IconButton(onClick = { navController.navigate("settings") }) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings")
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("home") {
                    HomeScreen(viewModel = timeViewModel)
                }
                composable("settings") {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
                composable("stats") {
                    StatsScreen(
                        timeViewModel = timeViewModel,
                        settingsViewModel = settingsViewModel,
                        onBackClick = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}
