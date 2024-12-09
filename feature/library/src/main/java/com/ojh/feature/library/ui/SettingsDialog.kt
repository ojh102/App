package com.ojh.feature.library.ui

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ojh.core.compose.theme.AppTheme
import com.ojh.feature.library.R

@Composable
internal fun SettingsDialog(
    onDismissRequest: () -> Unit,
    onClickPositiveButton: () -> Unit,
    onClickNegativeButton: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = stringResource(R.string.feature_library_settings_dialog_title))
        },
        confirmButton = {
            TextButton(onClick = onClickPositiveButton) {
                Text(stringResource(R.string.feature_library_settings_dialog_cofirm_button_text))
            }
        },
        dismissButton = {
            TextButton(onClick = onClickNegativeButton) {
                Text(stringResource(R.string.feature_library_settings_dialog_dismiss_button_text))
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
