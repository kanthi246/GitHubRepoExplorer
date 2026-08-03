package com.kanthi.githubrepoexplorer.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kanthi.githubrepoexplorer.data.local.entity.RepositoryEntity
import kotlinx.coroutines.flow.Flow

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
