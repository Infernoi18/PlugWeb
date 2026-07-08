package com.example.plugweb.plugins

import android.content.Context
import android.util.Log
import org.json.JSONObject

class LocalePlugin(private val context: Context) {

    companion object {
        private const val TAG = "LocalePlugin"
    }

    fun getLocale(): JSONObject {
        return try {
            val locale = context.resources.configuration.locales[0]

            val data = JSONObject().apply {
                put("language", locale.language)
                put("country", locale.country)
                put("displayName", locale.displayName)
                put("localeTag", locale.toLanguageTag())
            }

            Log.d(TAG, "Locale fetched: $data")

            JSONObject().apply {
                put("success", true)
                put("plugin", "PLGN_CRNT_LOCALE")
                put("data", data)
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error fetching locale: ${e.message}")
            JSONObject().apply {
                put("success", false)
                put("plugin", "PLGN_CRNT_LOCALE")
                put("error", e.message ?: "Unable to fetch locale")
            }
        }
    }
}