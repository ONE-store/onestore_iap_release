package com.onestore.sample.compose.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.gaa.sdk.iap.ProductDetail
import com.onestore.sample.compose.util.formatPrice

/**
 * 상품 정보(ID, 가격)를 표시하는 핵심 콘텐츠 영역입니다.
 * 리스트 아이템이나 카드 형태 등 다양한 곳에서 공통으로 사용됩니다.
 */
@Composable
fun ProductInfoContent(
    productId: String,
    price: String? = null,
    priceCurrencyCode: String? = null,
    contentColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    Column {
        // ID 정보 행
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Rounded.Key,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = contentColor.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = productId,
                style = MaterialTheme.typography.bodyMedium,
                color = contentColor
            )
        }

        // 가격 정보 행 (가격 정보가 있을 때만 표시)
        if (!price.isNullOrBlank()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Rounded.CreditCard,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = contentColor.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = formatPrice(price, priceCurrencyCode),
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor
                )
            }
        }
    }
}

/**
 * 상품 정보를 리스트 형태로 표시하는 공통 컴포넌트입니다.
 */
@Composable
fun ProductInfoListItem(
    title: String,
    productId: String,
    price: String? = null,
    priceCurrencyCode: String? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    AppListItem(
        headlineText = title,
        supportingContent = {
            ProductInfoContent(
                productId = productId,
                price = price,
                priceCurrencyCode = priceCurrencyCode
            )
        },
        trailingContent = trailingContent,
        onClick = onClick,
        headlineStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
    )
}

/**
 * ProductDetail 객체를 직접 받는 편의 함수
 */
@Composable
fun ProductInfoListItem(
    product: ProductDetail,
    trailingContent: @Composable (() -> Unit)? = null,
    onClick: () -> Unit
) {
    ProductInfoListItem(
        title = product.title,
        productId = product.productId,
        price = product.price,
        priceCurrencyCode = product.priceCurrencyCode,
        trailingContent = trailingContent,
        onClick = onClick
    )
}
