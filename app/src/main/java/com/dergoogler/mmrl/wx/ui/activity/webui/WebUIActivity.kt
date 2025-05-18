package com.dergoogler.mmrl.wx.ui.activity.webui

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import com.dergoogler.mmrl.ext.managerVersion
import com.dergoogler.mmrl.platform.Platform
import com.dergoogler.mmrl.platform.model.ModId
import com.dergoogler.mmrl.ui.component.Failed
import com.dergoogler.mmrl.ui.component.Loading
import com.dergoogler.mmrl.webui.interfaces.WXOptions
import com.dergoogler.mmrl.webui.screen.WebUIScreen
import com.dergoogler.mmrl.webui.util.rememberWebUIOptions
import com.dergoogler.mmrl.webui.webUiConfig
import com.dergoogler.mmrl.wx.R
import com.dergoogler.mmrl.wx.ui.activity.webui.interfaces.KernelSUInterface
import com.dergoogler.mmrl.wx.util.BaseActivity
import com.dergoogler.mmrl.wx.util.initPlatform
import com.dergoogler.mmrl.wx.util.setBaseContent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

@AndroidEntryPoint
class WebUIActivity : BaseActivity() {
    private val userPrefs get() = runBlocking { userPreferencesRepository.data.first() }
    private lateinit var webView: WebView
    private var isKeyboardShowing by mutableStateOf(false)
    private lateinit var rootView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        webView = WebView(this)
        rootView = findViewById(android.R.id.content)

        lifecycleScope.launch {
            initPlatform(userPrefs.workingMode.toPlatform())
        }

        val mModId = intent.getStringExtra("MOD_ID")

        if (mModId.isNullOrEmpty()) {
            setBaseContent {
                Failed(
                    message = stringResource(id = R.string.missing_mod_id),
                )
            }

            return
        }

        val modId = ModId(mModId)

        setBaseContent {
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(Platform.isAlive) {
                while (!Platform.isAlive) {
                    delay(1000)
                }

                isLoading = false
            }

            if (isLoading) {
                Loading()

                return@setBaseContent
            }

            val config = webUiConfig(modId)

            if (config.windowResize) {
                rootView.getViewTreeObserver().addOnGlobalLayoutListener {
                    val r = Rect()
                    rootView.getWindowVisibleDisplayFrame(r)
                    val screenHeight: Int = rootView.getRootView().height
                    val keypadHeight: Int = screenHeight - r.bottom
                    if (keypadHeight > screenHeight * 0.15) {
                        if (!isKeyboardShowing) {
                            isKeyboardShowing = true
                            adjustWebViewHeight(keypadHeight)
                        }
                    } else {
                        if (isKeyboardShowing) {
                            isKeyboardShowing = false
                            resetWebViewHeight()
                        }
                    }
                }
            }
            val isDarkMode = userPrefs.isDarkMode()


            val options = rememberWebUIOptions(
                modId = modId,
                debug = userPrefs.developerMode,
                appVersionCode = baseContext.managerVersion.second.toInt(),
                remoteDebug = userPrefs.useWebUiDevUrl,
                enableEruda = userPrefs.enableErudaConsole,
                debugDomain = userPrefs.webUiDevUrl,
                isDarkMode = isDarkMode,
                cls = this::class.java
            )

            WebUIScreen(
                webView = webView,
                options = options,
                interfaces = listOf(
                    KernelSUInterface.factory(
                        WXOptions(
                            context = this@WebUIActivity,
                            webView = webView,
                            modId = modId
                        ),
                        userPrefs.developerMode
                    )
                )
            )
        }
    }

    private fun adjustWebViewHeight(keypadHeight: Int) {
        val params = webView.layoutParams
        params.height = rootView.height - keypadHeight
        webView.layoutParams = params
    }

    private fun resetWebViewHeight() {
        val params = webView.layoutParams
        params.height = LinearLayout.LayoutParams.MATCH_PARENT
        webView.layoutParams = params
    }

    companion object {
        fun start(context: Context, modId: String) {
            val intent = Intent(context, WebUIActivity::class.java)
                .apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
                    putExtra("MOD_ID", modId)
                }

            context.startActivity(intent)
        }
    }
}
