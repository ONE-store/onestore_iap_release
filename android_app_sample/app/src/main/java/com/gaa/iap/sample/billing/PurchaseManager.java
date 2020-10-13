package com.gaa.iap.sample.billing;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.gaa.sdk.iap.AcknowledgeListener;
import com.gaa.sdk.iap.AcknowledgeParams;
import com.gaa.sdk.iap.ConsumeListener;
import com.gaa.sdk.iap.ConsumeParams;
import com.gaa.sdk.iap.IapResult;
import com.gaa.sdk.iap.IapResultListener;
import com.gaa.sdk.iap.ProductDetailsListener;
import com.gaa.sdk.iap.ProductDetailsParams;
import com.gaa.sdk.iap.PurchaseClient;
import com.gaa.sdk.iap.PurchaseClient.ProductType;
import com.gaa.sdk.iap.PurchaseClient.ResponseCode;
import com.gaa.sdk.iap.PurchaseClientStateListener;
import com.gaa.sdk.iap.PurchaseData;
import com.gaa.sdk.iap.PurchaseFlowParams;
import com.gaa.sdk.iap.PurchasesListener;
import com.gaa.sdk.iap.PurchasesUpdatedListener;
import com.gaa.sdk.iap.RecurringProductListener;
import com.gaa.sdk.iap.RecurringProductParams;
import com.gaa.sdk.iap.StoreInfoListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class PurchaseManager implements PurchasesUpdatedListener {

    private final String TAG = PurchaseManager.class.getSimpleName();
    private Activity mActivity;
    private PurchaseClient mPurchaseClient;
    private Callback mCallback;

    private Set<String> mTokenToBe;
    private boolean isServiceConnected;

    public interface Callback {
        void onPurchaseClientSetupFinished();
        void onConsumeFinished(PurchaseData purchaseData, IapResult iapResult);
        void onAcknowledgeFinished(PurchaseData purchaseData, IapResult iapResult);
        void onPurchaseUpdated(List<PurchaseData> purchases);
        void onNeedLogin();
        void onNeedUpdate();
        void onError(String message);
    }

    public PurchaseManager(@NonNull Activity activity, @NonNull Callback callback) {
        mActivity = activity;
        mCallback = callback;
        mPurchaseClient = PurchaseClient.newBuilder(activity)
                .setBase64PublicKey(AppSecurity.getPublicKey())
                .setListener(this)
                .build();

        startConnection(new Runnable() {
            @Override
            public void run() {
                mCallback.onPurchaseClientSetupFinished();
                Log.d(TAG, "Setup successful. Querying inventory.");
                queryPurchasesAsync();
            }
        });
    }

    public void destroy() {
        if (mPurchaseClient != null) {
            mPurchaseClient.endConnection();
            mPurchaseClient = null;
        }
    }

    public void startConnection(final Runnable executeOnSuccess) {
        mPurchaseClient.startConnection(new PurchaseClientStateListener() {
            @Override
            public void onSetupFinished(IapResult iapResult) {
                if (iapResult.isSuccess()) {
                    isServiceConnected = true;
                    if (executeOnSuccess != null) {
                        executeOnSuccess.run();
                    }
                    return;
                }
                /*
                 * delay time 을 주는 이유는 PurchaseManager 생성자에서 connection 을 호출 했지만
                 * error 가 발생하면 MainActivity 에서 다음 작업 할 때 아직 PurchaseManager 의 instance 가 변수에 할당이 되지 않아서
                 * 이후 작업을 하려고 PurchaseManager를 참조할 때 NullPointerException 발생한다.
                 */
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        handleErrorCode(iapResult);
                    }
                }, 300);
            }

            @Override
            public void onServiceDisconnected() {
                isServiceConnected = false;
            }
        });
    }

    private void executeServiceRequest(Runnable runnable) {
        if (isServiceConnected) {
            runnable.run();
        } else {
            startConnection(runnable);
        }
    }

    private void handleErrorCode(IapResult iapResult) {
        if (iapResult.getResponseCode() == ResponseCode.RESULT_NEED_LOGIN) {
            Log.w(TAG, "handleErrorCode() RESULT_NEED_LOGIN");
            mCallback.onNeedLogin();
        } else if (iapResult.getResponseCode() == ResponseCode.RESULT_NEED_UPDATE) {
            Log.w(TAG, "handleErrorCode() RESULT_NEED_UPDATE");
            mCallback.onNeedUpdate();
        } else {
            String message = iapResult.getMessage() + "(" + iapResult.getResponseCode() + ")";
            Log.d(TAG, "handleErrorCode() error: " + message);
            mCallback.onError(message);
        }
    }

    // =================================================================================================================
    // implements PurchasesUpdatedListener
    // =================================================================================================================
    @Override
    public void onPurchasesUpdated(IapResult iapResult, List<PurchaseData> purchaseData) {
        if (iapResult.isSuccess()) {
            mCallback.onPurchaseUpdated(purchaseData);
            return;
        }

        handleErrorCode(iapResult);
    }

    public void launchLoginFlow(IapResultListener listener) {
        mPurchaseClient.launchLoginFlowAsync(mActivity, listener);
    }

    public void launchUpdateOrInstall(IapResultListener listener) {
        mPurchaseClient.launchUpdateOrInstallFlow(mActivity, listener);
    }


    public void launchPurchaseFlow(final PurchaseFlowParams params) {
        executeServiceRequest(new Runnable() {
            @Override
            public void run() {
                mPurchaseClient.launchPurchaseFlow(mActivity, params);
            }
        });
    }

    public void queryProductDetailAsync(final List<String> productIdList, @ProductType final String productType, final ProductDetailsListener listener) {
        executeServiceRequest(new Runnable() {
            @Override
            public void run() {
                ProductDetailsParams params = ProductDetailsParams.newBuilder()
                        .setProductIdList(productIdList)
                        .setProductType(productType)
                        .build();
                mPurchaseClient.queryProductDetailsAsync(params, listener);
            }
        });
    }

    public void consumeAsync(final PurchaseData data) {
        if (mTokenToBe == null) {
            mTokenToBe = new HashSet<>();
        } else if (mTokenToBe.contains(data.getPurchaseToken())) {
            Log.i(TAG, "Token was already scheduled to be consumed - skipping...");
            return;
        }

        mTokenToBe.add(data.getPurchaseToken());

        executeServiceRequest(new Runnable() {
            @Override
            public void run() {
                ConsumeParams params = ConsumeParams.newBuilder().setPurchaseData(data).build();
                mPurchaseClient.consumeAsync(params, new ConsumeListener() {
                    @Override
                    public void onConsumeResponse(IapResult iapResult, PurchaseData purchaseData) {
                        if (iapResult.isSuccess()) {
                            if (purchaseData.getPurchaseToken().equals(data.getPurchaseToken())) {
                                mTokenToBe.remove(data.getPurchaseToken());
                                mCallback.onConsumeFinished(purchaseData, iapResult);
                            } else {
                                mCallback.onError("purchaseToken not equal");
                            }
                        } else {
                            handleErrorCode(iapResult);
                        }
                    }
                });
            }
        });
    }

    public void acknowledgeAsync(final PurchaseData data) {
        if (mTokenToBe == null) {
            mTokenToBe = new HashSet<>();
        } else if (mTokenToBe.contains(data.getPurchaseToken())) {
            Log.i(TAG, "Token was already scheduled to be acknowledged - skipping...");
            return;
        }

        mTokenToBe.add(data.getPurchaseToken());

        executeServiceRequest(new Runnable() {
            @Override
            public void run() {
                AcknowledgeParams params = AcknowledgeParams.newBuilder().setPurchaseData(data).build();
                mPurchaseClient.acknowledgeAsync(params, new AcknowledgeListener() {
                    @Override
                    public void onAcknowledgeResponse(IapResult iapResult, PurchaseData purchaseData) {
                        if (iapResult.isSuccess()) {
                            if (data.getPurchaseToken().equals(purchaseData.getPurchaseToken())) {
                                mTokenToBe.remove(data.getPurchaseToken());
                                mCallback.onAcknowledgeFinished(purchaseData, iapResult);
                            } else {
                                mCallback.onError("purchaseToken not equal");
                            }
                        } else {
                            handleErrorCode(iapResult);
                        }
                    }
                });
            }
        });
    }

    /**
     * TODO: 개발사에서는 소비되지 않는 관리형상품(inapp)에 대해, 애플리케이션의 적절한 life cycle 에 맞춰 구매내역조회를 진행 후 소비를 진행해야합니다.
     * <p>
     * 구매내역조회 API를 이용하여 소비되지 않는 관리형상품(inapp)과 자동결제중인 월정액상품(auto) 목록을 받아옵니다.
     * 관리형상품(inapp)의 경우 소비를 하지 않을 경우 재구매요청을 하여도 구매가 되지 않습니다. 꼭, 소비 과정을 통하여 소모성상품 소비를 진행하여야합니다.
     * 월정액상품(auto)의 경우 구매내역조회 시 recurringState 정보를 통하여 현재상태정보를 확인할 수 있습니다. -> recurringState 0(자동 결제중), 1(해지 예약중)
     * manageRecurringProduct API를 통해 해지예약요청을 할 경우 recurringState는 0 -> 1로 변경됩니다. 해지예약 취소요청을 할경우 recurringState는 1 -> 0로 변경됩니다.
     * <p>
     * 월정액상품(auto)을 11월 10일에 구매를 할 경우 구매내역조회에서 월정액상품의 recurringState는 0(자동 결제중)으로 내려옵니다.
     * 월정액상품은 매달 구매일(12월 10일)에 자동결제가 발생하므로 11월 10일 ~ 12월 9일까지 현재 상태를 유지합니다.
     * 11월 15일에 월정액상태변경API를 이용하여 해지예약(cancel)을 진행할 경우, 12월 9일까지 월정액상품 상태(recurringState)는 1(해지 예약중)이 됩니다.
     * 12월 9일 이전에 월정액상태변경API를 이용하여 해지예약 취소(reactivate)를 진행할 경우, 해당 상품의 상태(recurringState)는 0(자동 결제중)이 됩니다.
     */
    public void queryPurchasesAsync() {
        final List<PurchaseData> result = new ArrayList<>();
        final long time = System.currentTimeMillis();

        final Runnable auto = new Runnable() {
            @Override
            public void run() {
                mPurchaseClient.queryPurchasesAsync(ProductType.AUTO, new PurchasesListener() {
                    @Override
                    public void onPurchasesResponse(IapResult iapResult, List<PurchaseData> purchaseData) {
                        Log.i(TAG, "AUTO - Querying purchases elapsed time: " + (System.currentTimeMillis() - time + "ms"));
                        if (iapResult.isSuccess()) {
                            result.addAll(purchaseData);
                        } else {
                            Log.w(TAG, "AUTO - queryPurchasesAsync() got an error response code: " + iapResult.getResponseCode());
                        }

                        onPurchasesUpdated(iapResult, result);
                    }
                });
            }
        };

        executeServiceRequest(new Runnable() {
            @Override
            public void run() {
                mPurchaseClient.queryPurchasesAsync(ProductType.INAPP, new PurchasesListener() {
                    @Override
                    public void onPurchasesResponse(IapResult iapResult, List<PurchaseData> purchaseData) {
                        Log.i(TAG, "INAPP - Querying purchases elapsed time: " + (System.currentTimeMillis() - time + "ms"));
                        if (iapResult.isSuccess()) {
                            result.addAll(purchaseData);
                        } else {
                            Log.w(TAG, "INAPP - queryPurchasesAsync() got an error response code: " + iapResult.getResponseCode());
                        }
                        auto.run();
                    }
                });
            }
        });
    }

    public void queryPurchasesAsync(@ProductType String productType) {
        final long time = System.currentTimeMillis();
        executeServiceRequest(new Runnable() {
            @Override
            public void run() {
                mPurchaseClient.queryPurchasesAsync(productType, new PurchasesListener() {
                    @Override
                    public void onPurchasesResponse(IapResult iapResult, List<PurchaseData> purchaseData) {
                        Log.i(TAG, productType + " - Querying purchases elapsed time: " + (System.currentTimeMillis() - time + "ms"));
                        if (iapResult.isSuccess()) {
                            mCallback.onPurchaseUpdated(purchaseData);
                        } else {
                            Log.w(TAG, productType + " - queryPurchasesAsync() got an error response code: " + iapResult.getResponseCode());
                            handleErrorCode(iapResult);
                        }
                    }
                });
            }
        });
    }

    public void manageRecurringProductAsync(final PurchaseData purchaseData, final String recurringAction, final RecurringProductListener listener) {
        executeServiceRequest(new Runnable() {
            @Override
            public void run() {
                RecurringProductParams params = RecurringProductParams.newBuilder()
                        .setPurchaseData(purchaseData)
                        .setRecurringAction(recurringAction)
                        .build();
                mPurchaseClient.manageRecurringProductAsync(params, listener);
            }
        });
    }

    public void getStoreCode(final StoreInfoListener listener) {
        executeServiceRequest(new Runnable() {
            @Override
            public void run() {
                mPurchaseClient.getStoreInfoAsync(listener);
            }
        });
    }
}
