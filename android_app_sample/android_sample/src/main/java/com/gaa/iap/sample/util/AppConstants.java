package com.gaa.iap.sample.util;

import androidx.annotation.StringDef;

import com.gaa.sdk.iap.PurchaseClient.ProductType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Lucky ONE - 게임 동작에 필요한 상수 값
 */

public class AppConstants {
    private AppConstants() {
        throw new IllegalAccessError("Utility class");
    }

    /*
     * Custom pid : 허용 가능한 문자 셋 (1-9, a-z, .(점), _(밑줄))
     */

    @StringDef({
            InappType.PRODUCT_INAPP_5000,
            InappType.PRODUCT_INAPP_10000,
            InappType.PRODUCT_INAPP_50000
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface InappType {
        String PRODUCT_INAPP_5000 = "p5000";    // 소모성 상품코드 (5 coins) - 5000원권 아이템
        String PRODUCT_INAPP_10000 = "p10000";  // 소모성 상품코드 (10 coins) - 10,000원권 아이템
        String PRODUCT_INAPP_50000 = "p50000";  // 소모성 상품코드 (One more item) - 50,000원권 아이템
    }

    @StringDef({
        AutoType.PRODUCT_AUTO_100000
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface AutoType {
        String PRODUCT_AUTO_100000 = "a100000"; // 월정액 상품코드 (Unlimited Play) - 100,000원권 아이템
    }

    private static final String[] IN_APP_PRODUCTS = {InappType.PRODUCT_INAPP_5000, InappType.PRODUCT_INAPP_10000, InappType.PRODUCT_INAPP_50000};
    private static final String[] AUTO_PRODUCTS = {AutoType.PRODUCT_AUTO_100000};

    public static List<String> getProductList(@ProductType String productType) {
        return new ArrayList<String>(Arrays.asList(ProductType.INAPP.equals(productType) ? IN_APP_PRODUCTS : AUTO_PRODUCTS));
    }

    public static List<String> getAllProductList() {
        List<String> result = getProductList(ProductType.INAPP);
        result.addAll(getProductList(ProductType.AUTO));
        return result;
    }

    // Lucky ONE Shared Preference Key
    public static final String KEY_MODE_MONTHLY = "MODE_MONTHLY";
    public static final String KEY_ONE_MORE = "ONE_MORE";
    public static final String KEY_TOTAL_COIN = "TOTAL_COIN";
    public static final String KEY_PAYLOAD = "PAYLOAD";
    public static final String KEY_IS_FIRST = "IS_FIRST";
    public static final String KEY_STORE_CODE = "STORE_CODE";

}
