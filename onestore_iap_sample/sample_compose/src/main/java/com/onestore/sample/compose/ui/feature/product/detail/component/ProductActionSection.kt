package com.onestore.sample.compose.ui.feature.product.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Slider
import androidx.compose.material.SliderDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gaa.sdk.iap.PurchaseClient.ProductType
import com.onestore.sample.compose.R
import com.onestore.sample.compose.util.formatPrice

/**
 * 상품 구매, 구독 관리, 업그레이드/다운그레이드 등의 액션 버튼과
 * 인앱 상품의 경우 수량 조절 슬라이더를 포함하는 섹션입니다.
 */
@Composable
fun ProductActionSection(
    productType: String,
    isPurchased: Boolean,
    purchaseCount: Float,
    onPurchaseCountChange: (Float) -> Unit,
    onPurchaseClick: () -> Unit,
    onManageRecurringClick: () -> Unit,
    onManageSubscriptionClick: () -> Unit,
    onUpgradeClick: () -> Unit,
    isUpgradeAvailable: Boolean,
    modifier: Modifier = Modifier,
    price: String? = null,
    priceCurrencyCode: String? = null
) {
    Column(modifier = modifier) {
        // 관리형 상품 구매 수량 조절
        if (productType == ProductType.INAPP && !isPurchased) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .size(16.dp)
                        .padding(end = 1.dp)
                )
                Text(
                    text = stringResource(R.string.product_detail_purchase_count, purchaseCount.toInt()),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Slider(
                value = purchaseCount,
                onValueChange = onPurchaseCountChange,
                valueRange = 1f..10f,
                steps = 9,
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // 액션 버튼 행
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (!isPurchased) {
                Button(
                    onClick = onPurchaseClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    val buttonText = when (productType) {
                        ProductType.AUTO -> stringResource(R.string.product_detail_purchase_monthly)
                        ProductType.INAPP -> stringResource(R.string.product_detail_purchase_inapp)
                        ProductType.SUBS -> stringResource(R.string.product_detail_purchase_subscription)
                        else -> stringResource(R.string.product_detail_purchase_default)
                    }
                    
                    // 관리형 상품일 경우 최종 가격 계산 및 표시
                    val finalPriceText = if (productType == ProductType.INAPP && !price.isNullOrBlank()) {
                        try {
                            val cleanPrice = price.replace(",", "").filter { it.isDigit() || it == '.' }.trim()
                            val unitPrice = cleanPrice.toDoubleOrNull() ?: 0.0
                            val totalPrice = unitPrice * purchaseCount.toInt()
                            val formattedPrice = formatPrice(totalPrice.toString(), priceCurrencyCode)
                            " · $formattedPrice"
                        } catch (e: Exception) {
                            ""
                        }
                    } else {
                        ""
                    }
                    
                    Text(
                        text = buttonText + finalPriceText,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            } else {
                if (productType == ProductType.AUTO) {
                    Button(
                        onClick = onManageRecurringClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.product_detail_manage_monthly),
                            textAlign = TextAlign.Center
                        )
                    }
                } else if (productType == ProductType.SUBS) {
                    Button(
                        onClick = onManageSubscriptionClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = stringResource(R.string.product_detail_manage_subscription),
                            textAlign = TextAlign.Center
                        )
                    }

                    if (isUpgradeAvailable) {
                        FilledTonalButton(
                            onClick = onUpgradeClick,
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            shape = MaterialTheme.shapes.medium
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sync,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = stringResource(R.string.product_detail_upgrade_downgrade),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
