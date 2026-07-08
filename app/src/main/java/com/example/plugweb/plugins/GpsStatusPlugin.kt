package com.example.plugweb.plugins

import android.content.Context
import android.location.LocationManager
import android.util.Log
import org.json.JSONObject

class GpsStatusPlugin(private val context: Context) {

    companion object {
        private const val TAG = "GpsStatusPlugin"
    }

    fun checkGpsStatus(): JSONObject {
        return try {
            val locationManager =
                context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkLocationEnabled =
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            val data = JSONObject().apply {
                put("gpsEnabled", isGpsEnabled)
                put("networkLocationEnabled", isNetworkLocationEnabled)
                put("anyLocationEnabled", isGpsEnabled || isNetworkLocationEnabled)
            }

            Log.d(TAG, "GPS status fetched: $data")

            JSONObject().apply {
                put("success", true)
                put("plugin", "PLGN_CHECK_GPS_STATUS")
                put("data", data)
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error checking GPS status: ${e.message}")
            JSONObject().apply {
                put("success", false)
                put("plugin", "PLGN_CHECK_GPS_STATUS")
                put("error", e.message ?: "Unable to check GPS status")
            }
        }
    }
}