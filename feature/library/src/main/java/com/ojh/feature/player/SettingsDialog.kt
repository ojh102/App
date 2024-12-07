package com.ojh.feature.player

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.ojh.core.compose.theme.AppTheme

@Composable
internal fun SettingsDialog(
    onDismissRequest: () -> Unit,
    onClickPositiveButton: () -> Unit,
    onClickNegativeButton: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = "설정에서 권한을 켜주세요")
        },
        confirmButton = {
            TextButton(onClick = onClickPositiveButton) {
                Text("설정으로 가기")
            }
        },
        dismissButton = {
            TextButton(onClick = onClickNegativeButton) {
                Text("취소")
            }
        }
    )
}

@Preview
@Composable
private fun SettingsDialogPreview() {
    AppTheme {
        SettingsDialog({}, {}, {})
    }
}
