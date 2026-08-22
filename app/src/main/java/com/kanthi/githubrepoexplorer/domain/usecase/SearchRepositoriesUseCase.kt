package com.kanthi.githubrepoexplorer.domain.usecase

import androidx.paging.PagingData
import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.model.SortOption
import com.kanthi.githubrepoexplorer.domain.repository.GithubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Use case: run a paginated GitHub repository search with a sort order and optional language filter. Returns a Flow of PagingData (see data/paging/RepositorySearchPagingSource.kt). Used by SearchViewModel. See GetFavoriteRepositoriesUseCase for the general use-case pattern. */
class SearchRepositoriesUseCase @Inject constructor(
    private val repository: GithubRepository,
) {
    operator fun invoke(
        query: String,
        sort: SortOption = SortOption.BEST_MATCH,
        language: String? = null,
    ): Flow<PagingData<Repository>> = repository.searchRepositories(query, sort, language)
}
