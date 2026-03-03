package com.onestore.sample.compose.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 여러 아이템을 그룹화하여 보여주는 공통 컨테이너입니다.
 * 그림자가 없는 Flat한 Surface를 사용하여 배경색으로 영역을 구분합니다.
 *
 * @param modifier 추가적인 Modifier
 * @param horizontalPadding 좌우 패딩 (기본값 16dp)
 * @param content 내부에 표시할 내용
 */
@Composable
fun AppGroupedCard(
    modifier: Modifier = Modifier,
    horizontalPadding: Dp = 16.dp,
    content: @Composable () -> Unit
) {
    AppCard(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding),
        content = { content() }
    )
}
