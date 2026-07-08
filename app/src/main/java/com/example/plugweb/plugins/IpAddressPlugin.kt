package com.example.plugweb.plugins

import android.util.Log
import org.json.JSONObject
import java.net.NetworkInterface
import java.util.Collections

class IpAddressPlugin {

    companion object {
        private const val TAG = "IpAddressPlugin"
    }

    fun getIpAddress(): JSONObject {
        return try {
            var ipAddress: String? = null

            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress && addr.hostAddress?.contains(":") == false) {
                        ipAddress = addr.hostAddress
                        break
                    }
                }
                if (ipAddress != null) break
            }

            Log.d(TAG, "IP address fetched: $ipAddress")

            JSONObject().apply {
                put("success", true)
                put("plugin", "PLGN_GET_IP")
                put("data", JSONObject().apply {
                    put("ipAddress", ipAddress ?: "Not available")
                })
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error fetching IP: ${e.message}")
            JSONObject().apply {
                put("success", false)
                put("plugin", "PLGN_GET_IP")
                put("error", e.message ?: "Unable to fetch IP address")
            }
        }
    }
}