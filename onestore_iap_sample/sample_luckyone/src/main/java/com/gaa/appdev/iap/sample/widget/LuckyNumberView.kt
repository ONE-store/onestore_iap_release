package com.gaa.appdev.iap.sample.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.gaa.appdev.iap.sample.R

class LuckyNumberView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : ConstraintLayout(context, attrs, defStyle) {

    private var mBallLayout: LinearLayout

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.view_lucky, this, true)
        mBallLayout = view.findViewById(R.id.balls)
        childInVisible()
    }

    fun setNumber(numbers: List<Int>) {
        val childCount = mBallLayout.childCount
        if (numbers.size == childCount) {
            for (i in 0 until childCount) {
                val ball: BallView = mBallLayout.getChildAt(i) as BallView
                ball.setNumber(numbers[i], true)
                ball.visibility = VISIBLE
            }
        } else {
            childInVisible()
        }
    }

    private fun childInVisible() {
        for (i in 0 until mBallLayout.childCount) {
            mBallLayout.getChildAt(i).visibility = INVISIBLE
        }
    }
}
