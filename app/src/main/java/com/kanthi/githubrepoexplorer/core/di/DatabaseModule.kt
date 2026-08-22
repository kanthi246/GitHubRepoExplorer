package com.kanthi.githubrepoexplorer.core.di

import android.content.Context
import androidx.room.Room
import com.kanthi.githubrepoexplorer.data.local.dao.RepositoryDao
import com.kanthi.githubrepoexplorer.data.local.dao.SearchHistoryDao
import com.kanthi.githubrepoexplorer.data.local.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * core/di holds Hilt "modules" — recipes that teach the dependency injection framework how to
 * construct objects it doesn't know how to build automatically (mainly third-party classes like
 * Room's database builder or Retrofit, which have no @Inject constructor of their own).
 *
 * This module builds the local Room database (the on-device SQLite database used for offline
 * caching and favorites — see data/local) and exposes its DAOs.
 *
 * @InstallIn(SingletonComponent::class) + @Singleton mean there is exactly one AppDatabase
 * instance for the whole app lifetime.
 *
 * Benefit: any class (e.g. GithubRepositoryImpl) can simply declare `private val dao: RepositoryDao`
 * in its constructor and Hilt hands it the shared instance — no manual wiring, no leaked
 * duplicate database connections.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME).build()

    @Provides
    fun provideRepositoryDao(database: AppDatabase): RepositoryDao = database.repositoryDao()

    @Provides
    fun provideSearchHistoryDao(database: AppDatabase): SearchHistoryDao = database.searchHistoryDao()
}
