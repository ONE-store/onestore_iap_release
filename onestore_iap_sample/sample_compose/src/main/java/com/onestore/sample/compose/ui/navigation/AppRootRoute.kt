package com.onestore.sample.compose.ui.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * 앱의 루트 레벨 네비게이션 라우트
 *
 * 전체 화면 전환을 위한 라우트들을 정의합니다.
 *
 * @property isBackVisible 뒤로가기 버튼 표시 여부
 * @property name 화면 제목
 */
@Serializable
sealed class AppRootRoute(
    @Transient open val isBackVisible:Boolean = false,
    @Transient open val name: String = ""
):NavRoute {
    /**
     * 메인 화면 (하단 탭 네비게이션 포함)
     */
    @Serializable @SerialName("Main")
    data class Main(override val name: String = "상품 목록") : AppRootRoute(name = name)

    /**
     * 상품 상세 화면
     *
     * @property productId 조회할 상품의 ID
     */
    @Serializable @SerialName("PurchaseDetail")
    data class PurchaseDetail(
        override val isBackVisible: Boolean = true,
        override val name: String = "상품 상세 정보",
        val productId:String
    ) : AppRootRoute(isBackVisible, name)
}