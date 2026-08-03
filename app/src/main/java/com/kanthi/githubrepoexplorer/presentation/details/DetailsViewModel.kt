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
