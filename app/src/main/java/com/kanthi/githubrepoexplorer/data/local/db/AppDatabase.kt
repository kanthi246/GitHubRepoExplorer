package com.kanthi.githubrepoexplorer.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kanthi.githubrepoexplorer.data.local.dao.RepositoryDao
import com.kanthi.githubrepoexplorer.data.local.dao.SearchHistoryDao
import com.kanthi.githubrepoexplorer.data.local.entity.RepositoryEntity
import com.kanthi.githubrepoexplorer.data.local.entity.SearchHistoryEntity

/**
 * The Room database definition — the on-device SQLite database that backs offline caching and
 * favorites. Lists every table (`entities`) and hands out the DAOs used to query them.
 *
 * `version` must be bumped (with a migration) any time a table's shape changes; `exportSchema =
 * false` skips writing a schema history file to disk, which is fine for a small app without
 * formal migration tracking.
 *
 * Benefit: Room verifies your SQL queries (in the DAOs) at compile time against this schema, so
 * typos or column mismatches are caught while building instead of crashing at runtime.
 */
@Database(
    entities = [RepositoryEntity::class, SearchHistoryEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao
    abstract fun searchHistoryDao(): SearchHistoryDao

    companion object {
        const val DATABASE_NAME = "github_explorer.db"
    }
}
