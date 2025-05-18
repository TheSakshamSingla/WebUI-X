package com.dergoogler.mmrl.wx.ui.navigation.graphs

import com.dergoogler.mmrl.wx.ui.navigation.MainScreen
import com.dergoogler.mmrl.wx.ui.screens.settings.SettingsScreen
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.dergoogler.mmrl.wx.ui.screens.settings.appTheme.AppThemeScreen

enum class SettingsScreen(val route: String) {
    Home("Settings"),
    AppTheme("AppTheme"),
}

fun NavGraphBuilder.settingsScreen() = navigation(
    startDestination = SettingsScreen.Home.route,
    route = MainScreen.Settings.route
) {
    composable(
        route = SettingsScreen.Home.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        SettingsScreen()
    }

    composable(
        route = SettingsScreen.AppTheme.route,
        enterTransition = { fadeIn() },
        exitTransition = { fadeOut() }
    ) {
        AppThemeScreen()
    }
}