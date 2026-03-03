package com.onestore.sample.compose.ui.feature.product.detail

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.gaa.sdk.iap.PurchaseClient.ProductType
import com.onestore.sample.compose.util.printLog
import com.onestore.sample.compose.ui.feature.product.detail.component.ProductActionSection
import com.onestore.sample.compose.ui.feature.product.detail.component.ProductDetailInfoCard
import com.onestore.sample.compose.ui.feature.product.detail.component.ProductSubscriptionPager
import com.onestore.sample.compose.ui.feature.product.detail.component.UpgradeBottomSheet
import com.onestore.sample.compose.ui.navigation.AppRootRoute
import com.onestore.sample.compose.ui.utils.LocalViewModelFactory
import com.onestore.sample.compose.R
import kotlinx.coroutines.launch

private const val TAG = "PurchaseDetailScreen"

/**
 * 상품 상세 정보를 보여주고 구매/구독 관리를 수행하는 화면입니다.
 *
 * @param viewModel 상품 상세 로직을 처리하는 ViewModel
 * @param rootNavController 화면 이동을 위한 네비게이션 컨트롤러
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: ProductDetailViewModel = viewModel(factory = LocalViewModelFactory.current),
    rootNavController: NavHostController,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val purchaseManagerUiState by viewModel.purchaseManagerUiState.collectAsStateWithLifecycle()

    val productDetail = purchaseManagerUiState.getProductDetail(uiState.productId)
    
    if (productDetail == null) {
        AlertDialog(
            onDismissRequest = { rootNavController.popBackStack() },
            title = { Text(stringResource(R.string.alert_title_notification)) },
            text = { Text(stringResource(R.string.product_empty_message)) },
            confirmButton = {
                TextButton(
                    onClick = { rootNavController.popBackStack() }
                ) {
                    Text(stringResource(R.string.confirm))
                }
            }
        )
        Box(modifier = Modifier.fillMaxSize())
        return
    }

    val context = LocalContext.current
    val activity = context as Activity

    val purchaseData = purchaseManagerUiState.getPurchaseData(productDetail.productId)
    val otherPurchaseList = purchaseManagerUiState.getOtherPurchases(productDetail.productId)
    val availableSubscriptionUpgrades = purchaseManagerUiState.getAvailableSubscriptionUpgrades(productDetail.productId)

    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var purchaseCount by remember { mutableFloatStateOf(1f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp), // 전체 여백
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            ProductDetailInfoCard(
                productDetail = productDetail,
                purchaseData = purchaseData,
                actions = {
                    ProductActionSection(
                        productType = productDetail.type,
                        isPurchased = purchaseData != null,
                        purchaseCount = purchaseCount,
                        onPurchaseCountChange = { purchaseCount = it },
                        onPurchaseClick = {
                            viewModel.purchaseRequest(
                                activity = activity,
                                productId = productDetail.productId,
                                type = productDetail.type,
                                count = purchaseCount.toInt()
                            ) {
                                printLog(TAG, "purchaseRequest onPurchaseComplete")
                                if (productDetail.type == ProductType.INAPP) {
                                    rootNavController.popBackStack()
                                }
                            }
                        },
                        onManageRecurringClick = {
                            purchaseData?.let { viewModel.manageRecurring(it) }
                        },
                        onManageSubscriptionClick = {
                            purchaseData?.let { viewModel.launchManageSubscription(activity, it) }
                        },
                        onUpgradeClick = {
                            showBottomSheet = true
                        },
                        isUpgradeAvailable = purchaseData != null && purchaseData.isAcknowledged && availableSubscriptionUpgrades.isNotEmpty(),
                        price = productDetail.price,
                        priceCurrencyCode = productDetail.priceCurrencyCode
                    )
                }
            )
        }

        Spacer(Modifier.weight(1f))

        ProductSubscriptionPager(
            otherPurchaseList = otherPurchaseList,
            getProductDetail = { id ->
                purchaseManagerUiState.getProductDetail(id)
            },
            onProductClick = { id ->
                rootNavController.navigate(
                    AppRootRoute.PurchaseDetail(productId = id)
                )
            }
        )
    }

    // 업그레이드/다운그레이드 바텀 시트
    UpgradeBottomSheet(
        showBottomSheet = showBottomSheet,
        sheetState = sheetState,
        availableUpgrades = availableSubscriptionUpgrades,
        onDismissRequest = {
            showBottomSheet = false
        },
        onProductSelected = { item ->
            coroutineScope.launch {
                sheetState.hide()
            }.invokeOnCompletion {
                if (!sheetState.isVisible) {
                    showBottomSheet = false
                }
            }
            purchaseData?.let {
                viewModel.updateSubscription(activity, it, item) {
                    printLog(TAG, "launchPurchaseFlow onPurchaseComplete")
                }
            }
        }
    )
}