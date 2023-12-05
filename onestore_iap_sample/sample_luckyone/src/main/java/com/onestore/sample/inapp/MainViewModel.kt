package com.onestore.sample.inapp

import android.util.Log
import androidx.databinding.ObservableInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.onestore.sample.inapp.auth.AuthManager
import com.onestore.sample.inapp.billing.PurchaseManager
import com.onestore.sample.inapp.common.Error
import com.onestore.sample.inapp.util.AppConstants
import com.gaa.sdk.auth.SignInResult
import com.gaa.sdk.iap.*
import com.gaa.sdk.iap.PurchaseClient.ProductType

class MainViewModel : ViewModel() {
    private val TAG = "MainViewModel"

    private val _showProgress = MutableLiveData<Boolean>()
    private val _errorIapResult = MutableLiveData<IapResult>()
    private val _errorAuthResult = MutableLiveData<SignInResult>()
    private val _errorMessage = MutableLiveData<String>()

    val showProgress: LiveData<Boolean>
        get() = _showProgress

    val errorIapResult: LiveData<IapResult>
        get() = _errorIapResult

    val errorAuthResult: LiveData<SignInResult>
        get() = _errorAuthResult

    val errorMessage: LiveData<String>
        get() = _errorMessage

    var savedCoin = ObservableInt(0)

    private var authManager: AuthManager? = null
    private var purchaseManager: PurchaseManager? = null

    fun setAuthManager(authManager: AuthManager?) {
        this.authManager = authManager
    }

    fun setPurchaseManager(purchaseManager: PurchaseManager?) {
        this.purchaseManager = purchaseManager
    }

    fun fetch(listener: (SignInResult) -> Unit) {
        _showProgress.postValue(true)
        authManager?.launchSignInFlow() { result ->
            _showProgress.postValue(false)
            listener.invoke(result)
        } ?: _showProgress.postValue(false)
    }

    fun destroy() {
        purchaseManager?.destroy()
    }

    fun getStoreCode(success: (String) -> Unit) {
        purchaseManager?.getStoreInfo(success)
    }

    fun updateOrInstallPaymentModule() {
        purchaseManager?.launchUpdateOrInstall {
            // 업데이트 / 설치 완료 후 개발사에 맞는 동작 적용이 필요합니다.
        }
    }

    fun launchLoginFlow(success: () -> Unit) {
        authManager?.launchSignInFlow { signInResult ->
            if (signInResult.isSuccessful) {
                success()
            } else {
                _errorAuthResult.postValue(signInResult)
            }
        }
    }

    fun buyProduct(payload: String?, productId: String, @ProductType productType: String) {
        Log.d(TAG, "buyProduct() - productId:$productId productType: $productType")
        _showProgress.postValue(true)

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
         * quantity 상품의 수량을 지정합니다. (기본값: 1)
         */
        val paramsBuilder = PurchaseFlowParams.newBuilder()
            .setProductId(productId)            // mandatory
            .setProductType(productType)        // mandatory
            .setDeveloperPayload(payload)       // optional
//            .setProductName("")                 // optional
//            .setGameUserId("")                  // optional
//            .setPromotionApplicable(false)      // optional

        // optional
        when (productId) {
            AppConstants.InappType.PRODUCT_INAPP_500 -> paramsBuilder.setQuantity(3)
            else -> paramsBuilder.setQuantity(1)
        }

        purchaseManager?.launchPurchaseFlow(paramsBuilder.build())

        if (purchaseManager == null) {
            _showProgress.postValue(false)
        }
    }

    fun openSubscriptionMenu(purchaseData: PurchaseData?) {
        purchaseManager?.launchSubscriptionsMenu(purchaseData)
    }

    fun fetchProductDetail(success: (List<ProductDetail>) -> Unit) {
        _showProgress.postValue(true)
        val allItems: List<String> = AppConstants.allProductList

        purchaseManager?.queryProductDetailAsync(allItems, ProductType.ALL,
            ProductDetailsListener { iapResult, productDetails ->
                _showProgress.postValue(false)
                if (iapResult.isSuccess) {
                    if (productDetails!!.isEmpty()) {
                        _errorMessage.postValue(Error.ERROR_EMPTY)
                        return@ProductDetailsListener
                    }
                    success(productDetails)
                } else {
                    _errorIapResult.postValue(iapResult)
                }
            })

        if (purchaseManager == null) {
            _showProgress.postValue(false)
        }
    }

    fun fetchPurchasesWithType(@ProductType type: String?) {
        purchaseManager?.queryPurchasesAsync(type!!)
    }

    fun acknowledge(purchaseData: PurchaseData?) {
        purchaseManager?.acknowledgeAsync(purchaseData!!)
    }

    fun consume(purchaseData: PurchaseData?) {
        purchaseManager?.consumeAsync(purchaseData!!)
    }

    fun fetchPurchases() {
        // TODO: 서비스하지 않는 상품 타입은 요청하지 않도록 구현해야 합니다.
        purchaseManager?.queryPurchasesAsync(ProductType.INAPP, ProductType.SUBS)
    }
}