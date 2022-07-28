package com.gaa.appdev.subscription.sample.bindings

import android.annotation.SuppressLint
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gaa.appdev.subscription.sample.R
import com.gaa.appdev.subscription.sample.base.BaseRecyclerViewAdapter
import com.gaa.appdev.subscription.sample.common.LinearSpacingDecorator
import com.gaa.appdev.subscription.sample.common.ProductAdapter
import com.gaa.sdk.iap.ProductDetail
import com.gaa.sdk.iap.PurchaseData

@SuppressLint("NotifyDataSetChanged")
@BindingAdapter(value = ["products", "listener", "purchase_product"], requireAll = false)
fun RecyclerView.setAdapter(items: List<ProductDetail>?, listener: BaseRecyclerViewAdapter.OnItemClickListener, purchase: List<PurchaseData>?) {
    var adapter: ProductAdapter? = this.adapter as? ProductAdapter

    if (adapter == null) {
        adapter = ProductAdapter(items?.toMutableList())
        this.adapter = adapter
    }

    if (this.itemDecorationCount == 0) {
        addItemDecoration(LinearSpacingDecorator(this.context, resources.getDimensionPixelSize(R.dimen.global_layout_margin_5), LinearLayoutManager.VERTICAL))
    }

    adapter.items = items?.toMutableList()
    adapter.purchaseItem = purchase?.toMutableList()
    adapter.listener = listener
    adapter.notifyDataSetChanged()
}