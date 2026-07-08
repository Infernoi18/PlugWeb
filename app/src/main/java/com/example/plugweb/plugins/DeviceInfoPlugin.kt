package com.example.plugweb.plugins

import android.content.Context
import android.os.Build
import android.util.Log
import org.json.JSONObject

class DeviceInfoPlugin(private val context: Context) {

    companion object {
        private const val TAG = "DeviceInfoPlugin"
    }

    fun getDeviceInfo(): JSONObject {
        return try {
            val data = JSONObject().apply {
                put("manufacturer", Build.MANUFACTURER)
                put("model", Build.MODEL)
                put("device", Build.DEVICE)
                put("brand", Build.BRAND)
                put("osVersion", Build.VERSION.RELEASE)
                put("sdkInt", Build.VERSION.SDK_INT)
                put("hardware", Build.HARDWARE)
            }

            Log.d(TAG, "Device info fetched: $data")

            JSONObject().apply {
                put("success", true)
                put("plugin", "PLGN_DEV_DETAILS")
                put("data", data)
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error fetching device info: ${e.message}")
            JSONObject().apply {
                put("success", false)
                put("plugin", "PLGN_DEV_DETAILS")
                put("error", e.message ?: "Unable to fetch device info")
            }
        }
    }
}