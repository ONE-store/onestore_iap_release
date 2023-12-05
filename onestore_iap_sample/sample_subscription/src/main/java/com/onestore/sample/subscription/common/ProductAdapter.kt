package com.onestore.sample.subscription.common

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gaa.sdk.iap.ProductDetail
import com.gaa.sdk.iap.PurchaseData
import com.onestore.sample.subscription.base.BaseRecyclerViewAdapter
import com.onestore.sample.subscription.databinding.ItemProductBinding

class ProductAdapter(
    var items: MutableList<ProductDetail>?
) : BaseRecyclerViewAdapter<ProductAdapter.ProductViewHolder>() {

    var purchaseItem: MutableList<PurchaseData>? = null

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(pos: Int, item: ProductDetail) {
            binding.title = item.title
            binding.callback = View.OnClickListener {
                listener.onItemClick(pos, item)
            }

            if (purchaseItem?.find { it.productId == item.productId } != null) {
                binding.llBack.isEnabled = false
                binding.tvTitle.isEnabled = false
            } else {
                binding.llBack.isEnabled = true
                binding.tvTitle.isEnabled = true
            }

            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ProductViewHolder(
            ItemProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        items?.get(position)?.let { (holder as ProductViewHolder).bind(position, it) }
    }

    override fun getItemCount(): Int = items?.size ?: 0
}