package com.gaa.iap.sample;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.gaa.sdk.iap.Logger;
import com.google.gson.Gson;
import com.gaa.sdk.iap.IapResult;
import com.gaa.sdk.iap.IapResultListener;
import com.gaa.sdk.iap.ProductDetail;
import com.gaa.sdk.iap.ProductDetailsListener;
import com.gaa.sdk.iap.PurchaseClient.ProductType;
import com.gaa.sdk.iap.PurchaseClient.RecurringAction;
import com.gaa.sdk.iap.PurchaseClient.ResponseCode;
import com.gaa.sdk.iap.PurchaseData;
import com.gaa.sdk.iap.PurchaseData.RecurringState;
import com.gaa.sdk.iap.PurchaseFlowParams;
import com.gaa.sdk.iap.RecurringProductListener;
import com.gaa.sdk.iap.StoreInfoListener;
import com.gaa.iap.sample.billing.AppSecurity;
import com.gaa.iap.sample.billing.PurchaseManager;
import com.gaa.iap.sample.util.AppConstants;
import com.gaa.iap.sample.util.LuckyUtils;
import com.gaa.iap.sample.widget.BuyProductPopup;
import com.gaa.iap.sample.widget.LuckyNumberView;
import com.gaa.iap.sample.widget.ResultNumberView;
import com.gaa.iap.sample.widget.SettingPopup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Lucky ONE
 * <p>
 * 원스토어 In-app purchasing v19을 이용한 게임
 * <p>
 * TODO: Lucky ONE 샘플 앱에서는 개발사에서 스토어 인앱결제를 이용하기 위한 일반적인 IAP v19 API 구매 시나리오를 나열하고 있습니다. 샘플 구동 전에 해당 가이드를 꼼꼼히 읽어보시기 바랍니다.
 * <p>
 * Lucky ONE 샘플 앱은 많은 사람들이 사랑하는 로또의 번호 추천 시스템과 비슷하게 개발되었습니다. 사용자는 번호를 생성하여 추첨을 하기 위해서 사용자는 코인을 구매하여야 하며, 구매된 코인을 이용하여 게임을 이용할 수 있습니다.
 * <p>
 * 메인화면에서는 보유코인 수와 사용자의 추천 랜덤 번호, 그리고 실제 추첨된 5개의 번호를 나열하고 있습니다.
 * 3개의 번호 일치시 5개의 코인을 지급하며, 4개 일치 시 30개 코인을 지급, 5개일때는 100개의 코인, 6개일때는 300개의 코인을 지급합니다.
 * 또한, 월정액 상품을 제공하여 보유한 코인 갯수와 무관하게 계속해서 번호를 생성할 수 있는 상품이 있습니다.
 * <p>
 * 사용자는 구매 버튼을 선택 할 경우 상품을 선택할 수 있는 창이 뜨게되며, 사용자의 선택에 따라 5, 10coins 와 ONE more 상품(관리형상품)과 월정액상품 구매를 할 수 있습니다.
 * 관리형상품의 경우 구매를 완료하게 될 경우 상품소비를 통하여 해당 구매상품 소비과정을 진행해야합니다. 또한 월정액상품은 구매 이후 각 개발사에서 자체적으로 구매 상태를 관리하여야 합니다.
 * 샘플에서 월정액상품의 구매 상태를 관리하기 위하여 애플리케이션 프리퍼런스에 해당 정보를 저장하고있으며, 이후에 구매 정보가 필요할 시점에 사용하고 있습니다.
 * <p>
 * Lucky ONE 앱에서는 앱 구동시점에 구매내역조회 API를 이용하여 관리형상품(inapp)/월정액상품(auto)에 대하여 구매내역 정보를 받아오고 있으며,
 * 관리형상품(inapp) 구매내역을 받아올 경우 상품소비가 진행되지 않는 상품이므로 상품소비과정을 진행합니다.
 * 관리형상품(inapp) p50000의 경우는 소비를 하지 않고 확인만 하여 6줄의 번호를 생성할 수 있습니다.
 * 또한 월정액상품(auto) 구매내역을 받아올 경우 메인화면 하단의 "번호 생성하기" 버튼을 무한으로 플레이 할 수 있도록 변경하고 있습니다.
 * <p>
 * 월정액상품(auto)의 경우 구매내역의 recurringState 정보에 따라 설정화면에서 월정액상품(auto) 해지예약과 해지예약 취소 버튼을 노출하고 있습니다.
 * <p>
 * <p>
 * - Lucky ONE 샘플앱 구동 전 준비 사항 -
 * 1. 샘플앱의 패키지 이름을 변경합니다.
 * 3. AppSecurity 클래스의 getPublicKey()메서드를 이용하기 위하여 jin/public_keys.c 코드 상의 변경된 패키지 이름에 맞게 Native 메서드 명을 변경해줍니다.
 * 2. 스토어 개발자센터에 애플리케이션을 등록 후 base64 public key 값을 jin/public_keys.c 코드 내에 붙여넣습니다.
 * 4. 스토어 개발자센터에 상품을 등록합니다. 샘플앱의 경우 상품 정보는 AppConstant 클래스에 있습니다.
 * 5. 애플리케이션 위변조 체크를 위해서 개발자 센터에 등록하는 앱은 사이닝이 된 APK여야 합니다. (앱 사이닝 없이 테스트를 위해서는 Sandbox 계정을 등록하여 확인하시면 됩니다.)
 */
public class MainActivity extends AppCompatActivity implements PurchaseManager.Callback {

    private static final String TAG = "MainActivity";

    private LuckyNumberView mLuckyNumberView;
    private ResultNumberView mResultNumberView;
    private TextView mCoinView;
    private Button mBuyButton;
    private CardView mPlayButton;
    private ProgressDialog mProgressDialog;
    private PurchaseManager mPurchaseManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        mCoinView = findViewById(R.id.coinView);
        mBuyButton = findViewById(R.id.buyBtn);
        mBuyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBuyProductDialog();
            }
        });

        mLuckyNumberView = findViewById(R.id.luckyView);
        mResultNumberView = findViewById(R.id.resultView);
        mPlayButton = findViewById(R.id.playBtn);
        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playGame();
            }
        });

        // 첫 시작시 20코인 지급
        if (isFirstLaunched()) {
            updateCoin(20);
            updateFirstLaunched();
        } else {
            mCoinView.setText(String.valueOf(getCurrentCoin()));
        }

        updatePlayButtonText(isMonthlyItemAvailable());

        showProgressDialog();

        Logger.setLogLevel(Log.VERBOSE);
        mPurchaseManager = new PurchaseManager(this, this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveMonthlyItem(null);
        updateOneMore(false);
        mPurchaseManager.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final @RecurringState int recurringState;

        // 설정화면 진입 전 월정액 UI 업데이트를 위하여,
        // 현재 저장된 월정액상품(auto) recurringState 정보를 설정화면으로 넘겨줍니다.
        if (isMonthlyItemAvailable()) {
            if (RecurringState.RECURRING == getMonthlyItem().getRecurringState()) {
                // 월정액 상태 - 자동결제중
                recurringState = RecurringState.RECURRING;
            } else {
                // 월정액 상태 - 해지예약중
                recurringState = RecurringState.CANCEL;
            }
        } else {
            recurringState = RecurringState.NON_AUTO_PRODUCT;
        }

        new SettingPopup(this, isMonthlyItemAvailable(), recurringState, new SettingPopup.UserCallback() {
            @Override
            public void manageRecurringProduct() {
                PurchaseData purchaseData = getMonthlyItem();
                if (purchaseData != null) {
                    String recurringAction = RecurringAction.REACTIVATE;
                    if (recurringState == RecurringState.RECURRING) {
                        recurringAction = RecurringAction.CANCEL;
                    }
                    // 월정액 해지예약 또는 해지예약 취소 수행
                    manageRecurringAuto(purchaseData, recurringAction);
                }
            }
        }).show();
        return true;
    }

    public void updatePlayButtonText(boolean isAuto) {
        if (isAuto) {
            ((TextView) mPlayButton.findViewById(R.id.play_text)).setText(R.string.btn_number_generate_auto_ko);
            ((TextView) mPlayButton.findViewById(R.id.play_desc)).setText(R.string.btn_number_generate_auto_en);
        } else {
            ((TextView) mPlayButton.findViewById(R.id.play_text)).setText(R.string.btn_number_generate_inapp_ko);
            ((TextView) mPlayButton.findViewById(R.id.play_desc)).setText(R.string.btn_number_generate_inapp_en);
        }
    }

    private void updateCoinsPurchased(String productId) {
        int coins = getPurchasedCoins(productId);
        updateCoin(coins);
    }

    private int getPurchasedCoins(String productId) {
        switch (productId) {
            case AppConstants.InappType.PRODUCT_INAPP_5000:
                return 5;
            case AppConstants.InappType.PRODUCT_INAPP_10000:
                return 10;
            case AppConstants.InappType.PRODUCT_INAPP_50000:
                return 60;
            default:
                return 0;
        }
    }

    private void showProgressDialog() {
        if (!isFinishing() && !isShowingProgressDialog()) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this);
            }
            mProgressDialog.setMessage("Service connection...");
            mProgressDialog.show();
        }
    }

    private boolean isShowingProgressDialog() {
        return mProgressDialog != null && mProgressDialog.isShowing();
    }

    private void dismissProgressDialog() {
        if (isShowingProgressDialog()) mProgressDialog.dismiss();
    }

    private void showDialog(CharSequence message) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create().show();
    }

    private void showDialog(CharSequence message, DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, listener)
                .setNegativeButton(android.R.string.cancel, null)
                .create().show();
    }

    private void showBuyProductDialog() {
        showProgressDialog();

        List<String> allItems = AppConstants.getAllProductList();
        mPurchaseManager.queryProductDetailAsync(allItems, ProductType.ALL, new ProductDetailsListener() {
            @Override
            public void onProductDetailsResponse(IapResult iapResult, List<ProductDetail> productDetails) {
                dismissProgressDialog();

                if (iapResult.isSuccess()) {
                    if (productDetails.isEmpty()) {
                        showDialog(getString(R.string.msg_product_detail_not_found));
                        return;
//                        productDetailList = LuckyUtils.getDummyList();
                    }

                    Collections.sort(productDetails, new Comparator<ProductDetail>() {
                        @Override
                        public int compare(ProductDetail o1, ProductDetail o2) {
                            int i1 = Integer.parseInt(o1.getPrice());
                            int i2 = Integer.parseInt(o2.getPrice());
                            return Integer.compare(i1, i2);
                        }
                    });

                    for (int i = 0; i < productDetails.size(); i++) {
                        Log.d(TAG, "ProductDetail[" + i + "]=" + productDetails.get(i).toString());
                    }

                    new BuyProductPopup(MainActivity.this, productDetails, new BuyProductPopup.OnItemClickListener() {
                        @Override
                        public void onClick(ProductDetail item) {
                            buyProduct(item.getProductId(), item.getType());
                        }
                    }).show();
                } else {
                    showDialog(iapResult.getMessage());
                }
            }
        });
    }

    private void playGame() {
        if (!isMonthlyItemAvailable() && getCurrentCoin() < 5) {
            showDialog(getString(R.string.msg_alert_no_coin), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    showBuyProductDialog();
                }
            });
            return;
        }

        if (!isMonthlyItemAvailable()) {
            updateCoin(-5);
        }

        List<Integer> luckyBalls = LuckyUtils.getMyNumbers();
        List<List<Integer>> myBalls = LuckyUtils.getSuggestNumbers(isOneMore() ? 6 : 5);

        mLuckyNumberView.setNumber(luckyBalls);
        mResultNumberView.clear();
        mResultNumberView.setData(luckyBalls, myBalls);

        int earningCoin = LuckyUtils.getWonCoin(luckyBalls, myBalls);
        if (earningCoin > 0) {
            updateCoin(earningCoin);
            CharSequence message = Html.fromHtml(getResources().getString(R.string.msg_earning_coins, earningCoin));
            showDialog(message);
        }
    }

    private void buyProduct(final String productId, @ProductType String productType) {
        Log.d(TAG, "buyProduct() - productId:" + productId + " productType: " + productType);
        /*
         * TODO: AppSecurity.generatePayload() 는 예제일 뿐, 각 개발사의 규칙에 맞는 payload를 생성하여야 한다.
         *
         * 구매요청을 위한 Developer payload를 생성합니다.
         * Developer Payload 는 상품의 구매 요청 시에 개발자가 임의로 지정 할 수 있는 문자열입니다.
         * 이 Payload 값은 결제 완료 이후에 응답 값에 다시 전달 받게 되며 결제 요청 시의 값과 차이가 있다면 구매 요청에 변조가 있다고 판단 하면 됩니다.
         * Payload 검증을 통해 Freedom 과 같은 변조 된 요청을 차단 할 수 있으며, Payload 의 발급 및 검증 프로세스를 자체 서버를 통해 이루어 지도록합니다.
         * 입력 가능한 Developer Payload는 최대 200byte까지 입니다.
         */
        String devPayload = AppSecurity.generatePayload();

        // 구매 후 dev payload를 검증하기 위하여 프리퍼런스에 임시로 저장합니다.
        savePayloadString(devPayload);
        showProgressDialog();

        /*
         * PurchaseClient의 launchPurchaseFlowAsync API를 이용하여 구매요청을 진행합니다.
         * 상품명을 공백("")으로 요청할 경우 개발자센터에 등록된 상품명을 결제화면에 노출됩니다. 구매시 지정할 경우 해당 문자열이 결제화면에 노출됩니다.
         */
        PurchaseFlowParams params = PurchaseFlowParams.newBuilder()
                .setProductId(productId)
                .setProductType(productType)
                .setDeveloperPayload(devPayload)
                .setProductName("")
                .setGameUserId("")
                .setPromotionApplicable(false)
                .build();

        mPurchaseManager.launchPurchaseFlow(params);
    }

    // 월정액상품(auto)의 상태변경(해지예약 / 해지예약 취소)를 진행합니다.
    private void manageRecurringAuto(final PurchaseData purchaseData, final String recurringAction) {
        showProgressDialog();

        mPurchaseManager.manageRecurringProductAsync(purchaseData, recurringAction, new RecurringProductListener() {
            @Override
            public void onRecurringResponse(IapResult iapResult, PurchaseData purchaseData, @RecurringAction String action) {

                if (iapResult.isSuccess()) {
                    Log.d(TAG, "manageRecurringProductAsync() onSuccess, " + action + " " + purchaseData.toString());

                    if (RecurringAction.CANCEL.equalsIgnoreCase(action)) {
                        showDialog(getString(R.string.msg_setting_cancel_auto_complete));
                    } else {
                        showDialog(getString(R.string.msg_setting_resubscribe_auto_complete));
                    }

                    mPurchaseManager.queryPurchasesAsync(ProductType.AUTO);
                } else if (iapResult.getResponseCode() == ResponseCode.RESULT_NEED_UPDATE) {
                    dismissProgressDialog();
                    Log.e(TAG, "manageRecurringProductAsync() - Payment module 앱의 업데이트가 필요합니다");
                    updateOrInstallPaymentModule();
                } else {
                    dismissProgressDialog();
                    showDialog(iapResult.getMessage());
                }
            }
        });
    }

    /*
     * 에러 코드 중 로긴이 필요로 할 경우 launchLoginFlowAsync API를 이용하여 명시적 로그인을 수행합니다.
     * launchLoginFlowAsync API를 호출할 경우 UI적으로 스토어 로그인화면이 뜰 수 있습니다.
     * 개발사에서는 로그인 성공 시 파라미터로 넘겨준 Activity의 onActivityResult에서 Intent값을 전달 받아서,
     * PurchaseClient의 handleLoginResult() API를 이용하여 응답값을 파싱합니다.
     */
    private void launchLoginFlow(final Runnable executeOnSuccess) {
        showDialog("스토어 계정 정보를 확인 할 수 없습니다. 로그인 하시겠습니까?", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mPurchaseManager.launchLoginFlow(new IapResultListener() {
                    @Override
                    public void onResponse(IapResult iapResult) {
                        if (iapResult.isSuccess()) {
                            if (executeOnSuccess != null) {
                                executeOnSuccess.run();
                            }
                        } else {
                            Log.w(TAG, "launchLoginFlow() got an error response code: " + iapResult.getResponseCode());
                        }
                    }
                });
            }
        });
    }

    private void updateOrInstallPaymentModule() {
        mPurchaseManager.launchUpdateOrInstall(new IapResultListener() {
            @Override
            public void onResponse(IapResult iapResult) {
                if (iapResult.isSuccess()) {
                    mPurchaseManager.startConnection(new Runnable() {
                        @Override
                        public void run() {
                            onPurchaseClientSetupFinished();
                            mPurchaseManager.queryPurchasesAsync();
                        }
                    });
                } else {
                    Log.w(TAG, "launchUpdateOrInstall() got an error response code: " + iapResult.getResponseCode());
                }
            }
        });
    }

    // =================================================================================================================
    // PurchaseManager Callback
    // =================================================================================================================

    @Override
    public void onPurchaseClientSetupFinished() {
        dismissProgressDialog();
        mPurchaseManager.getStoreCode(new StoreInfoListener() {
            @Override
            public void onStoreInfoResponse(IapResult iapResult, String storeCode) {
                if (iapResult.isSuccess()) {
                    Log.d(TAG, "onPurchaseClientSetupFinished: storeCode: " + storeCode);
                    saveStoreCode(storeCode);
                }
            }
        });
    }

    @Override
    public void onPurchaseUpdated(List<PurchaseData> purchases) {
        dismissProgressDialog();

        for (PurchaseData purchase: purchases) {
//            if (!isValidPayload(purchase.getDeveloperPayload())) {
//                Log.d(TAG, "onPurchaseUpdated() - OK :: invalid dev payload: " + purchase.getDeveloperPayload());
//                showDialog(getString(R.string.msg_alert_dev_payload_invalid));
//                return;
//            }

            if (AppSecurity.verifyPurchase(purchase.getOriginalJson(), purchase.getSignature())) {
                final String productId = purchase.getProductId();
                if (AppConstants.AutoType.PRODUCT_AUTO_100000.equals(productId)) {
                    // 월정액상품이면 소비를 수행하지 않는다.
                    if (purchase.isAcknowledged()) {
                        saveMonthlyItem(purchase);
                    } else {
                        mPurchaseManager.acknowledgeAsync(purchase);
                    }
                } else if (AppConstants.InappType.PRODUCT_INAPP_50000.equals(productId)) {
                    // 프리미엄 인앱 상품도 소비를 수행하지 않는다.
                    if (purchase.isAcknowledged()) {
                        updateOneMore(true);
                    } else {
                        mPurchaseManager.acknowledgeAsync(purchase);
                    }
                } else {
                    // 관리형상품(inapp)의 구매완료 이후 또는 구매내역조회 이후 소비되지 않는 관리형상품에 대해서 소비를 진행합니다.
                    mPurchaseManager.consumeAsync(purchase);
                }
            } else {
                showDialog(getString(R.string.msg_alert_signature_invalid));
            }
        }
    }

    @Override
    public void onConsumeFinished(PurchaseData purchaseData, IapResult iapResult) {
        dismissProgressDialog();

        if (iapResult.isSuccess()) {
            updateCoinsPurchased(purchaseData.getProductId());
            Spanned message = Html.fromHtml(
                    String.format(getString(R.string.msg_purchase_success_inapp), getPurchasedCoins(purchaseData.getProductId()))
            );
            showDialog(message);
        } else {
            showDialog(iapResult.getMessage());
        }
    }

    @Override
    public void onAcknowledgeFinished(PurchaseData purchase, IapResult iapResult) {
        dismissProgressDialog();

        if (iapResult.isSuccess()) {
            if (AppConstants.AutoType.PRODUCT_AUTO_100000.equals(purchase.getProductId())) {
                // 월정액상품이면 소비를 수행하지 않는다.
                // 월정액상품의 acknowledge 완료되면 recurringState 와 acknowledgeState 의 변경된 값을 가져오기 위해
                // 다시 queryPurchasesAsync 를 호출해야한다.
                mPurchaseManager.queryPurchasesAsync(ProductType.AUTO);
                showDialog(Html.fromHtml(getString(R.string.msg_purchase_success_auto)));
            } else if (AppConstants.InappType.PRODUCT_INAPP_50000.equals(purchase.getProductId())) {
                updateOneMore(true);
                showDialog(Html.fromHtml(getString(R.string.msg_purchase_success_one_more)));
            }
        } else {
            showDialog(iapResult.getMessage());
        }
    }

    @Override
    public void onNeedLogin() {
        dismissProgressDialog();
        launchLoginFlow(null);
    }

    @Override
    public void onNeedUpdate() {
        dismissProgressDialog();
        updateOrInstallPaymentModule();
    }

    @Override
    public void onError(String message) {
        dismissProgressDialog();
        showDialog(message);
    }

    // =================================================================================================================
    // SharedPreferences func
    // =================================================================================================================

    private boolean isFirstLaunched() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        Log.d(TAG, "isFirstLaunched() - " + sp.getBoolean(AppConstants.KEY_IS_FIRST, true));
        return sp.getBoolean(AppConstants.KEY_IS_FIRST, true);
    }

    private void updateFirstLaunched() {
        SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();
        spe.putBoolean(AppConstants.KEY_IS_FIRST, false);
        spe.apply();
    }

    private void updateCoin(int coin) {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();

        int savedCoins = sp.getInt(AppConstants.KEY_TOTAL_COIN, 0);
        savedCoins += coin;
        spe.putInt(AppConstants.KEY_TOTAL_COIN, savedCoins);
        spe.apply();

        mCoinView.setText(String.valueOf(savedCoins));
    }

    private int getCurrentCoin() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        int coin = sp.getInt(AppConstants.KEY_TOTAL_COIN, 0);
        Log.d(TAG, "getCurrentCoin() coin: " + coin);

        return coin;
    }

    private boolean isOneMore() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        boolean isOneMore = sp.getBoolean(AppConstants.KEY_ONE_MORE, false);
        Log.d(TAG, "isOneMore() - " + isOneMore);
        return isOneMore;
    }

    private void updateOneMore(boolean update) {
        SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();
        spe.putBoolean(AppConstants.KEY_ONE_MORE, update);
        spe.apply();
    }

    private void saveMonthlyItem(PurchaseData purchaseData) {
        SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();

        if (purchaseData == null) {
            spe.remove(AppConstants.AutoType.PRODUCT_AUTO_100000);
            spe.putBoolean(AppConstants.KEY_MODE_MONTHLY, false);
            updatePlayButtonText(false);

        } else {
            Gson gson = new Gson();
            String json = gson.toJson(purchaseData);

            spe.putString(AppConstants.AutoType.PRODUCT_AUTO_100000, json);
            spe.putBoolean(AppConstants.KEY_MODE_MONTHLY, true);
            updatePlayButtonText(true);
        }

        spe.apply();
    }

    private PurchaseData getMonthlyItem() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);

        Gson gson = new Gson();
        String json = sp.getString(AppConstants.AutoType.PRODUCT_AUTO_100000, "");
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        Log.d(TAG, "getMonthlyItem: " + json);
        return gson.fromJson(json, PurchaseData.class);
    }

    public boolean isMonthlyItemAvailable() {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        return sp.getBoolean(AppConstants.KEY_MODE_MONTHLY, false);
    }

    private boolean isValidPayload(String payload) {
        SharedPreferences sp = getPreferences(MODE_PRIVATE);
        String savedPayload = sp.getString(AppConstants.KEY_PAYLOAD, "");

        Log.d(TAG, "isValidPayload() saved  ::" + savedPayload);
        Log.d(TAG, "isValidPayload() server ::" + payload);

        return savedPayload.equals(payload);
    }

    private void savePayloadString(String payload) {
        SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();
        spe.putString(AppConstants.KEY_PAYLOAD, payload);
        spe.apply();
    }

    private void saveStoreCode(String storeCode) {
        SharedPreferences.Editor spe = getPreferences(MODE_PRIVATE).edit();
        spe.putString(AppConstants.KEY_STORE_CODE, storeCode);
        spe.apply();
    }
}
