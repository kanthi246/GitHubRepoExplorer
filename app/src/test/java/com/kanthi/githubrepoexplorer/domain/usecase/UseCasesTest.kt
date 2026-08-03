package com.kanthi.githubrepoexplorer.domain.usecase

import androidx.paging.PagingData
import com.kanthi.githubrepoexplorer.core.common.AppResult
import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.model.SortOption
import com.kanthi.githubrepoexplorer.domain.repository.GithubRepository
import com.kanthi.githubrepoexplorer.testutil.repository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Use cases are thin, single-purpose wrappers around [GithubRepository] — the point of testing
 * them isn't complex logic (there isn't any), it's guaranteeing each one forwards the *correct*
 * arguments to the *correct* repository method, since a typo there wouldn't be caught by types.
 */
class UseCasesTest {

    private val repository: GithubRepository = mockk()

    @Test
    fun `SearchRepositoriesUseCase forwards query, sort and language to the repository`() = runTest {
        val expected = flowOf<PagingData<Repository>>()
        every { repository.searchRepositories("android", SortOption.STARS, "Kotlin") } returns expected

        val result = SearchRepositoriesUseCase(repository)("android", SortOption.STARS, "Kotlin")

        assertEquals(expected, result)
    }

    @Test
    fun `SearchRepositoriesUseCase defaults to best-match sort and no language filter`() = runTest {
        val expected = flowOf<PagingData<Repository>>()
        every { repository.searchRepositories("android", SortOption.BEST_MATCH, null) } returns expected

        val result = SearchRepositoriesUseCase(repository)("android")

        assertEquals(expected, result)
    }

    @Test
    fun `GetRepositoryDetailsUseCase forwards owner and name`() = runTest {
        val repo = repository(id = 1L)
        coEvery { repository.getRepositoryDetails("octocat", "Hello-World") } returns AppResult.Success(repo)

        val result = GetRepositoryDetailsUseCase(repository)("octocat", "Hello-World")

        assertTrue(result is AppResult.Success)
        assertEquals(repo, (result as AppResult.Success).data)
    }

    @Test
    fun `GetFavoriteRepositoriesUseCase returns the repository's favorites flow`() = runTest {
        val favorites = flowOf(listOf(repository(isFavorite = true)))
        every { repository.getFavorites() } returns favorites

        assertEquals(favorites, GetFavoriteRepositoriesUseCase(repository)())
    }

    @Test
    fun `ToggleFavoriteUseCase delegates to the repository`() = runTest {
        val repo = repository(id = 1L)
        coEvery { repository.toggleFavorite(repo) } returns Unit

        ToggleFavoriteUseCase(repository)(repo)

        coVerify { repository.toggleFavorite(repo) }
    }

    @Test
    fun `ObserveFavoriteStatusUseCase forwards the repository id`() = runTest {
        val favoriteFlow = flowOf(true)
        every { repository.isFavorite(42L) } returns favoriteFlow

        assertEquals(favoriteFlow, ObserveFavoriteStatusUseCase(repository)(42L))
    }

    @Test
    fun `GetSearchHistoryUseCase returns the repository's history flow`() = runTest {
        val history = flowOf(listOf("kotlin", "compose"))
        every { repository.getSearchHistory() } returns history

        assertEquals(history, GetSearchHistoryUseCase(repository)())
    }

    @Test
    fun `AddSearchQueryUseCase forwards the query`() = runTest {
        coEvery { repository.addSearchQuery("kotlin") } returns Unit

        AddSearchQueryUseCase(repository)("kotlin")

        coVerify { repository.addSearchQuery("kotlin") }
    }

    @Test
    fun `ClearSearchHistoryUseCase delegates to the repository`() = runTest {
        coEvery { repository.clearSearchHistory() } returns Unit

        ClearSearchHistoryUseCase(repository)()

        coVerify { repository.clearSearchHistory() }
    }
}
