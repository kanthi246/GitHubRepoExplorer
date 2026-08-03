package com.kanthi.githubrepoexplorer.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kanthi.githubrepoexplorer.data.mapper.toDomain
import com.kanthi.githubrepoexplorer.data.remote.api.GithubApiService
import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.model.SortOption
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE = 1

class RepositorySearchPagingSource(
    private val api: GithubApiService,
    private val query: String,
    private val sort: SortOption,
    private val language: String?,
) : PagingSource<Int, Repository>() {

    override fun getRefreshKey(state: PagingState<Int, Repository>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val page = state.closestPageToPosition(anchorPosition)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repository> {
        val page = params.key ?: STARTING_PAGE
        return try {
            val effectiveQuery = if (language.isNullOrBlank()) query else "$query language:$language"
            val response = api.searchRepositories(
                query = effectiveQuery,
                sort = sort.apiValue,
                page = page,
                perPage = params.loadSize.coerceAtMost(GithubApiService.PAGE_SIZE),
            )
            val repositories = response.items.map { it.toDomain() }
            LoadResult.Page(
                data = repositories,
                prevKey = if (page == STARTING_PAGE) null else page - 1,
                nextKey = if (repositories.isEmpty()) null else page + 1,
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}
