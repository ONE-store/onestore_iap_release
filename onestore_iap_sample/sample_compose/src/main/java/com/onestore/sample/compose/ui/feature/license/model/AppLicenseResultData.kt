package com.onestore.sample.compose.ui.feature.license.model

/**
 * 앱 라이선스 검증 결과 카드에 표시할 전체 데이터 모델입니다.
 */
data class AppLicenseResultData(
    val title: String,
    val firstLine: AppLicenseDetailLine,
    val secondLine: AppLicenseDetailLine,
    val status: AppLicenseStatus = AppLicenseStatus.INFO
)
