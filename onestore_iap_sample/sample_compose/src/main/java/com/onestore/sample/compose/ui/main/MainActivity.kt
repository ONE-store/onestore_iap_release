package com.onestore.sample.compose.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.onestore.sample.compose.App
import com.onestore.sample.compose.ui.main.components.LoadingDialog
import com.onestore.sample.compose.ui.main.components.LoginRequiredDialog
import com.onestore.sample.compose.ui.main.components.MessageDialog
import com.onestore.sample.compose.ui.main.components.RootNavHost
import com.onestore.sample.compose.ui.main.components.UpdateRequiredDialog
import com.onestore.sample.compose.ui.main.factory.AppViewModelFactory
import com.onestore.sample.compose.ui.theme.AppTheme
import androidx.compose.runtime.CompositionLocalProvider
import com.onestore.sample.compose.ui.utils.LocalViewModelFactory

/**
 * ONE Store IAP Compose Sample
 * 
 * Jetpack Compose를 사용한 ONE Store 인앱 결제(IAP) v21 통합 샘플 앱입니다.
 * 
 * ## 앱 개요
 * 
 * 이 샘플 앱은 ONE Store IAP SDK v21의 주요 기능들을 Jetpack Compose로 구현한
 * 통합 데모 애플리케이션입니다. 관리형 상품(INAPP), 월정액(AUTO, Deprecated), 
 * 구독형(SUBS) 상품의 구매, 관리, 소비 등 전체 결제 플로우를 확인할 수 있습니다.
 * 
 * ## 주요 화면 구성
 * 
 * ### 1. 상품 목록 (Product List)
 * - 등록된 모든 상품 조회 및 표시
 * - 상품 타입별 그룹화 (월정액/관리형/구독형)
 * - 구매 상태 표시 및 상세 화면 이동
 * 
 * ### 2. 구매 처리 대기 (Purchase Waiting)
 * - 미승인/미소비 상품 목록 표시
 * - 승인(acknowledge) 및 소비(consume) 처리
 * - 타입별 구매 항목 관리
 * 
 * ### 3. 앱 라이선스 검증
 * - 앱 라이선스 체크 기능
 * - 일반/엄격 모드 라이선스 검증
 * - Public Key 표시
 * 
 * ### 4. 설정 (Settings)
 * - 로그인/로그아웃
 * - 스토어 정보 표시
 * - SDK 로그 레벨 설정
 * 
 * ## 기술 스택
 * 
 * - **UI Framework**: Jetpack Compose
 * - **Architecture**: MVVM (ViewModel + StateFlow)
 * - **Navigation**: Compose Navigation (Type-safe)
 * - **Dependency Injection**: Manual Factory Pattern
 * - **Theme**: Material Design 3 with Custom Extended Colors
 * 
 * ## ONE Store IAP 주요 기능
 * 
 * ### 상품 구매 플로우
 * 1. PurchaseClient 초기화 및 서비스 연결
 * 2. 상품 정보 조회 (queryProductDetailsAsync)
 * 3. 구매 요청 (launchPurchaseFlow)
 * 4. 구매 결과 수신 (PurchasesUpdatedListener)
 * 5. 승인/소비 처리 (acknowledgeAsync/consumeAsync)
 * 
 * ### 관리형 상품 (INAPP)
 * - 구매 후 소비(consume) 처리 필요
 * - 소비 전까지 재구매 불가
 * - 미소비 상품은 구매 처리 대기 화면에서 관리
 * 
 * ### 월정액 상품 (AUTO) [DEPRECATED]
 * - **중요**: SDK V21 (API V7)부터 새로운 월정액 상품 생성 불가
 * - **대안**: 결제 주기가 한 달인 구독형 상품(SUBS) 사용 권장
 * - 최초 구매 후 익월 동일일에 자동 갱신
 * - 구매 후 승인(acknowledge) 처리 필요
 * - 갱신 상태 관리 (RECURRING/CANCEL)
 * - 상태 변경: manageRecurringProductAsync() (Deprecated)
 * - 기존에 생성된 월정액 상품은 계속 사용 가능
 * 
 * ### 구독형 상품 (SUBS)
 * - 정기 구독 상품 (월, 분기, 년 등 다양한 주기 설정 가능)
 * - 구매 후 승인(acknowledge) 처리 필요
 * - 구독 업그레이드/다운그레이드 지원
 * - 갱신 상태 관리 (RECURRING/CANCEL)
 * - 구독 관리 화면 제공 (manageSubscription)
 * - 월정액 상품 대체 상품으로 권장됨
 * 
 * ## 샘플 앱 구동 전 준비 사항
 * 
 * 1. **패키지 이름 변경**
 *    - `build.gradle.kts`에서 `applicationId` 변경
 * 
 * 2. **Public Key 설정**
 *    - 개발자 센터에서 앱 등록 후 Base64 Public Key 발급
 *    - `Constant.kt` 파일의 `PUBLIC_KEY` 변수에 설정
 * 
 * 3. **상품 등록**
 *    - 개발자 센터에서 인앱 상품 등록
 *    - `Constant.kt` 파일의 `PRODUCT_IDS` 배열에 상품 ID 추가
 *    - 새로운 정기결제 상품은 구독형(SUBS) 타입으로 등록
 * 
 * 4. **앱 서명**
 *    - 위변조 체크를 위해 서명된 APK 필요
 *    - 테스트 시에는 Sandbox 계정 등록으로 대체 가능
 * 
 * 5. **테스트 환경 설정** (선택)
 *    - `AndroidManifest.xml`의 `onestore:dev_option` meta-data 설정
 *    - 값: onestore_00(한국), onestore_01(싱가폴/대만), onestore_02(미국), onestore_03(ONE Billing Lab)
 * 
 * ## 참고 사항
 * 
 * - 이 샘플 앱은 ONE Store IAP SDK v21.04.00 이상에서 동작합니다.
 * - 구독형(SUBS) 상품은 v21에서만 제공됩니다.
 * - 월정액(AUTO) 상품은 SDK V21(API V7)부터 Deprecated되었으며, 
 *   새로운 월정액 상품은 생성할 수 없습니다. 대신 구독형(SUBS) 상품 사용을 권장합니다.
 * - Compose 기반이므로 API 25 (Android 7.1) 이상이 필요합니다.
 * 
 * @see MainActivityViewModel
 * @see AppViewModelFactory
 * @see RootNavHost
 * @see PurchaseManager
 */
class MainActivity : ComponentActivity() {

    /**
     * 앱 전체에서 사용할 ViewModel Factory
     *
     * 하위 네비게이션 컴포넌트에서도 동일한 Factory를 사용하여
     * ViewModel 인스턴스를 공유할 수 있도록 합니다.
     */
    private val viewModelFactory = AppViewModelFactory(App.purchaseManager, App.licenseCheckerManager)

    /**
     * MainActivity와 연결된 ViewModel
     *
     * 구매 상태, 다이얼로그 표시 여부 등 전역 UI 상태를 관리합니다.
     */
    private val viewModel by viewModels<MainActivityViewModel> { viewModelFactory }

    /**
     * 액티비티 생성 시 호출됩니다.
     *
     * Edge-to-Edge UI를 활성화하고, PurchaseManager를 초기화하며,
     * Compose UI를 설정합니다. 시스템 테마에 따라 상태 바와 네비게이션 바의
     * 색상을 자동으로 조정합니다.
     *
     * @param savedInstanceState 이전 상태를 복원하기 위한 Bundle (사용하지 않음)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        viewModel.onActivityInit(this)

        setContent {
            val purchaseState by viewModel.purchaseUiState.collectAsStateWithLifecycle()

            // 시스템 기본 테마 사용
            val useDarkTheme = isSystemInDarkTheme()

            // 다크 모드에 따른 시스템 바 색상 조정
            LaunchedEffect(useDarkTheme) {
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !useDarkTheme
                    isAppearanceLightNavigationBars = !useDarkTheme
                }
            }

            AppTheme(useDarkTheme = useDarkTheme) {
                CompositionLocalProvider(LocalViewModelFactory provides viewModelFactory) {
                    Box(Modifier.fillMaxSize()) {
                        // 메인 네비게이션 컨테이너
                        RootNavHost(
                            viewModelFactory = viewModelFactory,
                            onUpdateAppBar = { _, _ -> }
                        )

                        // 전역 다이얼로그들
                        if (purchaseState.showMessage.isNotBlank()) {
                            MessageDialog(
                                message = purchaseState.showMessage,
                                onDismiss = { viewModel.hideMessage() }
                            )
                        }

                        if (purchaseState.showLogin) {
                            LoginRequiredDialog(
                                onDismiss = { viewModel.hideLogin() },
                                onLoginClick = { viewModel.launchSignInFlow(this@MainActivity) }
                            )
                        }

                        if (purchaseState.showUpdate) {
                            UpdateRequiredDialog(
                                onDismiss = { viewModel.hideUpdate() },
                                onUpdateClick = { viewModel.launchUpdateOrInstallFlow(this@MainActivity) }
                            )
                        }

                        if (purchaseState.showLoading) {
                            LoadingDialog(
                                onDismiss = { viewModel.hideLoading() }
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * 액티비티가 다시 활성화될 때 호출됩니다.
     *
     * 백그라운드에서 돌아올 때 구매 상태를 동기화하고,
     * 미처리된 구매 항목이 있는지 확인합니다.
     */
    override fun onResume() {
        super.onResume()
        viewModel.onActivityResume(this)
    }
}
