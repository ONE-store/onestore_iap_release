package com.onestore.sample.inapp

import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.gaa.sdk.auth.SignInResult
import com.gaa.sdk.base.Logger
import com.gaa.sdk.base.StoreEnvironment
import com.gaa.sdk.base.StoreEnvironment.StoreType
import com.gaa.sdk.iap.*
import com.gaa.sdk.iap.PurchaseClient.ProductType
import com.google.gson.Gson
import com.onestore.sample.inapp.auth.AuthManager
import com.onestore.sample.inapp.billing.AppSecurity
import com.onestore.sample.inapp.billing.PurchaseManager
import com.onestore.sample.inapp.common.Error
import com.onestore.sample.inapp.common.ViewModelFactory
import com.onestore.sample.inapp.databinding.ActivityMainBinding
import com.onestore.sample.inapp.util.AppConstants
import com.onestore.sample.inapp.util.LuckyUtils
import com.onestore.sample.inapp.widget.BuyProductDialog
import com.onestore.sample.inapp.widget.LuckyNumberView
import com.onestore.sample.inapp.widget.ManageProductDialog
import com.onestore.sample.inapp.widget.ResultNumberView
import java.util.*

/**
 * Lucky ONE
 * <p>
 * 스토어 In-app purchasing v21을 이용한 게임
 * <p>
 * TODO: Lucky ONE 샘플 앱에서는 개발사에서 스토어 인앱결제를 이용하기 위한 일반적인 IAP v21 API 구매 시나리오를 나열하고 있습니다. 샘플 구동 전에 해당 가이드를 꼼꼼히 읽어보시기 바랍니다.
 * <p>
 * Lucky ONE 샘플 앱은 많은 사람들이 사랑하는 로또의 번호 추천 시스템과 비슷하게 개발되었습니다. 사용자는 번호를 생성하여 추첨을 하기 위해서 사용자는 코인을 구매하여야 하며, 구매된 코인을 이용하여 게임을 이용할 수 있습니다.
 * <p>
 * 메인화면에서는 보유코인 수와 사용자의 추천 랜덤 번호, 그리고 실제 추첨된 5개의 번호를 나열하고 있습니다.
 * 3개의 번호 일치시 5개의 코인을 지급하며, 4개 일치 시 30개 코인을 지급, 5개일때는 100개의 코인, 6개일때는 300개의 코인을 지급합니다.
 * <p>
 * 사용자는 구매 버튼을 선택 할 경우 상품을 선택할 수 있는 창이 뜨게되며, 사용자의 선택에 따라 5, 10coins 와 ONE more 상품(관리형상품)과 구독형상품 구매를 할 수 있습니다.
 * 관리형상품의 경우 구매를 완료하게 될 경우 상품소비를 통하여 해당 구매상품 소비과정을 진행해야합니다.
 * <p>
 * Lucky ONE 앱에서는 앱 구동시점에 구매내역조회 API를 이용하여 관리형상품(inapp)/구독형상품(sub)에 대하여 구매내역 정보를 받아오고 있으며,
 * 관리형상품(inapp) 구매내역을 받아올 경우 상품소비가 진행되지 않는 상품이므로 상품소비과정을 진행합니다.
 * 구독형상품(sub) 구매내역을 받아올 경우 정기 구독이 시작되며, 메인화면 우측상단에 설정버튼을 통해 구독관리 메뉴와 구독 상세 화면을 확인할 수 있습니다.<br/>
 *
 * [참고]
 * - 구독형 상품(sub)은 v21에서만 제공됩니다.
 * <p>
 * - Lucky ONE 샘플앱 구동 전 준비 사항 -
 * 1. 샘플앱의 패키지 이름을 변경합니다.
 * 2. 스토어 개발자센터에 애플리케이션을 등록 후 base64 public key 값을 AppSecurity클래스의 PUBLIC_KEY 변수에 붙여넣습니다.
 * 3. 스토어 개발자센터에 상품을 등록합니다. 샘플앱의 경우 상품 정보는 AppConstant 클래스에 있습니다.
 * 4. 애플리케이션 위변조 체크를 위해서 개발자 센터에 등록하는 앱은 사이닝이 된 APK여야 합니다. (앱 사이닝 없이 테스트를 위해서는 Sandbox 계정을 등록하여 확인하시면 됩니다.)
 */
class MainActivity : AppCompatActivity(), PurchaseManager.Callback {

    private val TAG = "MainActivity"

    private val mLuckyNumberView: LuckyNumberView by lazy { findViewById(R.id.luckyView) }
    private val mResultNumberView: ResultNumberView by lazy { findViewById(R.id.resultView) }

    private val mPlayButton: CardView by lazy { findViewById(R.id.playBtn) }
    private var mProgressDialog: ProgressDialog? = null

    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mViewModel: MainViewModel

    private var mSubsPurchaseData: PurchaseData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mViewModel = ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java]
        mBinding.view = this
        mBinding.vm = mViewModel

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.title = ""
        setSupportActionBar(toolbar)


        // 첫 시작시 20코인 지급
        if (isFirstLaunched()) {
            updateCoin(20)
            updateFirstLaunched()
        } else {
            mViewModel.savedCoin.set(getCurrentCoin())
        }

        updatePlayButtonText(getSubscriptionItem() != null)
        showProgressDialog()

        /**
         * SDK 로그레벨 설정
         * 경고! 이 코드는 보안 취약점이 발생할 수 있으므로 릴리스 빌드에서는 반드시 삭제해야 합니다.
         * 개발 환경에서만 사용하십시오.
         */
        Logger.setLogLevel(Log.VERBOSE)
        detectStoreEnvironment()

        mViewModel.setAuthManager(AuthManager(this))
        mViewModel.fetch {
            if (it.isSuccessful){
                mViewModel.setPurchaseManager(PurchaseManager(this@MainActivity, this@MainActivity))
            } else {
                showDialog(getString(R.string.msg_signin_failed) + ": ${it.message} (${it.code})")
            }
        }

        addObservers()
    }

    /**
     * 현재 앱이 설치된 스토어 환경을 감지하고 해당 정보를 로그로 출력하는 함수입니다.
     */
    private fun detectStoreEnvironment() {
        val storeType = StoreEnvironment.getStoreType(this)
        when (storeType) {
            StoreType.ONESTORE -> println("ONE Store에서 설치된 앱입니다.")
            StoreType.VENDING -> println("Google Play Store에서 설치된 앱입니다.")
            StoreType.ETC -> println("기타 스토어에서 설치된 앱입니다.")
            StoreType.UNKNOWN -> println("스토어 정보를 알 수 없습니다.")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        removeObservers()
        mViewModel.destroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.manage_recurring) {
            // 구독상품 메뉴 화면으로 이동
            ManageProductDialog(this, mSubsPurchaseData,
                object : ManageProductDialog.UserCallback {
                    override fun openSubscriptionsMenu(purchaseData: PurchaseData?) {
                        mViewModel.openSubscriptionMenu(purchaseData)
                    }
                }).show()
            return true
        }
        return false
    }

    private fun addObservers() {
        mViewModel.showProgress.observe(this) { isShow ->
            if (isShow) {
                showProgressDialog()
            } else {
                dismissProgressDialog()
            }
        }
        mViewModel.errorIapResult.observe(this) { iapResult ->
            /**
             * IAP Result의 에러 결과에 맞는 액션을 정의합니다.
             */
            showDialogForIapResult(iapResult)
        }
        mViewModel.errorAuthResult.observe(this) { signInResult ->
            /**
             * SignIn Result의 에러 결과에 맞는 액션을 정의합니다.
             */
            showDialogForAuthResult(signInResult)
        }
        mViewModel.errorMessage.observe(this) { code ->
            /**
             * 에러 결과에 맞는 액션을 정의합니다.
             */
            if (code == Error.ERROR_EMPTY) {
                showDialog(getString(R.string.msg_product_detail_not_found))
            }
        }
    }

    private fun removeObservers() {
        mViewModel.showProgress.removeObservers(this)
        mViewModel.errorIapResult.removeObservers(this)
        mViewModel.errorAuthResult.removeObservers(this)
        mViewModel.errorMessage.removeObservers(this)
    }

    private fun updatePlayButtonText(isSubs: Boolean) {
        if (!isSubs) {
            (mPlayButton.findViewById<View>(R.id.play_text) as TextView).setText(R.string.btn_number_generate_inapp_primary)
            (mPlayButton.findViewById<View>(R.id.play_desc) as TextView).setText(R.string.btn_number_generate_inapp_desc)
        } else {
            (mPlayButton.findViewById<View>(R.id.play_text) as TextView).setText(R.string.btn_number_generate_subs_primary)
            (mPlayButton.findViewById<View>(R.id.play_desc) as TextView).setText(R.string.btn_number_generate_subs_desc)
        }
    }

    private fun updateCoinsPurchased(productId: String, quantity: Int) {
        val coins = getPurchasedCoins(productId, quantity)
        updateCoin(coins)
    }

    private fun getPurchasedCoins(productId: String, quantity: Int): Int {
        return when (productId) {
            AppConstants.InappType.PRODUCT_INAPP_500 -> 5 * quantity
            AppConstants.InappType.PRODUCT_INAPP_510 -> 10 * quantity
            else -> 0
        }
    }

    private fun showProgressDialog() {
        if (!isFinishing && !isShowingProgressDialog()) {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog(this)
            }
            mProgressDialog!!.setMessage("Service connection...")
            mProgressDialog!!.show()
        }
    }

    private fun isShowingProgressDialog(): Boolean {
        return mProgressDialog != null && mProgressDialog!!.isShowing
    }

    private fun dismissProgressDialog() {
        if (isShowingProgressDialog()) mProgressDialog!!.dismiss()
    }

    private fun showDialog(message: CharSequence) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .create().show()
    }

    private fun showDialog(message: CharSequence, listener: DialogInterface.OnClickListener) {
        AlertDialog.Builder(this)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, listener)
            .setNegativeButton(android.R.string.cancel, null)
            .create().show()
    }

    private fun showDialogForIapResult(iapResult: IapResult) {
        val message = iapResult.message + " (" + iapResult.responseCode + ")"
        showDialog(message)
    }

    private fun showDialogForAuthResult(signInResult: SignInResult) {
        val message = signInResult.message + " (" + signInResult.code + ")"
        showDialog(message)
    }

    fun showBuyProductDialog() {
        mViewModel.fetchProductDetail { productDetails ->
            Collections.sort(productDetails) { o1: ProductDetail, o2: ProductDetail ->
                o1.type.compareTo(o2.type)
            }

//            for ((index, item) in productDetails.withIndex()) {
//                Log.d(TAG, "ProductDetail[$index]=$item")
//            }

            BuyProductDialog(this@MainActivity, productDetails,
                object : BuyProductDialog.OnItemClickListener {
                    override fun onClick(item: ProductDetail) {
                        buy(item.productId, item.type)
                    }
                }).show()
        }
    }

    fun playGame() {
        val subsItem = getSubscriptionItem()
        if (subsItem == null && getCurrentCoin() < 5) {
            showDialog(getString(R.string.msg_alert_no_coin))
            return
        }

        if (subsItem == null) {
            updateCoin(-5)
        }

        val luckyBalls: List<Int> = LuckyUtils.luckyNumbers
        val jackpot: List<Int>
        val num = Random().nextInt(1000)

        jackpot = if (num in 1..18 || num in 402..418 || num in 802..818) {
            luckyBalls
        } else {
            mutableListOf()
        }
        val myBalls: List<List<Int>> = LuckyUtils.getSuggestNumbers(5, jackpot)

        mLuckyNumberView.setNumber(luckyBalls)
        mResultNumberView.clear()
        mResultNumberView.setData(luckyBalls, myBalls)

        val earningCoin = LuckyUtils.getWonCoin(luckyBalls, myBalls)
        if (earningCoin > 0) {
            updateCoin(earningCoin)
            val message: CharSequence =
                Html.fromHtml(resources.getString(R.string.msg_earning_coins, earningCoin))
            showDialog(message)
        }
    }

    private fun buy(productId: String, @ProductType productType: String) {
        Log.d(TAG, "buyProduct() - productId:$productId productType: $productType")
        /*
         * TODO: AppSecurity.generatePayload() 는 예제일 뿐, 각 개발사의 규칙에 맞는 payload를 생성하여야 한다.
         *
         * 구매요청을 위한 Developer payload를 생성합니다.
         * Developer Payload 는 상품의 구매 요청 시에 개발자가 임의로 지정 할 수 있는 문자열입니다.
         * 이 Payload 값은 결제 완료 이후에 응답 값에 다시 전달 받게 되며 결제 요청 시의 값과 차이가 있다면 구매 요청에 변조가 있다고 판단 하면 됩니다.
         * Payload 검증을 통해 Freedom 과 같은 변조 된 요청을 차단 할 수 있으며, Payload 의 발급 및 검증 프로세스를 자체 서버를 통해 이루어 지도록합니다.
         * 입력 가능한 Developer Payload는 최대 200byte까지 입니다.
         */
        val devPayload = AppSecurity.generatePayload()

        // 구매 후 dev payload를 검증하기 위하여 프리퍼런스에 임시로 저장합니다.
        savePayloadString(devPayload)
        mViewModel.buyProduct(devPayload, productId, productType)
    }

    /*
     * 에러 코드 중 로그인이 필요로 할 경우 AuthManager.launchSignInFlow API를 이용하여 명시적 로그인을 수행합니다.
     * AuthManager.launchSignInFlow API를 호출할 경우 UI적으로 스토어 로그인화면이 뜰 수 있습니다.
     * 개발사에서는 로그인 성공 시 IapResultListener 를 통해 넘겨준 IapResult를 파싱하고 다음을 수행합니다.
     */
    private fun launchLoginFlow(executeOnSuccess: () -> Unit) {
        showDialog("스토어 계정 정보를 확인 할 수 없습니다. 로그인 하시겠습니까?") { _: DialogInterface?, _: Int ->
            mViewModel.launchLoginFlow(executeOnSuccess)
        }
    }

    // =================================================================================================================
    // PurchaseManager.Callback implements
    // =================================================================================================================

    override fun onPurchaseClientSetupFinished() {
        dismissProgressDialog()
        mViewModel.getStoreCode { saveStoreCode(it) }
        mViewModel.fetchPurchases()
    }

    override fun onPurchaseSucceed(purchases: List<PurchaseData>) {
        dismissProgressDialog()
        for (purchase in purchases) {
            // 구매 데이터의 유효성을 검사합니다.
            if (AppSecurity.verifyPurchase(AppSecurity.PUBLIC_KEY, purchase.originalJson, purchase.signature)) {
                val productId = purchase.productId

                if (AppConstants.SubsType.PRODUCT_SUBS_500 == productId) {
                    Log.d(TAG, "purchase isAcknowledged => ${purchase.isAcknowledged}")
                    if (purchase.isAcknowledged) {
                        mSubsPurchaseData = purchase
                        saveSubscriptionItem(purchase)
                        updatePlayButtonText(getSubscriptionItem() != null)
                    } else {
                        // 구독 상품을 구매했을 때는 반드시 API acknowledge 요청해야 합니다.
                        mViewModel.acknowledge(purchase)
                    }
                } else {
                    // 관리형상품(inapp)의 구매완료 이후 또는 구매내역조회 이후 소비되지 않는 관리형상품에 대해서 소비를 진행합니다.
                    mViewModel.consume(purchase)
                }
            } else {
                showDialog(getString(R.string.msg_alert_signature_invalid))
            }
        }
    }

    override fun onPurchaseFailed(iapResult: IapResult) {
        // 구매 실패에 대한 동작 정의가 필요합니다.
        dismissProgressDialog()
    }

    override fun onConsumeFinished(purchaseData: PurchaseData, iapResult: IapResult) {
        dismissProgressDialog()
        if (iapResult.isSuccess) {
            updateCoinsPurchased(purchaseData.productId, purchaseData.quantity)
            val message = Html.fromHtml(
                String.format(
                    getString(R.string.msg_purchase_success_inapp),
                    getPurchasedCoins(purchaseData.productId, purchaseData.quantity)
                )
            )
            showDialog(message)
        } else {
            showDialogForIapResult(iapResult)
        }
    }

    override fun onAcknowledgeFinished(purchaseData: PurchaseData, iapResult: IapResult) {
        dismissProgressDialog()
        if (iapResult.isSuccess) {
            if (AppConstants.SubsType.PRODUCT_SUBS_500 == purchaseData.productId) {
                mSubsPurchaseData = purchaseData
                showDialog(Html.fromHtml(getString(R.string.msg_alert_move_subscription_detail)))
            }
        } else {
            showDialogForIapResult(iapResult)
        }
    }

    /**
     * TODO : 인증 flow 처리 후 각 단계별로 로직을 재 수행해야 합니다.
     *
     * 앱 진입 시 혹은 상점 진입 시, AuthManager를 통해 인증 flow를 구성하고 시작한다면 need login을 경함할 일이 없으나,
     * 사용자가 앱 사용 중 원스토어 로그아웃을 할 경우 발생될 수 있습니다.
     * 인증 처리가 힘들 경우, 사용자에게 원스토어 로그인이 필요하다는 안내 정도를 추천합니다.
     */
    override fun onNeedLogin() {
        dismissProgressDialog()
        launchLoginFlow {
            // 이후 로직 필요
        }
    }

    override fun onNeedUpdate() {
        dismissProgressDialog()
        mViewModel.updateOrInstallPaymentModule()
    }

    override fun onError(message: String) {
        dismissProgressDialog()
        showDialog(message)
    }

    // =================================================================================================================
    // SharedPreferences func
    // =================================================================================================================

    private fun saveSubscriptionItem(purchaseData: PurchaseData?) {
        val spe = getPreferences(MODE_PRIVATE).edit()
        if (purchaseData == null) {
            spe.remove(AppConstants.SubsType.PRODUCT_SUBS_500)
            spe.putBoolean(AppConstants.KEY_MODE_SUBSCRIPTION, false)
            updatePlayButtonText(false)
        } else {
            val gson = Gson()
            val json = gson.toJson(purchaseData)
            spe.putString(AppConstants.SubsType.PRODUCT_SUBS_500, json)
            spe.putBoolean(AppConstants.KEY_MODE_SUBSCRIPTION, true)
            updatePlayButtonText(true)
        }
        spe.apply()
    }

    private fun getSubscriptionItem(): PurchaseData? {
        val sp = getPreferences(MODE_PRIVATE)
        val gson = Gson()
        val json = sp.getString(AppConstants.SubsType.PRODUCT_SUBS_500, "")
        if (TextUtils.isEmpty(json)) {
            return null
        }
        Log.d(TAG, "getMonthlyItem: $json")
        return gson.fromJson(json, PurchaseData::class.java)
    }

    private fun isFirstLaunched(): Boolean {
        val sp = getPreferences(MODE_PRIVATE)
        Log.d(TAG, "isFirstLaunched() - " + sp.getBoolean(AppConstants.KEY_IS_FIRST, true))
        return sp.getBoolean(AppConstants.KEY_IS_FIRST, true)
    }

    private fun updateFirstLaunched() {
        val spe = getPreferences(MODE_PRIVATE).edit()
        spe.putBoolean(AppConstants.KEY_IS_FIRST, false)
        spe.apply()
    }

    fun updateCoin(coin: Int) {
        val sp = getPreferences(MODE_PRIVATE)
        val spe = getPreferences(MODE_PRIVATE).edit()
        var savedCoins = sp.getInt(AppConstants.KEY_TOTAL_COIN, 0)
        savedCoins += coin
        spe.putInt(AppConstants.KEY_TOTAL_COIN, savedCoins)
        spe.apply()
        mViewModel.savedCoin.set(savedCoins)
    }

    private fun getCurrentCoin(): Int {
        val sp = getPreferences(MODE_PRIVATE)
        val coin = sp.getInt(AppConstants.KEY_TOTAL_COIN, 0)
        Log.d(TAG, "getCurrentCoin() coin: $coin")
        return coin
    }

    private fun savePayloadString(payload: String) {
        val spe = getPreferences(MODE_PRIVATE).edit()
        spe.putString(AppConstants.KEY_PAYLOAD, payload)
        spe.apply()
    }

    private fun saveStoreCode(storeCode: String) {
        val spe = getPreferences(MODE_PRIVATE).edit()
        spe.putString(AppConstants.KEY_STORE_CODE, storeCode)
        spe.apply()
    }
}