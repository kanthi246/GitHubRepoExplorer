package com.kanthi.githubrepoexplorer.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanthi.githubrepoexplorer.core.common.AppResult
import com.kanthi.githubrepoexplorer.core.common.UiState
import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.usecase.GetRepositoryDetailsUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.ObserveFavoriteStatusUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * The ViewModel for DetailsScreen — this is the "VM" in MVVM (Model-View-ViewModel). It holds
 * the screen's state (`uiState`, `isFavorite`) as StateFlow and exposes functions the UI calls in
 * response to user actions (`loadDetails`, `onToggleFavorite`). It talks only to use cases
 * (domain layer), never directly to Retrofit or Room.
 *
 * `SavedStateHandle` reads the `owner`/`name` navigation arguments (see AppNavGraph.kt) so this
 * ViewModel knows which repository to load. A ViewModel survives configuration changes (like
 * screen rotation), so in-flight state isn't lost when the Activity briefly gets recreated.
 *
 * Benefit: DetailsScreen (the Composable) stays a "dumb" rendering function with no business
 * logic, while all the loading/error/favorite-toggling logic lives here where it can be unit
 * tested without needing to render any actual UI.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getRepositoryDetailsUseCase: GetRepositoryDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    private val observeFavoriteStatusUseCase: ObserveFavoriteStatusUseCase,
) : ViewModel() {

    private val owner: String = checkNotNull(savedStateHandle["owner"])
    private val repoName: String = checkNotNull(savedStateHandle["name"])

    private val _uiState = MutableStateFlow<UiState<Repository>>(UiState.Loading)
    val uiState: StateFlow<UiState<Repository>> = _uiState.asStateFlow()

    val isFavorite: StateFlow<Boolean> = _uiState
        .flatMapLatest { state ->
            val repositoryId = (state as? UiState.Success)?.data?.id
            if (repositoryId != null) observeFavoriteStatusUseCase(repositoryId) else flowOf(false)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    init {
        loadDetails()
    }

    fun loadDetails() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            when (val result = getRepositoryDetailsUseCase(owner, repoName)) {
                is AppResult.Success -> _uiState.value = UiState.Success(result.data)
                is AppResult.Error -> _uiState.value = UiState.Error(result.message)
            }
        }
    }

    fun onToggleFavorite() {
        val repository = (_uiState.value as? UiState.Success)?.data ?: return
        viewModelScope.launch { toggleFavoriteUseCase(repository) }
    }
}
