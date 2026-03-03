package com.onestore.sample.compose.ui.feature.purchase

import PurchaseWaitingItem
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Autorenew
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gaa.sdk.iap.PurchaseClient.ProductType
import com.onestore.sample.compose.ui.component.AppGroupedCard
import com.onestore.sample.compose.ui.component.AppSectionHeader
import com.onestore.sample.compose.ui.feature.purchase.component.PurchaseEmptyView
import com.onestore.sample.compose.ui.utils.LocalViewModelFactory
import com.onestore.sample.compose.util.runOnMainOnce
import com.onestore.sample.compose.R

/**
 * 구매 대기 목록(미소비, 미승인 상품)을 보여주고 처리할 수 있는 화면입니다.
 * 공통 컴포넌트(AppSectionHeader, AppGroupedCard)를 사용하여 일관된 UI를 제공합니다.
 *
 * @param viewModel 구매 대기 목록 데이터를 관리하는 ViewModel
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseWaitingListScreen(
    viewModel: PurchaseWaitingListViewModel = viewModel(factory = LocalViewModelFactory.current),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val purchaseManagerUiState by viewModel.purchaseManagerUiState.collectAsStateWithLifecycle()
    val pullToRefreshState = rememberPullToRefreshState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        PullToRefreshBox(
            state = pullToRefreshState,
            isRefreshing = uiState.isRefreshing,
            onRefresh = {
                viewModel.setRefreshing(true)
                viewModel.loadData()
                runOnMainOnce(500) { viewModel.setRefreshing(false) }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (purchaseManagerUiState.unacknowledgedPurchases.isEmpty()) {
                    item(key = "empty_view") {
                        PurchaseEmptyView(
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                } else {
                    val typeList = listOf(
                        Triple(ProductType.AUTO, R.string.product_type_monthly, purchaseManagerUiState.unacknowledgedAutoList),
                        Triple(ProductType.INAPP, R.string.product_type_inapp, purchaseManagerUiState.purchasesInAppList),
                        Triple(ProductType.SUBS, R.string.product_type_subscription, purchaseManagerUiState.unacknowledgedSubsList)
                    )

                    typeList.forEach { (type, titleRes, list) ->
                        if (list.isEmpty()) return@forEach

                        val icon = when (type) {
                            ProductType.AUTO -> Icons.Outlined.DateRange
                            ProductType.INAPP -> Icons.Outlined.ShoppingBag
                            ProductType.SUBS -> Icons.Outlined.Autorenew
                            else -> null
                        }
                        
                        // 섹션 헤더 (공통 컴포넌트)
                        item(key = "header_$type") {
                            AppSectionHeader(
                                title = stringResource(titleRes),
                                icon = icon
                            )
                        }

                        // 그룹화된 카드 (공통 컴포넌트)
                        item(key = "card_$type") {
                            AppGroupedCard {
                                list.toList().forEachIndexed { index, purchase ->
                                    val product = purchaseManagerUiState.productDetails.firstOrNull { it.productId == purchase.productId }

                                    PurchaseWaitingItem(
                                        purchase = purchase,
                                        product = product,
                                        productType = type,
                                        onHandlePurchase = {
                                            viewModel.handlePurchase(type, purchase)
                                        }
                                    )

                                    // 아이템 사이의 구분선
                                    if (index < list.size - 1) {
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
                }
            }
        }
    }
}
