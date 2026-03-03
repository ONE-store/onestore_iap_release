package com.onestore.sample.compose.ui.feature.license.model

/**
 * 앱 라이선스 검증 모드를 정의하는 Enum입니다.
 */
enum class AppLicenseCheckMode {
    /** 선택되지 않음 */
    NONE,
    /** 정책 기반 확인 (캐시 사용 가능) */
    POLICY,
    /** 엄격한 확인 (서버 강제) */
    STRICT
}
