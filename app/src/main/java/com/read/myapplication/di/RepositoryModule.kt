package com.read.myapplication.di

import com.read.myapplication.data.repository.PostRepositoryImpl
import com.read.myapplication.domain.repository.PostRepository
import com.read.myapplication.domain.usecase.GetCachedPostsUseCase
import com.read.myapplication.domain.usecase.GetCachedThenRefreshUseCase
import com.read.myapplication.domain.usecase.GetPostUseCase
import com.read.myapplication.domain.usecase.GetPostsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RepositoryModule {
    @Provides
    @Singleton
    fun providePostRepository(impl: PostRepositoryImpl): PostRepository = impl
}

@Module
@InstallIn(SingletonComponent::class)
object RepositoryProvides {

    // Provide GetPostsUseCase exactly once
    @Provides
    fun provideGetPostsUseCase(repo: PostRepository): GetPostsUseCase {
        return GetPostsUseCase(repo)
    }

    // Provide GetCachedPostsUseCase (note different type)
    @Provides
    fun provideGetCachedPostsUseCase(repo: PostRepository): GetCachedPostsUseCase {
        return GetCachedPostsUseCase(repo)
    }

    // Provide GetPostUseCase
    @Provides
    fun provideGetPostUseCase(repo: PostRepository): GetPostUseCase {
        return GetPostUseCase(repo)
    }

    // Provide GetCachedThenRefreshUseCase
    @Provides
    fun provideGetCachedThenRefreshUseCase(repo: PostRepository): GetCachedThenRefreshUseCase {
        return GetCachedThenRefreshUseCase(repo)
    }
}

