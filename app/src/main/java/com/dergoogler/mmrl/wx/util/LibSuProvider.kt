package com.dergoogler.mmrl.wx.util

import android.content.Context
import android.content.ServiceConnection
import com.dergoogler.mmrl.platform.Platform
import com.dergoogler.mmrl.platform.model.IProvider
import com.dergoogler.mmrl.platform.model.PlatformIntent
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class LibSuProvider(
    private val context: Context,
    private val platform: Platform,
) : IProvider {
    override val name = "LibSu"

    override fun isAvailable() = true

    override suspend fun isAuthorized() = suspendCancellableCoroutine { continuation ->
        Shell.EXECUTOR.execute {
            runCatching {
                Shell.getShell()
            }.onSuccess {
                continuation.resume(true)
            }.onFailure {
                continuation.resume(false)
            }
        }
    }

    private val serviceIntent
        get() = PlatformIntent(
            context,
            platform,
            SuService::class.java
        )

    override fun bind(connection: ServiceConnection) {
        RootService.bind(serviceIntent.intent, connection)
    }

    override fun unbind(connection: ServiceConnection) {
        RootService.stop(serviceIntent.intent)
    }
}

suspend fun Context.initPlatform(platform: Platform) = Platform.init {
    this.context = this@initPlatform
    this.platform = platform
    this.provider = from(LibSuProvider(this@initPlatform, platform))
}