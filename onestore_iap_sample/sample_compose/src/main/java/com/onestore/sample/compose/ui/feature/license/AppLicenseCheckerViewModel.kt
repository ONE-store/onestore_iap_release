package com.onestore.sample.compose.ui.feature.license

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.onestore.sample.compose.manager.license.LicenseCheckerManager
import com.onestore.sample.compose.model.AppLicenseState
import com.onestore.sample.compose.ui.feature.license.model.AppLicenseCheckMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 앱 라이선스 검증 화면의 UI 상태 및 비즈니스 로직을 관리하는 ViewModel
 *
 * 앱 라이선스 검증 기능을 제공합니다.
 *
 * @property licenseCheckerManager 라이선스 검증 매니저
 */
class AppLicenseCheckerViewModel(
    private val licenseCheckerManager: LicenseCheckerManager
): ViewModel() {
    
    private val _uiState = MutableStateFlow(AppLicenseCheckUiState())
    
    /** 앱 라이선스 검증 화면의 UI 상태 */
    val uiState: StateFlow<AppLicenseCheckUiState> = _uiState.asStateFlow()
    
    /** LicenseCheckerManager의 앱 라이선스 상태 */
    val appLicenseState: StateFlow<AppLicenseState> = licenseCheckerManager.appLicenseState

    /**
     * 선택된 라이선스 검증 모드를 설정합니다
     *
     * @param mode 선택할 검증 모드
     */
    fun setSelectedMode(mode: AppLicenseCheckMode) {
        _uiState.update { it.copy(selectedMode = mode) }
    }

    /**
     * 라이선스를 조회합니다 (캐시 정책에 따름)
     *
     * @param activity 라이선스 조회를 수행할 액티비티
     */
    fun queryLicense(activity: Activity) {
        licenseCheckerManager.queryLicense(activity)
    }

    /**
     * 엄격한 라이선스 조회를 수행합니다 (서버 확인)
     *
     * @param activity 라이선스 조회를 수행할 액티비티
     */
    fun strictQueryLicense(activity: Activity) {
        licenseCheckerManager.strictQueryLicense(activity)
    }
}

/**
 * 앱 라이선스 검증 화면의 UI 상태
 *
 * @property selectedMode 현재 선택된 검증 모드
 */
data class AppLicenseCheckUiState(
    val selectedMode: AppLicenseCheckMode = AppLicenseCheckMode.NONE
)