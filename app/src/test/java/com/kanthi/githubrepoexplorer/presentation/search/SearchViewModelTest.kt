@file:OptIn(ExperimentalCoroutinesApi::class)

package com.kanthi.githubrepoexplorer.presentation.search

import androidx.paging.PagingData
import com.kanthi.githubrepoexplorer.domain.model.Repository
import com.kanthi.githubrepoexplorer.domain.model.SortOption
import com.kanthi.githubrepoexplorer.domain.usecase.AddSearchQueryUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.ClearSearchHistoryUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.GetFavoriteRepositoriesUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.GetSearchHistoryUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.SearchRepositoriesUseCase
import com.kanthi.githubrepoexplorer.domain.usecase.ToggleFavoriteUseCase
import com.kanthi.githubrepoexplorer.testutil.MainDispatcherRule
import com.kanthi.githubrepoexplorer.testutil.repository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val searchRepositoriesUseCase: SearchRepositoriesUseCase = mockk()
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk()
    private val getFavoriteRepositoriesUseCase: GetFavoriteRepositoriesUseCase = mockk()
    private val getSearchHistoryUseCase: GetSearchHistoryUseCase = mockk()
    private val addSearchQueryUseCase: AddSearchQueryUseCase = mockk()
    private val clearSearchHistoryUseCase: ClearSearchHistoryUseCase = mockk()

    @Before
    fun setUp() {
        every { getFavoriteRepositoriesUseCase() } returns MutableStateFlow(emptyList())
        every { getSearchHistoryUseCase() } returns MutableStateFlow(emptyList())
        every { searchRepositoriesUseCase(any(), any(), any()) } returns emptyFlow<PagingData<Repository>>()
    }

    // `searchKey` is a stateIn(WhileSubscribed) flow: it only starts settling debounced changes
    // once something actually collects it, so every test keeps a background collector alive —
    // in the real app that collector is the Compose screen; here it's this background job.
    private fun TestScope.viewModel(): SearchViewModel {
        val vm = SearchViewModel(
            searchRepositoriesUseCase = searchRepositoriesUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase,
            getFavoriteRepositoriesUseCase = getFavoriteRepositoriesUseCase,
            getSearchHistoryUseCase = getSearchHistoryUseCase,
            addSearchQueryUseCase = addSearchQueryUseCase,
            clearSearchHistoryUseCase = clearSearchHistoryUseCase,
        )
        backgroundScope.launch { vm.searchKey.collect {} }
        return vm
    }

    @Test
    fun `onQueryChanged updates the raw query immediately, with no debounce`() = runTest {
        val sut = viewModel()

        sut.onQueryChanged("kotlin")

        assertEquals("kotlin", sut.query.value)
    }

    @Test
    fun `searchKey only settles after the debounce window passes without further typing`() = runTest {
        val sut = viewModel()
        runCurrent() // let the background collector subscribe, anchoring debounce timing at t=0

        sut.onQueryChanged("k")
        advanceTimeBy(200)
        sut.onQueryChanged("ko")
        advanceTimeBy(200)
        sut.onQueryChanged("kotlin")
        // Still within 500ms of the last keystroke — nothing should have settled yet.
        advanceTimeBy(400)
        assertEquals("", sut.searchKey.value.first)

        advanceTimeBy(200)
        assertEquals("kotlin", sut.searchKey.value.first)
    }

    @Test
    fun `rapid retyping only produces one settled searchKey, not one per keystroke`() = runTest {
        val sut = viewModel()
        runCurrent() // let the background collector subscribe, anchoring debounce timing at t=0

        "kotlin".forEachIndexed { index, _ ->
            sut.onQueryChanged("kotlin".take(index + 1))
            advanceTimeBy(50)
        }
        advanceUntilIdle()

        assertEquals("kotlin", sut.searchKey.value.first)
    }

    @Test
    fun `onSortSelected updates sortOption immediately and feeds into searchKey`() = runTest {
        val sut = viewModel()

        sut.onSortSelected(SortOption.STARS)

        assertEquals(SortOption.STARS, sut.sortOption.value)
        advanceUntilIdle()
        assertEquals(SortOption.STARS, sut.searchKey.value.second)
    }

    @Test
    fun `onLanguageSelected updates languageFilter immediately and feeds into searchKey`() = runTest {
        val sut = viewModel()

        sut.onLanguageSelected("Kotlin")

        assertEquals("Kotlin", sut.languageFilter.value)
        advanceUntilIdle()
        assertEquals("Kotlin", sut.searchKey.value.third)
    }

    @Test
    fun `onSearchSubmitted records the trimmed query in history`() = runTest {
        coEvery { addSearchQueryUseCase("kotlin") } returns Unit
        val sut = viewModel()
        sut.onQueryChanged("  kotlin  ")

        sut.onSearchSubmitted()
        advanceUntilIdle()

        coVerify { addSearchQueryUseCase("kotlin") }
    }

    @Test
    fun `onSearchSubmitted does nothing for a blank query`() = runTest {
        val sut = viewModel()
        sut.onQueryChanged("   ")

        sut.onSearchSubmitted()
        advanceUntilIdle()

        coVerify(exactly = 0) { addSearchQueryUseCase(any()) }
    }

    @Test
    fun `onHistoryItemSelected sets the query and records it in history`() = runTest {
        coEvery { addSearchQueryUseCase("compose") } returns Unit
        val sut = viewModel()

        sut.onHistoryItemSelected("compose")

        assertEquals("compose", sut.query.value)
        advanceUntilIdle()
        coVerify { addSearchQueryUseCase("compose") }
    }

    @Test
    fun `onClearHistory delegates to the use case`() = runTest {
        coEvery { clearSearchHistoryUseCase() } returns Unit
        val sut = viewModel()

        sut.onClearHistory()
        advanceUntilIdle()

        coVerify { clearSearchHistoryUseCase() }
    }

    @Test
    fun `onToggleFavorite delegates to the use case with the given repository`() = runTest {
        val repo = repository(id = 1L)
        coEvery { toggleFavoriteUseCase(repo) } returns Unit
        val sut = viewModel()

        sut.onToggleFavorite(repo)
        advanceUntilIdle()

        coVerify { toggleFavoriteUseCase(repo) }
    }
}
