package com.kanthi.githubrepoexplorer.core.common

/**
 * core/common holds tiny generic types shared across every layer (data, domain, presentation) —
 * they don't belong to any one feature, so they live in a neutral "core" package instead.
 *
 * AppResult wraps the outcome of a single operation that can fail, such as a network call
 * (see GithubRepositoryImpl.getRepositoryDetails). It is either Success(data) or Error(message).
 *
 * Benefit: forces every caller to explicitly handle both the success and failure case (Kotlin's
 * `when` won't compile unless both branches of a sealed class are covered), instead of relying on
 * exceptions or nullable values that are easy to forget to check.
 */
sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : AppResult<Nothing>()
}
