package com.kanthi.githubrepoexplorer.core.common

/**
 * Represents everything a screen can show while it waits for and displays data: a loading
 * spinner, the successfully loaded data, or an error message. ViewModels expose this to their
 * screen (e.g. DetailsViewModel.uiState) instead of exposing raw data.
 *
 * Benefit: the Composable UI can render itself with a single exhaustive `when (uiState)` block
 * instead of juggling separate isLoading/error/data variables that could get out of sync with
 * each other.
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
