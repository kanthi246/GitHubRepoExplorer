package com.kanthi.githubrepoexplorer.data.paging

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.testing.TestPager
import com.kanthi.githubrepoexplorer.data.remote.api.GithubApiService
import com.kanthi.githubrepoexplorer.data.remote.dto.SearchRepositoriesResponseDto
import com.kanthi.githubrepoexplorer.domain.model.SortOption
import com.kanthi.githubrepoexplorer.testutil.repositoryDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class RepositorySearchPagingSourceTest {

    private val api: GithubApiService = mockk()
    private val config = PagingConfig(pageSize = 20, enablePlaceholders = false)

    private fun pager(query: String = "android", sort: SortOption = SortOption.BEST_MATCH, language: String? = null) =
        TestPager(config, RepositorySearchPagingSource(api, query, sort, language))

    @Test
    fun `first page loads with a null prevKey and the next page as nextKey`() = runTest {
        coEvery {
            api.searchRepositories(query = "android", sort = null, page = 1, perPage = 20)
        } returns SearchRepositoriesResponseDto(totalCount = 2, items = listOf(repositoryDto(id = 1), repositoryDto(id = 2)))

        val result = pager().refresh() as PagingSource.LoadResult.Page

        assertEquals(2, result.data.size)
        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)
    }

    @Test
    fun `an empty page has no next key, so paging stops`() = runTest {
        coEvery {
            api.searchRepositories(query = "android", sort = null, page = 1, perPage = 20)
        } returns SearchRepositoriesResponseDto(totalCount = 0, items = emptyList())

        val result = pager().refresh() as PagingSource.LoadResult.Page

        assertTrue(result.data.isEmpty())
        assertNull(result.nextKey)
    }

    @Test
    fun `a network failure surfaces as a LoadResult Error, not an exception`() = runTest {
        val failure = IOException("no connection")
        coEvery {
            api.searchRepositories(query = "android", sort = null, page = 1, perPage = 20)
        } throws failure

        val result = pager().refresh()

        assertTrue(result is PagingSource.LoadResult.Error)
        assertEquals(failure, (result as PagingSource.LoadResult.Error).throwable)
    }

    @Test
    fun `a language filter is appended to the query as a qualifier`() = runTest {
        coEvery {
            api.searchRepositories(query = "android language:Kotlin", sort = null, page = 1, perPage = 20)
        } returns SearchRepositoriesResponseDto(totalCount = 1, items = listOf(repositoryDto()))

        val result = pager(language = "Kotlin").refresh()

        assertTrue(result is PagingSource.LoadResult.Page)
    }

    @Test
    fun `sort option is forwarded to the API as its GitHub query value`() = runTest {
        coEvery {
            api.searchRepositories(query = "android", sort = "stars", page = 1, perPage = 20)
        } returns SearchRepositoriesResponseDto(totalCount = 1, items = listOf(repositoryDto()))

        val result = pager(sort = SortOption.STARS).refresh()

        assertTrue(result is PagingSource.LoadResult.Page)
    }

    @Test
    fun `appending a second page requests page 2 and keeps prevKey pointing at page 1`() = runTest {
        coEvery {
            api.searchRepositories(query = "android", sort = null, page = 1, perPage = 20)
        } returns SearchRepositoriesResponseDto(totalCount = 40, items = List(20) { repositoryDto(id = it.toLong()) })
        coEvery {
            api.searchRepositories(query = "android", sort = null, page = 2, perPage = 20)
        } returns SearchRepositoriesResponseDto(totalCount = 40, items = List(20) { repositoryDto(id = (20 + it).toLong()) })

        val testPager = pager()
        testPager.refresh()
        val secondPage = testPager.append() as PagingSource.LoadResult.Page

        assertEquals(1, secondPage.prevKey)
        assertEquals(20, secondPage.data.size)
    }
}
