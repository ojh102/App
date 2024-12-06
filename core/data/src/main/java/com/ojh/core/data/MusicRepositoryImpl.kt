package com.ojh.core.data

import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import com.ojh.core.model.Album
import com.ojh.core.model.Track
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

internal class MusicRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MusicRepository {

    override fun observeAlbums(): Flow<List<Album>> = callbackFlow {
        val contentResolver = context.contentResolver
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                trySend(getAlbumList(context))
            }
        }

        contentResolver.registerContentObserver(uri, true, observer)

        trySend(getAlbumList(context))

        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }

    override fun observeTracksByAlbumId(albumId: Long): Flow<List<Track>> = callbackFlow {
        val contentResolver: ContentResolver = context.contentResolver
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val observer = object : ContentObserver(Handler(Looper.getMainLooper())) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                trySend(getTracksByAlbumId(albumId)) // 변경 시 최신 트랙 리스트 전송
            }
        }
        contentResolver.registerContentObserver(uri, true, observer)
        trySend(getTracksByAlbumId(albumId))
        awaitClose {
            contentResolver.unregisterContentObserver(observer)
        }
    }

    private fun getAlbumList(context: Context): List<Album> {
        val albumList = mutableListOf<Album>()
        val contentResolver: ContentResolver = context.contentResolver
        val uri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Albums._ID,
            MediaStore.Audio.Albums.ALBUM,
            MediaStore.Audio.Albums.ARTIST
        )

        val cursor = contentResolver.query(
            uri,
            projection,
            null,
            null,
            MediaStore.Audio.Albums.ALBUM // 앨범명 기준 정렬
        )

        cursor?.use {
            val idColumn = it.getColumnIndex(MediaStore.Audio.Albums._ID)
            val albumColumn = it.getColumnIndex(MediaStore.Audio.Albums.ALBUM)
            val artistColumn = it.getColumnIndex(MediaStore.Audio.Albums.ARTIST)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val name = it.getString(albumColumn)
                val artist = it.getString(artistColumn)
                val albumArtUri = ContentUris.withAppendedId(
                    Uri.parse(ALBUM_ART_URI), id
                ).toString()

                albumList.add(Album(id, name, artist, albumArtUri))
            }
        }

        return albumList
    }


    private fun getTracksByAlbumId(albumId: Long): List<Track> {
        val trackList = mutableListOf<Track>()
        val contentResolver: ContentResolver = context.contentResolver
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA
        )


        val selection = "${MediaStore.Audio.Media.ALBUM_ID} = ?"
        val selectionArgs = arrayOf(albumId.toString())

        val cursor = contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            MediaStore.Audio.Media.TITLE
        )

        cursor?.use {
            val idColumn = it.getColumnIndex(MediaStore.Audio.Media._ID)
            val titleColumn = it.getColumnIndex(MediaStore.Audio.Media.TITLE)
            val artistColumn = it.getColumnIndex(MediaStore.Audio.Media.ARTIST)
            val durationColumn = it.getColumnIndex(MediaStore.Audio.Media.DURATION)
            val dataColumn = it.getColumnIndex(MediaStore.Audio.Media.DATA)

            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val title = it.getString(titleColumn)
                val artist = it.getString(artistColumn)
                val duration = it.getLong(durationColumn)
                val data = it.getString(dataColumn)

                trackList.add(Track(id, title, artist, duration, data))
            }
        }

        return trackList
    }

    companion object {
        private const val ALBUM_ART_URI = "content://media/external/audio/albumart"
    }
}