package com.onestore.sample.compose.ui.feature.product.list.component

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.EventBusy
import androidx.compose.material.icons.rounded.NotificationImportant
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.gaa.sdk.iap.ProductDetail
import com.gaa.sdk.iap.PurchaseClient.ProductType
import com.gaa.sdk.iap.PurchaseData
import com.gaa.sdk.iap.PurchaseData.RecurringState
import com.onestore.sample.compose.R
import com.onestore.sample.compose.ui.component.AppStatusBadge
import com.onestore.sample.compose.ui.component.ProductInfoListItem

/**
 * 개별 상품 정보를 표시하는 리스트 아이템 컴포넌트입니다.
 * 내부적으로 ProductInfoListItem을 사용하여 통일된 스타일을 제공합니다.
 *
 * @param product 상품 상세 정보
 * @param purchaseItem 해당 상품의 구매 데이터 (있을 경우)
 * @param onClick 아이템 클릭 시 호출되는 콜백
 */
@Composable
fun ProductItemCard(
    product: ProductDetail,
    purchaseItem: PurchaseData?,
    onClick: () -> Unit
) {
    ProductInfoListItem(
        product = product,
        trailingContent = {
            if (purchaseItem != null) {
                ProductStatusInfo(
                    productType = product.type,
                    purchaseItem = purchaseItem
                )
            }
        },
        onClick = onClick
    )
}

/**
 * 상품의 현재 구매 상태를 아이콘으로 표시하는 보조 컴포저블입니다.
 * 원형 배지에 아이콘만 표시하여 공간을 절약하고 깔끔한 UI를 제공합니다.
 *
 * 표시 가능한 상태:
 * - 정상 구독/결제 중
 * - 해지 예약됨
 * - 처리 필요 (미승인)
 *
 * @param productType 상품 타입
 * @param purchaseItem 구매 데이터
 */
@Composable
private fun ProductStatusInfo(
    productType: String,
    purchaseItem: PurchaseData
) {
    // 상태에 따른 아이콘, 색상, 설명 결정
    data class StatusConfig(
        val icon: ImageVector,
        val containerColor: Color,
        val contentColor: Color,
        val description: String
    )
    
    val config = when (productType) {
        ProductType.AUTO, ProductType.SUBS -> {
            when {
                // 미승인 상태 - 처리 필요
                !purchaseItem.isAcknowledged -> {
                    StatusConfig(
                        icon = Icons.Rounded.NotificationImportant,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        description = stringResource(R.string.product_status_need_confirmation)
                    )
                }
                // 해지 예정
                purchaseItem.recurringState == RecurringState.CANCEL -> {
                    StatusConfig(
                        icon = Icons.Rounded.EventBusy,
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        description = if (productType == ProductType.AUTO) 
                            stringResource(R.string.product_status_auto_canceling)
                        else 
                            stringResource(R.string.product_status_subs_canceling)
                    )
                }
                // 정상 구독중
                else -> {
                    StatusConfig(
                        icon = Icons.Rounded.CheckCircle,
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        description = if (productType == ProductType.AUTO)
                            stringResource(R.string.product_status_auto_recurring)
                        else
                            stringResource(R.string.product_status_subscribing)
                    )
                }
            }
        }
        ProductType.INAPP -> {
            StatusConfig(
                icon = Icons.Rounded.NotificationImportant,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer,
                description = stringResource(R.string.product_status_need_confirmation)
            )
        }
        else -> return
    }

    AppStatusBadge(
        icon = config.icon,
        text = null,
        contentDescription = config.description,
        containerColor = config.containerColor,
        contentColor = config.contentColor
    )
}