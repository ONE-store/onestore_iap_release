package com.onestore.sample.compose.ui.feature.product.detail.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.gaa.sdk.iap.ProductDetail
import com.gaa.sdk.iap.PurchaseData
import com.onestore.sample.compose.R
import com.onestore.sample.compose.ui.component.AppSectionHeader
import com.onestore.sample.compose.ui.component.ProductInfoContent
import kotlin.math.absoluteValue

/**
 * 사용자가 구독 중인 다른 상품들을 가로 페이징 형태로 보여주는 컴포넌트입니다.
 * 구분선과 인디케이터를 제거하여 심플하게 구성했습니다.
 *
 * @param otherPurchaseList 사용자가 구매한 다른 구독 상품 목록
 * @param getProductDetail 상품 ID로 ProductDetail을 조회하는 함수
 * @param onProductClick 상품 클릭 시 호출되는 콜백
 */
@Composable
fun ProductSubscriptionPager(
    otherPurchaseList: List<PurchaseData>,
    getProductDetail: (String) -> ProductDetail?,
    onProductClick: (String) -> Unit
) {
    if (otherPurchaseList.isNotEmpty()) {
        // 섹션 헤더 적용 (공통 컴포넌트 사용)
        AppSectionHeader(
            title = stringResource(R.string.product_detail_subscription_list),
            icon = Icons.Outlined.Autorenew
        )

        val pagerState = rememberPagerState(
            pageCount = { 
                otherPurchaseList.count() 
            }
        )

        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            pageSpacing = 12.dp
        ) { page ->
            val purchaseItem = otherPurchaseList[page]
            val productData = getProductDetail(purchaseItem.productId)

            if (productData != null) {
                Surface(
                    onClick = { 
                        onProductClick(productData.productId) 
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            val pageOffset = (
                                    (pagerState.currentPage - page) + pagerState
                                        .currentPageOffsetFraction
                                    ).absoluteValue

                            alpha = lerp(
                                start = 0.5f,
                                stop = 1f,
                                fraction = 1f - pageOffset.coerceIn(0f, 1f)
                            )
                        },
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shadowElevation = 0.dp,
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = productData.title,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // 공통 콘텐츠 컴포넌트 사용 (아이콘, 콤마 포맷팅 등이 자동으로 적용됨)
                        ProductInfoContent(
                            productId = purchaseItem.productId,
                            price = productData.price,
                            priceCurrencyCode = productData.priceCurrencyCode,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }
        }
    }
}