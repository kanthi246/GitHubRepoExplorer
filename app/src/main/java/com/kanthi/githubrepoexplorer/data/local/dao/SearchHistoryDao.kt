package com.kanthi.githubrepoexplorer.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.kanthi.githubrepoexplorer.data.local.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Upsert
    suspend fun upsert(entity: SearchHistoryEntity)

    @Query("SELECT query FROM search_history ORDER BY searchedAt DESC LIMIT 10")
    fun getRecent(): Flow<List<String>>

    @Query("DELETE FROM search_history")
    suspend fun clear()
}
