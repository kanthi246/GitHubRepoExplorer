package com.kanthi.githubrepoexplorer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Stores repositories that need to survive process death or a lost connection:
 * favorites (isFavorite = true) and the most recently viewed details screen (for
 * offline viewing). Search results themselves are not persisted here — only what
 * the user has explicitly favorited or opened.
 */
@Entity(tableName = "repositories")
data class RepositoryEntity(
    @PrimaryKey val id: Long,
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
    val isFavorite: Boolean,
    val cachedAt: Long,
)
