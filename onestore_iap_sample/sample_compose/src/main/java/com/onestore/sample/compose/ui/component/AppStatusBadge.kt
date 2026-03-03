package com.onestore.sample.compose.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * 상태 정보나 액션 버튼을 표시하기 위한 공통 배지 컴포넌트입니다.
 * text가 null이면 원형 아이콘 배지로, 값이 있으면 텍스트 + 아이콘 배지로 표시됩니다.
 *
 * @param icon 표시할 아이콘
 * @param text 표시할 텍스트 (null이면 아이콘만 표시)
 * @param contentDescription 접근성을 위한 설명 (text가 null일 때 특히 중요)
 * @param containerColor 배경 색상
 * @param contentColor 텍스트 및 아이콘 색상
 * @param onClick 클릭 시 동작 (null일 경우 클릭 불가능한 상태 표시용)
 */
@Composable
fun AppStatusBadge(
    icon: ImageVector,
    text: String? = null,
    contentDescription: String? = null,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    onClick: (() -> Unit)? = null
) {
    // text가 null이면 원형 아이콘 배지, 아니면 긴 배지
    if (text == null) {
        // 원형 아이콘 배지
        val modifier = Modifier.size(36.dp)
        
        if (onClick != null) {
            Surface(
                onClick = onClick,
                modifier = modifier,
                shape = CircleShape,
                color = containerColor,
                contentColor = contentColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else {
            Surface(
                modifier = modifier,
                shape = CircleShape,
                color = containerColor,
                contentColor = contentColor
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = contentDescription,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    } else {
        // 긴 배지 (텍스트 + 아이콘)
        val commonModifier = Modifier

        if (onClick != null) {
            Surface(
                onClick = onClick,
                modifier = commonModifier,
                shape = MaterialTheme.shapes.medium,
                color = containerColor,
                contentColor = contentColor
            ) {
                BadgeContent(text, icon)
            }
        } else {
            Surface(
                modifier = commonModifier,
                shape = MaterialTheme.shapes.medium,
                color = containerColor,
                contentColor = contentColor
            ) {
                BadgeContent(text, icon)
            }
        }
    }
}

/**
 * 배지의 내용물을 표시하는 내부 컴포넌트입니다.
 * 
 * 아이콘과 텍스트를 가로로 배치하여 배지의 콘텐츠를 구성합니다.
 *
 * @param text 표시할 텍스트
 * @param icon 표시할 아이콘
 */
@Composable
private fun BadgeContent(
    text: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold
        )
    }
}

