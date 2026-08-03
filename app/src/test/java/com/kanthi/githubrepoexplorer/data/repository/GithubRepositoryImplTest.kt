package com.kanthi.githubrepoexplorer.data.repository

import com.kanthi.githubrepoexplorer.core.common.AppResult
import com.kanthi.githubrepoexplorer.data.local.dao.RepositoryDao
import com.kanthi.githubrepoexplorer.data.local.dao.SearchHistoryDao
import com.kanthi.githubrepoexplorer.data.local.entity.RepositoryEntity
import com.kanthi.githubrepoexplorer.data.local.entity.SearchHistoryEntity
import com.kanthi.githubrepoexplorer.data.remote.api.GithubApiService
import com.kanthi.githubrepoexplorer.testutil.repository
import com.kanthi.githubrepoexplorer.testutil.repositoryDto
import com.kanthi.githubrepoexplorer.testutil.repositoryEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

class GithubRepositoryImplTest {

    private val api: GithubApiService = mockk()
    private val repositoryDao: RepositoryDao = mockk()
    private val searchHistoryDao: SearchHistoryDao = mockk()
    private lateinit var sut: GithubRepositoryImpl

    @Before
    fun setUp() {
        sut = GithubRepositoryImpl(api, repositoryDao, searchHistoryDao)
    }

    // -- getRepositoryDetails --------------------------------------------------------------

    @Test
    fun `getRepositoryDetails caches the freshly fetched repo and returns it`() = runTest {
        coEvery { api.getRepository("octocat", "Hello-World") } returns repositoryDto(id = 1L)
        coEvery { repositoryDao.getById(1L) } returns null
        coEvery { repositoryDao.upsert(any()) } returns Unit

        val result = sut.getRepositoryDetails("octocat", "Hello-World")

        assertTrue(result is AppResult.Success)
        assertEquals(1L, (result as AppResult.Success).data.id)
        coVerify { repositoryDao.upsert(any()) }
    }

    @Test
    fun `getRepositoryDetails preserves the existing favorite flag from the cache`() = runTest {
        coEvery { api.getRepository("octocat", "Hello-World") } returns repositoryDto(id = 1L)
        coEvery { repositoryDao.getById(1L) } returns repositoryEntity(id = 1L, isFavorite = true)
        val savedEntity = slot<RepositoryEntity>()
        coEvery { repositoryDao.upsert(capture(savedEntity)) } returns Unit

        val result = sut.getRepositoryDetails("octocat", "Hello-World") as AppResult.Success

        assertTrue(result.data.isFavorite)
        assertTrue(savedEntity.captured.isFavorite)
    }

    @Test
    fun `getRepositoryDetails falls back to the cached copy when the network call fails`() = runTest {
        coEvery { api.getRepository("octocat", "Hello-World") } throws IOException("offline")
        coEvery { repositoryDao.getByFullName("octocat/Hello-World") } returns repositoryEntity(fullName = "octocat/Hello-World")

        val result = sut.getRepositoryDetails("octocat", "Hello-World")

        assertTrue(result is AppResult.Success)
        assertEquals("octocat/Hello-World", (result as AppResult.Success).data.fullName)
    }

    @Test
    fun `getRepositoryDetails returns an error when offline with nothing cached`() = runTest {
        coEvery { api.getRepository("octocat", "Hello-World") } throws IOException("offline")
        coEvery { repositoryDao.getByFullName("octocat/Hello-World") } returns null

        val result = sut.getRepositoryDetails("octocat", "Hello-World")

        assertTrue(result is AppResult.Error)
        assertEquals("offline", (result as AppResult.Error).message)
    }

    // -- toggleFavorite -----------------------------------------------------------------

    @Test
    fun `toggling favorite on a repo with no cache entry marks it favorited`() = runTest {
        coEvery { repositoryDao.getById(1L) } returns null
        val savedEntity = slot<RepositoryEntity>()
        coEvery { repositoryDao.upsert(capture(savedEntity)) } returns Unit

        sut.toggleFavorite(repository(id = 1L))

        assertTrue(savedEntity.captured.isFavorite)
    }

    @Test
    fun `toggling favorite on an already-favorited repo un-favorites it`() = runTest {
        coEvery { repositoryDao.getById(1L) } returns repositoryEntity(id = 1L, isFavorite = true, cachedAt = 777L)
        val savedEntity = slot<RepositoryEntity>()
        coEvery { repositoryDao.upsert(capture(savedEntity)) } returns Unit

        sut.toggleFavorite(repository(id = 1L))

        assertFalse(savedEntity.captured.isFavorite)
    }

    @Test
    fun `toggling favorite preserves the original cachedAt instead of resetting it`() = runTest {
        coEvery { repositoryDao.getById(1L) } returns repositoryEntity(id = 1L, isFavorite = false, cachedAt = 777L)
        val savedEntity = slot<RepositoryEntity>()
        coEvery { repositoryDao.upsert(capture(savedEntity)) } returns Unit

        sut.toggleFavorite(repository(id = 1L))

        assertEquals(777L, savedEntity.captured.cachedAt)
    }

    // -- favorites & search history -------------------------------------------------------

    @Test
    fun `getFavorites maps cached entities to domain repositories`() = runTest {
        coEvery { repositoryDao.getFavorites() } returns flowOf(listOf(repositoryEntity(id = 9L, isFavorite = true)))

        val favorites = sut.getFavorites().first()

        assertEquals(listOf(9L), favorites.map { it.id })
        assertTrue(favorites.single().isFavorite)
    }

    @Test
    fun `isFavorite treats an absent row as not favorited`() = runTest {
        coEvery { repositoryDao.observeFavorite(1L) } returns flowOf(null)

        var observed: Boolean? = null
        sut.isFavorite(1L).collect { observed = it }

        assertEquals(false, observed)
    }

    @Test
    fun `addSearchQuery does nothing for a blank query`() = runTest {
        sut.addSearchQuery("   ")

        coVerify(exactly = 0) { searchHistoryDao.upsert(any()) }
    }

    @Test
    fun `addSearchQuery trims whitespace before persisting`() = runTest {
        val savedEntity = slot<SearchHistoryEntity>()
        coEvery { searchHistoryDao.upsert(capture(savedEntity)) } returns Unit

        sut.addSearchQuery("  kotlin  ")

        assertEquals("kotlin", savedEntity.captured.query)
    }

    @Test
    fun `clearSearchHistory delegates straight to the dao`() = runTest {
        coEvery { searchHistoryDao.clear() } returns Unit

        sut.clearSearchHistory()

        coVerify { searchHistoryDao.clear() }
    }
}
