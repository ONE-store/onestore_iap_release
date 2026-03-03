package com.onestore.sample.compose.ui.feature.product.list

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gaa.sdk.iap.PurchaseClient.ProductType
import com.onestore.sample.compose.ui.component.AppGroupedCard
import com.onestore.sample.compose.ui.component.AppSectionHeader
import com.onestore.sample.compose.ui.feature.product.list.component.ProductEmptyView
import com.onestore.sample.compose.ui.feature.product.list.component.ProductItemCard
import com.onestore.sample.compose.ui.navigation.AppRootRoute
import com.onestore.sample.compose.ui.utils.LocalViewModelFactory
import com.onestore.sample.compose.util.runOnMainOnce
import com.onestore.sample.compose.R

/**
 * 인앱 상품 목록을 보여주는 메인 화면입니다.
 * 상품들을 하나의 카드 안에 그룹화하여 보여주는 리스트 스타일을 적용했습니다.
 * 공통 컴포넌트(AppSectionHeader, AppGroupedCard)를 사용하여 일관된 UI를 제공합니다.
 *
 * @param viewModel 상품 목록 데이터를 관리하는 ViewModel
 * @param rootNavController 상위 네비게이션 컨트롤러
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = viewModel(factory = LocalViewModelFactory.current),
    rootNavController: NavHostController
) {
    val context = LocalContext.current
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
                runOnMainOnce(500) {
                    viewModel.setRefreshing(false)
                }
            },
            modifier = Modifier.fillMaxSize()
        ) {
            val productDetails = purchaseManagerUiState.productDetails
            val purchaseList = purchaseManagerUiState.allPurchases

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 10.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (productDetails.isEmpty()) {
                    item(key = "empty_view") {
                        ProductEmptyView(
                            modifier = Modifier.fillParentMaxSize()
                        )
                    }
                } else {
                    val categories = listOf(
                        Triple(ProductType.AUTO, R.string.product_category_monthly, Icons.Outlined.DateRange),
                        Triple(ProductType.INAPP, R.string.product_category_inapp, Icons.Outlined.ShoppingBag),
                        Triple(ProductType.SUBS, R.string.product_category_subscription, Icons.Outlined.Autorenew)
                    )

                    categories.forEach { (type, title, icon) ->
                        val filteredProducts = productDetails
                            .filter { it.type == type }
                            .sortedWith(
                                compareBy(
                                    { product -> purchaseList.none { it.productId == product.productId } },
                                    { product -> purchaseList.find { it.productId == product.productId }?.isAcknowledged }
                                )
                            )

                        if (filteredProducts.isNotEmpty()) {
                            // 섹션 헤더 (공통 컴포넌트)
                            item(key = "header_$type") {
                                AppSectionHeader(
                                    title = stringResource(title),
                                    icon = icon
                                )
                            }

                            // 그룹화된 카드 (공통 컴포넌트)
                            item(key = "card_$type") {
                                AppGroupedCard {
                                    filteredProducts.forEachIndexed { index, product ->
                                        val purchaseItem = purchaseList.find { it.productId == product.productId }

                                        ProductItemCard(
                                            product = product,
                                            purchaseItem = purchaseItem,
                                            onClick = {
                                                if (product.type == ProductType.INAPP && purchaseItem != null) {
                                                    Toast.makeText(
                                                        context,
                                                        context.getString(R.string.toast_check_purchase_waiting_list),
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                } else {
                                                    rootNavController.navigate(
                                                        AppRootRoute.PurchaseDetail(productId = product.productId)
                                                    )
                                                }
                                            }
                                        )

                                        // 아이템 사이의 구분선 (마지막 아이템 뒤에는 생략)
                                        if (index < filteredProducts.lastIndex) {
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
}