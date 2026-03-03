package com.onestore.sample.compose.ui.main

import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.onestore.sample.compose.manager.iap.PurchaseManager
import com.onestore.sample.compose.manager.iap.PurchaseManagerUiState
import com.onestore.sample.compose.manager.license.LicenseCheckerManager
import com.onestore.sample.compose.model.AppLicenseState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * MainActivity의 UI 상태 및 비즈니스 로직을 관리하는 ViewModel
 *
 * PurchaseManager와 LicenseCheckerManager의 기능을 래핑하여 MainActivity에서 사용할 수 있도록 제공합니다.
 *
 * @property purchaseManager 인앱 결제 관리 매니저
 * @property licenseCheckerManager 라이선스 검증 관리 매니저
 */
class MainActivityViewModel(
    private val purchaseManager: PurchaseManager,
    private val licenseCheckerManager: LicenseCheckerManager
): ViewModel() {

    /** PurchaseManager의 UI 상태 */
    val purchaseUiState: StateFlow<PurchaseManagerUiState> = purchaseManager.uiState

    /** LicenseCheckerManager의 앱 라이선스 상태 */
    val appLicenseState: StateFlow<AppLicenseState> = licenseCheckerManager.appLicenseState

    /** MainActivity 자체 UI 상태 (앱바 등) */
    private val _uiState = MutableStateFlow(MainActivityUiState())
    val uiState: StateFlow<MainActivityUiState> = _uiState.asStateFlow()

    /**
     * 액티비티 초기화 시 호출
     *
     * @param activity 초기화할 액티비티
     */
    fun onActivityInit(activity: ComponentActivity) {
        purchaseManager.onActivityInit(activity)
        licenseCheckerManager.onActivityInit(activity)
    }

    /**
     * 액티비티 Resume 시 호출
     *
     * @param activity Resume된 액티비티
     */
    fun onActivityResume(activity: ComponentActivity) {
        purchaseManager.onActivityResume(activity)
        
        // 앱 라이선스가 아직 승인되지 않은 경우 자동으로 라이선스 조회
        if (appLicenseState.value !is AppLicenseState.Granted) {
            licenseCheckerManager.queryLicense(activity)
        }
    }

    /**
     * 앱바 상태 업데이트
     *
     * @param title 앱바 제목
     * @param icon 앱바 아이콘
     * @param showBackButton 뒤로가기 버튼 표시 여부
     */
    fun updateAppBar(title: String, icon: ImageVector, showBackButton: Boolean) {
        _uiState.update { it.copy(
            appBarTitle = title,
            appBarIcon = icon,
            isBackButtonVisible = showBackButton
        ) }
    }

    /**
     * 공통 메시지 다이얼로그 숨김
     */
    fun hideMessage() {
        purchaseManager.hideMessage()
    }

    /**
     * 로그인 요청 다이얼로그 숨김
     */
    fun hideLogin() {
        purchaseManager.hideLogin()
    }

    /**
     * 업데이트 요청 다이얼로그 숨김
     */
    fun hideUpdate() {
        purchaseManager.hideUpdate()
    }

    /**
     * 로딩 인디케이터 숨김
     */
    fun hideLoading() {
        purchaseManager.hideLoading()
    }

    /**
     * 사용자 로그인 플로우 실행
     *
     * @param activity 로그인 플로우를 시작할 액티비티
     */
    fun launchSignInFlow(activity: ComponentActivity) {
        purchaseManager.launchSignInFlow(activity)
    }

    /**
     * 앱 업데이트 또는 설치 플로우 실행
     *
     * @param activity 업데이트/설치 플로우를 시작할 액티비티
     */
    fun launchUpdateOrInstallFlow(activity: ComponentActivity) {
        purchaseManager.launchUpdateOrInstallFlow(activity)
    }
}