package com.gaa.appdev.subscription.sample.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.gaa.appdev.subscription.sample.R
import com.gaa.appdev.subscription.sample.common.Constant
import com.gaa.appdev.subscription.sample.common.ViewModelFactory
import com.gaa.appdev.subscription.sample.databinding.ActivitySettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsActivity : AppCompatActivity() {
    private val TAG = "SettingsActivity"
    private lateinit var binding: ActivitySettingsBinding
    private val viewModel: SettingsViewModel by lazy { ViewModelProvider(this, ViewModelFactory())[SettingsViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)
        binding.view = this@SettingsActivity
        binding.vm = viewModel
    }

    fun showOptionDialog(view: View) {
        val options = resources.getStringArray(R.array.option_items)
        var selected = -1
        // 선택된 비례배분 옵션은 메인화면에서 상품을 구매시 적용됩니다.
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.option_title)
            .setNeutralButton(android.R.string.cancel, null)
            .setPositiveButton(android.R.string.ok) { _, _ ->
                saveOption(selected + 1)
            }
            .setSingleChoiceItems(options, selected) {
                    _, which ->
                Log.e(TAG, "select option ===> " + options[which])
                selected = which
            }
            .show()
    }

    private fun saveOption(option: Int) {
        val editor = getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE).edit()
        editor.putInt(Constant.KEY_OPTION, option)
        editor.apply()
    }
}