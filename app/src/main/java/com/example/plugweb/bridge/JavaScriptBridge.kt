package com.example.plugweb.bridge

import android.content.Context
import android.webkit.JavascriptInterface
import android.webkit.WebView
import android.os.Handler
import android.os.Looper
import android.util.Log
import org.json.JSONObject

class JavaScriptBridge(
    private val context: Context,
    private val webView: WebView
) {

    private val mainHandler = Handler(Looper.getMainLooper())
    private val pluginManager = PluginManager(context)

    companion object {
        private const val TAG = "JavaScriptBridge"
    }

    @JavascriptInterface
    fun callPlugin(pluginName: String, paramsJson: String?) {
        Log.d(TAG, "callPlugin invoked -> plugin: $pluginName, params: $paramsJson")

        Thread {
            val resultJson: JSONObject = try {
                pluginManager.execute(pluginName, paramsJson)
            } catch (e: Exception) {
                Log.d(TAG, "Error executing plugin $pluginName: ${e.message}")
                JSONObject().apply {
                    put("success", false)
                    put("plugin", pluginName)
                    put("error", e.message ?: "Unknown error")
                }
            }
            postResultToWebView(pluginName, resultJson)
        }.start()
    }

    private fun postResultToWebView(pluginName: String, result: JSONObject) {
        mainHandler.post {
            val js = "window.onPluginResult('$pluginName', ${result});"
            Log.d(TAG, "Posting result to WebView: $js")
            webView.evaluateJavascript(js, null)
        }
    }
}