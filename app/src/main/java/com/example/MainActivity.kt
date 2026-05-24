package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DownloadScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MainViewModel
import java.net.URLDecoder
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val navController = rememberNavController()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentDestination = currentBackStack?.destination?.route

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    topBar = {
                        if (currentDestination != null && !currentDestination.startsWith("login")) {
                            CenterAlignedTopAppBar(
                                title = { Text("MediaVault", fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold) },
                                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                                    containerColor = MaterialTheme.colorScheme.background
                                )
                            )
                        }
                    },
                    bottomBar = {
                        // Avoid showing bottom bar on full screen views like login
                        if (currentDestination != null && !currentDestination.startsWith("login")) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                tonalElevation = 0.dp
                            ) {
                                NavigationBarItem(
                                    selected = currentDestination == "dashboard",
                                    onClick = {
                                        navController.navigate("dashboard") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                                    label = { Text("Home", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium) }
                                )
                                NavigationBarItem(
                                    selected = currentDestination == "download",
                                    onClick = {
                                        navController.navigate("download") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Add, contentDescription = "Download") },
                                    label = { Text("Downloads", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium) }
                                )
                                NavigationBarItem(
                                    selected = currentDestination == "settings",
                                    onClick = {
                                        navController.navigate("settings") {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                                    label = { Text("Settings", fontWeight = androidx.compose.ui.text.font.FontWeight.Medium) }
                                )
                            }
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable("dashboard") {
                            DashboardScreen(viewModel = viewModel)
                        }
                        composable("download") {
                            DownloadScreen(
                                viewModel = viewModel,
                                onNavigateToLogin = { url ->
                                    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    navController.navigate("login/$encodedUrl")
                                }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(
                                onNavigateToLogin = { url ->
                                    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                                    navController.navigate("login/$encodedUrl")
                                }
                            )
                        }
                        composable("login/{url}") { backStackEntry ->
                            val url = backStackEntry.arguments?.getString("url") ?: ""
                            val decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.toString())
                            LoginScreen(
                                platformUrl = decodedUrl,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
