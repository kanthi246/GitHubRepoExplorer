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
