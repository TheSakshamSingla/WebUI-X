package com.dergoogler.mmrl.wx.util

import androidx.compose.runtime.Composable
import com.dergoogler.mmrl.datastore.model.WorkingMode
import com.dergoogler.mmrl.datastore.providable.LocalUserPreferences
import com.dergoogler.mmrl.ext.exception.BrickException
import com.dergoogler.mmrl.ext.toFormattedDateSafely
import com.dergoogler.mmrl.platform.Platform
import com.dergoogler.mmrl.platform.content.LocalModule
import com.topjohnwu.superuser.Shell

val LocalModule.versionDisplay
    get(): String {
        val included = "\\(.*?${versionCode}.*?\\)".toRegex()
            .containsMatchIn(version)

        return if (included) {
            version
        } else {
            "$version (${versionCode})"
        }
    }

val Float.toFormattedDateSafely: String
    @Composable
    get() {
        val prefs = LocalUserPreferences.current
        return this.toFormattedDateSafely(prefs.datePattern)
    }

val Long.toFormattedDateSafely: String
    @Composable
    get() {
        val prefs = LocalUserPreferences.current
        return this.toFormattedDateSafely(prefs.datePattern)
    }

fun Platform.toWorkingMode() = when (this) {
    Platform.Magisk -> WorkingMode.MODE_MAGISK
    Platform.KernelSU -> WorkingMode.MODE_KERNEL_SU
    Platform.KsuNext -> WorkingMode.MODE_KERNEL_SU_NEXT
    Platform.APatch -> WorkingMode.MODE_APATCH
    else -> throw BrickException("Unsupported Platform")
}


inline fun <T> withNewRootShell(
    globalMnt: Boolean = false,
    debug: Boolean = false,
    commands: Array<String> = arrayOf("su"),
    block: Shell.() -> T,
): T {
    return createRootShell(globalMnt, debug, commands).use(block)
}

fun createRootShell(
    globalMnt: Boolean = false,
    debug: Boolean = false,
    commands: Array<String> = arrayOf("su"),
): Shell {
    Shell.enableVerboseLogging = debug
    val builder = Shell.Builder.create()
    if (globalMnt) {
        builder.setFlags(Shell.FLAG_MOUNT_MASTER)
    }
    return builder.build(*commands)
}