package com.dergoogler.mmrl.wx.ui.screens.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.dergoogler.mmrl.datastore.providable.LocalUserPreferences
import com.dergoogler.mmrl.ext.isLocalWifiUrl
import com.dergoogler.mmrl.ext.navigateSingleTopTo
import com.dergoogler.mmrl.ext.none
import com.dergoogler.mmrl.ext.nullable
import com.dergoogler.mmrl.ext.takeTrue
import com.dergoogler.mmrl.ui.component.TopAppBar
import com.dergoogler.mmrl.ui.component.TopAppBarTitle
import com.dergoogler.mmrl.ui.component.listItem.ListButtonItem
import com.dergoogler.mmrl.ui.component.listItem.ListEditTextSwitchItem
import com.dergoogler.mmrl.ui.component.listItem.ListHeader
import com.dergoogler.mmrl.ui.component.listItem.ListRadioCheckItem
import com.dergoogler.mmrl.ui.component.listItem.ListSwitchItem
import com.dergoogler.mmrl.ui.providable.LocalNavController
import com.dergoogler.mmrl.wx.R
import com.dergoogler.mmrl.wx.model.FeaturedManager
import com.dergoogler.mmrl.wx.model.managers
import com.dergoogler.mmrl.wx.ui.navigation.graphs.SettingsScreen
import com.dergoogler.mmrl.wx.util.toWorkingMode
import com.dergoogler.mmrl.wx.viewmodel.LocalSettings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen() {
    val userPreferences = LocalUserPreferences.current
    val viewModel = LocalSettings.current
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val navController = LocalNavController.current

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    TopAppBarTitle(text = stringResource(id = R.string.settings))
                },
                scrollBehavior = scrollBehavior
            )
        },
        contentWindowInsets = WindowInsets.none
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            ListButtonItem(
                icon = R.drawable.color_swatch,
                title = stringResource(id = R.string.settings_app_theme),
                desc = stringResource(id = R.string.settings_app_theme_desc),
                onClick = {
                    navController.navigateSingleTopTo(SettingsScreen.AppTheme.route)
                }
            )

            val manager: FeaturedManager? =
                managers.find { userPreferences.workingMode.toPlatform() == it.platform }

            manager.nullable { mng ->
                ListRadioCheckItem(
                    icon = mng.icon,
                    title = stringResource(id = R.string.platform),
                    desc = stringResource(mng.name),
                    options = managers.map { it.toRadioOption() },
                    onConfirm = {
                        viewModel.setWorkingMode(it.value.toWorkingMode())
                    },
                    value = mng.platform
                )
            }

            ListHeader(title = stringResource(id = R.string.webui))

            ListSwitchItem(
                title = stringResource(id = R.string.settings_developer_mode),
                desc = stringResource(id = R.string.settings_developer_mode_desc),
                checked = userPreferences.developerMode,
                onChange = viewModel::setDeveloperMode,
            )

            var webuiRemoteUrlInfo by remember { mutableStateOf(false) }
            if (webuiRemoteUrlInfo) AlertDialog(
                title = {
                    Text(text = stringResource(id = R.string.settings_webui_remote_url))
                },
                text = {
                    Text(text = stringResource(id = R.string.settings_webui_remote_url_alert_desc))
                },
                onDismissRequest = {
                    webuiRemoteUrlInfo = false
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            webuiRemoteUrlInfo = false
                        }
                    ) {
                        Text(text = stringResource(id = android.R.string.ok))
                    }
                },
            )

            ListEditTextSwitchItem(
                enabled = userPreferences.developerMode,
                title = stringResource(id = R.string.settings_webui_remote_url),
                desc = stringResource(id = R.string.settings_webui_remote_url_desc),
                value = userPreferences.webUiDevUrl,
                checked = userPreferences.useWebUiDevUrl,
                onChange = viewModel::setUseWebUiDevUrl,
                onConfirm = {
                    viewModel.setWebUiDevUrl(it)
                },
                onValid = { !it.isLocalWifiUrl() },
                base = {
                    learnMore = {
                        webuiRemoteUrlInfo = true
                    }
                },
                dialog = {
                    supportingText = { isError ->
                        isError.takeTrue {
                            Text(
                                text = stringResource(R.string.invalid_ip),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                },
            )

            ListSwitchItem(
                enabled = userPreferences.developerMode && !userPreferences.useWebUiDevUrl,
                title = stringResource(R.string.settings_security_inject_eruda),
                desc = stringResource(R.string.settings_security_inject_eruda_desc),
                checked = userPreferences.enableErudaConsole && !userPreferences.useWebUiDevUrl,
                onChange = viewModel::setEnableEruda
            )
        }
    }
}