package com.onestore.sample.compose.ui.utils

import androidx.compose.runtime.compositionLocalOf
import androidx.lifecycle.ViewModelProvider

/**
 * Compose 전역에서 사용할 수 있는 ViewModelProvider.Factory를 제공하는 CompositionLocal
 *
 * 이 CompositionLocal을 통해 Compose 트리의 모든 하위 컴포넌트에서
 * ViewModel 인스턴스를 생성할 수 있는 Factory에 접근할 수 있습니다.
 *
 * 사용 예시:
 * ```
 * val factory = LocalViewModelFactory.current
 * val viewModel: MyViewModel = viewModel(factory = factory)
 * ```
 *
 * @throws IllegalStateException CompositionLocalProvider로 값을 제공하지 않은 경우
 */
val LocalViewModelFactory = compositionLocalOf<ViewModelProvider.Factory> {
    error("No ViewModelFactory provided")
}
