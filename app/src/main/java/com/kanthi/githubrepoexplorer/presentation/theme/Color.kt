package com.kanthi.githubrepoexplorer.presentation.theme

import androidx.compose.ui.graphics.Color

/**
 * presentation/theme defines the app's visual identity: raw color values (this file), how they
 * combine into light/dark color schemes (Theme.kt), and text styles (Type.kt) — deliberately
 * modeled after GitHub's own light/dark palettes.
 *
 * Benefit: every screen pulls colors from `MaterialTheme.colorScheme` instead of hardcoding hex
 * values, so the whole app's look (including dark mode) can be changed by editing these few files.
 */
val GithubBlue = Color(0xFF0969DA)
val GithubBlueDark = Color(0xFF58A6FF)
val GithubGreen = Color(0xFF1A7F37)
val GithubGreenDark = Color(0xFF3FB950)

val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFF6F8FA)
val LightOnSurfaceVariant = Color(0xFF57606A)
val LightOutline = Color(0xFFD0D7DE)

val DarkBackground = Color(0xFF0D1117)
val DarkSurface = Color(0xFF161B22)
val DarkOnSurfaceVariant = Color(0xFF8B949E)
val DarkOutline = Color(0xFF30363D)
