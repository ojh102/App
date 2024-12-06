package com.ojh.core.data

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal interface DataModule {
    @Binds
    fun bindMusicRepository(repository: MusicRepositoryImpl): MusicRepository

    @Binds
    fun bindPermissionRepository(repository: PermissionRepositoryImpl): PermissionRepository
}