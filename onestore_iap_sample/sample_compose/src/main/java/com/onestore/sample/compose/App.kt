package com.onestore.sample.compose

import android.app.Application
import com.onestore.sample.compose.manager.iap.PurchaseManager
import com.onestore.sample.compose.manager.license.LicenseCheckerManager

/**
 * 애플리케이션 전역 설정 및 초기화를 담당하는 클래스
 *
 * 앱 시작 시 PurchaseManager와 LicenseCheckerManager를 초기화하고 전역적으로 접근 가능하도록 관리합니다.
 */
class App : Application() {

    companion object {
        /**
         * 전역 PurchaseManager 인스턴스
         *
         * 앱 전체에서 인앱 결제 기능에 접근할 수 있도록 제공됩니다.
         */
        lateinit var purchaseManager: PurchaseManager
            private set

        /**
         * 전역 LicenseCheckerManager 인스턴스
         *
         * 앱 전체에서 라이선스 검증 기능에 접근할 수 있도록 제공됩니다.
         */
        lateinit var licenseCheckerManager: LicenseCheckerManager
            private set
    }

    override fun onCreate() {
        super.onCreate()

        purchaseManager = PurchaseManager()
        purchaseManager.onAppInit()

        licenseCheckerManager = LicenseCheckerManager()
    }
}