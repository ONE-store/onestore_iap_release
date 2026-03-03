package com.onestore.sample.compose.ui.feature.product.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gaa.sdk.iap.ProductDetail
import com.gaa.sdk.iap.PurchaseClient.ProductType
import com.gaa.sdk.iap.PurchaseData
import com.gaa.sdk.iap.PurchaseData.RecurringState
import com.onestore.sample.compose.R
import com.onestore.sample.compose.ui.theme.LocalExtendedColors
import com.onestore.sample.compose.util.formatPrice

/**
 * 상품의 상세 정보와 현재 구매 상태를 표시하는 카드 컴포넌트입니다.
 * 하단에 액션 버튼을 포함할 수 있는 슬롯을 제공합니다.
 *
 * @param productDetail 상품 상세 정보
 * @param purchaseData 구매 데이터 (구매한 경우)
 * @param actions 카드 하단에 표시할 버튼 등의 액션 컴포저블
 */
@Composable
fun ProductDetailInfoCard(
    productDetail: ProductDetail,
    purchaseData: PurchaseData?,
    actions: @Composable () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainerLow,
        shape = MaterialTheme.shapes.large,
        shadowElevation = 0.dp, // Flat 스타일 적용
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // 헤더 (제목)
            Text(
                text = productDetail.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(24.dp))

            // 상세 정보 섹션
            DetailRow(
                label = stringResource(R.string.product_detail_id), 
                value = productDetail.productId,
                leadingIcon = Icons.Default.Tag
            )
            
            val typeString = when(productDetail.type) {
                ProductType.AUTO -> stringResource(R.string.product_type_monthly)
                ProductType.INAPP -> stringResource(R.string.product_type_inapp)
                ProductType.SUBS -> stringResource(R.string.product_type_subscription)
                else -> stringResource(R.string.product_detail_unknown)
            }
            DetailRow(
                label = stringResource(R.string.product_detail_type), 
                value = typeString,
                leadingIcon = Icons.Default.Category
            )
            
            DetailRow(
                label = stringResource(R.string.product_detail_price), 
                value = formatPrice(productDetail.price, productDetail.priceCurrencyCode),
                valueColor = MaterialTheme.colorScheme.primary, 
                isBold = true,
                leadingIcon = Icons.Default.AttachMoney
            )
            DetailRow(
                label = stringResource(R.string.product_detail_currency_code), 
                value = productDetail.priceCurrencyCode,
                leadingIcon = Icons.Default.Money
            )

            // 구분선 및 상태 정보
            if (productDetail.type == ProductType.AUTO || productDetail.type == ProductType.SUBS) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                
                val statusText = if (purchaseData != null) {
                    purchaseData.billingKey
                    when(purchaseData.recurringState) {
                        RecurringState.CANCEL -> if (productDetail.type == ProductType.AUTO) stringResource(R.string.product_status_auto_canceling) else "구독 해지 예약중"
                        RecurringState.RECURRING -> if (productDetail.type == ProductType.AUTO) stringResource(R.string.product_status_auto_recurring) else "구독 중"
                        else -> stringResource(R.string.product_detail_unpurchased)
                    }
                } else {
                    stringResource(R.string.product_detail_unpurchased)
                }
                
                val isActive = purchaseData?.recurringState == RecurringState.RECURRING
                
                DetailRow(
                    label = stringResource(R.string.product_detail_status),
                    value = statusText,
                    valueColor = if (isActive) MaterialTheme.colorScheme.primary else LocalExtendedColors.current.error,
                    isBold = true,
                    leadingIcon = Icons.Default.Info
                )

                if(purchaseData != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow(
                        label = stringResource(R.string.product_detail_acknowledged),
                        value = purchaseData.isAcknowledged.toString(),
                        valueColor = if (purchaseData.isAcknowledged) MaterialTheme.colorScheme.onSurface else LocalExtendedColors.current.error,
                        leadingIcon = Icons.Default.CheckCircle
                    )
                }
            }
            
            // 구독 기간 정보 (구독형일 때만)
            if (productDetail.type == ProductType.SUBS) {
                HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                DetailRow(
                    label = stringResource(R.string.product_detail_subscription_period), 
                    value = productDetail.subscriptionPeriod,
                    leadingIcon = Icons.Default.Schedule
                )
                DetailRow(
                    label = stringResource(R.string.product_detail_subscription_unit), 
                    value = productDetail.subscriptionPeriodUnitCode,
                    leadingIcon = Icons.Default.CalendarMonth
                )
            }

            // 하단 액션 버튼 영역
            Spacer(modifier = Modifier.height(24.dp))
            actions()
        }
    }
}

/**
 * 상품 상세 정보의 개별 항목을 표시하는 행 컴포넌트입니다.
 * 
 * 레이블과 값을 좌우로 배치하며, 선택적으로 아이콘을 추가할 수 있습니다.
 *
 * @param label 항목의 레이블 (예: "상품 ID", "가격" 등)
 * @param value 항목의 값 (null이면 빈 문자열 표시)
 * @param valueColor 값의 텍스트 색상 (기본값: onSurface)
 * @param isBold 값을 굵게 표시할지 여부 (기본값: false)
 * @param leadingIcon 레이블 앞에 표시할 아이콘 (기본값: null)
 */
@Composable
private fun DetailRow(
    label: String,
    value: String?,
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    isBold: Boolean = false,
    leadingIcon: ImageVector? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(end = 3.dp)
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value ?: "",
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            textAlign = TextAlign.End
        )
    }
}