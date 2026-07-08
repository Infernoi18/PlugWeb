package com.example.plugweb.plugins

import android.content.Context
import androidx.biometric.BiometricManager
import com.example.plugweb.PluginConstants
import org.json.JSONObject

class BiometricPlugin(private val context: Context) {

    fun checkBiometricStatus(): JSONObject {
        val biometricManager = BiometricManager.from(context)
        
        val (status, message) = when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> 
                true to "Biometric features are available and can be used."
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> 
                false to "No biometric features available on this device."
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> 
                false to "Biometric features are currently unavailable."
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> 
                false to "The user has not enrolled any biometrics."
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED ->
                false to "A security update is required before using biometrics."
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED ->
                false to "Biometrics are unsupported on this version."
            else -> 
                false to "Unknown biometric error."
        }

        return JSONObject().apply {
            put("success", true) // The plugin call itself succeeded
            put("plugin", PluginConstants.PLGN_CHECK_BIOMETRIC)
            put("data", JSONObject().apply {
                put("isAvailable", status)
                put("message", message)
            })
        }
    }
}