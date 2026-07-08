package com.example.plugweb.plugins

import android.content.Context
import androidx.biometric.BiometricManager
import com.example.plugweb.MainActivity
import com.example.plugweb.PluginConstants
import org.json.JSONObject

class BiometricPlugin(private val context: Context) {

    fun checkBiometricStatus(): JSONObject {
        val biometricManager = BiometricManager.from(context)
        
        // Check for strong biometrics (fingerprint/face)
        val fingerAvailable = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
        // Check for device credentials (PIN/Pattern/Password)
        val passwordAvailable = biometricManager.canAuthenticate(BiometricManager.Authenticators.DEVICE_CREDENTIAL) == BiometricManager.BIOMETRIC_SUCCESS

        val data = JSONObject().apply {
            put("fingerprint_availability", if (fingerAvailable) "Yes" else "No")
            put("password_availability", if (passwordAvailable) "Yes" else "No")
        }

        val canAuth = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)

        if (canAuth == BiometricManager.BIOMETRIC_SUCCESS) {
            (context as? MainActivity)?.authenticateBiometric()
            return JSONObject().apply {
                put("success", true)
                put("plugin", PluginConstants.PLGN_CHECK_BIOMETRIC)
                put("data", data.apply { 
                    put("status_of_verification", "Initiated")
                })
            }
        } else {
            val message = when (canAuth) {
                BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> "No biometric features available."
                BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> "Biometric features are currently unavailable."
                BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> "No biometrics enrolled. Please set up fingerprint or PIN."
                else -> "Biometrics unavailable (Error: $canAuth)"
            }
            return JSONObject().apply {
                put("success", false)
                put("plugin", PluginConstants.PLGN_CHECK_BIOMETRIC)
                put("error", message)
                put("data", data.apply { 
                    put("status_of_verification", "Unavailable")
                    put("answer", "Verification failed: $message")
                })
            }
        }
    }
}