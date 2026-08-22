package com.kanthi.githubrepoexplorer.domain.model

/**
 * The app's own clean representation of a GitHub repository — what every screen and use case
 * actually works with, as opposed to RepositoryDto (raw network JSON shape) or RepositoryEntity
 * (database row shape). See data/mapper/RepositoryMappers.kt for how those convert into this.
 *
 * Benefit: this class has zero dependency on Retrofit, Gson, or Room annotations — the domain and
 * presentation layers stay pure Kotlin, easy to unit test, and unaffected by data-layer changes.
 */
data class Repository(
    val id: Long,
    val name: String,
    val fullName: String,
    val description: String?,
    val ownerLogin: String,
    val ownerAvatarUrl: String,
    val stars: Int,
    val forks: Int,
    val openIssues: Int,
    val language: String?,
    val defaultBranch: String,
    val updatedAt: String,
    val htmlUrl: String,
    val isFavorite: Boolean = false,
)
