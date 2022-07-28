package com.gaa.appdev.iap.sample.widget

import android.content.Context
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaa.appdev.iap.sample.R
import com.gaa.sdk.iap.ProductDetail

class BuyProductDialog(context: Context, items: List<ProductDetail>, listener: OnItemClickListener) : AlertDialog(context) {

    init {
        require(items.isNotEmpty())
        setTitle("Buy Product")
        val recyclerView = RecyclerView(context)
        recyclerView.overScrollMode = View.OVER_SCROLL_NEVER
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.addItemDecoration(PaddingItemDecoration(context, 10))
        recyclerView.adapter = ProductAdapter(context, items, listener)
        setView(recyclerView)
    }

    interface OnItemClickListener {
        fun onClick(item: ProductDetail)
    }

    private inner class ProductAdapter constructor(
        context: Context,
        val items: List<ProductDetail>,
        val listener: OnItemClickListener) : RecyclerView.Adapter<ProductViewHolder>() {

        private val inflater: LayoutInflater = LayoutInflater.from(context)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
            return ProductViewHolder(
                inflater.inflate(
                    R.layout.dialog_buy_product_row,
                    parent,
                    false
                )
            )
        }

        override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
            holder.setData(items[position], listener)
        }

        override fun getItemCount(): Int {
            return items.size
        }

    }

    private inner class ProductViewHolder constructor(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        private val textView: TextView = itemView.findViewById(R.id.text1)

        fun setData(productDetail: ProductDetail, listener: OnItemClickListener) {
            textView.text = productDetail.title
            itemView.setOnClickListener {
                listener.onClick(productDetail)
                dismiss()
            }
        }

    }

    private class PaddingItemDecoration constructor(context: Context, space: Int)
        : RecyclerView.ItemDecoration() {

        private val PADDING: Int
        private val LEFT_RIGHT: Int
        private val space: Int

        init {
            val density = context.resources.displayMetrics.density
            PADDING = (15 * density).toInt()
            LEFT_RIGHT = (20 * density).toInt()
            this.space = (space * density).toInt()
        }

        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val adapterPosition = parent.getChildAdapterPosition(view)
            val itemCount = parent.adapter!!.itemCount - 1

            if (adapterPosition != itemCount) {
                outRect.bottom = space
            } else {
                outRect.bottom = space + PADDING
            }

            if (adapterPosition == 0) {
                outRect.top = space + PADDING
            }

            outRect.left = LEFT_RIGHT
            outRect.right = LEFT_RIGHT
        }
    }
}