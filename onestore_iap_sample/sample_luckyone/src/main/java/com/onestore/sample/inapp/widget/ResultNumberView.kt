package com.onestore.sample.inapp.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.onestore.sample.inapp.R

class ResultNumberView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0)
    : ConstraintLayout(context, attrs, defStyle) {

    private var mRecyclerView: RecyclerView

    init {
        val view: View = LayoutInflater.from(context).inflate(R.layout.view_result, this, true)
        mRecyclerView = view.findViewById(R.id.recycler)

        with(mRecyclerView) {
            addItemDecoration(DividerItemDecoration(context))
            layoutManager = LinearLayoutManager(context)
            overScrollMode = OVER_SCROLL_NEVER
        }
    }

    fun setData(luckyNumbers: List<Int>, myNumbers: List<List<Int>>) {
        mRecyclerView.adapter = BallAdapter(luckyNumbers, myNumbers)
    }

    fun clear() {
        val adapter = mRecyclerView.adapter
        if (adapter != null) {
            val count = adapter.itemCount
            adapter.notifyItemRangeRemoved(0, count)
        }
    }

    class BallAdapter constructor(private val mLuckyNumbers: List<Int>, private val mMyNumbers: List<List<Int>>)
        : RecyclerView.Adapter<BallViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BallViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return BallViewHolder(inflater.inflate(R.layout.view_result_item, parent, false))
        }

        override fun onBindViewHolder(holder: BallViewHolder, position: Int) {
            holder.setNumber(mLuckyNumbers, mMyNumbers[position])
        }

        override fun getItemCount(): Int {
            return mMyNumbers.size
        }
    }

    class BallViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val mRankView: TextView = itemView.findViewById(R.id.rank)
        private val mBallViews = ArrayList<BallView>()

        fun setNumber(luckyNums: List<Int>, myNums: List<Int>) {
            var luckyCount = 0
            for ((index, myNum) in myNums.withIndex()) {
                var isLucy = false
                for (lucky in luckyNums) {
                    if (myNum == lucky) {
                        luckyCount++
                        isLucy = true
                        break
                    }
                }
                mBallViews[index].setNumber(myNum, isLucy)
            }
            mRankView.text = getRankString(luckyCount)
        }

        private fun getRankString(number: Int): String {
            return when (number) {
                3 -> "4th"
                4 -> "3rd"
                5 -> "2nd"
                6 -> "1st"
                else -> " - "
            }
        }

        init {
            val group = itemView as ViewGroup
            for (i in 1 until group.childCount) {
                val child = group.getChildAt(i)
                if (child is BallView) {
                    mBallViews.add(child)
                }
            }
        }
    }

    class DividerItemDecoration constructor(context: Context)
        : RecyclerView.ItemDecoration() {

        private val divider: Drawable?

        init {
            val styledAttributes = context.obtainStyledAttributes(intArrayOf(android.R.attr.listDivider))
            divider = styledAttributes.getDrawable(0)
            styledAttributes.recycle()
        }

        override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
            if (divider != null) {
                val left = parent.paddingLeft
                val right = parent.width - parent.paddingRight
                val childCount = parent.childCount

                for (i in 0 until childCount) {
                    val child = parent.getChildAt(i)
                    val params = child.layoutParams as RecyclerView.LayoutParams
                    val top = child.bottom + params.bottomMargin
                    val bottom = top + divider.intrinsicHeight

                    divider.setBounds(left, top, right, bottom)
                    divider.draw(canvas)
                }
            }
        }
    }
}