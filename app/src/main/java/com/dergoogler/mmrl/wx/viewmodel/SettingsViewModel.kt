package com.dergoogler.mmrl.wx.viewmodel

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.ViewModel
import com.dergoogler.mmrl.datastore.repository.UserPreferencesRepository
import androidx.lifecycle.viewModelScope
import com.dergoogler.mmrl.datastore.model.DarkMode
import com.dergoogler.mmrl.datastore.model.Homepage
import com.dergoogler.mmrl.datastore.model.WorkingMode
import com.dergoogler.mmrl.platform.Platform
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

val LocalSettings = staticCompositionLocalOf<SettingsViewModel> {
    error("CompositionLocal SettingsViewModel not present")
}

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {
    val version
        get() = Platform.get("") {
            with(moduleManager) { "$version (${versionCode})" }
        }

    val versionCode
        get() = Platform.get(-1) {
            with(moduleManager) { versionCode }
        }

    val isSuEnabled = Platform.get(true) {
        with(moduleManager) { isSuEnabled }
    }

    fun setSuEnabled(value: Boolean) = Platform.get(true) {
        with(moduleManager) { setSuEnabled(value) }
    }

    fun setWorkingMode(value: WorkingMode) {
        viewModelScope.launch {
            userPreferencesRepository.setWorkingMode(value)
        }
    }

    fun setDarkTheme(value: DarkMode) {
        viewModelScope.launch {
            userPreferencesRepository.setDarkTheme(value)
        }
    }

    fun setThemeColor(value: Int) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeColor(value)
        }
    }

    fun setDeleteZipFile(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setDeleteZipFile(value)
        }
    }

    fun setUseDoh(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setUseDoh(value)
        }
    }

    fun setDownloadPath(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setDownloadPath(value)
        }
    }

    fun setConfirmReboot(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setConfirmReboot(value)
        }
    }

    fun setTerminalTextWrap(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setTerminalTextWrap(value)
        }
    }

    fun setDatePattern(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setDatePattern(value)
        }
    }

    fun setAutoUpdateRepos(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setAutoUpdateRepos(value)
        }
    }

    fun setAutoUpdateReposInterval(value: Long) {
        viewModelScope.launch {
            userPreferencesRepository.setAutoUpdateReposInterval(value)
        }
    }

    fun setCheckModuleUpdates(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setCheckModuleUpdates(value)
        }
    }

    fun setCheckModuleUpdatesInterval(value: Long) {
        viewModelScope.launch {
            userPreferencesRepository.setCheckModuleUpdatesInterval(value)
        }
    }

    fun setCheckAppUpdates(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setCheckAppUpdates(value)
        }
    }

    fun setCheckAppUpdatesPreReleases(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setCheckAppUpdatesPreReleases(value)
        }
    }

    fun setHideFingerprintInHome(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setHideFingerprintInHome(value)
        }
    }

    fun setStrictMode(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setStrictMode(value)
        }
    }

    fun setHomepage(value: Homepage) {
        viewModelScope.launch {
            userPreferencesRepository.setHomepage(value)
        }
    }

    fun setWebUiDevUrl(value: String) {
        viewModelScope.launch {
            userPreferencesRepository.setWebUiDevUrl(value)
        }
    }

    fun setDeveloperMode(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setDeveloperMode(value)
        }
    }

    fun setUseWebUiDevUrl(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setUseWebUiDevUrl(value)
        }
    }

    fun setUseShellForModuleStateChange(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setUseShellForModuleStateChange(value)
        }
    }

    fun setUseShellForModuleAction(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setUseShellForModuleAction(value)
        }
    }

    fun setClearInstallTerminal(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setClearInstallTerminal(value)
        }
    }

    fun setAllowCancelInstall(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setAllowCancelInstall(value)
        }
    }

    fun setAllowCancelAction(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setAllowCancelAction(value)
        }
    }

    fun setBlacklistAlerts(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setBlacklistAlerts(value)
        }
    }

    fun setInjectEruda(value: List<String>) {
        viewModelScope.launch {
            userPreferencesRepository.setInjectEruda(value)
        }
    }

    fun setAllowedFsModules(value: List<String>) {
        viewModelScope.launch {
            userPreferencesRepository.setAllowedFsModules(value)
        }
    }

    fun setAllowedKsuModules(value: List<String>) {
        viewModelScope.launch {
            userPreferencesRepository.setAllowedKsuModules(value)
        }
    }

    fun setEnableEruda(value: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setEnableEruda(value)
        }
    }
}