package com.onestore.sample.compose.ui.feature.product.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gaa.sdk.iap.ProductDetail
import com.onestore.sample.compose.R
import com.onestore.sample.compose.ui.component.AppGroupedCard
import com.onestore.sample.compose.ui.component.AppListItem
import com.onestore.sample.compose.ui.component.ProductInfoListItem

private val OPTIONS = listOf(
    "IMMEDIATE_WITH_TIME_PRORATION",
    "IMMEDIATE_AND_CHARGE_PRORATED_PRICE",
    "IMMEDIATE_WITHOUT_PRORATION",
    "DEFERRED"
)

/**
 * 구독 상품 업그레이드/다운그레이드를 선택하는 BottomSheet 컴포넌트입니다.
 * 공통 컴포넌트(AppGroupedCard, ProductInfoListItem)를 사용하여 일관된 UI 스타일을 유지합니다.
 *
 * @param showBottomSheet 시트 표시 여부
 * @param sheetState BottomSheet 상태
 * @param availableUpgrades 업그레이드 가능한 상품 목록
 * @param onDismissRequest 시트 닫기 요청 콜백
 * @param onProductSelected 상품 선택 시 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpgradeBottomSheet(
    showBottomSheet: Boolean,
    sheetState: SheetState,
    availableUpgrades: List<ProductDetail>,
    onDismissRequest: () -> Unit,
    onProductSelected: (ProductDetail) -> Unit
) {
    if (showBottomSheet) {
        var expanded by remember { mutableStateOf(false) }

        var selectedIndex by remember { mutableIntStateOf(0) }

        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = onDismissRequest,
            containerColor = MaterialTheme.colorScheme.surface // 시트 배경색 명시
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    text = stringResource(R.string.product_detail_upgrade_downgrade),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 상품 목록 (AppGroupedCard 사용)
                // 시트 배경(Surface)과 구분을 위해 AppGroupedCard는 surfaceContainerLow 유지
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false), // 내용만큼만 차지하되 최대 높이 제한
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        AppGroupedCard(
                            horizontalPadding = 0.dp
                        ) {
                            availableUpgrades.forEachIndexed { index, item ->
                                ProductInfoListItem(
                                    product = item,
                                    trailingContent = {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                            contentDescription = stringResource(R.string.action_select),
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    },
                                    onClick = {
                                        onProductSelected(item)
                                    }
                                )

                                if (index < availableUpgrades.lastIndex) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(horizontal = 16.dp),
                                        thickness = 0.5.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(10.dp))

                AppGroupedCard(
                    horizontalPadding = 0.dp
                ) {
                    AppListItem(
                        headlineText = stringResource(R.string.product_detail_proration_mode),
                        supportingContent = {
                            Text(
                                text = OPTIONS.getOrNull(selectedIndex) ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Tune,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        trailingContent = {
                            Box {
                                Icon(
                                    imageVector = Icons.Filled.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    modifier = Modifier
                                        .background(MaterialTheme.colorScheme.surfaceContainerHighest)
                                ) {
                                    OPTIONS.forEachIndexed { index, option ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    if (selectedIndex == index) {
                                                        Icon(
                                                            imageVector = Icons.Filled.Check,
                                                            contentDescription = null,
                                                            modifier = Modifier.size(20.dp),
                                                            tint = MaterialTheme.colorScheme.primary
                                                        )
                                                        Spacer(modifier = Modifier.width(8.dp))
                                                    } else {
                                                        Spacer(modifier = Modifier.width(28.dp))
                                                    }

                                                    Text(
                                                        text = option,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                }
                                            },
                                            onClick = {
                                                selectedIndex = index
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        },
                        onClick = { expanded = true }
                    )
                }
                
                Spacer(modifier = Modifier.height(10.dp)) // 하단 여백
            }
        }
    }
}