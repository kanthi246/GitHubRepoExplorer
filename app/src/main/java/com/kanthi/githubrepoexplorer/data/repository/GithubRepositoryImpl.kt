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
