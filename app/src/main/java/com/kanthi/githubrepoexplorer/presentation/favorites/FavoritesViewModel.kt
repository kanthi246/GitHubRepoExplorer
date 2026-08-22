package com.kanthi.githubrepoexplorer.presentation.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.usecase.GetFavoriteRepositoriesUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ViewModel for FavoritesScreen. `stateIn` turns the repository's cold Flow into a StateFlow the UI can collect, with `SharingStarted.WhileSubscribed(5_000)` keeping it active for 5s after the screen goes off-screen (e.g. during rotation) so it doesn't needlessly restart. See DetailsViewModel for more on the ViewModel pattern. */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    getFavoriteRepositoriesUseCase: GetFavoriteRepositoriesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
) : ViewModel() {

    val favorites: StateFlow<List<Repository>> = getFavoriteRepositoriesUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun onToggleFavorite(repository: Repository) {
        viewModelScope.launch { toggleFavoriteUseCase(repository) }
    }
}
