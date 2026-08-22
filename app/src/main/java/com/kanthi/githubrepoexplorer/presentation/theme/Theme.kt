package com.kanthi.githubrepoexplorer.presentation.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Assembles the app's Material3 theme: picks a light or dark color scheme (or, on Android 12+
 * with `dynamicColor`, one generated from the user's wallpaper) and wraps `content` in a
 * MaterialTheme so every Composable inside can read `MaterialTheme.colorScheme`/`.typography`.
 * Applied once, at the very top of the UI tree, in MainActivity.
 */
private val LightColors = lightColorScheme(
    primary = GithubBlue,
    secondary = GithubGreen,
    background = LightBackground,
    surface = LightSurface,
    onSurfaceVariant = LightOnSurfaceVariant,
    outline = LightOutline,
)

private val DarkColors = darkColorScheme(
    primary = GithubBlueDark,
    secondary = GithubGreenDark,
    background = DarkBackground,
    surface = DarkSurface,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
)

@Composable
fun GitHubRepoExplorerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
