package com.onestore.sample.compose.manager.license

import android.app.Activity
import com.onestore.extern.licensing.AppLicenseChecker
import com.onestore.extern.licensing.LicenseCheckerListener
import com.onestore.sample.compose.common.Constant
import com.onestore.sample.compose.model.AppLicenseState
import com.onestore.sample.compose.util.printLog
import com.onestore.sample.compose.util.runOnMainOnce
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

private const val TAG = "LicenseCheckerManager"

/**
 * LicenseCheckerManager는 앱 라이선스 검증 기능을 전담하는 Helper Class입니다.
 *
 * ##주요 기능
 * - 유료 앱에 대한 라이선스 유효성 검증
 * - 사용자 앱 구매 이력 기반의 라이선스 상태 관리
 *
 * ##사용 대상
 * - 원스토어에 유료 앱(Paid App)으로 등록하여 배포하는 앱/게임
 *
 * ##비대상
 * - 무료 앱
 *
 * ## 구현 인터페이스
 * - [LicenseCheckerListener]: 라이선스 검증 결과 콜백 처리
 *
 * @see AppLicenseChecker
 */
class LicenseCheckerManager : LicenseCheckerListener {

    private var appLicenseChecker: AppLicenseChecker? = null

    /**
     * 앱 라이선스 상태
     */
    private val _appLicenseState = MutableStateFlow<AppLicenseState>(AppLicenseState.None)
    val appLicenseState: StateFlow<AppLicenseState> = _appLicenseState.asStateFlow()

    /**
     * 액티비티 초기화 시 호출합니다.
     * AppLicenseChecker를 초기화합니다.
     *
     * @param activity 초기화할 액티비티
     */
    fun onActivityInit(activity: Activity) {
        printLog(TAG, "onActivityInit publicKey=${Constant.PUBLIC_KEY}")
        runOnMainOnce {
            strictQueryLicense(activity)
        }
    }

    /**
     * AppLicenseChecker 인스턴스를 확인하고 필요 시 초기화합니다.
     *
     * @param activity 액티비티
     */
    private fun ensureAppLicenseChecker(activity: Activity) {
        if (appLicenseChecker == null) {
            appLicenseChecker = AppLicenseChecker.get(activity, Constant.PUBLIC_KEY, this)
        }
    }

    /**
     * 라이선스 조회를 수행합니다.
     *
     * @param activity 액티비티
     */
    fun queryLicense(activity: Activity) {
        ensureAppLicenseChecker(activity)
        appLicenseChecker?.queryLicense()
    }

    /**
     * 엄격한 라이선스 조회를 수행합니다.
     *
     * @param activity 액티비티
     */
    fun strictQueryLicense(activity: Activity) {
        ensureAppLicenseChecker(activity)
        appLicenseChecker?.strictQueryLicense()
    }

    /**
     * 라이선스 승인 콜백입니다.
     *
     * @param license 라이선스 정보
     * @param signature 서명 정보
     */
    override fun granted(license: String?, signature: String?) {
        printLog(TAG, "LicenseCheckerListener granted license=$license signature=$signature")
        _appLicenseState.update {
            AppLicenseState.Granted(license, signature)
        }
    }

    /**
     * 라이선스 거부 콜백입니다.
     */
    override fun denied() {
        printLog(TAG, "LicenseCheckerListener denied")
        _appLicenseState.update {
            AppLicenseState.Denied
        }
    }

    /**
     * 라이선스 오류 콜백입니다.
     *
     * @param code 오류 코드
     * @param message 오류 메시지
     */
    override fun error(code: Int, message: String?) {
        printLog(TAG, "LicenseCheckerListener error code=$code message='$message'")
        _appLicenseState.update {
            AppLicenseState.Error(code.toString(), message)
        }
    }
}
