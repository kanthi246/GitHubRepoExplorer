package com.kanthi.githubrepoexplorer.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kanthi.githubrepoexplorer.presentation.details.DetailsScreen
import com.kanthi.githubrepoexplorer.presentation.favorites.FavoritesScreen
import com.kanthi.githubrepoexplorer.presentation.search.SearchScreen

/**
 * Defines every screen the app has and how to navigate between them, using Jetpack Compose
 * Navigation. Each `composable(route) { ... }` block maps a route string (see Screen.kt) to the
 * screen Composable that should be shown for it. This is the single map of the entire app's
 * screen flow: Search -> Details, Search -> Favorites -> Details.
 *
 * Benefit: navigation logic (which screen leads to which, what data passes between them) is
 * centralized here instead of scattered across each screen, and the back stack (the "back
 * button" history) is handled automatically by NavHost.
 */
@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Search.route) {
        composable(Screen.Search.route) {
            SearchScreen(
                onRepositoryClick = { repository ->
                    navController.navigate(Screen.Details.createRoute(repository.ownerLogin, repository.name))
                },
                onFavoritesClick = { navController.navigate(Screen.Favorites.route) },
            )
        }
        composable(Screen.Favorites.route) {
            FavoritesScreen(
                onBackClick = { navController.popBackStack() },
                onRepositoryClick = { repository ->
                    navController.navigate(Screen.Details.createRoute(repository.ownerLogin, repository.name))
                },
            )
        }
        composable(
            route = Screen.Details.route,
            arguments = listOf(
                navArgument("owner") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType },
            ),
        ) {
            DetailsScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
