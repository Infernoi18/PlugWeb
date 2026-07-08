package com.example.plugweb

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plugweb.bridge.PluginManager
import com.example.plugweb.databinding.ActivityDashboardBinding
import com.example.plugweb.databinding.ItemPluginCardBinding

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private lateinit var pluginManager: PluginManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        pluginManager = PluginManager(this)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        val plugins = listOf(
            PluginItem(PluginConstants.PLGN_IS_NETWORK_AVAILABLE, "Network Status", "Check internet connectivity", R.drawable.ic_network, R.color.color_network),
            PluginItem(PluginConstants.PLGN_DEV_DETAILS, "Device Info", "Hardware and OS details", R.drawable.ic_device, R.color.color_device),
            PluginItem(PluginConstants.PLGN_GET_IP, "IP Address", "Local network IP", R.drawable.ic_network, R.color.color_network),
            PluginItem(PluginConstants.PLGN_APP_VERSION, "App Version", "Build and version info", R.drawable.ic_app_info, R.color.color_app),
            PluginItem(PluginConstants.PLGN_CRNT_LOCALE, "Locale", "Language and region", R.drawable.ic_language, R.color.color_location),
            PluginItem(PluginConstants.PLGN_APP_SIZE_INFO, "App Size", "Storage usage", R.drawable.ic_app_info, R.color.color_app),
            PluginItem(PluginConstants.PLGN_CHECK_GPS_STATUS, "GPS Status", "Location provider info", R.drawable.ic_location, R.color.color_location),
            PluginItem(PluginConstants.PLGN_CHECK_ACCESSIBILITY_SETTINGS, "Accessibility", "Service status", R.drawable.ic_security, R.color.color_security),
            PluginItem(PluginConstants.PLGN_CHECK_BIOMETRIC, "Biometrics", "Security and auth status", R.drawable.ic_security, R.color.color_security),
            PluginItem(PluginConstants.PLGN_OPEN_CAMERA, "Camera", "Test camera access", R.drawable.ic_camera, R.color.accent)
        )

        binding.rvPlugins.layoutManager = LinearLayoutManager(this)
        binding.rvPlugins.adapter = PluginAdapter(plugins) { item ->
            val result = pluginManager.execute(item.id, null)
            val intent = Intent(this, PluginDetailActivity::class.java).apply {
                putExtra("EXTRA_PLUGIN_NAME", item.id)
                putExtra("EXTRA_JSON_DATA", result.toString())
            }
            startActivity(intent)
        }
    }

    data class PluginItem(
        val id: String,
        val title: String,
        val subtitle: String,
        val icon: Int,
        val color: Int
    )

    inner class PluginAdapter(
        private val items: List<PluginItem>,
        private val onClick: (PluginItem) -> Unit
    ) : RecyclerView.Adapter<PluginAdapter.ViewHolder>() {

        inner class ViewHolder(val binding: ItemPluginCardBinding) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val binding = ItemPluginCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.binding.tvTitle.text = item.title
            holder.binding.tvSubtitle.text = item.subtitle
            holder.binding.ivIcon.setImageResource(item.icon)
            holder.binding.cardView.setOnClickListener { onClick(item) }
        }

        override fun getItemCount() = items.size
    }
}