package com.onestore.sample.compose.ui.feature.setting.component

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.onestore.sample.compose.ui.component.AppListItem

/**
 * 스위치(Switch)가 포함된 설정 항목
 * 내부적으로 AppListItem을 사용합니다.
 *
 * @param headlineText 주 제목 텍스트
 * @param supportingText 부가 설명 텍스트 (옵션)
 * @param leadingIcon 왼쪽에 표시할 아이콘 (옵션)
 * @param checked 스위치 ON/OFF 상태
 * @param onCheckedChange 스위치 상태 변경 시 호출되는 콜백
 */
@Composable
fun SettingsSwitchItem(
    headlineText: String,
    supportingText: String? = null,
    leadingIcon: ImageVector? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    AppListItem(
        headlineText = headlineText,
        supportingText = supportingText,
        leadingContent = if (leadingIcon != null) {
            {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        } else null,
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange
            )
        },
        minHeight = 0.dp
    )
}