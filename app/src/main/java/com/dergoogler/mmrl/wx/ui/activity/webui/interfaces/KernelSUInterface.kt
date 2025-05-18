package com.dergoogler.mmrl.wx.ui.activity.webui.interfaces

import android.text.TextUtils
import android.view.Window
import android.webkit.JavascriptInterface
import android.widget.Toast
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.dergoogler.mmrl.webui.interfaces.WXInterface
import com.dergoogler.mmrl.webui.interfaces.WXOptions
import com.dergoogler.mmrl.webui.model.JavaScriptInterface
import com.dergoogler.mmrl.wx.util.createRootShell
import com.dergoogler.mmrl.wx.util.withNewRootShell
import com.topjohnwu.superuser.CallbackList
import com.topjohnwu.superuser.ShellUtils
import com.topjohnwu.superuser.internal.UiThreadHandler
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

class KernelSUInterface(
    wxOptions: WXOptions,
    private val debug: Boolean = false,
) : WXInterface(wxOptions) {
    override var name: String = "ksu"
    companion object {
        fun factory(wxOptions: WXOptions, debug: Boolean) = JavaScriptInterface(
            clazz = KernelSUInterface::class.java,
            initargs = arrayOf(
                wxOptions,
                debug
            ),
            parameterTypes = arrayOf(
                WXOptions::class.java,
                Boolean::class.java
            )
        )
    }

    @JavascriptInterface
    fun toast(msg: String) {
        runPost {
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
        }
    }

    @JavascriptInterface
    fun fullScreen(enable: Boolean) {
        runMainLooperPost {
            if (enable) {
                hideSystemUI(activity.window)
            } else {
                showSystemUI(activity.window)
            }
        }
    }

    @JavascriptInterface
    fun moduleInfo(): String {
        console.warn("ksu.moduleInfo() have been removed due to security reasons.")
        val currentModuleInfo = JSONObject()
        currentModuleInfo.put("moduleDir", null)
        currentModuleInfo.put("id", null)
        return currentModuleInfo.toString()
    }


    @JavascriptInterface
    fun exec(cmd: String): String {
        return withNewRootShell(
            globalMnt = true,
            debug = debug
        ) { ShellUtils.fastCmd(this, cmd) }
    }

    @JavascriptInterface
    fun exec(cmd: String, callbackFunc: String) {
        exec(cmd, null, callbackFunc)
    }

    private fun processOptions(sb: StringBuilder, options: String?) {
        val opts = if (options == null) JSONObject() else {
            JSONObject(options)
        }

        val cwd = opts.optString("cwd")
        if (!TextUtils.isEmpty(cwd)) {
            sb.append("cd ${cwd};")
        }

        opts.optJSONObject("env")?.let { env ->
            env.keys().forEach { key ->
                sb.append("export ${key}=${env.getString(key)};")
            }
        }
    }

    @JavascriptInterface
    fun exec(
        cmd: String,
        options: String?,
        callbackFunc: String,
    ) {
        val finalCommand = StringBuilder()
        processOptions(finalCommand, options)
        finalCommand.append(cmd)

        val result = withNewRootShell(
            globalMnt = true,
            debug = debug
        ) {
            newJob().add(finalCommand.toString()).to(ArrayList(), ArrayList()).exec()
        }
        val stdout = result.out.joinToString(separator = "\n")
        val stderr = result.err.joinToString(separator = "\n")

        val jsCode =
            "(function() { try { ${callbackFunc}(${result.code}, ${
                JSONObject.quote(
                    stdout
                )
            }, ${JSONObject.quote(stderr)}); } catch(e) { console.error(e); } })();"

        runJs(jsCode)
    }

    @JavascriptInterface
    fun spawn(command: String, args: String, options: String?, callbackFunc: String) {
        val finalCommand = StringBuilder()

        processOptions(finalCommand, options)

        if (!TextUtils.isEmpty(args)) {
            finalCommand.append(command).append(" ")
            JSONArray(args).let { argsArray ->
                for (i in 0 until argsArray.length()) {
                    finalCommand.append(argsArray.getString(i))
                    finalCommand.append(" ")
                }
            }
        } else {
            finalCommand.append(command)
        }

        val shell = createRootShell(
            globalMnt = true,
            debug = debug
        )

        val emitData = fun(name: String, data: String) {
            val jsCode =
                "(function() { try { ${callbackFunc}.${name}.emit('data', ${
                    JSONObject.quote(
                        data
                    )
                }); } catch(e) { console.error('emitData', e); } })();"

            runJs(jsCode)
        }

        val stdout = object : CallbackList<String>(UiThreadHandler::runAndWait) {
            override fun onAddElement(s: String) {
                emitData("stdout", s)
            }
        }

        val stderr = object : CallbackList<String>(UiThreadHandler::runAndWait) {
            override fun onAddElement(s: String) {
                emitData("stderr", s)
            }
        }

        val future = shell.newJob().add(finalCommand.toString()).to(stdout, stderr).enqueue()
        val completableFuture = CompletableFuture.supplyAsync {
            future.get()
        }

        completableFuture.thenAccept { result ->
            val emitExitCode =
                "(function() { try { ${callbackFunc}.emit('exit', ${result.code}); } catch(e) { console.error(`emitExit error: \${e}`); } })();"
            runJs(emitExitCode)


            if (result.code != 0) {
                val emitErrCode =
                    "(function() { try { var err = new Error(); err.exitCode = ${result.code}; err.message = ${
                        JSONObject.quote(
                            result.err.joinToString(
                                "\n"
                            )
                        )
                    };${callbackFunc}.emit('error', err); } catch(e) { console.error('emitErr', e); } })();"
                runJs(emitErrCode)
            }
        }.whenComplete { _, _ ->
            runJsCatching { shell.close() }
        }
    }
}

fun hideSystemUI(window: Window) =
    WindowInsetsControllerCompat(window, window.decorView).let { controller ->
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }

fun showSystemUI(window: Window) =
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.systemBars())