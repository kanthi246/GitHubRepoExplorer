package com.kanthi.githubrepoexplorer.domain.model

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
