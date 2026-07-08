package com.example.plugweb.plugins

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import org.json.JSONObject

class AppVersionPlugin(private val context: Context) {

    companion object {
        private const val TAG = "AppVersionPlugin"
    }

    fun getAppVersion(): JSONObject {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.packageManager.getPackageInfo(
                    context.packageName,
                    PackageManager.PackageInfoFlags.of(0)
                )
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, 0)
            }

            val versionName = packageInfo.versionName
            val versionCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }

            Log.d(TAG, "App version fetched: $versionName ($versionCode)")

            JSONObject().apply {
                put("success", true)
                put("plugin", "PLGN_APP_VERSION")
                put("data", JSONObject().apply {
                    put("versionName", versionName)
                    put("versionCode", versionCode)
                })
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error fetching app version: ${e.message}")
            JSONObject().apply {
                put("success", false)
                put("plugin", "PLGN_APP_VERSION")
                put("error", e.message ?: "Unable to fetch app version")
            }
        }
    }
}