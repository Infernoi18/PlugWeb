package com.example.plugweb

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.plugweb.databinding.ActivityPluginDetailBinding
import com.example.plugweb.databinding.ItemPluginDataBinding
import org.json.JSONObject

class PluginDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPluginDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPluginDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pluginName = intent.getStringExtra("EXTRA_PLUGIN_NAME") ?: "Plugin Info"
        val jsonData = intent.getStringExtra("EXTRA_JSON_DATA") ?: "{}"

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        
        setupUI(pluginName)
        displayData(jsonData)

        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupUI(pluginName: String) {
        val (title, icon, colorRes, description) = when (pluginName) {
            PluginConstants.PLGN_IS_NETWORK_AVAILABLE -> listOf(
                "Network Status",
                R.drawable.ic_network,
                R.color.color_network,
                "Current connectivity state and network type details."
            )
            PluginConstants.PLGN_DEV_DETAILS -> listOf(
                "Device Details",
                R.drawable.ic_device,
                R.color.color_device,
                "Hardware specifications and OS version information."
            )
            PluginConstants.PLGN_GET_IP -> listOf(
                "IP Address",
                R.drawable.ic_network,
                R.color.color_network,
                "Local IP address assigned to this device."
            )
            PluginConstants.PLGN_APP_VERSION -> listOf(
                "App Version",
                R.drawable.ic_app_info,
                R.color.color_app,
                "Current application build and versioning details."
            )
            PluginConstants.PLGN_OPEN_CAMERA -> listOf(
                "Camera",
                R.drawable.ic_camera,
                R.color.accent,
                "Camera access and image capture results."
            )
            PluginConstants.PLGN_CRNT_LOCALE -> listOf(
                "Locale Info",
                R.drawable.ic_language,
                R.color.color_location,
                "System language and regional configuration."
            )
            PluginConstants.PLGN_APP_SIZE_INFO -> listOf(
                "Storage Usage",
                R.drawable.ic_app_info,
                R.color.color_app,
                "Cache and data storage metrics for this app."
            )
            PluginConstants.PLGN_CHECK_GPS_STATUS -> listOf(
                "GPS Status",
                R.drawable.ic_location,
                R.color.color_location,
                "Location services and provider availability."
            )
            PluginConstants.PLGN_CHECK_ACCESSIBILITY_SETTINGS -> listOf(
                "Accessibility",
                R.drawable.ic_security,
                R.color.color_security,
                "Accessibility services and permission status."
            )
            PluginConstants.PLGN_CHECK_BIOMETRIC -> listOf(
                "Biometrics",
                R.drawable.ic_security,
                R.color.color_security,
                "Biometric hardware availability and verification status."
            )
            else -> listOf(
                pluginName.replace("PLGN_", "").replace("_", " ").lowercase().replaceFirstChar { it.uppercase() },
                R.drawable.ic_app_info,
                R.color.accent,
                "Details for $pluginName plugin."
            )
        }

        binding.collapsingToolbar.title = title as String
        binding.ivHeaderIcon.setImageResource(icon as Int)
        val color = ContextCompat.getColor(this, colorRes as Int)
        binding.appBar.setBackgroundColor(color)
        binding.collapsingToolbar.setContentScrimColor(color)
        binding.tvDescription.text = description as String
    }

    private fun displayData(jsonData: String) {
        try {
            val json = JSONObject(jsonData)
            
            // Check for success/error
            if (!json.optBoolean("success", true)) {
                addItem("Status", "Failed", R.color.error_red)
                if (json.has("error")) {
                    addItem("Error Message", json.getString("error"), R.color.error_red)
                }
            } else {
                addItem("Status", "Success", R.color.success_green)
            }

            val data = json.optJSONObject("data") ?: json

            val keys = data.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                if (key == "success" || key == "plugin" || key == "error") continue
                
                val value = data.get(key)
                if (value is JSONObject) {
                    // Flatten simple nested objects
                    val nestedKeys = value.keys()
                    while (nestedKeys.hasNext()) {
                        val nKey = nestedKeys.next()
                        addItem("$key: $nKey", value.get(nKey).toString())
                    }
                } else {
                    addItem(key, value.toString())
                }
            }
        } catch (e: Exception) {
            addItem("Error", "Failed to parse data: ${e.message}", R.color.error_red)
        }
    }

    private fun addItem(label: String, value: String, valueColorRes: Int? = null) {
        val itemBinding = ItemPluginDataBinding.inflate(LayoutInflater.from(this), binding.container, false)
        
        val formattedLabel = label.replace(Regex("([a-z])([A-Z])"), "$1 $2")
            .replace("_", " ")
            .lowercase()
            .replaceFirstChar { it.uppercase() }

        itemBinding.tvLabel.text = formattedLabel
        itemBinding.tvValue.text = value
        
        valueColorRes?.let {
            itemBinding.tvValue.setTextColor(ContextCompat.getColor(this, it))
        }

        binding.container.addView(itemBinding.root)
    }
}