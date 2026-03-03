package com.onestore.sample.compose.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Material 3 기본 색상 외에 추가로 필요한 확장 색상 정의
 * 
 * @property isDarkMode 다크 모드 여부
 * @property warning 경고 색상
 * @property grant 승인/성공 색상
 * @property error 에러 색상
 */
data class ExtendedColors(
    val isDarkMode: Boolean = false,

    val warning: Color = Color.Black,
    val grant: Color = Color.Black,
    val error: Color = Color.Black,
)

/**
 * 확장 색상에 접근하기 위한 CompositionLocal
 * 
 * 사용 예시:
 * ```kotlin
 * val extendedColors = LocalExtendedColors.current
 * Text(
 *     text = "Error",
 *     color = extendedColors.error
 * )
 * ```
 */
val LocalExtendedColors = staticCompositionLocalOf { ExtendedColors() }