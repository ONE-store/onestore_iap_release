package com.gaa.appdev.iap.sample.widget

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.widget.TextViewCompat
import com.gaa.appdev.iap.sample.R

class BallView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : FrameLayout(context, attrs, defStyle) {

    private var mImageView: ImageView
    private var mTextView: TextView
    private var mFitDirection: FitDirection? = FitDirection.UNSPECIFIED

    private enum class FitDirection(private val nativeInt: Int) {
        UNSPECIFIED(-1), WIDTH(0), HEIGHT(1);

        companion object {
            fun valueOf(value: Int): FitDirection? {
                for (fit in values()) {
                    if (fit.nativeInt == value) {
                        return fit
                    }
                }
                return null
            }
        }
    }

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.view_ball, this, true)
        mImageView = view.findViewById(R.id.image)
        mTextView = view.findViewById(R.id.text)

        TextViewCompat.setAutoSizeTextTypeWithDefaults(
            mTextView,
            TextViewCompat.AUTO_SIZE_TEXT_TYPE_NONE
        )

        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(
            mTextView,
            2, 100, 2, TypedValue.COMPLEX_UNIT_SP
        )

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.BallView, 0, 0)
            val fit = a.getInt(R.styleable.BallView_fitDirection, -1)
            if (fit >= 0) {
                mFitDirection = FitDirection.valueOf(fit)
            }
            a.recycle()
        }
    }

    fun setNumber(number: Int, isLucky: Boolean) {
        mTextView.text = number.toString()
        mImageView.setBackgroundResource(getLuckyBgResourceId(number, isLucky))
    }

    private fun getLuckyBgResourceId(number: Int, isLucky: Boolean): Int {
        return if (isLucky) {
            if (number <= 10) {
                R.drawable.ball_1
            } else if (number <= 20) {
                R.drawable.ball_10
            } else if (number <= 30) {
                R.drawable.ball_20
            } else if (number <= 40) {
                R.drawable.ball_30
            } else {
                R.drawable.ball_40
            }
        } else {
            R.drawable.ball_gray
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mFitDirection == FitDirection.WIDTH) {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        } else if (mFitDirection == FitDirection.HEIGHT) {
            super.onMeasure(heightMeasureSpec, heightMeasureSpec)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }
}