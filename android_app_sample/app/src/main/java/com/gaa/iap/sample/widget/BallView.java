package com.gaa.iap.sample.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.gaa.iap.sample.R;

public class BallView extends FrameLayout {

    private ImageView mImageView;
    private TextView mTextView;

    public BallView(@NonNull Context context) {
        super(context);
        init(context, null);
    }

    public BallView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_ball, this, true);
        mImageView = view.findViewById(R.id.image);
        mTextView = view.findViewById(R.id.text);
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BallView, 0, 0);

            int style = a.getInt(R.styleable.BallView_ballStyle, 0);
            int size;
            ViewGroup.LayoutParams params = mImageView.getLayoutParams();
            if ( style == 0) {
                size = dpToPx(48);
                params.width = size;
                params.height = size;
                mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 26);
            } else {
                size = dpToPx(38);
                params.width = size;
                params.height = size;
                mTextView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
            }

            a.recycle();
        }
    }

    public void setNumber(int number, boolean isLucky) {
        mTextView.setText(String.valueOf(number));
        mImageView.setBackgroundResource(getLuckyBgResourceId(number, isLucky));
    }

    private int getLuckyBgResourceId(int number, boolean isLucky) {
        if (isLucky) {
            if (number <= 10) {
                return R.drawable.ball_1;
            } else if (number <= 20) {
                return R.drawable.ball_10;
            } else if (number <= 30) {
                return R.drawable.ball_20;
            } else if (number <= 40) {
                return R.drawable.ball_30;
            } else {
                return R.drawable.ball_40;
            }
        } else {
            return R.drawable.ball_gray;
        }
    }

    private int dpToPx(int dp) {
        return (int)(dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
