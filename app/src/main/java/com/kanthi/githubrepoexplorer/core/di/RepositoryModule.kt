package com.kanthi.githubrepoexplorer.core.di

import com.kanthi.githubrepoexplorer.data.repository.GithubRepositoryImpl
import com.kanthi.githubrepoexplorer.domain.repository.GithubRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Tells Hilt which concrete class to hand out whenever something asks for the GithubRepository
 * *interface* (defined in domain/repository). Here that's GithubRepositoryImpl (in data/repository).
 *
 * This is the key piece that connects the domain layer to the data layer: domain/usecase classes
 * depend only on the GithubRepository interface and have no idea GithubRepositoryImpl, Retrofit,
 * or Room even exist.
 *
 * Benefit: the domain layer stays independent of *how* data is fetched. Swapping the data source
 * (e.g. a different backend, or a fake implementation for tests) only means changing this one
 * binding — no other code needs to change.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindGithubRepository(impl: GithubRepositoryImpl): GithubRepository
}
