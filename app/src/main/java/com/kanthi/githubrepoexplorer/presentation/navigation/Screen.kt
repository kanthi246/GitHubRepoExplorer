package com.kanthi.githubrepoexplorer.presentation.navigation

sealed class Screen(val route: String) {
    data object Search : Screen("search")
    data object Favorites : Screen("favorites")
    data object Details : Screen("details/{owner}/{name}") {
        fun createRoute(owner: String, name: String) = "details/$owner/$name"
    }
}
