package com.onestore.sample.compose.ui.feature.purchase

import androidx.lifecycle.ViewModel
import com.gaa.sdk.iap.PurchaseData
import com.onestore.sample.compose.manager.iap.PurchaseManager
import com.onestore.sample.compose.manager.iap.PurchaseManagerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 구매 처리 대기 목록 화면의 UI 상태 및 비즈니스 로직을 관리하는 ViewModel
 *
 * 미승인(Unacknowledged) 구매 항목들을 관리하고 승인/소비 처리를 수행합니다.
 *
 * @property purchaseManager 인앱 결제 및 라이선스 관리 매니저
 */
class PurchaseWaitingListViewModel(
    private val purchaseManager: PurchaseManager
): ViewModel() {

    private val _uiState = MutableStateFlow(PurchaseWaitingListUiState())
    
    /** 구매 처리 대기 목록 화면의 UI 상태 */
    val uiState: StateFlow<PurchaseWaitingListUiState> = _uiState.asStateFlow()

    /** PurchaseManager의 UI 상태 */
    val purchaseManagerUiState: StateFlow<PurchaseManagerUiState> = purchaseManager.uiState

    /**
     * 구매 목록 데이터를 로드합니다
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

    /**
     * 구매 항목을 처리합니다 (승인/소비)
     *
     * @param type 상품 타입 (INAPP, SUBS, AUTO)
     * @param purchase 처리할 구매 데이터
     */
    fun handlePurchase(type: String, purchase: PurchaseData) {
        purchaseManager.handlePurchase(type, purchase)
    }
}

/**
 * 구매 처리 대기 목록 화면의 UI 상태
 *
 * @property isRefreshing 새로고침 중 여부
 */
data class PurchaseWaitingListUiState(
    val isRefreshing: Boolean = false
)