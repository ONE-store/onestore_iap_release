package com.gaa.appdev.subscription.sample.common

import android.content.Context
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaa.appdev.subscription.sample.R

class LinearSpacingDecorator(private val context: Context, var spacing: Int, var orientation: Int)
    : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val position = parent.getChildAdapterPosition(view)
        val adapter = parent.adapter
        if (adapter != null) {
            val itemSpacing = if (position == adapter.itemCount.minus(1) ?: 0) {
                context.resources.getDimensionPixelSize(R.dimen.global_layout_margin_5)
            } else {
                spacing
            }

            when (orientation) {
                LinearLayoutManager.VERTICAL -> setupVertical(outRect)
                LinearLayoutManager.HORIZONTAL -> setupHorizontal(outRect, itemSpacing)
            }
        }
    }

    private fun setupHorizontal(rect: Rect, spacing: Int) {
        rect.right = spacing
    }

    private fun setupVertical(rect: Rect) {
        rect.bottom = spacing
    }
}