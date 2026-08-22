package com.kanthi.githubrepoexplorer.domain.usecase

import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.repository.GithubRepository
import javax.inject.Inject

/** Use case: flip a repository's favorite status on/off. Shared by SearchViewModel, DetailsViewModel, and FavoritesViewModel so the "toggle favorite" behavior stays identical everywhere it appears. See GetFavoriteRepositoriesUseCase for the general use-case pattern. */
class ToggleFavoriteUseCase @Inject constructor(
    private val repository: GithubRepository,
) {
    suspend operator fun invoke(repo: Repository) = repository.toggleFavorite(repo)
}
