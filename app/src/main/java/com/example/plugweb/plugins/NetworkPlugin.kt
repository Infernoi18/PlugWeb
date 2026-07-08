package com.example.plugweb.plugins

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import org.json.JSONObject

class NetworkPlugin(private val context: Context) {

    companion object {
        private const val TAG = "NetworkPlugin"
    }

    fun check(): JSONObject {
        return try {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)

            val isConnected = capabilities != null && (
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            )

            val type = when {
                capabilities == null -> "none"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> "wifi"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> "cellular"
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> "ethernet"
                else -> "unknown"
            }

            Log.d(TAG, "Network connected: $isConnected, type: $type")

            JSONObject().apply {
                put("success", true)
                put("plugin", "PLGN_IS_NETWORK_AVAILABLE")
                put("data", JSONObject().apply {
                    put("isConnected", isConnected)
                    put("type", type)
                })
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error checking network: ${e.message}")
            JSONObject().apply {
                put("success", false)
                put("plugin", "PLGN_IS_NETWORK_AVAILABLE")
                put("error", e.message ?: "Unable to check network")
            }
        }
    }
}