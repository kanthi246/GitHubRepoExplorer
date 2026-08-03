package com.kanthi.githubrepoexplorer.domain.usecase

import com.kanthi.githubrepoexplorer.core.common.AppResult
import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.repository.GithubRepository
import javax.inject.Inject

class GetRepositoryDetailsUseCase @Inject constructor(
    private val repository: GithubRepository,
) {
    suspend operator fun invoke(owner: String, name: String): AppResult<Repository> =
        repository.getRepositoryDetails(owner, name)
}
