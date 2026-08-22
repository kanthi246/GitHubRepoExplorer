package com.kanthi.githubrepoexplorer.domain.usecase

import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.repository.GithubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * A "use case" (a.k.a. interactor) wraps one single, specific business action — here, "observe
 * the list of favorited repositories" — in its own small class. The `operator fun invoke(...)`
 * means callers can use it like a function: `getFavoriteRepositoriesUseCase()`.
 *
 * Every use case in this package follows the same shape: inject the GithubRepository interface,
 * expose exactly one action. ViewModels (presentation layer) call these instead of talking to the
 * repository directly.
 *
 * Benefit: business logic is named, testable in isolation, and reusable — e.g. this exact use
 * case is used by both SearchViewModel (to overlay favorite state on search results) and
 * FavoritesViewModel (to show the favorites list), with no duplicated logic.
 */
class GetFavoriteRepositoriesUseCase @Inject constructor(
    private val repository: GithubRepository,
) {
    operator fun invoke(): Flow<List<Repository>> = repository.getFavorites()
}
