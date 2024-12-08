package com.ojh.feature.album

import androidx.lifecycle.SavedStateHandle
import com.ojh.core.navigation.AppDestination
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
internal class AlbumModule {
    @Provides
    fun provideParams(savedStateHandle: SavedStateHandle): AlbumParams {
        val albumId = savedStateHandle.get<Long>(AppDestination.Album.albumIdArg)
            ?: throw IllegalArgumentException("invalid args")
        return AlbumParams(albumId = albumId)
    }
}