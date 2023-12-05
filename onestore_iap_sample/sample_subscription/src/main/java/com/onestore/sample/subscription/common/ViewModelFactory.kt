package com.onestore.sample.subscription.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.onestore.sample.subscription.ui.MainViewModel
import com.onestore.sample.subscription.ui.SettingsViewModel

class ViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel() as T
        } else if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            return SettingsViewModel() as T
        }
        throw IllegalArgumentException("")
    }

}

