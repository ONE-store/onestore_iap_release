package com.onestore.sample.compose.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * OneStore IAP 샘플 앱의 메인 테마
 * 
 * Material 3 디자인 시스템을 기반으로 하며, 다크/라이트 모드와 동적 색상을 지원합니다.
 * 
 * @param useDarkTheme 다크 테마 사용 여부 (기본값: 시스템 설정 따름)
 * @param dynamicColor 동적 색상 사용 여부 (Android 12+, 기본값: true)
 * @param content 테마가 적용될 컴포저블 콘텐츠
 */
@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    // Android 12(S) 이상에서는 동적 색상 지원
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (useDarkTheme) dynamicDarkColorScheme(LocalContext.current)
            else dynamicLightColorScheme(LocalContext.current)
        }
        useDarkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    // 다크/라이트 모드에 따른 확장 색상 설정
    val extendedColors = if (useDarkTheme) {
        ExtendedColors(
            isDarkMode = true,
            warning = Color(0xFFFFB74D),  // 경고
            grant = Color(0xFF81C784),    // 성공/승인
            error = Color(0xFFEF9A9A),    // 에러
        )
    } else {
        ExtendedColors(
            isDarkMode = false,
            warning = Color(0xFFFF9800),  // 경고
            grant = Color(0xFF4CAF50),    // 성공/승인
            error = Color(0xFFD32F2F),    // 에러
        )
    }

    // 확장 색상을 CompositionLocal로 제공
    CompositionLocalProvider(
        LocalExtendedColors provides extendedColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography(),
            content = content
        )
    }
}