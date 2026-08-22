package com.kanthi.githubrepoexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kanthi.githubrepoexplorer.presentation.navigation.AppNavGraph
import com.kanthi.githubrepoexplorer.presentation.theme.GitHubRepoExplorerTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * The single Activity that hosts the entire app. This is a "single-activity" architecture:
 * instead of one Android Activity per screen (the old pattern), there is only one Activity and
 * every screen (Search, Details, Favorites) is a Composable function swapped in and out by
 * Jetpack Compose Navigation (see presentation/navigation/AppNavGraph.kt).
 *
 * @AndroidEntryPoint lets Hilt inject dependencies into this Activity and its Compose tree.
 *
 * Benefit: navigation between screens becomes fast in-process UI swapping (no Activity restart
 * overhead), state and animations can flow smoothly across screens, and there's a single place
 * that wires up the app's theme and navigation graph.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GitHubRepoExplorerTheme {
                AppNavGraph()
            }
        }
    }
}
