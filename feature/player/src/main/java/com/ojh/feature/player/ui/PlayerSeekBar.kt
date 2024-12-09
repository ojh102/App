package com.ojh.feature.player.ui

import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun PlayerSeekBar(
    currentPosition: Long,
    duration: Long,
    onChangeProgress: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember { mutableFloatStateOf(currentPosition / duration.toFloat()) }
    LaunchedEffect(currentPosition, duration) {
        sliderPosition = currentPosition / duration.toFloat()
    }
    Slider(
        modifier = modifier.height(14.dp),
        value = sliderPosition,
        onValueChange = {
            sliderPosition = it
        },
        onValueChangeFinished = {
            onChangeProgress(sliderPosition)
        },
        colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.secondary),
    )
}
