package com.kanthi.githubrepoexplorer.domain.usecase

import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.repository.GithubRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteRepositoriesUseCase @Inject constructor(
    private val repository: GithubRepository,
) {
    operator fun invoke(): Flow<List<Repository>> = repository.getFavorites()
}
