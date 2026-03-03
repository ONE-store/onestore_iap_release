package com.onestore.sample.compose.ui.feature.product.detail

import android.app.Activity
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.gaa.sdk.iap.ProductDetail
import com.gaa.sdk.iap.PurchaseData
import com.onestore.sample.compose.manager.iap.PurchaseManager
import com.onestore.sample.compose.manager.iap.PurchaseManagerUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 상품 상세 화면의 UI 상태 및 비즈니스 로직을 관리하는 ViewModel
 *
 * @property purchaseManager 인앱 결제 및 라이선스 관리 매니저
 * @param saveStateHandle 네비게이션으로 전달된 상태를 저장하는 핸들
 */
class ProductDetailViewModel(
    private val purchaseManager: PurchaseManager,
    saveStateHandle: SavedStateHandle
): ViewModel() {

    private val productId: String = saveStateHandle.get<String>("productId") ?: ""
    
    private val _uiState = MutableStateFlow(ProductDetailUiState(productId = productId))
    
    /** 상품 상세 화면의 UI 상태 */
    val uiState: StateFlow<ProductDetailUiState> = _uiState.asStateFlow()

    /** PurchaseManager의 UI 상태 */
    val purchaseManagerUiState: StateFlow<PurchaseManagerUiState> = purchaseManager.uiState

    /**
     * 상품 구매 요청
     *
     * @param activity 구매 플로우를 시작할 액티비티
     * @param productId 구매할 상품 ID
     * @param type 상품 타입 (INAPP, SUBS, AUTO)
     * @param count 구매 수량
     * @param onPurchaseComplete 구매 완료 시 호출되는 콜백
     */
    fun purchaseRequest(
        activity: Activity,
        productId: String,
        type: String,
        count: Int,
        onPurchaseComplete: () -> Unit
    ) {
        purchaseManager.purchaseRequest(activity, productId, type, count, onPurchaseComplete)
    }

    /**
     * 월정액 상품의 상태 관리 (취소/재활성화)
     *
     * @param purchaseData 상태를 변경할 구매 데이터
     */
    fun manageRecurring(purchaseData: PurchaseData) {
        purchaseManager.manageRecurring(purchaseData)
    }

    /**
     * 원스토어 구독 관리 화면으로 이동
     *
     * @param activity 구독 관리 화면을 시작할 액티비티
     * @param purchaseData 관리할 구독 구매 데이터
     */
    fun launchManageSubscription(activity: Activity, purchaseData: PurchaseData) {
        purchaseManager.launchManageSubscription(activity, purchaseData)
    }

    /**
     * 구독 업그레이드/다운그레이드 요청
     *
     * @param activity 구독 변경 플로우를 시작할 액티비티
     * @param purchaseData 기존 구독 구매 데이터
     * @param targetProduct 변경할 대상 상품
     * @param onPurchaseComplete 구매 완료 시 호출되는 콜백
     */
    fun updateSubscription(
        activity: Activity,
        purchaseData: PurchaseData,
        targetProduct: ProductDetail,
        onPurchaseComplete: () -> Unit
    ) {
        purchaseManager.updateSubscription(activity, purchaseData, targetProduct, onPurchaseComplete)
    }
}

/**
 * 상품 상세 화면의 UI 상태
 *
 * @property productId 조회할 상품 ID
 */
data class ProductDetailUiState(
    val productId: String = ""
)