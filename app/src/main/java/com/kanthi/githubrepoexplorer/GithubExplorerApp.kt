package com.kanthi.githubrepoexplorer

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * The Application class — the very first thing Android creates when the app process starts,
 * before any screen exists.
 *
 * @HiltAndroidApp turns this into the root of the Hilt dependency-injection graph: it generates
 * the container that holds app-wide singletons (Retrofit, Room database, repositories — see
 * core/di) and makes them available for injection anywhere in the app.
 *
 * Benefit: without this, every class that needs a dependency (like a database) would have to
 * construct it manually, making classes hard to test and tightly coupled. Hilt lets classes just
 * declare what they need (via @Inject) and the framework wires it up.
 */
@HiltAndroidApp
class GithubExplorerApp : Application()
