package com.gaa.iap.sample.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.gaa.iap.sample.R;

import java.util.List;

public class LuckyNumberView extends ConstraintLayout {

    private LinearLayout mBallLayout;

    public LuckyNumberView(Context context) {
        super(context);
        init();
    }

    public LuckyNumberView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_lucky, this, true);
        mBallLayout = view.findViewById(R.id.balls);
        childInVisible();
    }

    public void setNumber(List<Integer> numbers) {
        int childCount = mBallLayout.getChildCount();
        if (numbers != null && numbers.size() == childCount) {
            for (int i = 0; i < childCount; i++) {
                BallView ball = (BallView) mBallLayout.getChildAt(i);
                ball.setNumber(numbers.get(i), true);
                ball.setVisibility(View.VISIBLE);
            }
        } else {
            childInVisible();
        }
    }

    private void childInVisible() {
        for (int i = 0; i < mBallLayout.getChildCount(); i++) {
            mBallLayout.getChildAt(i).setVisibility(View.INVISIBLE);
        }
    }
}
