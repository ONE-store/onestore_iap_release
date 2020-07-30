package com.gaa.iap.sample.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gaa.sdk.iap.PurchaseData.RecurringState;
import com.gaa.iap.sample.R;

/**
 * Lucky ONE - 설정 / 월정액 관리 화면
 */

public class SettingPopup extends Dialog {

    private Context mContext;
    private ImageButton mIbClose;
    private RelativeLayout mRlCancelRecurring;
    private LinearLayout mLlPurchased;
    private LinearLayout mLlNotPurchased;

    private TextView mTvCancelButtonKr;
    private TextView mTvCancelButtonEn;

    private boolean mIsAutoPurchased;
    private @RecurringState int mRecurringState;
    private UserCallback mUserCallback;

    public SettingPopup(Context context, boolean isAutoPurchased, @RecurringState int recurringState, UserCallback callback) {
        super(context);
        this.mContext = context;
        this.mRecurringState = recurringState;
        this.mIsAutoPurchased = isAutoPurchased;
        this.mUserCallback = callback;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_settings);
        setCanceledOnTouchOutside(false);

        int width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.90);
        getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);

        mIbClose = findViewById(R.id.ib_close);
        mRlCancelRecurring = findViewById(R.id.rl_setting_manage_recurring_product);
        mLlPurchased = findViewById(R.id.ll_setting_purchased);
        mLlNotPurchased = findViewById(R.id.ll_setting_not_purchased);
        mTvCancelButtonKr = findViewById(R.id.tv_auto_cancel_auto_kr);
        mTvCancelButtonEn = findViewById(R.id.tv_auto_cancel_auto_en);

        if (mIsAutoPurchased) {
            mLlPurchased.setVisibility(View.VISIBLE);
            mLlNotPurchased.setVisibility(View.GONE);

            if (RecurringState.RECURRING == mRecurringState) {
                mTvCancelButtonKr.setText(R.string.msg_setting_cancel_auto_ko);
                mTvCancelButtonEn.setText(R.string.msg_setting_cancel_auto_en);
            } else {
                mTvCancelButtonKr.setText(R.string.msg_setting_resubscribe_auto_ko);
                mTvCancelButtonEn.setText(R.string.msg_setting_resubscribe_auto_en);
            }

        } else {
            mLlPurchased.setVisibility(View.GONE);
            mLlNotPurchased.setVisibility(View.VISIBLE);
        }

        mIbClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mRlCancelRecurring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RecurringState.RECURRING == mRecurringState) {
                    mUserCallback.manageRecurringProduct();
                    dismiss();

                } else {
                    AlertDialog.Builder bld = new AlertDialog.Builder(mContext);
                    bld.setMessage(R.string.msg_setting_resubscribe_auto_alert)
                            .setPositiveButton(R.string.btn_yes, new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mUserCallback.manageRecurringProduct();
                                    dismiss();
                                }
                            })
                            .setNegativeButton(R.string.btn_no, null)
                            .create()
                            .show();
                }

            }
        });
    }

    public interface UserCallback {
        void manageRecurringProduct();
    }
}
