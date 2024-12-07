package com.ojh.core.media

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal interface MediaModule {
    @Binds
    @Singleton
    fun bindRepository(repository: MediaSessionRepositoryImpl): MediaSessionRepository
}