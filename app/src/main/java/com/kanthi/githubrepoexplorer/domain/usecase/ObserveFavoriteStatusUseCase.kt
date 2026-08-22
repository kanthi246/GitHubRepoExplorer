package com.kanthi.githubrepoexplorer.domain.usecase

import com.kanthi.githubrepoexplorer.domain.repository.GithubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Use case: reactively observe whether one specific repository is currently favorited, so the UI's heart icon updates live. See GetFavoriteRepositoriesUseCase for the general use-case pattern. */
class ObserveFavoriteStatusUseCase @Inject constructor(
    private val repository: GithubRepository,
) {
    operator fun invoke(repositoryId: Long): Flow<Boolean> = repository.isFavorite(repositoryId)
}
