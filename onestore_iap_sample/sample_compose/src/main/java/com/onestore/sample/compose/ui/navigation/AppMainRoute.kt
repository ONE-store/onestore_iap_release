package com.onestore.sample.compose.ui.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * 앱의 메인 화면 내 탭 네비게이션 라우트
 *
 * 하단 네비게이션 바에서 선택 가능한 화면들을 정의합니다.
 *
 * @property name 화면 제목
 */
@Serializable
sealed class AppMainRoute(@Transient  open val name: String = ""):NavRoute {
    /**
     * 상품 목록 화면
     *
     * 월정액, 관리형, 구독형 상품들을 조회하고 구매할 수 있습니다.
     */
    @Serializable @SerialName("PurchaseList")
    data class PurchaseList(override val name: String = "상품 목록") : AppMainRoute(name)

    /**
     * 구매 처리 대기 목록 화면
     *
     * 미승인 구매 항목들을 확인하고 승인/소비 처리할 수 있습니다.
     */
    @Serializable @SerialName("PurchaseWaitingList")
    data class PurchaseWaitingList(override val name: String = "구매 처리") : AppMainRoute(name)

    /**
     * ALC (App License Checker) 화면
     *
     * 앱 라이선스 검증 기능을 제공합니다.
     */
    @Serializable @SerialName("ALC")
    data class ALC(override val name: String = "ALC") : AppMainRoute(name)

    /**
     * 설정 화면
     *
     * 앱 설정을 변경할 수 있습니다.
     */
    @Serializable @SerialName("Setting")
    data class Setting(override val name: String = "설정") : AppMainRoute(name)
}