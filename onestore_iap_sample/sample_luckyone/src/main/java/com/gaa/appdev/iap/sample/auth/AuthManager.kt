package com.gaa.appdev.iap.sample.auth

import android.app.Activity
import com.gaa.sdk.auth.GaaSignInClient
import com.gaa.sdk.auth.OnAuthListener
import com.gaa.sdk.base.ResultListener

class AuthManager(private val activity: Activity) {
    private var client: GaaSignInClient = GaaSignInClient.getClient(activity)

    fun silentSignLogin(listener: OnAuthListener) {
        client.silentSignIn(listener)
    }

    /**
     * 원스토어 로그인을 진행합니다.
     * 로그인이 되어 있지 않다면, 원스토어로 연결되어 로그인을 유도합니다.
     * @param listener
     */
    fun launchSignInFlow(listener: OnAuthListener) {
        client.launchSignInFlow(activity, listener)
    }

    /**
     * 원스토어 버전이 낮을 경우 업데이트/설치를 시도합니다.
     * @param listener
     */
    fun launchUpdateOrInstall(listener: ResultListener) {
        client.launchUpdateOrInstallFlow(activity, listener)
    }
}