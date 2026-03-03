package com.onestore.sample.compose.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.Currency
import java.util.Locale

/**
 * 전역 로그 함수
 *
 * 이 함수는 디버깅 목적으로 사용되며, **릴리스 빌드에서는 반드시 비활성화하거나 제거**해야 합니다.
 * 
 * ### 권장 방법
 * 1. BuildConfig.DEBUG로 조건부 로깅:
 *    ```kotlin
 *    if (BuildConfig.DEBUG) {
 *        printLog(TAG, message)
 *    }
 *    ```
 * 2. Timber 등 릴리스 대응 로깅 라이브러리 사용
 * 3. ProGuard/R8로 릴리스 빌드에서 자동 제거 설정
 *
 * ### 보안 고려사항
 * - 로그에 민감한 정보(사용자 정보, 결제 정보 등)가 포함되지 않도록 주의하세요
 * - 로그는 디바이스에서 다른 앱이 읽을 수 있습니다
 *
 * @param tag 로그 태그 (클래스명 등 식별자)
 * @param message 로그 메시지
 */
fun printLog(tag: String, message: String) {
    // TODO: 실제 배포 전 BuildConfig.DEBUG 조건 추가 또는 제거
    Log.i(tag, message)
}

/**
 * 메인 스레드에서 코루틴 블록을 실행하는 유틸리티 함수
 *
 * @param delayMillis 실행 전 대기 시간 (밀리초). 기본값은 0입니다.
 * @param block 메인 스레드에서 실행할 suspend 함수
 */
fun runOnMainOnce(delayMillis: Long = 0, block: suspend () -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        if (delayMillis > 0) delay(delayMillis)
        block()
    }
}

/**
 * 가격 문자열에 콤마 및 통화 포맷팅을 적용합니다.
 *
 * @param price 가격 문자열 (예: "1000", "1000원", "$1000")
 * @param currencyCode 통화 코드 (예: "KRW", "USD"). price가 숫자만 있을 때 사용됩니다.
 */
fun formatPrice(price: String?, currencyCode: String? = null): String {
    if (price.isNullOrBlank()) return ""

    // 숫자와 소수점만 추출하여 포맷팅 준비
    val cleanPriceDigits = price.replace(",", "").filter { it.isDigit() || it == '.' }.trim()
    if (cleanPriceDigits.isEmpty()) return price

    val formattedNumber = try {
        val number = cleanPriceDigits.toDouble()
        val formatter = DecimalFormat("#,###.##")
        formatter.format(number)
    } catch (e: Exception) {
        cleanPriceDigits
    }

    // 통화 코드에 따른 처리
    return when {
        currencyCode.equals("KRW", ignoreCase = true) -> {
            "${formattedNumber}원"
        }
        currencyCode.equals("USD", ignoreCase = true) -> {
            "$${formattedNumber}"
        }
        currencyCode != null -> {
            try {
                val currency = Currency.getInstance(currencyCode)
                val symbol = currency.getSymbol(Locale.getDefault())
                // 일반적인 경우 기호를 앞에 표시
                "$symbol$formattedNumber"
            } catch (e: Exception) {
                "$formattedNumber $currencyCode"
            }
        }
        else -> {
            // 통화 코드가 없고 원본에 기호가 이미 포함되어 있었다면 원본의 형식을 최대한 유지
            if (price.any { !it.isDigit() && it != ',' && it != '.' && it != ' ' }) {
                price
            } else {
                formattedNumber
            }
        }
    }
}