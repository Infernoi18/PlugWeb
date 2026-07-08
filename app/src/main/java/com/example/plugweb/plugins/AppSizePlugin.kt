package com.example.plugweb.plugins

import android.content.Context
import android.util.Log
import org.json.JSONObject
import java.io.File

class AppSizePlugin(private val context: Context) {

    companion object {
        private const val TAG = "AppSizePlugin"
    }

    fun getAppSize(): JSONObject {
        return try {
            val cacheSize = getFolderSize(context.cacheDir)
            val filesSize = getFolderSize(context.filesDir)
            val totalSize = cacheSize + filesSize

            val data = JSONObject().apply {
                put("cacheSizeBytes", cacheSize)
                put("filesSizeBytes", filesSize)
                put("totalSizeBytes", totalSize)
                put("cacheSizeReadable", toReadableSize(cacheSize))
                put("filesSizeReadable", toReadableSize(filesSize))
                put("totalSizeReadable", toReadableSize(totalSize))
            }

            Log.d(TAG, "App size fetched: $data")

            JSONObject().apply {
                put("success", true)
                put("plugin", "PLGN_APP_SIZE_INFO")
                put("data", data)
            }
        } catch (e: Exception) {
            Log.d(TAG, "Error fetching app size: ${e.message}")
            JSONObject().apply {
                put("success", false)
                put("plugin", "PLGN_APP_SIZE_INFO")
                put("error", e.message ?: "Unable to fetch app size")
            }
        }
    }

    private fun getFolderSize(dir: File?): Long {
        if (dir == null || !dir.exists()) return 0L
        var size = 0L
        dir.listFiles()?.forEach { file ->
            size += if (file.isDirectory) getFolderSize(file) else file.length()
        }
        return size
    }

    private fun toReadableSize(bytes: Long): String {
        if (bytes <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB")
        val digitGroups = (Math.log10(bytes.toDouble()) / Math.log10(1024.0)).toInt()
        return String.format("%.2f %s", bytes / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
    }
}