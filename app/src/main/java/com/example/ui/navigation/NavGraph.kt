package com.example.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.ui.screens.*
import com.example.ui.viewmodel.ModelViewModel

@Composable
fun NavGraph() {
    val navController = rememberNavController()
    val modelViewModel: ModelViewModel = viewModel()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route

            if (currentRoute == "home" || currentRoute == "library" || currentRoute == "favorites" || currentRoute == "progress") {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentRoute == "home",
                        onClick = { navController.navigate("home") },
                        icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                        label = { Text("Home") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "library",
                        onClick = { navController.navigate("library") },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "Library") },
                        label = { Text("Library") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "favorites",
                        onClick = { navController.navigate("favorites") },
                        icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
                        label = { Text("Favorites") }
                    )
                    NavigationBarItem(
                        selected = currentRoute == "progress",
                        onClick = { navController.navigate("progress") },
                        icon = { Icon(Icons.Default.BarChart, contentDescription = "Progress") },
                        label = { Text("Progress") }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("splash") {
                SplashScreen(
                    onSplashFinished = {
                        navController.navigate("home") {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }
            composable("home") {
                HomeScreen(
                    viewModel = modelViewModel,
                    onCategoryClick = { categoryId ->
                        navController.navigate("library?categoryId=$categoryId")
                    },
                    onModelClick = { modelId ->
                        navController.navigate("detail/$modelId")
                    },
                    onSearchClick = {
                        navController.navigate("library")
                    }
                )
            }
            composable(
                route = "library?categoryId={categoryId}",
                arguments = listOf(navArgument("categoryId") { type = NavType.StringType; nullable = true })
            ) { backStackEntry ->
                val categoryId = backStackEntry.arguments?.getString("categoryId")
                LibraryScreen(
                    viewModel = modelViewModel,
                    initialCategoryId = categoryId,
                    onModelClick = { modelId ->
                        navController.navigate("detail/$modelId")
                    },
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
            composable(
                route = "detail/{modelId}",
                arguments = listOf(navArgument("modelId") { type = NavType.StringType })
            ) { backStackEntry ->
                val modelId = backStackEntry.arguments?.getString("modelId") ?: ""
                ModelDetailScreen(
                    modelId = modelId,
                    viewModel = modelViewModel,
                    onBackClick = { navController.popBackStack() },
                    onRelatedModelClick = { relId ->
                        navController.navigate("detail/$relId")
                    }
                )
            }
            composable("favorites") {
                FavoritesScreen(
                    viewModel = modelViewModel,
                    onModelClick = { modelId ->
                        navController.navigate("detail/$modelId")
                    }
                )
            }
            composable("progress") {
                ProgressScreen(
                    viewModel = modelViewModel,
                    onModelClick = { modelId ->
                        navController.navigate("detail/$modelId")
                    },
                    onExploreLibraryClick = {
                        navController.navigate("library")
                    }
                )
            }
            composable("privacy") {
                PrivacyScreen(onBackClick = { navController.popBackStack() })
            }
            composable("terms") {
                TermsScreen(onBackClick = { navController.popBackStack() })
            }
        }
    }
}
