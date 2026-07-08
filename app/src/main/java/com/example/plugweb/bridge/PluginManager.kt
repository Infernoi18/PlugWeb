package com.example.plugweb.bridge

import android.content.Context
import android.util.Log
import com.example.plugweb.MainActivity
import com.example.plugweb.PluginConstants
import com.example.plugweb.plugins.*
import org.json.JSONObject

class PluginManager(private val context: Context) {

    companion object {
        private const val TAG = "PluginManager"
    }

    fun execute(pluginName: String, paramsJson: String?): JSONObject {
        Log.d(TAG, "Executing plugin: $pluginName")

        val params: JSONObject = try {
            if (paramsJson.isNullOrBlank()) JSONObject() else JSONObject(paramsJson)
        } catch (e: Exception) {
            JSONObject()
        }

        return when (pluginName) {
            PluginConstants.PLGN_IS_NETWORK_AVAILABLE ->
                NetworkPlugin(context).check()

            PluginConstants.PLGN_DEV_DETAILS ->
                DeviceInfoPlugin(context).getDeviceInfo()

            PluginConstants.PLGN_GET_IP ->
                IpAddressPlugin().getIpAddress()

            PluginConstants.PLGN_APP_VERSION ->
                AppVersionPlugin(context).getAppVersion()

            PluginConstants.PLGN_OPEN_CAMERA -> {
                (context as? MainActivity)?.openCamera()
                JSONObject().apply {
                    put("success", true)
                    put("plugin", PluginConstants.PLGN_OPEN_CAMERA)
                    put("data", "Camera intent launched")
                }
            }

            PluginConstants.PLGN_CRNT_LOCALE ->
                LocalePlugin(context).getLocale()

            PluginConstants.PLGN_APP_SIZE_INFO ->
                AppSizePlugin(context).getAppSize()

            PluginConstants.PLGN_CHECK_GPS_STATUS ->
                GpsStatusPlugin(context).checkGpsStatus()

            PluginConstants.PLGN_CHECK_ACCESSIBILITY_SETTINGS ->
                AccessibilityStatusPlugin(context).checkAccessibilityStatus()

            PluginConstants.PLGN_CHECK_BIOMETRIC ->
                BiometricPlugin(context).checkBiometricStatus()

            else -> JSONObject().apply {
                put("success", false)
                put("plugin", pluginName)
                put("error", "Unknown plugin: $pluginName")
            }
        }
    }
}