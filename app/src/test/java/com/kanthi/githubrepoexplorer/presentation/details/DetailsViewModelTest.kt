@file:OptIn(ExperimentalCoroutinesApi::class)

package com.kanthi.githubrepoexplorer.presentation.details

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.kanthi.githubrepoexplorer.core.common.AppResult
import com.kanthi.githubrepoexplorer.core.common.UiState
import com.kanthi.githubrepoexplorer.domain.repository.GithubRepository
import com.kanthi.githubrepoexplorer.domain.usecase.GetRepositoryDetailsUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.ObserveFavoriteStatusUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.ToggleFavoriteUseCase
import com.kanthi.githubrepoexplorer.testutil.MainDispatcherRule
import com.kanthi.githubrepoexplorer.testutil.repository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val githubRepository: GithubRepository = mockk()

    // `isFavorite` is a stateIn(WhileSubscribed) flow: it only starts observing the repository
    // once something actually collects it, so every test keeps a background collector alive —
    // otherwise `isFavorite.value` would just sit at its seed default forever.
    private fun TestScope.viewModel(owner: String = "octocat", name: String = "Hello-World"): DetailsViewModel {
        val vm = DetailsViewModel(
            savedStateHandle = SavedStateHandle(mapOf("owner" to owner, "name" to name)),
            getRepositoryDetailsUseCase = GetRepositoryDetailsUseCase(githubRepository),
            toggleFavoriteUseCase = ToggleFavoriteUseCase(githubRepository),
            observeFavoriteStatusUseCase = ObserveFavoriteStatusUseCase(githubRepository),
        )
        backgroundScope.launch { vm.isFavorite.collect {} }
        return vm
    }

    @Test
    fun `starts in Loading state and moves to Success once the fetch completes`() = runTest {
        val repo = repository(id = 1L, fullName = "octocat/Hello-World")
        coEvery { githubRepository.getRepositoryDetails("octocat", "Hello-World") } returns AppResult.Success(repo)
        every { githubRepository.isFavorite(1L) } returns flowOf(false)

        val sut = viewModel()

        sut.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Success(repo), awaitItem())
        }
    }

    @Test
    fun `surfaces a failed fetch as an Error state carrying the failure message`() = runTest {
        coEvery { githubRepository.getRepositoryDetails("octocat", "Hello-World") } returns
            AppResult.Error("offline")

        val sut = viewModel()

        sut.uiState.test {
            assertEquals(UiState.Loading, awaitItem())
            assertEquals(UiState.Error("offline"), awaitItem())
        }
    }

    @Test
    fun `loadDetails can be re-triggered after an error, e g by a retry button`() = runTest {
        val repo = repository(id = 1L)
        coEvery { githubRepository.getRepositoryDetails("octocat", "Hello-World") } returns
            AppResult.Error("offline") andThen AppResult.Success(repo)
        every { githubRepository.isFavorite(1L) } returns flowOf(false)

        val sut = viewModel()
        advanceUntilIdle()
        assertEquals(UiState.Error("offline"), sut.uiState.value)

        sut.loadDetails()
        advanceUntilIdle()

        assertEquals(UiState.Success(repo), sut.uiState.value)
    }

    @Test
    fun `isFavorite reflects the repository's favorite flow once details load`() = runTest {
        val repo = repository(id = 1L)
        coEvery { githubRepository.getRepositoryDetails("octocat", "Hello-World") } returns AppResult.Success(repo)
        every { githubRepository.isFavorite(1L) } returns flowOf(true)

        val sut = viewModel()
        advanceUntilIdle()

        assertTrue(sut.isFavorite.value)
    }

    @Test
    fun `onToggleFavorite forwards the currently loaded repository`() = runTest {
        val repo = repository(id = 1L)
        coEvery { githubRepository.getRepositoryDetails("octocat", "Hello-World") } returns AppResult.Success(repo)
        every { githubRepository.isFavorite(1L) } returns flowOf(false)
        coEvery { githubRepository.toggleFavorite(repo) } returns Unit

        val sut = viewModel()
        advanceUntilIdle()
        sut.onToggleFavorite()
        advanceUntilIdle()

        coVerify { githubRepository.toggleFavorite(repo) }
    }

    @Test
    fun `onToggleFavorite is a no-op while still loading`() = runTest {
        coEvery { githubRepository.getRepositoryDetails(any(), any()) } coAnswers {
            kotlinx.coroutines.delay(Long.MAX_VALUE / 2)
            AppResult.Error("never reached")
        }

        val sut = viewModel()
        runCurrent() // let the coroutine reach and suspend on the delay, without advancing past it
        sut.onToggleFavorite()
        runCurrent()

        coVerify(exactly = 0) { githubRepository.toggleFavorite(any()) }
    }
}
