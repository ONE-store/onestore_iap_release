package com.onestore.sample.compose.ui.main.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.onestore.sample.compose.manager.iap.PurchaseManager
import com.onestore.sample.compose.manager.license.LicenseCheckerManager
import com.onestore.sample.compose.ui.feature.license.AppLicenseCheckerViewModel
import com.onestore.sample.compose.ui.feature.product.detail.ProductDetailViewModel
import com.onestore.sample.compose.ui.feature.product.list.ProductListViewModel
import com.onestore.sample.compose.ui.feature.purchase.PurchaseWaitingListViewModel
import com.onestore.sample.compose.ui.feature.setting.SettingViewModel
import com.onestore.sample.compose.ui.main.MainActivityViewModel

/**
 * 앱 전체에서 사용되는 ViewModel들을 생성하는 Factory 클래스
 *
 * 이 클래스는 [ViewModelProvider.Factory]를 구현하여 앱의 모든 ViewModel 인스턴스를
 * 생성하는 역할을 담당합니다. [PurchaseManager]와 [LicenseCheckerManager]를 필요한 ViewModel에 주입하여
 * 통일된 구매 관리 및 라이선스 검증 기능을 제공합니다.
 *
 * @property purchaseManager 구매 및 결제 관련 로직을 처리하는 매니저
 * @property licenseCheckerManager 라이선스 검증 관련 로직을 처리하는 매니저
 *
 * @see ViewModelProvider.Factory
 * @see PurchaseManager
 * @see LicenseCheckerManager
 */
class AppViewModelFactory(
    private val purchaseManager: PurchaseManager,
    private val licenseCheckerManager: LicenseCheckerManager
) : ViewModelProvider.Factory {

    /**
     * 요청된 ViewModel 클래스의 인스턴스를 생성합니다.
     *
     * 이 메서드는 Android Framework에 의해 호출되며, ViewModel의 타입에 따라
     * 적절한 의존성을 주입하여 인스턴스를 생성합니다.
     *
     * @param T 생성할 ViewModel의 타입
     * @param modelClass 생성할 ViewModel의 클래스 객체
     * @param extras ViewModel 생성에 필요한 추가 정보 (SavedStateHandle 등)
     * @return 생성된 ViewModel 인스턴스
     * @throws IllegalArgumentException 지원하지 않는 ViewModel 타입인 경우
     *
     * @see MainActivityViewModel
     * @see SettingViewModel
     * @see ProductListViewModel
     * @see ProductDetailViewModel
     * @see AppLicenseCheckerViewModel
     * @see PurchaseWaitingListViewModel
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
        return when (modelClass) {
            MainActivityViewModel::class.java ->
                MainActivityViewModel(purchaseManager, licenseCheckerManager) as T
            SettingViewModel::class.java ->
                SettingViewModel(purchaseManager) as T
            ProductListViewModel::class.java ->
                ProductListViewModel(purchaseManager) as T
            ProductDetailViewModel::class.java ->
                ProductDetailViewModel(purchaseManager, extras.createSavedStateHandle()) as T
            AppLicenseCheckerViewModel::class.java ->
                AppLicenseCheckerViewModel(licenseCheckerManager) as T
            PurchaseWaitingListViewModel::class.java ->
                PurchaseWaitingListViewModel(purchaseManager) as T
            else -> throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
        }
    }
}
