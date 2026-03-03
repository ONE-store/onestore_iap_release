package com.onestore.sample.compose.manager.iap

import com.gaa.sdk.iap.ProductDetail
import com.gaa.sdk.iap.PurchaseClient.ProductType
import com.gaa.sdk.iap.PurchaseData

/**
 * PurchaseManager의 통합 UI 상태
 *
 * 인앱 결제와 관련된 모든 UI 상태 정보를 담고 있는 데이터 클래스입니다.
 *
 * @property isLogin 사용자 로그인 상태
 * @property showLoading 로딩 인디케이터 표시 여부
 * @property showMessage 공통 메시지 다이얼로그 메시지 (빈 문자열이면 숨김)
 * @property showLogin 로그인 필요 다이얼로그 표시 여부
 * @property showUpdate 업데이트 필요 다이얼로그 표시 여부
 * @property productDetails 등록된 상품 정보 목록
 * @property purchasesAutoList 월정액(AUTO) 구매 내역 목록
 * @property purchasesInAppList 관리형(INAPP) 구매 내역 목록
 * @property purchasesSubsList 구독형(SUBS) 구매 내역 목록
 */
data class PurchaseManagerUiState(
    // UI 상태
    val isLogin: Boolean = false,

    val showLoading: Boolean = false,
    val showMessage: String = "",
    val showLogin: Boolean = false,
    val showUpdate: Boolean = false,

    // 상품/구매 데이터
    val productDetails: List<ProductDetail> = emptyList(),
    val purchasesAutoList: List<PurchaseData> = emptyList(),
    val purchasesInAppList: List<PurchaseData> = emptyList(),
    val purchasesSubsList: List<PurchaseData> = emptyList()
)  {
    /**
     * 미승인(Unacknowledged) AUTO 구매 목록
     *
     * 월정액 상품 중 아직 승인 처리되지 않은 구매 목록을 반환합니다.
     */
    val unacknowledgedAutoList: List<PurchaseData>
        get() = purchasesAutoList.filter { !it.isAcknowledged }

    /**
     * 미승인(Unacknowledged) SUBS 구매 목록
     *
     * 구독 상품 중 아직 승인 처리되지 않은 구매 목록을 반환합니다.
     */
    val unacknowledgedSubsList: List<PurchaseData>
        get() = purchasesSubsList.filter { !it.isAcknowledged }

    /**
     * 전체 미승인 구매 목록 (AUTO + INAPP + SUBS)
     *
     * **중요**: INAPP(관리형 상품)은 소비(consume) 처리가 필요하므로
     * isAcknowledged 필터링을 하지 않고 전체 목록을 포함합니다.
     * AUTO와 SUBS는 미승인 항목만 포함됩니다.
     */
    val unacknowledgedPurchases: List<PurchaseData>
        get() = unacknowledgedAutoList + purchasesInAppList + unacknowledgedSubsList

    /**
     * 전체 구매 목록 (AUTO + INAPP + SUBS)
     *
     * 모든 타입의 구매 내역을 하나의 리스트로 반환합니다.
     */
    val allPurchases: List<PurchaseData>
        get() = purchasesAutoList + purchasesInAppList + purchasesSubsList

    /**
     * 특정 productId의 ProductDetail을 조회합니다.
     *
     * @param productId 조회할 상품 ID
     * @return 해당 상품 정보, 없으면 null
     */
    fun getProductDetail(productId: String): ProductDetail? = productDetails.firstOrNull { it.productId == productId }

    /**
     * 특정 productId의 PurchaseData를 조회합니다.
     *
     * 전체 구매 목록에서 해당 상품 ID의 구매 데이터를 찾아 반환합니다.
     *
     * @param productId 조회할 상품 ID
     * @return 해당 구매 데이터, 없으면 null
     */
    fun getPurchaseData(productId: String): PurchaseData? = allPurchases.firstOrNull { it.productId == productId }

    /**
     * 특정 productId를 제외한 다른 구독 상품의 구매 목록을 반환합니다.
     *
     * 구독 업그레이드/다운그레이드 시 현재 구독을 제외한 다른 구독 목록을 얻을 때 사용합니다.
     *
     * @param excludeProductId 제외할 상품 ID
     * @return 제외된 상품을 제외한 구독 구매 목록
     */
    fun getOtherPurchases(excludeProductId: String): List<PurchaseData> = purchasesSubsList.filter { it.productId != excludeProductId }

    /**
     * 특정 productId에 대해 업그레이드/다운그레이드 가능한 구독 상품 목록을 반환합니다.
     *
     * 현재 구독 중인 다른 상품과 중복되지 않는 구독 가능한 상품 목록을 반환합니다.
     *
     * @param currentProductId 현재 상품 ID
     * @return 업그레이드/다운그레이드 가능한 구독 상품 목록
     */
    fun getAvailableSubscriptionUpgrades(currentProductId: String): List<ProductDetail> {
        val otherPurchases = getOtherPurchases(currentProductId)
        return productDetails
            .filter { it.type == ProductType.SUBS && it.productId != currentProductId }
            .filter { productDetail ->
                !otherPurchases.any { purchase -> purchase.productId == productDetail.productId }
            }
    }
}
