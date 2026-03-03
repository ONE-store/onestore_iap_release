package com.onestore.sample.compose.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * 앱 전체에서 일관되게 사용되는 카드 스타일 컴포넌트입니다.
 * 둥근 모서리와 그림자가 없는(Flat) Surface를 기반으로 합니다.
 *
 * @param modifier 추가적인 Modifier
 * @param shape 카드 모양 (기본값: Large shape)
 * @param containerColor 컨테이너 배경색 (기본값: surfaceContainerLow)
 * @param contentColor 콘텐츠 색상 (기본값: containerColor에 맞는 색상)
 * @param content 내부에 표시할 내용 (Column Scope)
 */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    shape: Shape = MaterialTheme.shapes.large,
    containerColor: Color = MaterialTheme.colorScheme.surfaceContainerLow,
    contentColor: Color = contentColorFor(containerColor),
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        shadowElevation = 0.dp // Flat 스타일 강제
    ) {
        Column {
            content()
        }
    }
}
