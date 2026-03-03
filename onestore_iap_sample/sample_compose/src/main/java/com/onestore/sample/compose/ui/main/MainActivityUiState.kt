package com.onestore.sample.compose.ui.main

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * MainActivity의 UI 상태
 *
 * @property appBarTitle 앱바 제목
 * @property appBarIcon 앱바 아이콘
 * @property isBackButtonVisible 뒤로가기 버튼 표시 여부
 */
data class MainActivityUiState(
    val appBarTitle: String = "",
    val appBarIcon: ImageVector = Icons.Default.ShoppingCart,
    val isBackButtonVisible: Boolean = false
)
