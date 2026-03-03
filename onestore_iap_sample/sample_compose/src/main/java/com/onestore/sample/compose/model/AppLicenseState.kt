package com.onestore.sample.compose.model

/**
 * 앱 라이선스 검증 상태를 나타내는 Sealed Class
 */
sealed class AppLicenseState {
    /** 초기 상태 또는 라이선스 검증 전 */
    object None : AppLicenseState()

    /** 라이선스 승인 상태 */
    data class Granted(val license: String?, val signature: String?) : AppLicenseState()

    /** 라이선스 거부 상태 */
    object Denied : AppLicenseState()

    /** 라이선스 검증 오류 상태 */
    data class Error(val code: String, val message: String?) : AppLicenseState()
}
