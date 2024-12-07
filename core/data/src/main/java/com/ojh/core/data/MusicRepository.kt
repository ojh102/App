package com.ojh.core.data

import com.ojh.core.model.Album
import com.ojh.core.model.Track
import kotlinx.coroutines.flow.Flow

interface MusicRepository {
    fun observeAlbums(): Flow<List<Album>>
    fun observeTracksByAlbumId(albumId: Long): Flow<List<Track>>
    suspend fun getAlbumById(albumId: Long): Album
}