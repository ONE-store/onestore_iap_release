package com.onestore.sample.compose.ui.feature.setting

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.onestore.sample.compose.manager.iap.PurchaseManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * 설정 화면의 UI 상태 및 비즈니스 로직을 관리하는 ViewModel
 *
 * 앱 설정과 관련된 기능들을 제공합니다.
 *
 * @property purchaseManager 인앱 결제 및 라이선스 관리 매니저
 */
class SettingViewModel(
    private val purchaseManager: PurchaseManager
): ViewModel() {

    private val _uiState = MutableStateFlow(SettingUiState())
    
    /** 설정 화면의 UI 상태 */
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    /**
     * 로그 활성화 상태를 업데이트합니다
     *
     * @param enabled 로그 활성화 여부
     */
    fun updateLogEnable(enabled: Boolean) {
        _uiState.update { it.copy(logEnabled = enabled) }
        purchaseManager.setLogEnable(enabled)
    }

    /**
     * 원스토어 정기결제 관리 화면으로 이동합니다
     *
     * @param activity 정기결제 관리 화면을 시작할 액티비티
     */
    fun launchManageSubscription(activity: Activity) {
        purchaseManager.launchManageSubscription(activity)
    }
}

/**
 * 설정 화면의 UI 상태
 *
 * @property logEnabled 로그 활성화 여부
 */
data class SettingUiState(
    val logEnabled: Boolean = true
)
