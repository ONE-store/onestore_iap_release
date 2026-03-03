package com.onestore.sample.compose.ui.feature.setting.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.onestore.sample.compose.ui.component.AppListItem

/**
 * 클릭 가능한 설정 항목 (화살표 아이콘 자동 포함)
 * 내부적으로 AppListItem을 사용합니다.
 *
 * @param headlineText 주 제목 텍스트
 * @param supportingText 부가 설명 텍스트 (옵션)
 * @param leadingContent 왼쪽에 표시할 컴포저블 (옵션)
 * @param onClick 항목 클릭 시 호출되는 콜백
 */
@Composable
fun SettingsClickableItem(
    headlineText: String,
    supportingText: String? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    AppListItem(
        headlineText = headlineText,
        supportingText = supportingText,
        leadingContent = leadingContent,
        trailingContent = {
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        onClick = onClick,
        minHeight = 0.dp
    )
}
