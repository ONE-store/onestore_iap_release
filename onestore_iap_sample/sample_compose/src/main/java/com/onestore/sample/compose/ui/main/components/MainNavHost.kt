package com.onestore.sample.compose.ui.main.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PendingActions
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.onestore.sample.compose.R
import com.onestore.sample.compose.model.AppLicenseState
import com.onestore.sample.compose.ui.feature.license.AppLicenseCheckerScreen
import com.onestore.sample.compose.ui.feature.product.list.ProductListScreen
import com.onestore.sample.compose.ui.feature.purchase.PurchaseWaitingListScreen
import com.onestore.sample.compose.ui.feature.setting.SettingScreen
import com.onestore.sample.compose.ui.main.MainActivityViewModel
import com.onestore.sample.compose.ui.main.factory.AppViewModelFactory
import com.onestore.sample.compose.ui.navigation.AppMainRoute
import com.onestore.sample.compose.ui.theme.LocalExtendedColors
import com.onestore.sample.compose.ui.utils.LocalViewModelFactory

/**
 * 메인 화면의 하단 탭 네비게이션 컨테이너
 *
 * 이 Composable은 앱의 주요 4개 탭(상품 목록, 구매 처리 대기, 앱 라이선스 검증, 설정) 간의
 * 네비게이션을 관리합니다. 하단 NavigationBar와 각 탭의 화면을 표시합니다.
 *
 * ## 탭 구성
 * - **상품 목록**: 구매 가능한 상품들을 표시
 * - **구매 처리 대기**: 미승인 구매 항목들을 표시하며, Badge로 개수 표시
 * - **앱 라이선스 검증**: 앱 라이선스 검증 상태를 표시하며, Badge로 상태 표시 (승인/거부/경고)
 * - **설정**: 앱 설정 화면
 *
 * ## Badge 표시
 * - 구매 처리 대기: 미승인 구매 개수를 숫자로 표시
 * - 앱 라이선스 검증: 상태에 따른 색상 표시 (승인: 초록색, 거부/오류: 빨간색, 없음: 주황색)
 *
 * @param rootNavController 루트 레벨 네비게이션 컨트롤러 (상세 화면 이동 시 사용)
 * @param viewModelFactory ViewModel 생성을 위한 Factory 인스턴스
 * @param onUpdateAppBar 앱바 제목과 아이콘을 업데이트하기 위한 콜백 함수
 *
 * @see AppMainRoute
 * @see ProductListScreen
 * @see PurchaseWaitingListScreen
 * @see AppLicenseCheckerScreen
 * @see SettingScreen
 */
@Composable
fun MainNavHost(
    rootNavController: NavHostController,
    viewModelFactory: AppViewModelFactory,
    onUpdateAppBar: (String, ImageVector) -> Unit
) {
    CompositionLocalProvider(
        LocalViewModelFactory provides viewModelFactory
    ) {
        val viewModel: MainActivityViewModel = viewModel(factory = viewModelFactory)
        val purchaseState by viewModel.purchaseUiState.collectAsStateWithLifecycle()
        val appLicenseState by viewModel.appLicenseState.collectAsStateWithLifecycle()
        val navController = rememberNavController()

        // 탭별 아이콘 맵핑
        val navItems = mapOf(
            AppMainRoute.PurchaseList() to Icons.Default.ShoppingCart,
            AppMainRoute.PurchaseWaitingList() to Icons.Default.PendingActions,
            AppMainRoute.ALC() to Icons.Default.VerifiedUser,
            AppMainRoute.Setting() to Icons.Rounded.Settings
        )

        /**
         * 라우트에 따른 타이틀 리소스 ID를 반환합니다.
         *
         * @param route 현재 네비게이션 라우트
         * @return 타이틀 문자열 리소스 ID
         */
        fun getTitleResId(route: AppMainRoute): Int {
            return when (route) {
                is AppMainRoute.PurchaseList -> R.string.product_list_title
                is AppMainRoute.PurchaseWaitingList -> R.string.purchase_waiting_title
                is AppMainRoute.ALC -> R.string.app_license_title
                is AppMainRoute.Setting -> R.string.settings_title
            }
        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        val purchaseList = purchaseState.unacknowledgedPurchases

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                NavigationBar(windowInsets = WindowInsets(0, 0, 0, 0)) {
                    navItems.forEach { (destination, icon) ->
                        val title = stringResource(getTitleResId(destination))
                        NavigationBarItem(
                            icon = {
                                var showDefault = false
                                when (destination) {
                                    is AppMainRoute.PurchaseWaitingList -> {
                                        // 미승인 구매 개수 Badge 표시
                                        if (purchaseList.isEmpty()) {
                                            Icon(icon, contentDescription = title)
                                        } else {
                                            BadgedBox(
                                                badge = {
                                                    Badge(modifier = Modifier.offset(x = 10.dp)) {
                                                        Text("${purchaseList.count()}")
                                                    }
                                                }
                                            ) {
                                                Icon(icon, contentDescription = title)
                                            }
                                        }
                                    }
                                    is AppMainRoute.ALC -> {
                                        // 앱 라이선스 상태에 따른 Badge 표시
                                        BadgedBox(
                                            badge = {
                                                Badge(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .offset(x = 7.dp),
                                                    containerColor = when (appLicenseState) {
                                                        is AppLicenseState.Granted -> LocalExtendedColors.current.grant
                                                        is AppLicenseState.Denied, is AppLicenseState.Error -> MaterialTheme.colorScheme.error
                                                        AppLicenseState.None -> LocalExtendedColors.current.warning
                                                    }
                                                )
                                            }
                                        ) {
                                            Icon(icon, contentDescription = title)
                                        }
                                    }
                                    else -> Icon(icon, contentDescription = title)
                                }
                            },
                            label = { Text(text = title, textAlign = TextAlign.Center) },
                            selected = currentDestination?.hierarchy?.any { it.hasRoute(destination::class) } == true,
                            onClick = {
                                navController.navigate(destination) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = AppMainRoute.PurchaseList(),
                enterTransition = { slideInHorizontally(animationSpec = tween(150)) { it } },
                exitTransition = { slideOutHorizontally(animationSpec = tween(150)) { -it } },
                popEnterTransition = { slideInHorizontally(animationSpec = tween(150)) { -it } },
                popExitTransition = { slideOutHorizontally(animationSpec = tween(150)) { it } },
                modifier = Modifier.padding(
                    start = innerPadding.calculateStartPadding(LocalLayoutDirection.current),
                    top = 0.dp,
                    end = innerPadding.calculateEndPadding(LocalLayoutDirection.current),
                    bottom = innerPadding.calculateBottomPadding()
                ),
            ) {
                composable<AppMainRoute.PurchaseList> { backStackEntry ->
                    val route = backStackEntry.toRoute<AppMainRoute.PurchaseList>()
                    onUpdateAppBar(stringResource(getTitleResId(route)), Icons.Default.ShoppingCart)
                    ProductListScreen(rootNavController = rootNavController)
                }

                composable<AppMainRoute.PurchaseWaitingList> { backStackEntry ->
                    val route = backStackEntry.toRoute<AppMainRoute.PurchaseWaitingList>()
                    onUpdateAppBar(stringResource(getTitleResId(route)), Icons.Default.PendingActions)
                    PurchaseWaitingListScreen()
                }

                composable<AppMainRoute.ALC> { backStackEntry ->
                    onUpdateAppBar(stringResource(R.string.app_license_screen_title), Icons.Default.VerifiedUser)
                    AppLicenseCheckerScreen()
                }

                composable<AppMainRoute.Setting> { backStackEntry ->
                    val route = backStackEntry.toRoute<AppMainRoute.Setting>()
                    onUpdateAppBar(stringResource(getTitleResId(route)), Icons.Rounded.Settings)
                    SettingScreen()
                }
            }
        }
    }
}
