package com.onestore.sample.compose.ui.main.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.onestore.sample.compose.R

/**
 * 일반 메시지를 표시하는 다이얼로그
 *
 * 사용자에게 정보나 경고 메시지를 전달할 때 사용됩니다.
 * 확인 버튼만 제공하며, 버튼 클릭 시 다이얼로그가 닫힙니다.
 *
 * @param message 표시할 메시지 내용
 * @param onDismiss 다이얼로그를 닫을 때 호출되는 콜백
 *
 * @see AlertDialog
 */
@Composable
fun MessageDialog(
    message: String,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.padding(20.dp),
        onDismissRequest = onDismiss,
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.confirm))
            }
        }
    )
}

/**
 * 로그인이 필요함을 알리는 다이얼로그
 *
 * 사용자가 로그인하지 않은 상태에서 로그인이 필요한 기능에 접근할 때 표시됩니다.
 * 로그인 버튼을 클릭하면 로그인 플로우가 시작됩니다.
 *
 * @param onDismiss 다이얼로그를 닫을 때 호출되는 콜백
 * @param onLoginClick 로그인 버튼 클릭 시 호출되는 콜백
 *
 * @see AlertDialog
 */
@Composable
fun LoginRequiredDialog(
    onDismiss: () -> Unit,
    onLoginClick: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.padding(20.dp),
        onDismissRequest = onDismiss,
        text = { Text(stringResource(R.string.dialog_message_login_required)) },
        confirmButton = {
            Button(onClick = {
                onDismiss()
                onLoginClick()
            }) {
                Text(stringResource(R.string.login))
            }
        }
    )
}

/**
 * 앱 업데이트가 필요함을 알리는 다이얼로그
 *
 * 현재 앱 버전이 구형이거나 필수 업데이트가 필요할 때 표시됩니다.
 * 업데이트 버튼을 클릭하면 스토어의 업데이트 페이지로 이동합니다.
 *
 * @param onDismiss 다이얼로그를 닫을 때 호출되는 콜백
 * @param onUpdateClick 업데이트 버튼 클릭 시 호출되는 콜백
 *
 * @see AlertDialog
 */
@Composable
fun UpdateRequiredDialog(
    onDismiss: () -> Unit,
    onUpdateClick: () -> Unit
) {
    AlertDialog(
        modifier = Modifier.padding(20.dp),
        onDismissRequest = onDismiss,
        text = { Text(stringResource(R.string.dialog_message_update_required)) },
        confirmButton = {
            Button(onClick = {
                onDismiss()
                onUpdateClick()
            }) {
                Text(stringResource(R.string.dialog_button_update))
            }
        }
    )
}

/**
 * 로딩 중임을 나타내는 다이얼로그
 *
 * 백그라운드에서 작업이 진행 중일 때 사용자에게 대기를 요청합니다.
 * 기본적으로 외부 영역 클릭으로는 닫히지 않으며, 백 버튼으로만 닫을 수 있습니다.
 *
 * @param onDismiss 다이얼로그를 닫을 때 호출되는 콜백 (백 버튼으로 닫을 때)
 *
 * @see Dialog
 * @see CircularProgressIndicator
 */
@Composable
fun LoadingDialog(
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = false,
            dismissOnBackPress = true
        )
    ) {
        CircularProgressIndicator()
    }
}
