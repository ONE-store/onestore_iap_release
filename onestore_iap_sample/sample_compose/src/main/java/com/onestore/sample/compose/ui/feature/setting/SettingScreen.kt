package com.onestore.sample.compose.ui.feature.setting

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.onestore.sample.compose.ui.component.AppGroupedCard
import com.onestore.sample.compose.ui.feature.setting.component.SettingsClickableItem
import com.onestore.sample.compose.ui.feature.setting.component.SettingsSwitchItem
import com.onestore.sample.compose.ui.utils.LocalViewModelFactory
import com.onestore.sample.compose.R

/**
 * 앱의 환경 설정 및 정보를 확인하는 화면입니다.
 *
 * @param viewModel 설정 화면의 상태를 관리하는 ViewModel
 */
@Composable
fun SettingScreen(
    viewModel: SettingViewModel = viewModel(factory = LocalViewModelFactory.current),
) {
    val context = LocalContext.current
    val activity = context as Activity
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                AppGroupedCard {
                    SettingsClickableItem(
                        headlineText = stringResource(R.string.settings_action_manage_subscription),
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                        },
                        onClick = { viewModel.launchManageSubscription(activity) }
                    )

                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

                    SettingsSwitchItem(
                        headlineText = stringResource(R.string.settings_log_enable),
                        supportingText = stringResource(R.string.settings_log_enable_desc),
                        leadingIcon = Icons.Filled.BugReport,
                        checked = uiState.logEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.updateLogEnable(enabled)
                        }
                    )
                }
            }
        }
    }
}
