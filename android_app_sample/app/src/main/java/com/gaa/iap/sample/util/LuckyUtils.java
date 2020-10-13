package com.gaa.iap.sample.util;

import android.util.Log;

import com.gaa.sdk.iap.ProductDetail;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 * Lucky ONE - 게임 동작을 위한 Util 클래스
 */

public class LuckyUtils {
    private static final String TAG = LuckyUtils.class.getSimpleName();

    public static List<List<Integer>> getSuggestNumbers(int maxNum) {
        List<List<Integer>> suggestedNumbersList = new ArrayList<>(maxNum);
        suggestedNumbersList.clear();
        for (int j = 0; j < maxNum; j++) {
            suggestedNumbersList.add(createRandomNumberList());
        }
        return suggestedNumbersList;
    }

    public static List<Integer> getMyNumbers() {
        return createRandomNumberList();
    }

    private static List<Integer> createRandomNumberList() {
        List<Integer> numberList = new ArrayList<>();
        Random rand = new Random();



        while (numberList.size() < 6) {
            int number = rand.nextInt(45) + 1;
            if (numberList.contains(number)) {//중복 발생
                continue;
            }

            numberList.add(number);
        }

        Collections.sort(numberList, new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o1.compareTo(o2);
            }
        });

        Log.d(TAG, "createRandomNumberList numberList " + numberList);
        return numberList;
    }

    public static int getWonCoin(List<Integer> luckyBall, List<List<Integer>> myBallList) {
        int totalCoin = 0;

        for (List<Integer> myBalls : myBallList) {
            int lineCount = 0;

            for (Integer lucky : luckyBall) {
                for (Integer my : myBalls) {
                    if (lucky == my) {
                        lineCount++;
                    }
                }
            }
            totalCoin = totalCoin + getLuckyCoin(lineCount);
        }
        return totalCoin;
    }

    private static int getLuckyCoin(int number) {
        switch (number) {
            case 3: return 5;
            case 4: return 30;
            case 5: return 100;
            case 6: return 300;
            default: return 0;
        }
    }

    public static List<ProductDetail> getDummyList() {
        List<ProductDetail> result = new ArrayList<>();
        try {
            result.add(new ProductDetail("{\"price\":5000,\"priceAmountMicros\":5000000000,\"priceCurrencyCode\":\"KRW\",\"productId\":\"p5000\",\"title\":\"5 coins - 5,000 won\",\"type\":\"inapp\"}"));
            result.add(new ProductDetail("{\"price\":10000,\"priceAmountMicros\":10000000000,\"priceCurrencyCode\":\"KRW\",\"productId\":\"p10000\",\"title\":\"10 coins - 10,000 won\",\"type\":\"inapp\"}"));
            result.add(new ProductDetail("{\"price\":50000,\"priceAmountMicros\":50000000000,\"priceCurrencyCode\":\"KRW\",\"productId\":\"p50000\",\"title\":\"One more item - 50,000 won\",\"type\":\"inapp\"}"));
            result.add(new ProductDetail("{\"price\":100000,\"priceAmountMicros\":100000000000,\"priceCurrencyCode\":\"KRW\",\"productId\":\"a100000\",\"title\":\"Unlimited Play - 100,000 won\",\"type\":\"auto\"}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
