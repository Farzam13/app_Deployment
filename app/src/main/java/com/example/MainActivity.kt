package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.local.AppDatabase
import com.example.data.local.AssessmentRepository
import com.example.ui.navigation.NavGraph
import com.example.ui.navigation.Routes
import com.example.ui.theme.Burgundy800
import com.example.ui.theme.Cream25
import com.example.ui.theme.DrKAssessmentTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(applicationContext)
        val repository = AssessmentRepository(db)

        setContent {
            DrKAssessmentTheme {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Cream25,
                        bottomBar = {
                            NavigationBar(
                                containerColor = NavigationBarDefaults.containerColor,
                                tonalElevation = 3.dp,
                                modifier = Modifier.testTag("bottom_nav_bar")
                            ) {
                                NavigationBarItem(
                                    selected = currentRoute == Routes.HOME,
                                    onClick = {
                                        navController.navigate(Routes.HOME) {
                                            popUpTo(Routes.HOME) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Home, contentDescription = "خانه") },
                                    label = { Text("خانه") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Burgundy800,
                                        selectedTextColor = Burgundy800
                                    ),
                                    modifier = Modifier.testTag("nav_home")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == Routes.TOOLS_HUB,
                                    onClick = {
                                        navController.navigate(Routes.TOOLS_HUB) {
                                            popUpTo(Routes.HOME) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.List, contentDescription = "ابزارها") },
                                    label = { Text("۱۰ ابزار") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Burgundy800,
                                        selectedTextColor = Burgundy800
                                    ),
                                    modifier = Modifier.testTag("nav_tools")
                                )

                                NavigationBarItem(
                                    selected = currentRoute == Routes.ADMIN,
                                    onClick = {
                                        navController.navigate(Routes.ADMIN) {
                                            popUpTo(Routes.HOME) { saveState = true }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = "مدیریت") },
                                    label = { Text("مدیریت") },
                                    colors = NavigationBarItemDefaults.colors(
                                        selectedIconColor = Burgundy800,
                                        selectedTextColor = Burgundy800
                                    ),
                                    modifier = Modifier.testTag("nav_admin")
                                )
                            }
                        }
                    ) { innerPadding ->
                        NavGraph(
                            navController = navController,
                            repository = repository,
                            modifier = Modifier.padding(innerPadding)
                        )
                    }
                }
            }
        }
    }
}
