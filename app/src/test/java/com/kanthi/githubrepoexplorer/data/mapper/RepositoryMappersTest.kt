package com.kanthi.githubrepoexplorer.data.mapper

import com.kanthi.githubrepoexplorer.testutil.ownerDto
import com.kanthi.githubrepoexplorer.testutil.repository
import com.kanthi.githubrepoexplorer.testutil.repositoryDto
import com.kanthi.githubrepoexplorer.testutil.repositoryEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RepositoryMappersTest {

    @Test
    fun `dto maps owner fields and counts onto the domain model`() {
        val dto = repositoryDto(
            owner = ownerDto(login = "kanthi", avatarUrl = "https://avatars.example/kanthi.png"),
            stars = 42,
            forks = 7,
            openIssues = 3,
        )

        val domain = dto.toDomain()

        assertEquals("kanthi", domain.ownerLogin)
        assertEquals("https://avatars.example/kanthi.png", domain.ownerAvatarUrl)
        assertEquals(42, domain.stars)
        assertEquals(7, domain.forks)
        assertEquals(3, domain.openIssues)
    }

    @Test
    fun `dto defaults isFavorite to false unless told otherwise`() {
        assertFalse(repositoryDto().toDomain().isFavorite)
        assertTrue(repositoryDto().toDomain(isFavorite = true).isFavorite)
    }

    @Test
    fun `dto with a null default branch maps to an empty string, not null`() {
        val domain = repositoryDto(defaultBranch = null).toDomain()

        assertEquals("", domain.defaultBranch)
    }

    @Test
    fun `entity round-trips into the same domain fields`() {
        val entity = repositoryEntity(id = 99L, name = "Spoon-Knife", isFavorite = true)

        val domain = entity.toDomain()

        assertEquals(99L, domain.id)
        assertEquals("Spoon-Knife", domain.name)
        assertTrue(domain.isFavorite)
    }

    @Test
    fun `domain model converts to an entity carrying the same data`() {
        val domain = repository(id = 5L, name = "octokit", isFavorite = true)

        val entity = domain.toEntity(cachedAt = 42L)

        assertEquals(5L, entity.id)
        assertEquals("octokit", entity.name)
        assertTrue(entity.isFavorite)
        assertEquals(42L, entity.cachedAt)
    }

    @Test
    fun `toEntity defaults isFavorite from the domain model, not to false`() {
        val favorited = repository(isFavorite = true)

        assertTrue(favorited.toEntity().isFavorite)
    }

    @Test
    fun `toEntity favorite flag can be overridden independently of the domain model`() {
        val notFavorited = repository(isFavorite = false)

        assertTrue(notFavorited.toEntity(isFavorite = true).isFavorite)
    }
}
