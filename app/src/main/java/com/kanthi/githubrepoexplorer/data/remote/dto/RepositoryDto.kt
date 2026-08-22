package com.kanthi.githubrepoexplorer.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * DTOs ("Data Transfer Objects") mirror the exact JSON shape GitHub's API returns — field names
 * like `stargazers_count` stay snake_case via @SerializedName because that's what the API sends,
 * even though the rest of the Kotlin codebase uses camelCase.
 *
 * These are intentionally *not* used outside the data layer — see data/mapper/RepositoryMappers.kt,
 * which converts them into the app's own `Repository` domain model.
 *
 * Benefit: if GitHub's API response format changes, only these DTOs (and their mapper) need to
 * change — the rest of the app is insulated from it.
 */
data class SearchRepositoriesResponseDto(
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("items") val items: List<RepositoryDto>,
)

data class RepositoryDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("full_name") val fullName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("owner") val owner: OwnerDto,
    @SerializedName("stargazers_count") val stargazersCount: Int,
    @SerializedName("forks_count") val forksCount: Int,
    @SerializedName("open_issues_count") val openIssuesCount: Int,
    @SerializedName("language") val language: String?,
    @SerializedName("default_branch") val defaultBranch: String?,
    @SerializedName("updated_at") val updatedAt: String,
    @SerializedName("html_url") val htmlUrl: String,
)

data class OwnerDto(
    @SerializedName("login") val login: String,
    @SerializedName("avatar_url") val avatarUrl: String,
)
