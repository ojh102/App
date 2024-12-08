package com.ojh.feature.player.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ojh.core.compose.theme.AppTheme

@Composable
internal fun EmptyPlayerLayout() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(color = MaterialTheme.colorScheme.secondaryContainer),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "재생목록이 비어있습니다.",
            style = MaterialTheme.typography.titleSmall
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "앨범에서 트랙을 재생하세요.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview
@Composable
private fun EmptyPlayerLayoutPreview() {
    AppTheme {
        EmptyPlayerLayout()
    }
}