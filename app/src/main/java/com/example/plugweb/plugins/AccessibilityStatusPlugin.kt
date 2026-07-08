package com.example.plugweb.plugins

import android.content.Context
import android.util.Log
import android.view.accessibility.AccessibilityManager
import org.json.JSONObject

class AccessibilityStatusPlugin(private val context: Context) {

    companion object {
        private const val TAG = "AccessibilityStatusPlugin"
    }

    fun checkAccessibilityStatus(): JSONObject {
        return try {
            val accessibilityManager =
                context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager

            val isEnabled = accessibilityManager.isEnabled
            val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(
                android.accessibilityservice.AccessibilityServiceInfo.FEEDBACK_ALL_MASK
            )

            val data = JSONObject().apply {
                put("accessibilityEnabled", isEnabled)
                put("enabledServiceCount", enabledServices.size)
            }

            Log.d(TAG, "Accessibility status fetched: $data")

            JSONObject().apply {
                put("success", true)
                put("plugin", "PLGN_CHECK_ACCESSIBILITY_SETTINGS")
                put("data", data)
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error checking accessibility status: ${e.message}")
            JSONObject().apply {
                put("success", false)
                put("plugin", "PLGN_CHECK_ACCESSIBILITY_SETTINGS")
                put("error", e.message ?: "Unable to check accessibility status")
            }
        }
    }
}