package com.kanthi.githubrepoexplorer.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.kanthi.githubrepoexplorer.core.common.AppResult
import com.kanthi.githubrepoexplorer.data.local.dao.RepositoryDao
import com.kanthi.githubrepoexplorer.data.local.dao.SearchHistoryDao
import com.kanthi.githubrepoexplorer.data.local.entity.SearchHistoryEntity
import com.kanthi.githubrepoexplorer.data.mapper.toDomain
import com.kanthi.githubrepoexplorer.data.mapper.toEntity
import com.kanthi.githubrepoexplorer.data.paging.RepositorySearchPagingSource
import com.kanthi.githubrepoexplorer.data.remote.api.GithubApiService
import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.model.SortOption
import com.kanthi.githubrepoexplorer.domain.repository.GithubRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

/**
 * The concrete implementation of the domain-layer GithubRepository interface — this is the
 * "single source of truth" that decides where data actually comes from: the network (via
 * GithubApiService) or the local cache (via Room DAOs), and how they combine (e.g.
 * getRepositoryDetails tries the network first and falls back to the cached copy if offline).
 *
 * The domain and presentation layers never talk to Retrofit or Room directly — they only know
 * about the GithubRepository interface (see domain/repository/GithubRepository.kt). Hilt wires
 * this class in as the real implementation via core/di/RepositoryModule.kt.
 *
 * Benefit: all "where does this data come from and what happens if the network fails" logic is
 * centralized here, instead of being duplicated across every screen that needs repository data.
 */
class GithubRepositoryImpl @Inject constructor(
    private val api: GithubApiService,
    private val repositoryDao: RepositoryDao,
    private val searchHistoryDao: SearchHistoryDao,
) : GithubRepository {

    override fun searchRepositories(
        query: String,
        sort: SortOption,
        language: String?,
    ): Flow<PagingData<Repository>> = Pager(
        config = PagingConfig(pageSize = GithubApiService.PAGE_SIZE, enablePlaceholders = false),
        pagingSourceFactory = { RepositorySearchPagingSource(api, query, sort, language) },
    ).flow

    override suspend fun getRepositoryDetails(owner: String, name: String): AppResult<Repository> {
        return try {
            val dto = api.getRepository(owner, name)
            val cached = repositoryDao.getById(dto.id)
            val domain = dto.toDomain(isFavorite = cached?.isFavorite ?: false)
            repositoryDao.upsert(domain.toEntity())
            AppResult.Success(domain)
        } catch (e: IOException) {
            loadCachedDetails(owner, name, e)
        } catch (e: HttpException) {
            loadCachedDetails(owner, name, e)
        }
    }

    private suspend fun loadCachedDetails(owner: String, name: String, cause: Throwable): AppResult<Repository> {
        val cached = repositoryDao.getByFullName("$owner/$name")
        return if (cached != null) {
            AppResult.Success(cached.toDomain())
        } else {
            AppResult.Error(cause.message ?: "Unable to load repository details", cause)
        }
    }

    override fun getFavorites(): Flow<List<Repository>> =
        repositoryDao.getFavorites().map { list -> list.map { it.toDomain() } }

    override fun isFavorite(repositoryId: Long): Flow<Boolean> =
        repositoryDao.observeFavorite(repositoryId).map { it ?: false }

    override suspend fun toggleFavorite(repository: Repository) {
        val existing = repositoryDao.getById(repository.id)
        val newFavoriteState = !(existing?.isFavorite ?: false)
        val entity = repository.toEntity(
            isFavorite = newFavoriteState,
            cachedAt = existing?.cachedAt ?: System.currentTimeMillis(),
        )
        repositoryDao.upsert(entity)
    }

    override fun getSearchHistory(): Flow<List<String>> = searchHistoryDao.getRecent()

    override suspend fun addSearchQuery(query: String) {
        if (query.isBlank()) return
        searchHistoryDao.upsert(SearchHistoryEntity(query = query.trim(), searchedAt = System.currentTimeMillis()))
    }

    override suspend fun clearSearchHistory() {
        searchHistoryDao.clear()
    }
}
