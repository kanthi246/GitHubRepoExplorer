package com.kanthi.githubrepoexplorer.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/** One row in the "search_history" table: a past search query and when it was searched. The query text itself is the primary key, so re-searching the same term just bumps its timestamp instead of creating a duplicate row. */
@Entity(tableName = "search_history")
data class SearchHistoryEntity(
    @PrimaryKey val query: String,
    val searchedAt: Long,
)
