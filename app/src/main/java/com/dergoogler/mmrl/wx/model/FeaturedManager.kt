package com.dergoogler.mmrl.wx.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.dergoogler.mmrl.platform.Platform
import com.dergoogler.mmrl.ui.component.dialog.RadioOptionItem
import com.dergoogler.mmrl.wx.R

data class FeaturedManager(
    @StringRes val name: Int,
    @DrawableRes val icon: Int,
    val platform: Platform
) {
    @Composable
    fun toRadioOption() = RadioOptionItem(
        title = stringResource(name),
        value = platform
    )
}

val managers = listOf(
    FeaturedManager(
        name = R.string.magisk,
        icon = com.dergoogler.mmrl.ui.R.drawable.magisk_logo,
        platform = Platform.Magisk,
    ),

    FeaturedManager(
        name = R.string.kernelsu,
        icon = com.dergoogler.mmrl.ui.R.drawable.kernelsu_logo,
        platform = Platform.KernelSU,
    ),

    FeaturedManager(
        name = R.string.kernelsu_next,
        icon = com.dergoogler.mmrl.ui.R.drawable.kernelsu_next_logo,
        platform = Platform.KsuNext,
    ),

    FeaturedManager(
        name = R.string.apatch,
        icon = com.dergoogler.mmrl.ui.R.drawable.brand_android,
        platform = Platform.APatch
    ),
)