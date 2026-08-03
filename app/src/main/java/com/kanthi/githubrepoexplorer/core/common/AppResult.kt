package com.kanthi.githubrepoexplorer.core.common

/** Generic wrapper for one-shot operations that can fail, used across the data/domain boundary. */
sealed class AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>()
    data class Error(val message: String, val cause: Throwable? = null) : AppResult<Nothing>()
}
