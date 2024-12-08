package com.ojh.feature.album.ui.model

import androidx.compose.runtime.Immutable
import com.ojh.core.model.Track

@Immutable
internal data class TrackUiModel(
    val id: Long,
    val displayedTrackNumber: Int,
    val title: String
)

internal fun Track.toUiModel(index: Int): TrackUiModel {
    return TrackUiModel(
        id = id,
        displayedTrackNumber = index + 1,
        title = title,
    )
}