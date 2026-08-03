package com.kanthi.githubrepoexplorer.core.di

import com.kanthi.githubrepoexplorer.data.repository.GithubRepositoryImpl
import com.kanthi.githubrepoexplorer.domain.repository.GithubRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGithubRepository(impl: GithubRepositoryImpl): GithubRepository
}
