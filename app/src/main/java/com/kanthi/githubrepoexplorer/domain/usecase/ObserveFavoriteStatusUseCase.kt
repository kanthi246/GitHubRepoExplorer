package com.kanthi.githubrepoexplorer.domain.usecase

import com.kanthi.githubrepoexplorer.domain.repository.GithubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveFavoriteStatusUseCase @Inject constructor(
    private val repository: GithubRepository,
) {
    operator fun invoke(repositoryId: Long): Flow<Boolean> = repository.isFavorite(repositoryId)
}
