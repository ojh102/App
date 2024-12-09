package com.ojh.feature.library.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ojh.core.compose.theme.AppTheme
import com.ojh.feature.library.R

@Composable
internal fun NeedPermissionLayout(
    onClickRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(stringResource(R.string.feature_library_need_permission_layout_title))
        Button(onClick = onClickRequestPermission) {
            Text(stringResource(R.string.feature_library_need_permission_layout_request_button_text))
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
