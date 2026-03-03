package com.onestore.sample.compose.ui.feature.license.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cached
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.onestore.sample.compose.R
import com.onestore.sample.compose.ui.feature.license.model.AppLicenseCheckMode

/**
 * 정책 기반 확인 및 엄격한 확인 버튼을 포함하는 Segmented Button 스타일의 검증 모드 선택 그룹입니다.
 *
 * @param selectedMode 현재 선택된 검증 모드
 * @param onPolicyClick 정책 기반 확인 버튼 클릭 시 호출되는 콜백
 * @param onStrictClick 엄격한 확인 버튼 클릭 시 호출되는 콜백
 */
@Composable
fun AppLicenseCheckModeButtons(
    selectedMode: AppLicenseCheckMode,
    onPolicyClick: () -> Unit,
    onStrictClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        val cornerRadius = 24.dp
        val buttonHeight = 56.dp
        
        val leftShape = MaterialTheme.shapes.medium.copy(
            topEnd = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp),
            topStart = CornerSize(cornerRadius),
            bottomStart = CornerSize(cornerRadius)
        )
        
        if (selectedMode == AppLicenseCheckMode.POLICY) {
            Button(
                onClick = onPolicyClick,
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                shape = leftShape
            ) {
                PolicyButtonContent()
            }
        } else {
            OutlinedButton(
                onClick = onPolicyClick,
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight),
                shape = leftShape
            ) {
                PolicyButtonContent()
            }
        }

        val rightShape = MaterialTheme.shapes.medium.copy(
            topStart = CornerSize(0.dp),
            bottomStart = CornerSize(0.dp),
            topEnd = CornerSize(cornerRadius),
            bottomEnd = CornerSize(cornerRadius)
        )
        
        if (selectedMode == AppLicenseCheckMode.STRICT) {
            Button(
                onClick = onStrictClick,
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight)
                    .offset(x = (-1).dp),
                shape = rightShape
            ) {
                StrictButtonContent()
            }
        } else {
            OutlinedButton(
                onClick = onStrictClick,
                modifier = Modifier
                    .weight(1f)
                    .height(buttonHeight)
                    .offset(x = (-1).dp),
                shape = rightShape
            ) {
                StrictButtonContent()
            }
        }
    }
}

/**
 * 정책 기반 확인 버튼 내부 콘텐츠 (아이콘 + 텍스트)를 표시하는 컴포저블입니다.
 */
@Composable
private fun PolicyButtonContent() {
    Icon(
        imageVector = Icons.Default.Cached, 
        contentDescription = null, 
        modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
        text = stringResource(R.string.license_check_cached_policy),
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center
    )
}

/**
 * 엄격한 확인 버튼 내부 콘텐츠 (아이콘 + 텍스트)를 표시하는 컴포저블입니다.
 */
@Composable
private fun StrictButtonContent() {
    Icon(
        imageVector = Icons.Default.CloudSync, 
        contentDescription = null, 
        modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
        text = stringResource(R.string.license_check_server_check),
        style = MaterialTheme.typography.labelLarge,
        textAlign = TextAlign.Center
    )
}
