package com.onestore.sample.subscription.ui

import android.util.Log
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onestore.sample.subscription.auth.AuthManager
import com.onestore.sample.subscription.billing.AppSecurity
import com.onestore.sample.subscription.billing.PurchaseManager
import com.onestore.sample.subscription.common.Error
import com.gaa.sdk.auth.SignInResult
import com.gaa.sdk.iap.*

class MainViewModel : ViewModel() {
    private val TAG = "MainViewModel"
    private lateinit var authManager: AuthManager
    private val _errorIapResult = MutableLiveData<IapResult>()
    private val _errorAuthResult = MutableLiveData<SignInResult>()
    private val _errorMessage = MutableLiveData<String>()
    private var purchaseManager: PurchaseManager? = null
    lateinit var productIds: List<String>

    val errorIapResult: LiveData<IapResult>
        get() = _errorIapResult

    val errorAuthResult: LiveData<SignInResult>
        get() = _errorAuthResult

    val errorMessage: LiveData<String>
        get() = _errorMessage

    val purchaseData = ObservableArrayList<PurchaseData>()
    val productDetail = ObservableArrayList<ProductDetail>()
    val isSubscriptionManagement = ObservableBoolean(false)

    fun setAuth(manager: AuthManager) {
        this.authManager = manager
    }

    /**
     * 로그인
     * @param success 성공 이후 동작
     */
    fun fetchAuth(success: (SignInResult) -> Unit) {
        authManager.launchSignInFlow { result ->
            success(result)
        }
    }

    /**
     * 내역 및 상품 정보 갱신
     */
    fun refresh() {
        if (purchaseManager != null) {
            fetchProductDetails(productIds)
            fetchPurchaseData()
        }
    }

    fun fetchPurchaseManager(purchaseManger: PurchaseManager) {
        this.purchaseManager = purchaseManger
    }

    /**
     * 상품 구독하기
     * @param product 상품정보
     */
    fun purchase(product: ProductDetail) {
        /**
         * [PurchaseClient.launchPurchaseFlow] API를 이용하여 구매 요청을 진행합니다.
         *
         * 기본적으로 개발자 센터에 등록된 상품명이 결제 화면에 노출되지만
         * setProductName() 사용 시 결제 화면의 노출되는 상품명을 변경할 수 있습니다.
         *
         * gameUserId, promotionApplicable 파라미터는 옵션 값으로 원스토어 사업 부서 담당자와 프로모션에 대해 사전협의가
         * 된 상태에서만 사용하여야 하며, 일반적인 경우에는 값을 보내지 않습니다.
         * 또한, 사전협의가 되어 값을 보낼 경우에도 개인 정보보호를 위해 gameUserId는 hash된 고유한 값으로 전송하여야 합니다.
         *
         */

        val devPayload = AppSecurity.generatePayload()
        val params = PurchaseFlowParams.newBuilder()
            .setProductId(product.productId)    // mandatory
            .setProductType(product.type)       // mandatory
            .setDeveloperPayload(devPayload)    // optional
//            .setProductName("")               // optional
//            .setGameUserId("")                // optional
//            .setPromotionApplicable(false)    // optional
            .build()

        purchaseManager?.launchPurchaseFlow(params)
    }

    /**
     * 구독 업그레이드와 다운그레이드
     * 업그레이드는 현재 구독한 상품보다 절대 금액이 높을 경우,
     * 다운그레이드는 현재 구독한 상품보다 절대 금액이 낮은 경우, 수행된다.
     * 현재 구독 상품과 다른 상품을 선택 해야 한다.
     * @param purchase 이전 구매 정보
     * @param product 구독할 상품
     * @param option 비례배분 옵션 (설정 화면에서 변경 가능)
     */
    fun updateSubscription(purchase: PurchaseData, product: ProductDetail, @PurchaseFlowParams.ProrationMode option: Int) {
        Log.e(TAG, "updateSubscription [option] $option")
        purchaseManager?.launchUpdateSubscription(purchase, product, option)
    }

    /**
     * 구독 관리 화면으로 이동
     */
    fun launchManageSubscription() {
        if (!purchaseData.isEmpty()) {
            purchaseManager?.launchSubscriptionsMenu(purchaseData[0])
        } else {
            purchaseManager?.launchSubscriptionsMenu(null)
        }
    }

    /**
     * 원스토어 서비스 업데이트 / 설치 요청
     */
    fun doUpdateOrInstall() {
        purchaseManager?.launchUpdateOrInstall { refresh() }
    }

    /**
     * 구독 구매 내역
     */
    fun fetchPurchaseData() {
        purchaseManager?.queryPurchasesAsync(PurchaseClient.ProductType.SUBS)
    }

    /**
     * 구매 상품에 대한 인증요청합니다.
     * @param purchase 구매데이터
     */
    fun launchAcknowledge(purchase: PurchaseData) {
        purchaseManager?.acknowledgeAsync(purchase)
    }

    /**
     * 등록된 상품 조회 요청합니다.
     * @param ids 등록된 상품 ID 배열
     */
    private fun fetchProductDetails(ids: List<String>) {
        purchaseManager?.queryProductDetailAsync(ids, PurchaseClient.ProductType.SUBS, ProductDetailsListener { iapResult, products ->
            if (iapResult.isSuccess) {
                if (products != null) {
                    if (products.isEmpty()) {
                        error(Error.ERROR_EMPTY)
                        return@ProductDetailsListener
                    }

                    Log.d(TAG, "ProductDetail ==> $products")
                    if (productDetail.size > 0 ) {
                        productDetail.clear()
                    }

                    productDetail.addAll(products)
                } else {
                    _errorMessage.postValue(Error.ERROR_EMPTY)
                }
            } else  {
                _errorIapResult.postValue(iapResult)
            }
        })
    }

    /**
     * 마켓 정보 코드
     * @param action
     */
    fun getStoreInfo(action: (String) -> Unit) {
        purchaseManager?.getStoreInfo {
            action(it)
            refresh()
        }
    }

    /**
     * 서비스 연결 해제
     * Activity의 onDestroy 시 해제하는 것을 권장합니다.
     */
    fun destroy() {
        purchaseManager?.destroy()
    }
}