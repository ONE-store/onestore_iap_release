package com.onestore.sample.compose.manager.iap

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import com.gaa.sdk.auth.GaaSignInClient
import com.gaa.sdk.base.Logger
import com.gaa.sdk.base.Logger.LogLevel
import com.gaa.sdk.base.StoreEnvironment
import com.gaa.sdk.iap.AcknowledgeParams
import com.gaa.sdk.iap.ConsumeParams
import com.gaa.sdk.iap.IapResult
import com.gaa.sdk.iap.ProductDetail
import com.gaa.sdk.iap.ProductDetailsParams
import com.gaa.sdk.iap.PurchaseClient
import com.gaa.sdk.iap.PurchaseClient.ProductType
import com.gaa.sdk.iap.PurchaseClient.ResponseCode
import com.gaa.sdk.iap.PurchaseClientStateListener
import com.gaa.sdk.iap.PurchaseData
import com.gaa.sdk.iap.PurchaseData.RecurringState
import com.gaa.sdk.iap.PurchaseFlowParams
import com.gaa.sdk.iap.PurchasesUpdatedListener
import com.gaa.sdk.iap.RecurringProductParams
import com.gaa.sdk.iap.SubscriptionParams
import com.onestore.sample.compose.R
import com.onestore.sample.compose.common.Constant
import com.onestore.sample.compose.util.printLog
import com.onestore.sample.compose.util.runOnMainOnce
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val TAG = "PurchaseManager"

/**
 * PurchaseManager는 InApp Purchase SDK의 [PurchaseClient]을 쉽게 사용할 수 있도록 만들어진 Helper Class입니다.
 * 해당 class를 이용하여 IAP SDK를 적용 할 경우 내부 주석을 참고하여 개발해주세요.
 *
 * ## 주요 기능
 * - 인앱 SDK를 통한 인앱 결제 처리
 * - 사용자 로그인/로그아웃 관리
 * - 구매 내역 조회 및 관리
 * - 정기 결제 관리 (월정액 상품)
 * - 구매 상태 및 UI 상태 관리
 *
 * ## 구현 인터페이스
 * - [PurchasesUpdatedListener]: 구매 업데이트 콜백 처리
 *
 * @see PurchaseClient
 * @see PurchaseManagerUiState
 */
class PurchaseManager(): PurchasesUpdatedListener {

    private var signInClient: GaaSignInClient? = null
    private var purchaseClient: PurchaseClient? = null

    /** 초기화 완료 상태 */
    private var isInit = false

    /** 서비스 연결 상태 확인 */
    private val isServiceConnection: Boolean
        get() = purchaseClient?.connectionState == PurchaseClient.ConnectionState.CONNECTED

    /** 현재 구매 타입 (INAPP, SUBS, AUTO) */
    private var purchaseType = ProductType.INAPP

    /** 구매 완료 콜백 */
    private var onPurchaseComplete: (() -> Unit)? = null

    /**
     * 통합 UI 상태
     */
    private val _uiState = MutableStateFlow(PurchaseManagerUiState())
    val uiState: StateFlow<PurchaseManagerUiState> = _uiState.asStateFlow()

    /**
     * 앱 초기화 시 호출합니다.
     * SDK 로그를 활성화합니다.
     */
    fun onAppInit() {
        printLog(TAG, "onAppInit")
        Logger.setLogEnable(true)
    }

    /**
     * 로그 레벨을 설정합니다.
     *
     * @param level 설정할 로그 레벨
     */
    fun setLogLevel(@LogLevel level: Int) {
        printLog(
            TAG, "setLogLevel level=${
                when (level) {
                    Log.VERBOSE -> "Log.VERBOSE"
                    Log.DEBUG -> "Log.DEBUG"
                    Log.INFO -> "Log.INFO"
                    Log.WARN -> "Log.WARN"
                    Log.ERROR -> "Log.ERROR"
                    Log.ASSERT -> "Log.ASSERT"
                    else -> "Unknown"
                }
            }(${level})"
        )

        Logger.setLogLevel(level)
    }

    /**
     * 로그 활성화를 설정합니다.
     *
     * @param isEnable 로그 활성화 여부
     */
    fun setLogEnable(isEnable: Boolean) {
        printLog( TAG, "setLogEnable $isEnable")

        Logger.setLogEnable(isEnable)
    }

    /**
     * 초기화 완료 처리를 수행합니다.
     * 구매 내역 및 상품 정보를 조회합니다.
     */
    private fun onInitComplete() {
        printLog(TAG, "onInitComplete")
        isInit = true

        queryPurchasesAndProductDetailsAsync()
    }

    /**
     * 액티비티 초기화 시 호출합니다.
     * SDK 클라이언트들을 초기화하고 구매 클라이언트 연결을 시작합니다.
     *
     * @param activity 초기화할 액티비티
     */
    fun onActivityInit(activity: Activity) {
        printLog(TAG, "onActivityInit")
        isInit = false

        purchaseClient?.endConnection()
        purchaseClient = null

        signInClient = GaaSignInClient.getClient(activity)

        runOnMainOnce {
            initPurchaseClient(activity)
        }
    }

    /**
     * Activity Resume 시 호출합니다.
     * 초기화 상태 확인 후 자동 로그인을 수행합니다.
     *
     * @param activity Resume된 액티비티
     */
    fun onActivityResume(activity: Activity) {
        // 초기화 미완료 시 리턴
        if(!isInit) {
            printLog(TAG, "onActivityResume not init")
            return
        }

        if(onPurchaseComplete != null) {
            printLog(TAG, "onActivityResume Purchasing")
            return
        }

        printLog(TAG, "onActivityResume")

        runOnMainOnce {
            silentSignIn()
        }
    }

    /**
     * 사용자 상호작용 없이 기존 로그인 정보로 로그인을 시도합니다.
     */
    suspend fun silentSignIn() {
        suspendCoroutine { cont ->
            printLog(TAG, "silentSignIn")
            signInClient?.silentSignIn { signInResult ->
                printLog(TAG, "silentSignIn signInResult code=${signInResult.code} message='${signInResult.message}'")
                _uiState.update { it.copy(isLogin = signInResult.isSuccessful) }
                cont.resume(Unit)
            } ?: let {
                printLog(TAG, "silentSignIn signInClient is null")
                _uiState.update { it.copy(isLogin = false) }
                cont.resume(Unit)
            }
        }
    }

    /**
     * 사용자에게 로그인 UI를 표시하여 로그인을 수행합니다.
     *
     * 로그인 성공 시 자동으로 구매 내역 및 상품 정보를 조회합니다.
     *
     * @param activity 로그인 플로우를 시작할 액티비티
     */
    fun launchSignInFlow(activity: Activity) {
        printLog(TAG, "signIn")
        signInClient?.launchSignInFlow(activity) { signInResult ->
            printLog(TAG, "signIn signInResult code=${signInResult.code} message='${signInResult.message}'")
            _uiState.update { it.copy(isLogin = signInResult.isSuccessful) }

            if(signInResult.isSuccessful) {
                queryPurchasesAndProductDetailsAsync()
            }
        } ?: let {
            printLog(TAG, "signIn signInClient is null")
            _uiState.update { it.copy(isLogin = false) }
        }
    }

    /**
     * StoreType에 매칭되는 문자열 리소스 ID를 반환합니다.
     *
     * @param context 컨텍스트
     * @return StoreType에 매칭되는 문자열 리소스 ID
     */
    @StringRes
    fun getStoreTypeStringRes(context: Context): Int {
        return when(StoreEnvironment.getStoreType(context)) {
            StoreEnvironment.StoreType.ONESTORE -> R.string.store_type_onestore
            StoreEnvironment.StoreType.VENDING -> R.string.store_type_play_store
            StoreEnvironment.StoreType.ETC -> R.string.store_type_etc
            else -> R.string.store_type_unknown
        }
    }


    /**
     * 구매 클라이언트를 초기화합니다.
     * SDK의 PurchaseClient를 생성하고 연결을 시작합니다.
     *
     * @param activity 액티비티
     */
    private fun initPurchaseClient(activity: Activity) {
        printLog(TAG, "initPurchaseClient publicKey=${Constant.PUBLIC_KEY}")
        purchaseClient = PurchaseClient.newBuilder(activity)
            .setListener(this)
            .setBase64PublicKey(Constant.PUBLIC_KEY)
            .build()

        // 서비스 연결 시작
        startConnection {
            printLog(TAG, "startConnection connected")
            onInitComplete()
        }
    }

    /**
     * 원스토어 서비스(AIDL)와의 연결을 보장하기 위한 진입 함수입니다.
     *
     * API 호출 시점에 서비스 연결 상태를 확인하여,
     * - 이미 연결된 상태라면 즉시 runBlock 을 실행하고
     * - 연결이 끊어져 있거나 아직 연결되지 않은 상태라면 서비스 재연결을 시도한 뒤, 연결 성공 시 runBlock 을 실행합니다.
     *
     * 본 함수는 다음과 같은 환경적 요인으로 인해
     * AIDL 바인딩이 예기치 않게 해제될 수 있는 상황을 방어하기 위해 사용됩니다.
     * - 단말의 메모리 부족 상황
     * - OS 또는 단말 정책에 의한 백그라운드 프로세스 정리
     * - 앱이 백그라운드 → 포그라운드로 전환되는 과정에서의 서비스 재시작
     *
     * 위와 같은 경우,
     * 클라이언트에서는 명시적인 disconnect 없이도 서비스 연결이 끊어진 상태가 될 수 있으므로, 모든 원스토어 API 호출은 본 함수를 통해 수행하는 것을 권장합니다.
     *
     * @param runBlock 원스토어 서비스 연결 성공 이후 람다 형식으로 로직 수행
     */
    private fun startConnection(runBlock: () -> Unit = {}) {
        // 이미 연결된 경우 바로 실행
        if(isServiceConnection) {
            printLog(TAG, "startConnection connecting")
            runBlock()
            return
        }

        // 서비스 연결 시작
        purchaseClient?.startConnection(object : PurchaseClientStateListener {
            override fun onSetupFinished(iapResult: IapResult) {
                printLog(TAG, "startConnection onSetupFinished iapResult isSuccess=${iapResult.isSuccess} responseCode=${iapResult.responseCode} message=${iapResult.message}")
                if (iapResult.isSuccess) {
                    runBlock()
                } else {
                    // 초기화 완료 후 오류 발생 시 구매 업데이트 콜백 호출
                    if (isInit) {
                        onPurchasesUpdated(iapResult, null)
                    }
                }
            }

            override fun onServiceDisconnected() {
                printLog(TAG, "startConnection onServiceDisconnected")
            }
        })
    }

    /**
     * 구매 내역 및 모든 타입의 인앱 상품 목록을 조회합니다.
     */
    fun queryPurchasesAndProductDetailsAsync() {
        runOnMainOnce {
            printLog(TAG, "queryPurchasesAndProductDetailsAsync")
            purchaseType = ProductType.ALL
            queryProductDetailsAsync()

            delay(500)

            queryPurchasesAsync()
        }
    }

    /**
     * 등록된 상품 정보를 조회합니다.
     *
     * [Constant.PRODUCT_IDS]에 정의된 상품 ID 목록으로 상품 정보를 조회합니다.
     * 개발자 센터에서 앱 등록 시 등록한 상품들의 상세 정보를 가져옵니다.
     *
     * ## 조회 가능한 정보
     * - 상품 ID (productId)
     * - 상품명 (title)
     * - 가격 (price, priceCurrencyCode)
     * - 상품 타입 (type)
     * - 설명 (description)
     * - 구독 기간 정보 (구독 상품인 경우)
     */
    private fun queryProductDetailsAsync() {
        startConnection {
            printLog(TAG, "queryProductDetailsAsync")
            _uiState.update { it.copy(productDetails = listOf()) }

            val params = ProductDetailsParams.newBuilder()
                .setProductIdList(Constant.PRODUCT_IDS.toList())
                .setProductType(purchaseType)
                .build()

            purchaseClient?.queryProductDetailsAsync(params) { iapResult, productDetails ->
                printLog(TAG, "queryProductDetailsAsync type=${purchaseType} iapResult=$iapResult productDetails=$productDetails")
                if(iapResult.isSuccess) {
                    if (productDetails != null) {
                        _uiState.update { it.copy(productDetails = productDetails.sortedBy { it.type }) }
                    }
                } else {
                    checkIapResult(iapResult)
                }
            }
        }
    }

    /**
     * 구매 내역을 조회합니다.
     *
     * 개발사에서 필요한 상품타입을 인자로 전달하여 사용하여야 합니다.
     *
     * 구매내역조회 API를 이용하여 소비되지 않는 관리형상품(inapp)과 구독형상품(sub) 목록을 받아옵니다.
     * 관리형상품(inapp)의 경우 소비를 하지 않을 경우 재구매요청을 하여도 구매가 되지 않습니다.
     * 꼭, 소비 과정을 통하여 소모성상품 소비를 진행하여야합니다.
     *
     * manageRecurringProduct API를 통해 해지예약요청을 할 경우 recurringState는 0 -> 1로 변경됩니다.
     * 해지예약 취소요청을 할경우 recurringState는 1 -> 0로 변경됩니다.
     *
     * ## 개발사 구현 필수
     * **TODO**: 개발사에서는 소비되지 않는 관리형상품(INAPP)에 대해, 애플리케이션의 적절한 생명주기(life cycle)에 맞춰 
     * 구매내역조회를 진행 후 소비를 진행해야 합니다.
     *
     * ### 권장 구현 위치
     * - 앱 시작 시 (Application.onCreate 또는 MainActivity.onCreate)
     * - 사용자 로그인 후
     * - 특정 화면 진입 시 (상품 사용이 가능한 화면)
     *
     * ### 예시
     * ```kotlin
     * override fun onCreate() {
     *     super.onCreate()
     *     // 앱 시작 시 미소비 상품 확인 및 처리
     *     purchaseManager.queryPurchasesAsync(ProductType.INAPP)
     * }
     * ```
     *
     * @param productType 조회할 상품 타입 (null이면 모든 상품 조회)
     */
    private fun queryPurchasesAsync(productType: String? = null) {
        startConnection {
            printLog(TAG, "queryPurchasesAsync productType=$productType")

            if(productType == null || productType.equals(ProductType.AUTO, ignoreCase = true)) {
                purchaseClient?.queryPurchasesAsync(ProductType.AUTO) { iapResult, purchaseData ->
                    printLog(TAG, "queryPurchasesAsync AUTO iapResult='$iapResult' purchaseData=$purchaseData")
                    if(iapResult.isSuccess) {
                        _uiState.update { it.copy(purchasesAutoList = purchaseData ?: listOf()) }
                    } else {
                        checkIapResult(iapResult)
                    }
                }
            }

            if(productType == null || productType.equals(ProductType.INAPP, ignoreCase =  true)) {
                purchaseClient?.queryPurchasesAsync(ProductType.INAPP) { iapResult, purchaseData ->
                    printLog(TAG, "queryPurchasesAsync INAPP iapResult='$iapResult' purchaseData=$purchaseData")
                    if(iapResult.isSuccess) {
                        _uiState.update { it.copy(purchasesInAppList = purchaseData ?: listOf()) }
                    } else {
                        checkIapResult(iapResult)
                    }
                }
            }

            if(productType == null || productType.equals(ProductType.SUBS, ignoreCase =  true)) {
                purchaseClient?.queryPurchasesAsync(ProductType.SUBS) { iapResult, purchaseData ->
                    printLog(TAG, "queryPurchasesAsync SUBS iapResult='$iapResult' purchaseData=$purchaseData")
                    if(iapResult.isSuccess) {
                        _uiState.update { it.copy(purchasesSubsList = purchaseData ?: listOf()) }
                    } else {
                        checkIapResult(iapResult)
                    }
                }
            }
        }
    }

    /**
     * 구매한 월정액 상품의 상태 관리 (취소/재활성화)
     *
     * 월정액 상품의 해지예약 요청 또는 해지예약 취소를 수행합니다.
     * v21에서는 Deprecated 된 기능입니다.
     *
     * @param purchaseData 변경할 구매 데이터
     */
    fun manageRecurring(purchaseData: PurchaseData) {
        printLog(TAG, "manageRecurring purchaseData=${purchaseData}")

        val recurringParams = RecurringProductParams.newBuilder()
            .setPurchaseData(purchaseData)
            .setRecurringAction(
                if(purchaseData.recurringState == RecurringState.RECURRING)
                    PurchaseClient.RecurringAction.CANCEL
                else
                    PurchaseClient.RecurringAction.REACTIVATE
            )
            .build()

        _uiState.update { it.copy(showLoading = true) }
        startConnection {
            @Suppress("DEPRECATION")
            purchaseClient?.manageRecurringProductAsync(recurringParams) { iapResult, purchaseData, action ->
                _uiState.update { it.copy(showLoading = false) }
                printLog(TAG, "manageRecurring manageRecurringProductAsync iapResult='$iapResult' action=$action purchaseData=${purchaseData}")

                if (iapResult.isSuccess) {
                    // 월정액은 json 값이 변동이 없어, 새로고침 해야함
                    queryPurchasesAsync(ProductType.AUTO)
                } else {
                    checkIapResult(iapResult)
                }
            }
        }
    }

    /**
     * 구매 요청
     *
     * [PurchaseClient.launchPurchaseFlow] API를 이용하여 구매 요청을 진행합니다.
     * 구매 결과는 [onPurchasesUpdated]를 통해 전달받습니다.
     *
     * ## PurchaseFlowParams 설정 옵션
     *
     * ### 필수 파라미터
     * - `productId`: 구매할 상품 ID
     * - `productType`: 상품 타입 (INAPP, SUBS, AUTO)
     *
     * ### 선택 파라미터
     *
     * #### setProductName()
     * - 기본적으로 개발자 센터에 등록된 상품명이 결제 화면에 노출됩니다
     * - `setProductName()` 사용 시 결제 화면에 노출되는 상품명을 변경할 수 있습니다
     *
     * #### setGameUserId() / setPromotionApplicable()
     * - **중요**: 원스토어 사업 부서 담당자와 프로모션에 대해 사전협의가 된 상태에서만 사용해야 합니다
     * - 일반적인 경우에는 값을 보내지 않습니다
     * - 사전협의가 되어 값을 보낼 경우에도 **개인정보 보호를 위해 gameUserId는 hash된 고유한 값으로 전송**해야 합니다
     *
     * #### setQuantity()
     * - 상품의 수량을 지정합니다 (기본값: 1)
     * - 관리형 상품(INAPP)에서 주로 사용됩니다
     *
     * #### setDeveloperPayload()
     * - 개발자가 임의로 지정하는 문자열로, 결제 완료 후 다시 전달됩니다
     * - 변조 방지를 위해 서버에서 발급받은 Payload를 사용하는 것을 권장합니다
     *
     * @param activity 구매 플로우를 시작할 액티비티
     * @param productId 구매할 상품 ID
     * @param productType 상품 타입 (ProductType.INAPP, ProductType.SUBS, ProductType.AUTO)
     * @param quantity 구매 수량 (기본값: 1)
     * @param onPurchaseComplete 구매 완료 시 호출되는 콜백
     *
     * @see PurchaseFlowParams
     * @see PurchaseClient.launchPurchaseFlow
     */
    fun purchaseRequest(activity: Activity, productId: String, productType: String, quantity: Int, onPurchaseComplete: () -> Unit) {
        printLog(TAG, "purchaseRequest productId=$productId productType=$productType quantity=$quantity")
        startConnection {
            val purchaseFlowParams = PurchaseFlowParams.newBuilder()
                .setProductId(productId)
                .setProductType(productType)
                .setQuantity(quantity)
                .build()

            this.onPurchaseComplete = onPurchaseComplete
            val iapResult = purchaseClient?.launchPurchaseFlow(activity, purchaseFlowParams)
            printLog(TAG, "purchaseRequest purchaseClient.launchPurchaseFlow iapResult='$iapResult'")
        }
    }

    /**
     * 원스토어 구독 화면으로 이동합니다.
     *
     * @param activity 액티비티
     * @param purchaseData 구매 상품 데이터 (있으면 원스토어 구독 상세화면으로 이동)
     */
    fun launchManageSubscription(activity: Activity, purchaseData: PurchaseData? = null) {
        printLog(TAG, "launchManageSubscription purchaseData=$purchaseData")

        val subscriptionParams = SubscriptionParams.newBuilder()
            .apply { purchaseData?.let { setPurchaseData(it) } }
            .build()

        purchaseClient?.launchManageSubscription(activity, subscriptionParams)
    }

    /**
     * 구독 상품 업그레이드/다운그레이드를 수행합니다.
     *
     * 기존 구독을 새로운 구독으로 변경합니다.
     * 구매 결과는 [onPurchasesUpdated]를 통해 전달받습니다.
     *
     * ## 업그레이드 vs 다운그레이드
     * - **업그레이드**: 현재 구독한 상품보다 절대 금액이 **높은** 상품으로 변경
     * - **다운그레이드**: 현재 구독한 상품보다 절대 금액이 **낮은** 상품으로 변경
     *
     * ## 주의사항
     * - 현재 구독 상품과 **다른 상품**을 선택해야 합니다
     * - 동일한 상품으로는 변경할 수 없습니다
     *
     * ## 비례배분 모드 (Proration Mode)
     * - `IMMEDIATE_WITH_TIME_PRORATION`: 즉시 변경, 남은 기간 비례 계산
     * - `IMMEDIATE_WITHOUT_PRORATION`: 즉시 변경, 비례 계산 없음
     * - `DEFERRED`: 현재 구독 기간 종료 후 변경
     *
     * @param activity 구독 변경 플로우를 시작할 액티비티
     * @param oldPurchaseData 현재 구독 구매 데이터
     * @param newProductDetail 변경할 대상 상품
     * @param onPurchaseComplete 변경 완료 시 호출되는 콜백
     *
     * @see PurchaseFlowParams.ProrationMode
     */
    fun updateSubscription(activity: Activity, oldPurchaseData: PurchaseData, newProductDetail: ProductDetail, onPurchaseComplete: () -> Unit) {
        printLog(TAG, "updateSubscription oldPurchaseData=$oldPurchaseData newProductDetail=$newProductDetail")
        startConnection {
            val subscriptionUpdateParams = PurchaseFlowParams.SubscriptionUpdateParams.newBuilder()
                .setProrationMode(PurchaseFlowParams.ProrationMode.IMMEDIATE_WITH_TIME_PRORATION)
                .setOldPurchaseToken(oldPurchaseData.purchaseToken)
                .build()

            val purchaseFlowParams = PurchaseFlowParams.newBuilder()
                .setProductId(newProductDetail.productId)
                .setProductType(newProductDetail.type)
                .setSubscriptionUpdateParams(subscriptionUpdateParams)
                .build()

            this.onPurchaseComplete = onPurchaseComplete

            val iapResult = purchaseClient?.launchPurchaseFlow(activity, purchaseFlowParams)
            printLog(TAG, "updateSubscription purchaseClient.launchPurchaseFlow iapResult='$iapResult'")
        }
    }

    /**
     * IAP 결과 코드를 확인하고 적절한 UI 상태로 업데이트합니다.
     *
     * - RESULT_NEED_UPDATE: 원스토어 업데이트 필요 다이얼로그 표시
     * - RESULT_NEED_LOGIN: 로그인 필요 다이얼로그 표시
     * - 기타: 에러 메시지 다이얼로그 표시
     *
     * @param iapResult 확인할 IAP 결과
     */
    private fun checkIapResult(iapResult: IapResult) {
        when (iapResult.responseCode) {
            ResponseCode.RESULT_NEED_UPDATE -> {
                printLog(TAG, "checkIapResult RESULT_NEED_UPDATE")
                _uiState.update { it.copy(showUpdate = true) }
            }
            ResponseCode.RESULT_NEED_LOGIN -> {
                printLog(TAG, "checkIapResult RESULT_NEED_LOGIN")
                _uiState.update { it.copy(showLogin = true, isLogin = false) }
            }
            else -> {
                printLog(TAG, "checkIapResult responseCode=${iapResult.responseCode} message=${iapResult.message}")
                _uiState.update { it.copy(showMessage = "code=${iapResult.responseCode}\n${iapResult.message}") }
            }
        }
    }

    /**
     * 상품 구매에 대한 결과가 전송됩니다.
     *
     * [purchaseRequest]나 [updateSubscription] 호출 시 결과를 이 콜백으로 받습니다.
     * 구매 완료, 취소, 오류 등의 상황에서 호출되며, 성공 시 해당 타입의 구매 내역을 다시 조회합니다.
     *
     * @param iapResult 구매 업데이트 결과
     * @param purchases 구매 데이터 목록
     */
    override fun onPurchasesUpdated(iapResult: IapResult, purchases: MutableList<PurchaseData>?) {
        printLog(TAG, "onPurchasesUpdated iapResult='$iapResult' purchases=$purchases")

        if (iapResult.isSuccess) {
            /*
             * 구매 데이터 검증
             * 
             * 운영 환경에서는 구매 데이터의 서명을 검증하여 위변조를 방지해야 합니다.
             * 
             * 클라이언트 검증 (참고용):
             * - SDK 내부에서 자동으로 검증하지만, 앱에서 추가 검증도 가능합니다
             * - purchase.originalJson과 purchase.signature를 이용하여 서명 검증 가능
             * - 구현 예시는 다른 샘플 앱(sample_luckyone, sample_subscription)의 AppSecurity 클래스 참조
             * 
             * 서버 검증 (필수):
             * - 클라이언트 검증은 우회 가능하므로, 반드시 서버에서 영수증을 검증해야 합니다
             * - 구매 데이터를 서버로 전송하여 ONE Store API로 영수증 검증 수행
             * - 검증 성공 후 서버에서 상품 지급 처리
             * 
             * 참고: Constant.kt의 PUBLIC_KEY 주석에 서명 검증 상세 설명 참조
             */
            
            var isQueryPurchaseAuto = false
            var isQueryPurchaseInapp = false
            var isQueryPurchaseSubs = false

            for (purchase in purchases ?: listOf()) {
                val productDetail = _uiState.value.productDetails.firstOrNull { it.productId == purchase.productId }
                isQueryPurchaseAuto = isQueryPurchaseAuto || productDetail?.type == ProductType.AUTO
                isQueryPurchaseInapp = isQueryPurchaseInapp || productDetail?.type == ProductType.INAPP
                isQueryPurchaseSubs = isQueryPurchaseSubs || productDetail?.type == ProductType.SUBS
            }

            if (isQueryPurchaseAuto) {
                printLog(TAG, "onPurchasesUpdated queryPurchasesAsync type=AUTO")
                queryPurchasesAsync(ProductType.AUTO)
            }
            if (isQueryPurchaseInapp) {
                printLog(TAG, "onPurchasesUpdated queryPurchasesAsync type=INAPP")
                queryPurchasesAsync(ProductType.INAPP)
            }
            if (isQueryPurchaseSubs) {
                printLog(TAG, "onPurchasesUpdated queryPurchasesAsync type=SUBS")
                queryPurchasesAsync(ProductType.SUBS)
            }

            onPurchaseComplete?.invoke()
        } else {
            checkIapResult(iapResult)
        }
        onPurchaseComplete = null
    }

    /**
     * 구매 처리 (승인/소비)
     *
     * 상품 타입에 따라 적절한 처리를 수행합니다:
     * - AUTO (월정액): 승인(acknowledge) 처리. 월정액은 json 값이 변동이 없어 새로고침 필요
     * - INAPP (관리형): 소비(consume) 처리. 상품 구매 완료 후 3일 이내 소비되지 않으면 사용자에게 상품이 지급되지 않습니다
     * - SUBS (구독): 승인(acknowledge) 처리
     *
     * @param type 상품 타입 (ProductType.AUTO, ProductType.INAPP, ProductType.SUBS)
     * @param purchase 처리할 구매 데이터
     */
    fun handlePurchase(type: String, purchase: PurchaseData) {
        when(type) {
            ProductType.AUTO, ProductType.SUBS -> {
                acknowledgeProduct(type, purchase)
            }
            ProductType.INAPP -> {
                consumeProduct(type, purchase)
            }
        }
    }

    /**
     * 비소비성 상품(AUTO, SUBS)을 인증합니다.
     *
     * ## 인증 여부 확인
     * [PurchaseData.isAcknowledged] 함수를 사용하여 이미 인증되었는지 확인할 수 있습니다.
     *
     * ## 처리 플로우
     * 1. 구매 완료 시 전달받은 PurchaseData로 AcknowledgeParams 생성
     * 2. [PurchaseClient.acknowledgeAsync] 호출
     * 3. 성공 시 [queryPurchasesAsync]를 호출하여 구매 내역 갱신
     *
     * @param type 상품 타입 (AUTO, SUBS)
     * @param purchase 인증할 구매 데이터
     */
    private fun acknowledgeProduct(type: String, purchase: PurchaseData) {
        startConnection {
            val acknowledgeParams = AcknowledgeParams.newBuilder()
                .setPurchaseData(purchase)
                .build()

            purchaseClient?.acknowledgeAsync(acknowledgeParams) { iapResult, _ ->
                printLog(TAG, "acknowledgeProduct type=$type iapResult='$iapResult'")
                if (iapResult.isSuccess) {
                    queryPurchasesAsync(type)
                } else {
                    checkIapResult(iapResult)
                }
            }
        }
    }

    /**
     * 관리형 상품(INAPP)에 대한 소비를 진행합니다.
     *
     * **상품 구매 완료 후 3일 이내 소비되지 않았을 경우, 사용자에게 상품이 지급되지 않습니다.**
     * 구매 완료 후 가능한 한 빠르게 소비 처리를 해야 합니다.
     *
     * ## 처리 플로우
     * 1. 구매 완료 시 전달받은 PurchaseData로 ConsumeParams 생성
     * 2. [PurchaseClient.consumeAsync] 호출
     * 3. 성공 시 [queryPurchasesAsync]를 호출하여 구매 내역 갱신
     *
     * @param type 상품 타입 (INAPP)
     * @param purchase 소비할 구매 데이터
     */
    private fun consumeProduct(type: String, purchase: PurchaseData) {
        startConnection {
            val consumeParams = ConsumeParams.newBuilder()
                .setPurchaseData(purchase)
                .build()

            purchaseClient?.consumeAsync(consumeParams) { iapResult, _ ->
                printLog(TAG, "consumeProduct type=$type iapResult='$iapResult'")
                if (iapResult.isSuccess) {
                    queryPurchasesAsync(type)
                } else {
                    checkIapResult(iapResult)
                }
            }
        }
    }


    /**
     * 로딩 상태를 숨깁니다.
     */
    fun hideLoading() {
        _uiState.update { it.copy(showLoading = false) }
    }

    /**
     * 공통 메시지 다이얼로그를 닫습니다.
     */
    fun hideMessage() {
        _uiState.update { it.copy(showMessage = "") }
    }

    /**
     * 로그인 다이얼로그를 닫습니다.
     */
    fun hideLogin() {
        _uiState.update { it.copy(showLogin = false) }
    }

    /**
     * 업데이트 다이얼로그를 닫습니다.
     */
    fun hideUpdate() {
        _uiState.update { it.copy(showUpdate = false) }
    }

    /**
     * 앱 업데이트 또는 설치 플로우를 실행합니다.
     *
     * @param activity 액티비티
     */
    fun launchUpdateOrInstallFlow(activity: Activity) {
        purchaseClient?.launchUpdateOrInstallFlow(activity) { iapResult ->
            printLog(TAG, "PurchaseManager launchUpdateOrInstallFlow iapResult responseCode=${iapResult.responseCode} message=${iapResult.message}")
            if (iapResult.isSuccess) {
                startConnection {
                    printLog(TAG, "PurchaseManager launchUpdateOrInstallFlow startConnection connected")
                    onInitComplete()
                }
            }
        }
    }
}
