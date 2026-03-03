package com.onestore.sample.compose.ui.feature.product.list

import androidx.lifecycle.ViewModel
import com.onestore.sample.compose.manager.iap.PurchaseManager
import com.onestore.sample.compose.manager.iap.PurchaseManagerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 상품 목록 화면의 UI 상태 및 비즈니스 로직을 관리하는 ViewModel
 *
 * @property purchaseManager 인앱 결제 및 라이선스 관리 매니저
 */
class ProductListViewModel(
    private val purchaseManager: PurchaseManager
): ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState())
    
    /** 상품 목록 화면의 UI 상태 */
    val uiState: StateFlow<ProductListUiState> = _uiState.asStateFlow()

    /** PurchaseManager의 UI 상태 */
    val purchaseManagerUiState: StateFlow<PurchaseManagerUiState> = purchaseManager.uiState

    /**
     * 상품 목록 및 구매 내역 데이터를 로드합니다
     */
    fun loadData() {
        purchaseManager.queryPurchasesAndProductDetailsAsync()
    }

    /**
     * 새로고침 상태를 설정합니다
     *
     * @param isRefreshing 새로고침 중 여부
     */
    fun setRefreshing(isRefreshing: Boolean) {
        _uiState.update { it.copy(isRefreshing = isRefreshing) }
    }
}

/**
 * 상품 목록 화면의 UI 상태
 *
 * @property isRefreshing 새로고침 중 여부
 */
data class ProductListUiState(
    val isRefreshing: Boolean = false
)