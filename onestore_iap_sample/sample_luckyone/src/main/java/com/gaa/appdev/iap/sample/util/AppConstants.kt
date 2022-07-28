package com.gaa.appdev.iap.sample.util

import androidx.annotation.StringDef
import com.gaa.sdk.iap.PurchaseClient.ProductType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

object AppConstants {
    /*
     * Custom pid : 허용 가능한 문자 셋 (1-9, a-z, .(점), _(밑줄))
     */

    @StringDef(value = [InappType.PRODUCT_INAPP_500, InappType.PRODUCT_INAPP_510])
    @Retention(RetentionPolicy.SOURCE)
    annotation class InappType {
        companion object {
            const val PRODUCT_INAPP_500 = "p500"
            const val PRODUCT_INAPP_510 = "p510"
        }
    }

    @StringDef(value = [SubsType.PRODUCT_SUBS_500, SubsType.PRODUCT_SUBS_501])
    @Retention(RetentionPolicy.SOURCE)
    annotation class SubsType {
        companion object {
            const val PRODUCT_SUBS_500 = "week" // 구독 상품코드 (One more item)
            const val PRODUCT_SUBS_501 = "month" // 구독 상품코드 (One more item)
        }
    }

    private val IN_APP_PRODUCTS =
        arrayOf(InappType.PRODUCT_INAPP_500, InappType.PRODUCT_INAPP_510)

    private val SUBS_PRODUCTS = arrayOf(SubsType.PRODUCT_SUBS_500)
    private fun getProductList(@ProductType productType: String): List<String> {
        if (ProductType.INAPP == productType) {
            return IN_APP_PRODUCTS.toList()
        } else {
            return SUBS_PRODUCTS.toList()
        }
    }

    val allProductList: List<String>
        get() {
            return getProductList(ProductType.INAPP) + getProductList(ProductType.SUBS)
        }

    // Lucky ONE Shared Preference Key
    const val KEY_MODE_SUBSCRIPTION = "SUBSCRIPTION_MODE"
    const val KEY_TOTAL_COIN = "TOTAL_COIN"
    const val KEY_PAYLOAD = "PAYLOAD"
    const val KEY_IS_FIRST = "IS_FIRST"
    const val KEY_STORE_CODE = "STORE_CODE"
}