package com.kanthi.githubrepoexplorer.domain.usecase

import com.kanthi.githubrepoexplorer.domain.repository.GithubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Three small use cases for managing recent search queries, grouped in one file since each is a
 * one-liner: get the recent history, record a new query, clear the history. See
 * GetFavoriteRepositoriesUseCase for the general use-case pattern.
 */
class GetSearchHistoryUseCase @Inject constructor(
    private val repository: GithubRepository,
) {
    operator fun invoke(): Flow<List<String>> = repository.getSearchHistory()
}

class AddSearchQueryUseCase @Inject constructor(
    private val repository: GithubRepository,
) {
    suspend operator fun invoke(query: String) = repository.addSearchQuery(query)
}

class ClearSearchHistoryUseCase @Inject constructor(
    private val repository: GithubRepository,
) {
    suspend operator fun invoke() = repository.clearSearchHistory()
}
