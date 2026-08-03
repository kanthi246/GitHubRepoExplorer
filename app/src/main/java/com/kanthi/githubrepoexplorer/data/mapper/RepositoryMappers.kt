package com.kanthi.githubrepoexplorer.data.mapper

import com.kanthi.githubrepoexplorer.data.local.entity.RepositoryEntity
import com.kanthi.githubrepoexplorer.data.remote.dto.RepositoryDto
import com.kanthi.githubrepoexplorer.domain.model.Repository

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
