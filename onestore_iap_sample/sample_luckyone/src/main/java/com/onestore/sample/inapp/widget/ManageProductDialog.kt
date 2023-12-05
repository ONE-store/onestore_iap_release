package com.onestore.sample.inapp.widget

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView
import com.gaa.sdk.iap.PurchaseData
import com.onestore.sample.inapp.R

class ManageProductDialog(
    context: Context,
    private val subsPurchaseData: PurchaseData? = null,
    private val callback: UserCallback
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_manage_product)
        setCanceledOnTouchOutside(false)

        findViewById<ImageButton>(R.id.btn_close).setOnClickListener { dismiss() }

        findViewById<TextView>(R.id.btn_open_subs_menu).setOnClickListener {
            callback.openSubscriptionsMenu(null)
            this@ManageProductDialog.dismiss()
        }

        if (subsPurchaseData == null) {
            findViewById<ViewGroup>(R.id.subs_product_group).visibility = View.GONE
        } else {
            findViewById<ViewGroup>(R.id.subs_product_group).visibility = View.VISIBLE
            findViewById<TextView>(R.id.btn_open_subs_detail).setOnClickListener {
                callback.openSubscriptionsMenu(subsPurchaseData)
            }
        }
    }

    interface UserCallback {
        fun openSubscriptionsMenu(purchaseData: PurchaseData?)
    }
}