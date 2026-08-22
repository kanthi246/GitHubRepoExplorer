package com.kanthi.githubrepoexplorer.presentation.navigation

/**
 * The list of every navigable screen and its route string (a URL-like path Compose Navigation
 * uses internally, e.g. "details/{owner}/{name}"). `Details.createRoute(...)` fills in the
 * placeholders with real values when navigating to a specific repository.
 *
 * Benefit: route strings are defined in exactly one place and referenced by name (`Screen.Search.route`)
 * everywhere else, instead of being typo-prone hardcoded strings scattered through the navigation code.
 */
sealed class Screen(val route: String) {
    data object Search : Screen("search")
    data object Favorites : Screen("favorites")
    data object Details : Screen("details/{owner}/{name}") {
        fun createRoute(owner: String, name: String) = "details/$owner/$name"
    }
}
