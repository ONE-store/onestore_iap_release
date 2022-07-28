/**
 * @author tia.lee
 * @since 2021. 06.24
 */

package com.gaa.appdev.subscription.sample.base

import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T>: RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    interface OnItemClickListener {
        fun <T> onItemClick(position: Int, item: T)
    }

    lateinit var listener: OnItemClickListener
}