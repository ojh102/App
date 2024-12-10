package com.ojh.feature.player.ui

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ojh.core.compose.theme.AppTheme
import java.time.Duration
import java.util.Locale

@Composable
internal fun PlayerSeekBar(
    currentPosition: Long,
    duration: Long,
    showDurationText: Boolean,
    onChangeProgress: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderPosition by remember {
        mutableFloatStateOf(currentPosition / duration.toFloat())
    }
    val interactionSource = remember { MutableInteractionSource() }
    val isDragging by interactionSource.collectIsDraggedAsState()

    LaunchedEffect(currentPosition, duration) {
        if (!isDragging) {
            sliderPosition = currentPosition / duration.toFloat()
        }
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (showDurationText) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = (sliderPosition * duration).toLong().toDurationText(),
                    modifier = Modifier
                )

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = duration.toDurationText(),
                    modifier = Modifier
                )
            }
        }
        Slider(
            modifier = Modifier.height(14.dp),
            value = sliderPosition,
            interactionSource = interactionSource,
            onValueChange = {
                sliderPosition = it
            },
            onValueChangeFinished = {
                onChangeProgress(sliderPosition)
            },
            colors = SliderDefaults.colors(inactiveTrackColor = MaterialTheme.colorScheme.secondary),
        )
    }
}

private fun Long.toDurationText(): String {
    val (minutes, seconds) = with(Duration.ofMillis(this)) {
        toMinutesPart() to toSecondsPart()
    }
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}

@Preview
@Composable
private fun PlayerSeekBarPreview() {
    AppTheme {
        PlayerSeekBar(
            currentPosition = 5000,
            duration = 100000,
            showDurationText = false,
            onChangeProgress = {},
        )
    }
}

@Preview
@Composable
private fun PlayerSeekBarPreview_expanded() {
    AppTheme {
        PlayerSeekBar(
            currentPosition = 5000,
            duration = 100000,
            showDurationText = true,
            onChangeProgress = {},
        )
    }
}