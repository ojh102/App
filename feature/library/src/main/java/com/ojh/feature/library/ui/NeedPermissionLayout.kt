package com.ojh.feature.library.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ojh.core.compose.theme.AppTheme

@Composable
internal fun NeedPermissionLayout(
    onClickRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text("미디어 권한이 없습니다. 권한을 획득하세요.")
        Button(onClick = onClickRequestPermission) {
            Text("권한 획득하기")
        }
    }
}

@Preview
@Composable
private fun NeedPermissionLayoutPreview() {
    AppTheme {
        NeedPermissionLayout(onClickRequestPermission = {})
    }
}
