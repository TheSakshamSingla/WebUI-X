package com.dergoogler.mmrl.webuix.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.dergoogler.mmrl.webuix.R

enum class MainScreen(
    val route: String,
    @StringRes val label: Int,
    @DrawableRes val icon: Int,
    @DrawableRes val iconFilled: Int,
) {
    Modules(
        route = "ModulesScreen",
        label = R.string.modules,
        icon = R.drawable.keyframes,
        iconFilled = R.drawable.keyframes_filled
    ),

    Settings(
        route = "SettingsScreen",
        label = R.string.settings,
        icon = R.drawable.settings,
        iconFilled = R.drawable.settings_filled
    )
}