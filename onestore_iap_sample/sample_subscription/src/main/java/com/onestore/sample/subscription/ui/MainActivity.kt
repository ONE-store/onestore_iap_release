package com.onestore.sample.subscription.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.gaa.sdk.auth.SignInResult
import com.gaa.sdk.iap.IapResult
import com.gaa.sdk.iap.ProductDetail
import com.gaa.sdk.iap.PurchaseData
import com.gaa.sdk.iap.PurchaseFlowParams
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.tabs.TabLayout
import com.onestore.sample.subscription.R
import com.onestore.sample.subscription.auth.AuthManager
import com.onestore.sample.subscription.base.BaseRecyclerViewAdapter
import com.onestore.sample.subscription.billing.PurchaseManager
import com.onestore.sample.subscription.common.Constant
import com.onestore.sample.subscription.common.Error
import com.onestore.sample.subscription.common.ViewModelFactory
import com.onestore.sample.subscription.databinding.ActivityMainBinding

/**
 * Subscription Sample
 * <p>
 * 스토어 In-app purchasing v21의 구독 결제 샘플앱
 * </p>
 *
 * <p>
 * In-app purchasing v21에서만 제공되는 구독결제 기능 구현에 필요한 샘플앱입니다.
 * 메인화면에서는 SHOP/SUBS MGMT 두가지 탭으로 구성되어 있습니다.
 * </p>
 *
 * <p>
 * SHOP 탭에서는 등록된 구독상품 리스트가 조회됩니다.<br/>
 * 조회된 구독상품을 클릭했을 경우, 구독상품을 결제할 수 있습니다.<br/>
 * 구독상품은 한 번의 결제 이후 취소하지 않는다면 재결제가 이루어지지 않기 때문에 결제 완료시 버튼상태가 비활성화됩니다.<br/>
 * 결제한 구독상품 이외의 다른 구독상품은 결제가능합니다.<br/>
 * </p>
 *
 * <p>
 * SUB MGMT 탭에서는 구독 관리 메뉴로 이동할 수 있는 버튼이 있습니다.
 * 결제한 구독상품을 보여주는 화면이며, 해당 화면에서 구독상품을 취소할 수도 있습니다.
 * </p>
 *
 * <p>
 * 메인화면 우측 상단에는 구독상품리스트 상태를 재확인하는 Refresh 버튼이 있고, 설정화면 이동 메뉴가 존재합니다.
 * 설정화면에서는 구독상품의 업/다운그레이드 진행시 적용되는 비례배분옵션을 재설정할 수 있습니다.
 * 기본값은 IMMEDIATE_WITH_TIME_PRORATION(즉시) 입니다.
 * </p>
 *
 * <p>
 * 업/다운그레이드란?
 * 정기 결제 상품을 변경할 때 사용되는 단어입니다.
 * 업/다운그레이드의 구분 기준은 상품가격과 구독기간이며, 상품가격이 현재 구독한 상품보다 높거나 구독기간이 길 경우, 업그레이드라고 하고
 * 현재 구독한 상품보다 상품가격이 낮거나 구독기간이 짧을 경우, 다운그레이드라고 합니다.
 * </p>
 *
 * - 구독 샘플앱 구동 전 준비 사항 -
 * 1. 샘플앱의 패키지 이름을 변경합니다.
 * 3. AppSecurity 클래스의 getPublicKey()메서드를 이용하기 위하여 jin/public_keys.c 코드 상의 변경된 패키지 이름에 맞게 Native 메서드 명을 변경해줍니다.
 * 2. 스토어 개발자센터에 애플리케이션을 등록 후 base64 public key 값을 jin/public_keys.c 코드 내에 붙여넣습니다.
 * 4. 스토어 개발자센터에 상품을 등록합니다. 샘플앱의 경우 상품 정보는 resource/strings.xml에 inapp_products_ids string array에 있습니다.
 * 5. 애플리케이션 위변조 체크를 위해서 개발자 센터에 등록하는 앱은 사이닝이 된 APK여야 합니다. (앱 사이닝 없이 테스트를 위해서는 Sandbox 계정을 등록하여 확인하시면 됩니다.)
 */
class MainActivity : AppCompatActivity(), PurchaseManager.Callback {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by lazy { ViewModelProvider(this, ViewModelFactory())[MainViewModel::class.java] }

    val selectedProductListener = object : BaseRecyclerViewAdapter.OnItemClickListener {
        override fun <T> onItemClick(position: Int, item: T) {
            if (item is ProductDetail) {
                Log.e(TAG, "item ===> " + item.title)
                /**
                 * 구독한 상품이 없을 경우 선택한 상품 결제합니다.
                 * 구독한 상품이 있을 경우, 업/다운그레이드를 진행합니다.
                 */
                if (viewModel.purchaseData.isEmpty()) {
                    viewModel.purchase(item)
                } else {
                    val purchase = viewModel.purchaseData[0]
                    viewModel.updateSubscription(purchase, item, getOption())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        binding.view = this@MainActivity
        binding.vm = viewModel

        setSupportActionBar(binding.toolBar)

        binding.tlNav.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position == 1) {
                    viewModel.isSubscriptionManagement.set(true)
                } else {
                    viewModel.isSubscriptionManagement.set(false)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        viewModel.productIds = resources.getStringArray(R.array.inapp_products_ids).toList()
        viewModel.setAuth(AuthManager(this))

        // 로그인 확인
        doLogin()

        addObservers()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.refresh -> {
                Log.d(TAG, "refresh")
                viewModel.refresh()
                true
            }
            R.id.setting -> {
                Log.d(TAG, "setting")
                goSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun doLogin() {
        viewModel.fetchAuth { sigInResult ->
            if (sigInResult.isSuccessful) {
                viewModel.fetchPurchaseManager(PurchaseManager(this, this@MainActivity))
            } else {
                /**
                 * Purchase Client cannot query of InApp Products,
                 * when fail or cancel of ONEstore login for user.
                 * If you received other result code(not success),
                 * you should not show of InApp products.
                 */
                showDialogForSignInResult(sigInResult)
            }
        }
    }

    private fun goSettings() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun addObservers() {
        viewModel.errorIapResult.observe(this) { iapResult ->
            /**
             * IAP Result의 에러 결과에 맞는 액션을 정의합니다.
             */
            showDialogForIapResult(iapResult)
        }

        viewModel.errorAuthResult.observe(this) { signResult ->
            /**
             * SignIn Result의 에러 결과에 맞는 액션을 정의합니다.
             */
            showDialogForSignInResult(signResult)
        }
        
        viewModel.errorMessage.observe(this) { code ->
            /**
             * 에러 결과에 맞는 액션을 정의합니다.
             */
            if (code == Error.ERROR_EMPTY) {
                showDialog(getString(R.string.msg_product_detail_not_found))
            }
        }
    }

    private fun removeObservers() {
        viewModel.errorIapResult.removeObservers(this)
        viewModel.errorAuthResult.removeObservers(this)
        viewModel.errorMessage.removeObservers(this)
    }

    override fun onPurchaseClientSetupFinished() {
        viewModel.getStoreInfo {
            saveStoreCode(it)
        }
        viewModel.fetchPurchaseData()
    }

    override fun onConsumeFinished(purchaseData: PurchaseData, iapResult: IapResult) {
        // 개발사에 따라 상품 소비 완료 후 로직을 정의합니다.
    }

    override fun onPurchaseSucceed(purchases: List<PurchaseData>) {
        Log.e(TAG, "onPurchaseUpdated ===> $purchases")
        if (purchases.isNotEmpty() && !purchases[0].isAcknowledged) {
            viewModel.launchAcknowledge(purchases[0])
        } else {
            viewModel.purchaseData.clear()
            viewModel.purchaseData.addAll(purchases)
        }
    }

    override fun onNeedLogin() {
        Log.e(TAG, "onNeedLogin")
        doLogin()
    }

    override fun onNeedUpdate() {
        Log.e(TAG, "onNeedUpdate")
        viewModel.doUpdateOrInstall()
    }

    override fun onError(message: String) {
        Log.e(TAG, "onError ===> $message")
        showDialog(message)
    }

    override fun onAcknowledgeFinished(purchaseData: PurchaseData, iapResult: IapResult) {
        Log.e(TAG, "onAcknowledgeFinished")
        if (iapResult.isSuccess) {
            viewModel.purchaseData.clear()
            viewModel.purchaseData.add(purchaseData)
            viewModel.fetchPurchaseData()
            showDialog("Paid successfully.")
        } else {
            showDialogForIapResult(iapResult)
        }
    }

    override fun onPurchaseFailed(iapResult: IapResult) {
        // 이후 로직 필요
    }

    override fun onDestroy() {
        removeObservers()
        viewModel.destroy()
        super.onDestroy()
    }

    private fun saveStoreCode(storeCode: String) {
        val editor = getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.putString(Constant.KEY_STORE_CODE, storeCode)
        editor.apply()
    }

    private fun getOption(): Int {
        val sp = getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE)
        val option = sp.getInt(Constant.KEY_OPTION, -1)
        return if (option != -1) {
            option
        } else {
            PurchaseFlowParams.ProrationMode.IMMEDIATE_WITH_TIME_PRORATION
        }
    }

    private fun showDialog(message: String?) {
        MaterialAlertDialogBuilder(this)
            .setMessage(message)
            .setPositiveButton(android.R.string.ok, null)
            .create()
            .show()
    }

    private fun showDialogForIapResult(iapResult: IapResult) {
        showDialog(iapResult.message + " (" + iapResult.responseCode + ")")
    }

    private fun showDialogForSignInResult(signInResult: SignInResult) {
        showDialog(signInResult.message + " (" + signInResult.code + ")")
    }
}