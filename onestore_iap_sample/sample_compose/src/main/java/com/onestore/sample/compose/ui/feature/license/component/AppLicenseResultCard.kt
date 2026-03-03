package com.onestore.sample.compose.ui.feature.license.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.onestore.sample.compose.ui.component.AppCard
import com.onestore.sample.compose.ui.feature.license.model.AppLicenseDetailLine
import com.onestore.sample.compose.ui.feature.license.model.AppLicenseResultData
import com.onestore.sample.compose.ui.feature.license.model.AppLicenseStatus

/**
 * 앱 라이선스 검증 결과를 상세하게 보여주는 카드 컴포넌트입니다. (상태에 따라 색상 변경)
 *
 * @param data 카드에 표시할 데이터 (제목, 상세 내용, 상태 등)
 */
@Composable
fun AppLicenseResultCard(
    data: AppLicenseResultData
) {
    val (containerColor, contentColor, icon) = when (data.status) {
        AppLicenseStatus.SUCCESS -> Triple(
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer,
            Icons.Rounded.CheckCircle
        )
        AppLicenseStatus.ERROR -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            Icons.Rounded.Error
        )
        AppLicenseStatus.INFO -> Triple(
            MaterialTheme.colorScheme.surfaceContainerHigh,
            MaterialTheme.colorScheme.onSurface,
            Icons.Rounded.Info
        )
    }

    AppCard(
        modifier = Modifier.fillMaxWidth(),
        containerColor = containerColor,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = data.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            ResultDetailItem(
                line = data.firstLine,
                contentColor = contentColor
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 12.dp),
                color = contentColor.copy(alpha = 0.2f)
            )

            ResultDetailItem(
                line = data.secondLine,
                contentColor = contentColor
            )
        }
    }
}

/**
 * 결과 카드 내부의 상세 항목(라벨 + 값)을 표시하는 컴포저블입니다.
 *
 * @param line 표시할 라벨과 값 데이터
 * @param contentColor 텍스트 색상
 */
@Composable
private fun ResultDetailItem(
    line: AppLicenseDetailLine,
    contentColor: Color
) {
    Column {
        Text(
            text = line.label,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor.copy(alpha = 0.7f),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = line.value,
            style = MaterialTheme.typography.bodyMedium,
            color = contentColor,
            fontWeight = FontWeight.Medium
        )
    }
}
