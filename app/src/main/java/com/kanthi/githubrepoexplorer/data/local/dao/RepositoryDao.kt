package com.kanthi.githubrepoexplorer.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kanthi.githubrepoexplorer.data.local.entity.RepositoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * A Room DAO ("Data Access Object") — you write the SQL query, Room generates the code that runs
 * it and maps rows into RepositoryEntity objects. This is the only place raw SQL for the
 * "repositories" table appears.
 *
 * Methods returning Flow (getFavorites, observeFavorite) stay open and automatically emit a new
 * list whenever the underlying table changes — no manual refresh needed.
 *
 * Benefit: the rest of the app never writes SQL directly; it just calls plain Kotlin functions,
 * and the UI can reactively update itself the instant the database changes (e.g. tapping the
 * favorite star updates the Favorites screen immediately).
 */
@Dao
interface RepositoryDao {

    @Upsert
    suspend fun upsert(entity: RepositoryEntity)

    @Query("SELECT * FROM repositories WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): RepositoryEntity?

    @Query("SELECT * FROM repositories WHERE fullName = :fullName LIMIT 1")
    suspend fun getByFullName(fullName: String): RepositoryEntity?

    @Query("SELECT * FROM repositories WHERE isFavorite = 1 ORDER BY cachedAt DESC")
    fun getFavorites(): Flow<List<RepositoryEntity>>

    @Query("SELECT isFavorite FROM repositories WHERE id = :id")
    fun observeFavorite(id: Long): Flow<Boolean?>
}
