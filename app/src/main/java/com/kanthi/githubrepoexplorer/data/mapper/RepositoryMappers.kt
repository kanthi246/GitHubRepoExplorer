package com.kanthi.githubrepoexplorer.data.mapper

import com.kanthi.githubrepoexplorer.data.local.entity.RepositoryEntity
import com.kanthi.githubrepoexplorer.data.remote.dto.RepositoryDto
import com.kanthi.githubrepoexplorer.domain.model.Repository

/**
 * Converts between the three different shapes the "same" repository data takes as it moves
 * through the app: RepositoryDto (raw JSON shape from the GitHub API), RepositoryEntity (the
 * database row shape), and Repository (the clean domain model everything else in the app uses).
 *
 * Benefit: the domain and presentation layers only ever see the stable `Repository` model and
 * never need to know about JSON field names or database column types — if GitHub changes its API
 * response shape, only this file (and RepositoryDto) needs updating.
 */
fun RepositoryDto.toDomain(isFavorite: Boolean = false): Repository = Repository(
    id = id,
    name = name,
    fullName = fullName,
    description = description,
    ownerLogin = owner.login,
    ownerAvatarUrl = owner.avatarUrl,
    stars = stargazersCount,
    forks = forksCount,
    openIssues = openIssuesCount,
    language = language,
    defaultBranch = defaultBranch.orEmpty(),
    updatedAt = updatedAt,
    htmlUrl = htmlUrl,
    isFavorite = isFavorite,
)

fun RepositoryEntity.toDomain(): Repository = Repository(
    id = id,
    name = name,
    fullName = fullName,
    description = description,
    ownerLogin = ownerLogin,
    ownerAvatarUrl = ownerAvatarUrl,
    stars = stars,
    forks = forks,
    openIssues = openIssues,
    language = language,
    defaultBranch = defaultBranch,
    updatedAt = updatedAt,
    htmlUrl = htmlUrl,
    isFavorite = isFavorite,
)

fun Repository.toEntity(isFavorite: Boolean = this.isFavorite, cachedAt: Long = System.currentTimeMillis()): RepositoryEntity =
    RepositoryEntity(
        id = id,
        name = name,
        fullName = fullName,
        description = description,
        ownerLogin = ownerLogin,
        ownerAvatarUrl = ownerAvatarUrl,
        stars = stars,
        forks = forks,
        openIssues = openIssues,
        language = language,
        defaultBranch = defaultBranch,
        updatedAt = updatedAt,
        htmlUrl = htmlUrl,
        isFavorite = isFavorite,
        cachedAt = cachedAt,
    )
