package com.kanthi.githubrepoexplorer.domain.usecase

import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.repository.GithubRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: GithubRepository,
) {
    suspend operator fun invoke(repo: Repository) = repository.toggleFavorite(repo)
}
