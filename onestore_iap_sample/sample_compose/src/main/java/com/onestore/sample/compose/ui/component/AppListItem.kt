package com.onestore.sample.compose.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 앱 전반에서 공통으로 사용되는 리스트 아이템입니다.
 * Row 기반 커스텀 레이아웃으로 일관된 스타일(높이, 색상, 타이포그래피)과
 * 확실한 수직 중앙 정렬을 제공합니다.
 *
 * @param headlineText 주 제목 텍스트
 * @param modifier 아이템 Modifier
 * @param supportingText 부가 설명 텍스트 (단순 문자열 사용 시 편리함)
 * @param supportingContent 부가 설명 영역에 표시할 커스텀 컴포저블 (아이콘 등이 포함될 때 사용)
 * @param leadingContent 왼쪽에 표시할 아이콘이나 컴포저블 (옵션)
 * @param trailingContent 오른쪽에 표시할 컴포저블 (옵션)
 * @param onClick 아이템 클릭 시 호출되는 콜백 (null이면 클릭 불가)
 * @param minHeight 아이템의 최소 높이 (기본 72dp)
 * @param headlineStyle 제목 텍스트 스타일 (기본 titleMedium)
 * @param supportingTextColor 부가 설명 텍스트 색상 (supportingText 사용 시 적용)
 */
@Composable
fun AppListItem(
    headlineText: String,
    modifier: Modifier = Modifier,
    supportingText: String? = null,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    minHeight: Dp = 72.dp,
    headlineStyle: TextStyle = MaterialTheme.typography.titleMedium,
    supportingTextColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = minHeight)
            .then(
                if (onClick != null) Modifier.clickable(onClick = onClick) 
                else Modifier
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Leading content
        if (leadingContent != null) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                leadingContent()
            }
        }
        
        // Headline and supporting content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = headlineText,
                style = headlineStyle,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Supporting content
            if (supportingContent != null) {
                supportingContent()
            } else if (supportingText != null) {
                Text(
                    text = supportingText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = supportingTextColor
                )
            }
        }
        
        // Trailing content
        if (trailingContent != null) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                trailingContent()
            }
        }
    }
}