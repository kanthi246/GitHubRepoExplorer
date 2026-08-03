@file:OptIn(ExperimentalCoroutinesApi::class)

package com.kanthi.githubrepoexplorer.presentation.favorites

import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.repository.GithubRepository
import com.kanthi.githubrepoexplorer.domain.usecase.GetFavoriteRepositoriesUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.ToggleFavoriteUseCase
import com.kanthi.githubrepoexplorer.testutil.MainDispatcherRule
import com.kanthi.githubrepoexplorer.testutil.repository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class FavoritesViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val githubRepository: GithubRepository = mockk()

    private fun viewModel() = FavoritesViewModel(
        getFavoriteRepositoriesUseCase = GetFavoriteRepositoriesUseCase(githubRepository),
        toggleFavoriteUseCase = ToggleFavoriteUseCase(githubRepository),
    )

    // `favorites` is a stateIn(WhileSubscribed) flow: it can emit its seed value before the
    // upstream's real value arrives, so these tests collect into a list and assert on the
    // latest snapshot rather than an exact item-by-item sequence.

    @Test
    fun `exposes an empty list when nothing is favorited`() = runTest {
        every { githubRepository.getFavorites() } returns MutableStateFlow(emptyList())
        val sut = viewModel()
        val emissions = mutableListOf<List<Repository>>()
        val job = launch { sut.favorites.collect { emissions.add(it) } }

        advanceUntilIdle()

        assertEquals(emptyList<Repository>(), emissions.last())
        job.cancel()
    }

    @Test
    fun `reflects favorites as they're added, since it observes a live Room flow`() = runTest {
        val favoritesFlow = MutableStateFlow(listOf(repository(id = 1L, isFavorite = true)))
        every { githubRepository.getFavorites() } returns favoritesFlow
        val sut = viewModel()
        val emissions = mutableListOf<List<Repository>>()
        val job = launch { sut.favorites.collect { emissions.add(it) } }

        advanceUntilIdle()
        assertEquals(listOf(1L), emissions.last().map { it.id })

        favoritesFlow.value = listOf(repository(id = 1L, isFavorite = true), repository(id = 2L, isFavorite = true))
        advanceUntilIdle()

        assertEquals(listOf(1L, 2L), emissions.last().map { it.id })
        job.cancel()
    }

    @Test
    fun `onToggleFavorite un-favorites the repo it's given`() = runTest {
        every { githubRepository.getFavorites() } returns MutableStateFlow(emptyList())
        val repo = repository(id = 1L, isFavorite = true)
        coEvery { githubRepository.toggleFavorite(repo) } returns Unit

        val sut = viewModel()
        sut.onToggleFavorite(repo)
        advanceUntilIdle()

        coVerify { githubRepository.toggleFavorite(repo) }
    }
}
