package com.onestore.sample.compose.ui.feature.setting.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.onestore.sample.compose.ui.component.AppListItem

/**
 * 드롭다운 메뉴 선택이 가능한 설정 항목
 * 내부적으로 AppListItem을 사용합니다.
 *
 * @param headlineText 주 제목 텍스트
 * @param supportingText 부가 설명 텍스트 (옵션)
 * @param leadingIcon 왼쪽에 표시할 아이콘
 * @param currentValue 현재 선택된 값
 * @param options 선택 가능한 옵션 리스트 (라벨, 값, 아이콘)
 * @param onOptionSelected 옵션 선택 시 호출되는 콜백
 */
@Composable
fun <T> SettingsDropdownItem(
    headlineText: String,
    supportingText: String? = null,
    leadingIcon: ImageVector,
    currentValue: T,
    options: List<Triple<String, T, ImageVector>>,
    onOptionSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedOption = options.find { it.second == currentValue }

    AppListItem(
        headlineText = headlineText,
        supportingText = supportingText,
        leadingContent = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        trailingContent = {
            Box {
                Surface(
                    onClick = { expanded = true },
                    color = Color.Transparent
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = selectedOption?.first ?: "",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { (label, value, icon) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            leadingIcon = { Icon(icon, contentDescription = null) },
                            onClick = {
                                onOptionSelected(value)
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        onClick = { expanded = true },
        minHeight = 0.dp
    )
}
