package com.example.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.data.local.AssessmentRepository
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ToolDetailScreen
import com.example.ui.screens.ToolsHubScreen

object Routes {
    const val HOME = "home"
    const val TOOLS_HUB = "tools"
    const val TOOL_DETAIL = "tool/{slug}"
    const val ADMIN = "admin"

    fun toolDetail(slug: String) = "tool/$slug"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: AssessmentRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                onNavigateToTool = { slug -> navController.navigate(Routes.toolDetail(slug)) },
                onNavigateToHub = { navController.navigate(Routes.TOOLS_HUB) },
                onNavigateToAdmin = { navController.navigate(Routes.ADMIN) }
            )
        }

        composable(Routes.TOOLS_HUB) {
            ToolsHubScreen(
                onNavigateToTool = { slug -> navController.navigate(Routes.toolDetail(slug)) },
                onNavigateToAdmin = { navController.navigate(Routes.ADMIN) }
            )
        }

        composable(
            route = Routes.TOOL_DETAIL,
            arguments = listOf(navArgument("slug") { type = NavType.StringType })
        ) { backStackEntry ->
            val slug = backStackEntry.arguments?.getString("slug") ?: "bmi-assessment"
            ToolDetailScreen(
                slug = slug,
                repository = repository,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToRoute = { route ->
                    if (route == "tools") {
                        navController.navigate(Routes.TOOLS_HUB)
                    } else {
                        navController.navigate(Routes.toolDetail(route))
                    }
                }
            )
        }

        composable(Routes.ADMIN) {
            AdminScreen(
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
