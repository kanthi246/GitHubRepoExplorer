package com.kanthi.githubrepoexplorer.testutil

import com.kanthi.githubrepoexplorer.data.local.entity.RepositoryEntity
import com.kanthi.githubrepoexplorer.data.remote.dto.OwnerDto
import com.kanthi.githubrepoexplorer.data.remote.dto.RepositoryDto
import com.kanthi.githubrepoexplorer.domain.model.Repository

fun ownerDto(login: String = "octocat", avatarUrl: String = "https://avatars.example/octocat.png") =
    OwnerDto(login = login, avatarUrl = avatarUrl)

fun repositoryDto(
    id: Long = 1L,
    name: String = "Hello-World",
    fullName: String = "octocat/Hello-World",
    description: String? = "My first repository",
    owner: OwnerDto = ownerDto(),
    stars: Int = 1500,
    forks: Int = 300,
    openIssues: Int = 12,
    language: String? = "Kotlin",
    defaultBranch: String? = "main",
    updatedAt: String = "2024-05-12T10:15:23Z",
    htmlUrl: String = "https://github.com/octocat/Hello-World",
) = RepositoryDto(
    id = id,
    name = name,
    fullName = fullName,
    description = description,
    owner = owner,
    stargazersCount = stars,
    forksCount = forks,
    openIssuesCount = openIssues,
    language = language,
    defaultBranch = defaultBranch,
    updatedAt = updatedAt,
    htmlUrl = htmlUrl,
)

fun repositoryEntity(
    id: Long = 1L,
    name: String = "Hello-World",
    fullName: String = "octocat/Hello-World",
    description: String? = "My first repository",
    ownerLogin: String = "octocat",
    ownerAvatarUrl: String = "https://avatars.example/octocat.png",
    stars: Int = 1500,
    forks: Int = 300,
    openIssues: Int = 12,
    language: String? = "Kotlin",
    defaultBranch: String = "main",
    updatedAt: String = "2024-05-12T10:15:23Z",
    htmlUrl: String = "https://github.com/octocat/Hello-World",
    isFavorite: Boolean = false,
    cachedAt: Long = 1_000L,
) = RepositoryEntity(
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

fun repository(
    id: Long = 1L,
    name: String = "Hello-World",
    fullName: String = "octocat/Hello-World",
    description: String? = "My first repository",
    ownerLogin: String = "octocat",
    ownerAvatarUrl: String = "https://avatars.example/octocat.png",
    stars: Int = 1500,
    forks: Int = 300,
    openIssues: Int = 12,
    language: String? = "Kotlin",
    defaultBranch: String = "main",
    updatedAt: String = "2024-05-12T10:15:23Z",
    htmlUrl: String = "https://github.com/octocat/Hello-World",
    isFavorite: Boolean = false,
) = Repository(
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
