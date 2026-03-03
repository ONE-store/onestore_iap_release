package com.onestore.sample.compose.ui.main.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.onestore.sample.compose.R
import com.onestore.sample.compose.ui.feature.product.detail.ProductDetailScreen
import com.onestore.sample.compose.ui.main.MainActivityViewModel
import com.onestore.sample.compose.ui.main.factory.AppViewModelFactory
import com.onestore.sample.compose.ui.navigation.AppRootRoute
import com.onestore.sample.compose.ui.utils.LocalViewModelFactory

/**
 * 앱의 최상위 네비게이션 컨테이너
 *
 * 이 Composable은 앱의 루트 레벨 화면 전환을 관리합니다.
 * TopAppBar와 NavHost를 포함하며, 메인 화면과 상세 화면 간의 네비게이션을 처리합니다.
 *
 * ## 구조
 * - TopAppBar: 앱바, 타이틀, 뒤로가기 버튼, 아이콘 표시
 * - NavHost: Main ↔ PurchaseDetail 간의 화면 전환 관리
 *
 * ## 화면 전환 애니메이션
 * - 진입 시: 오른쪽에서 왼쪽으로 슬라이드 인
 * - 종료 시: 왼쪽에서 오른쪽으로 슬라이드 아웃
 *
 * @param viewModelFactory ViewModel 생성을 위한 Factory 인스턴스
 * @param onUpdateAppBar 앱바 제목과 아이콘을 업데이트하기 위한 콜백 함수 (더 이상 사용되지 않음, 하위 호환성 유지)
 *
 * @see AppRootRoute
 * @see MainNavHost
 * @see ProductDetailScreen
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RootNavHost(
    viewModelFactory: AppViewModelFactory,
    onUpdateAppBar: (String, ImageVector) -> Unit
) {
    val navController = rememberNavController()
    val viewModel: MainActivityViewModel = viewModel(factory = viewModelFactory)
    val mainUiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = mainUiState.appBarIcon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = mainUiState.appBarTitle
                        )
                    }
                },
                navigationIcon = {
                    if (mainUiState.isBackButtonVisible) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.action_back)
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRootRoute.Main(),
            enterTransition = { slideInHorizontally(animationSpec = tween(150)) { it } },
            exitTransition = { slideOutHorizontally(animationSpec = tween(150)) { -it } },
            popEnterTransition = { slideInHorizontally(animationSpec = tween(150)) { -it } },
            popExitTransition = { slideOutHorizontally(animationSpec = tween(150)) { it } },
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<AppRootRoute.Main> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRootRoute.Main>()
                viewModel.updateAppBar(
                    title = stringResource(R.string.product_list_title),
                    icon = Icons.Default.ShoppingCart,
                    showBackButton = route.isBackVisible
                )

                MainNavHost(
                    rootNavController = navController,
                    viewModelFactory = viewModelFactory,
                    onUpdateAppBar = { title, icon ->
                        viewModel.updateAppBar(
                            title = title,
                            icon = icon,
                            showBackButton = route.isBackVisible
                        )
                    }
                )
            }

            composable<AppRootRoute.PurchaseDetail> { backStackEntry ->
                val route = backStackEntry.toRoute<AppRootRoute.PurchaseDetail>()
                viewModel.updateAppBar(
                    title = stringResource(R.string.product_detail_title),
                    icon = Icons.Default.ShoppingCart,
                    showBackButton = route.isBackVisible
                )

                backStackEntry.savedStateHandle.set("productId", route.productId)
                ProductDetailScreen(rootNavController = navController)
            }
        }
    }
}
