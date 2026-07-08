package com.example.plugweb

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
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
        supportActionBar?.title = pluginName.replace("PLGN_", "").replace("_", " ").lowercase()
            .replaceFirstChar { it.uppercase() }

        binding.toolbar.setNavigationOnClickListener { finish() }

        displayData(jsonData)
    }

    private fun displayData(jsonData: String) {
        try {
            val json = JSONObject(jsonData)
            val data = json.optJSONObject("data") ?: json

            val keys = data.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                val value = data.get(key)
                
                if (value is JSONObject) {
                     // Nested objects could be handled recursively or simplified
                     addItem(key, value.toString())
                } else {
                    addItem(key, value.toString())
                }
            }
        } catch (e: Exception) {
            addItem("Error", "Failed to parse data: ${e.message}")
        }
    }

    private fun addItem(label: String, value: String) {
        val itemBinding = ItemPluginDataBinding.inflate(LayoutInflater.from(this), binding.container, false)
        
        // Format label: camelCase to Title Case
        val formattedLabel = label.replace(Regex("([a-z])([A-Z])"), "$1 $2")
            .replace("_", " ")
            .lowercase()
            .replaceFirstChar { it.uppercase() }

        itemBinding.tvLabel.text = formattedLabel
        itemBinding.tvValue.text = value
        
        binding.container.addView(itemBinding.root)
    }
}