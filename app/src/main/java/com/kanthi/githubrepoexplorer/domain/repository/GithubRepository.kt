package com.kanthi.githubrepoexplorer.domain.repository

import androidx.paging.PagingData
import com.kanthi.githubrepoexplorer.core.common.AppResult
import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.model.SortOption
import kotlinx.coroutines.flow.Flow

interface GithubRepository {

    fun searchRepositories(
        query: String,
        sort: SortOption,
        language: String?,
    ): Flow<PagingData<Repository>>

    suspend fun getRepositoryDetails(owner: String, name: String): AppResult<Repository>

    fun getFavorites(): Flow<List<Repository>>

    fun isFavorite(repositoryId: Long): Flow<Boolean>

    suspend fun toggleFavorite(repository: Repository)

    fun getSearchHistory(): Flow<List<String>>

    suspend fun addSearchQuery(query: String)

    suspend fun clearSearchHistory()
}
