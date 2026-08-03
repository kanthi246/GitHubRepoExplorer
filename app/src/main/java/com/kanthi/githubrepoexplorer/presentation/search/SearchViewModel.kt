package com.kanthi.githubrepoexplorer.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.model.SortOption
import com.kanthi.githubrepoexplorer.domain.usecase.AddSearchQueryUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.ClearSearchHistoryUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.GetFavoriteRepositoriesUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.GetSearchHistoryUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.SearchRepositoriesUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.ToggleFavoriteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SEARCH_DEBOUNCE_MS = 500L

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepositoriesUseCase: SearchRepositoriesUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase,
    getFavoriteRepositoriesUseCase: GetFavoriteRepositoriesUseCase,
    getSearchHistoryUseCase: GetSearchHistoryUseCase,
    private val addSearchQueryUseCase: AddSearchQueryUseCase,
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase,
) : ViewModel() {

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _sortOption = MutableStateFlow(SortOption.BEST_MATCH)
    val sortOption: StateFlow<SortOption> = _sortOption.asStateFlow()

    private val _languageFilter = MutableStateFlow<String?>(null)
    val languageFilter: StateFlow<String?> = _languageFilter.asStateFlow()

    val searchHistory: StateFlow<List<String>> = getSearchHistoryUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val favoriteIds: StateFlow<Set<Long>> = getFavoriteRepositoriesUseCase()
        .map { favorites -> favorites.map { it.id }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    /**
     * The debounced, settled (query, sort, language) combination that actually drives a search.
     * Exposed so the UI can key its paging collector on it: Paging3 does not clear on-screen
     * items when a *new* PagingSource's first page fails before returning any data, so without
     * a key change the previous query's stale results would linger next to the new error state.
     */
    val searchKey: StateFlow<Triple<String, SortOption, String?>> = combine(
        _query.debounce(SEARCH_DEBOUNCE_MS).distinctUntilChanged(),
        _sortOption,
        _languageFilter,
    ) { query, sort, language -> Triple(query, sort, language) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Triple("", SortOption.BEST_MATCH, null))

    private val basePagingFlow: Flow<PagingData<Repository>> = searchKey
        .flatMapLatest { (query, sort, language) ->
            if (query.isBlank()) emptyFlow() else searchRepositoriesUseCase(query, sort, language)
        }
        .cachedIn(viewModelScope)

    /** Overlays the live favorite state from Room onto whatever page of network results is showing. */
    val repositories: Flow<PagingData<Repository>> = combine(basePagingFlow, favoriteIds) { pagingData, favIds ->
        pagingData.map { it.copy(isFavorite = it.id in favIds) }
    }

    fun onQueryChanged(newQuery: String) {
        _query.value = newQuery
    }

    fun onSortSelected(sort: SortOption) {
        _sortOption.value = sort
    }

    fun onLanguageSelected(language: String?) {
        _languageFilter.value = language
    }

    fun onSearchSubmitted() {
        val current = _query.value.trim()
        if (current.isNotBlank()) {
            viewModelScope.launch { addSearchQueryUseCase(current) }
        }
    }

    fun onHistoryItemSelected(historyQuery: String) {
        _query.value = historyQuery
        viewModelScope.launch { addSearchQueryUseCase(historyQuery) }
    }

    fun onClearHistory() {
        viewModelScope.launch { clearSearchHistoryUseCase() }
    }

    fun onToggleFavorite(repository: Repository) {
        viewModelScope.launch { toggleFavoriteUseCase(repository) }
    }
}
