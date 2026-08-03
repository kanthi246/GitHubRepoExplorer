package com.kanthi.githubrepoexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kanthi.githubrepoexplorer.presentation.navigation.AppNavGraph
import com.kanthi.githubrepoexplorer.presentation.theme.GitHubRepoExplorerTheme
import dagger.hilt.android.AndroidEntryPoint

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
