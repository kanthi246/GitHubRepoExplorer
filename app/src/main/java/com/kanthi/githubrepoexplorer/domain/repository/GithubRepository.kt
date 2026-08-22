package com.kanthi.githubrepoexplorer.domain.repository

import androidx.paging.PagingData
import com.kanthi.githubrepoexplorer.core.common.AppResult
import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.model.SortOption
import kotlinx.coroutines.flow.Flow

/**
 * The domain layer's contract for "everything the app can do with repository data" — search,
 * fetch details, manage favorites, manage search history. It says *what* can be done, not *how*.
 *
 * The domain/usecase classes depend on this interface, never on the concrete implementation
 * (GithubRepositoryImpl, in the data layer). Hilt supplies the real implementation at runtime
 * via core/di/RepositoryModule.kt.
 *
 * Benefit: this is the classic "dependency inversion" at the heart of Clean Architecture — the
 * data layer depends on (implements) this domain interface, not the other way around. That means
 * the domain layer has no idea Retrofit or Room exist, and swapping either out, or substituting a
 * fake for testing, requires no changes to domain or presentation code.
 */
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
