package com.onestore.sample.compose.ui.feature.license

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.onestore.sample.compose.model.AppLicenseState
import com.onestore.sample.compose.ui.feature.license.component.AppLicenseCheckModeButtons
import com.onestore.sample.compose.ui.feature.license.component.AppLicensePublicKeyCard
import com.onestore.sample.compose.ui.feature.license.component.AppLicenseResultCard
import com.onestore.sample.compose.ui.feature.license.model.AppLicenseCheckMode
import com.onestore.sample.compose.ui.feature.license.model.AppLicenseDetailLine
import com.onestore.sample.compose.ui.feature.license.model.AppLicenseResultData
import com.onestore.sample.compose.ui.feature.license.model.AppLicenseStatus
import com.onestore.sample.compose.ui.utils.LocalViewModelFactory
import com.onestore.sample.compose.R

/**
 * 앱 라이선스 검증 기능을 테스트하고 결과를 확인하는 화면입니다.
 *
 * @param viewModel 앱 라이선스 검증 관련 로직을 처리하는 ViewModel
 */
@Composable
fun AppLicenseCheckerScreen(
    viewModel: AppLicenseCheckerViewModel = viewModel(factory = LocalViewModelFactory.current),
) {
    val context = LocalContext.current
    val activity = context as Activity
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val appLicenseState by viewModel.appLicenseState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppLicensePublicKeyCard()

        AppLicenseCheckModeButtons(
            selectedMode = uiState.selectedMode,
            onPolicyClick = {
                viewModel.setSelectedMode(AppLicenseCheckMode.POLICY)
                viewModel.queryLicense(activity)
            },
            onStrictClick = {
                viewModel.setSelectedMode(AppLicenseCheckMode.STRICT)
                viewModel.strictQueryLicense(activity)
            }
        )

        when (val state = appLicenseState) {
            is AppLicenseState.Granted -> {
                AppLicenseResultCard(
                    data = AppLicenseResultData(
                        title = stringResource(R.string.app_license_result_granted),
                        firstLine = AppLicenseDetailLine(
                            label = stringResource(R.string.app_license_label_license),
                            value = state.license ?: ""
                        ),
                        secondLine = AppLicenseDetailLine(
                            label = stringResource(R.string.app_license_label_signature),
                            value = state.signature ?: ""
                        ),
                        status = AppLicenseStatus.SUCCESS
                    )
                )
            }
            is AppLicenseState.Denied -> {
                AppLicenseResultCard(
                    data = AppLicenseResultData(
                        title = stringResource(R.string.app_license_result_denied),
                        firstLine = AppLicenseDetailLine(
                            label = stringResource(R.string.app_license_label_code),
                            value = "-1"
                        ),
                        secondLine = AppLicenseDetailLine(
                            label = stringResource(R.string.app_license_label_message),
                            value = "null"
                        ),
                        status = AppLicenseStatus.ERROR
                    )
                )
            }
            is AppLicenseState.Error -> {
                AppLicenseResultCard(
                    data = AppLicenseResultData(
                        title = stringResource(R.string.app_license_result_error),
                        firstLine = AppLicenseDetailLine(
                            label = stringResource(R.string.app_license_label_code),
                            value = state.code
                        ),
                        secondLine = AppLicenseDetailLine(
                            label = stringResource(R.string.app_license_label_message),
                            value = state.message ?: ""
                        ),
                        status = AppLicenseStatus.ERROR
                    )
                )
            }
            AppLicenseState.None -> {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.app_license_msg_check_license),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(80.dp))
    }
}
