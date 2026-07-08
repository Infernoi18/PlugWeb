package com.example.plugweb

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.plugweb.bridge.JavaScriptBridge
import com.example.plugweb.databinding.ActivityMainBinding
import com.example.plugweb.plugins.NetworkPlugin
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var photoUri: Uri? = null
    private var currentPhotoPath: String? = null

    companion object {
        private const val TAG = "MainActivity"
        private const val LOCAL_URL = "file:///android_asset/index.html"
    }

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            Log.d(TAG, "Photo saved at: $currentPhotoPath")
            sendCameraResult(true, currentPhotoPath)
        } else {
            Log.d(TAG, "Camera cancelled")
            sendCameraResult(false, "Camera cancelled or failed")
        }
    }

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            launchCamera()
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            sendCameraResult(false, "Permission denied")
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        setupWebView()
        updateNetworkStatus()

        binding.swipeRefresh.setOnRefreshListener {
            binding.webview.reload()
        }
    }

    private fun setupWebView() {
        val webView = binding.webview

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true

        val bridge = JavaScriptBridge(this, webView)
        webView.addJavascriptInterface(bridge, "PlugWebBridge")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: android.graphics.Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.progressBar.visibility = android.view.View.VISIBLE
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.progressBar.visibility = android.view.View.GONE
                binding.swipeRefresh.isRefreshing = false
            }
        }

        webView.loadUrl(LOCAL_URL)
    }

    fun openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCamera() {
        try {
            val photoFile = createImageFile()
            photoUri = FileProvider.getUriForFile(this, "$packageName.fileprovider", photoFile)
            
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            cameraLauncher.launch(intent)
        } catch (e: Exception) {
            Log.e(TAG, "Error launching camera", e)
            sendCameraResult(false, e.message ?: "Error creating file")
        }
    }

    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "PlugWebCamera")
        if (!storageDir.exists()) {
            storageDir.mkdirs()
        }
        return File.createTempFile("IMG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun sendCameraResult(success: Boolean, message: String?) {
        val result = JSONObject().apply {
            put("success", success)
            put("plugin", PluginConstants.PLGN_OPEN_CAMERA)
            if (success) {
                put("data", JSONObject().apply { put("path", message) })
            } else {
                put("error", message)
            }
        }
        
        runOnUiThread {
            val js = "window.onPluginResult('${PluginConstants.PLGN_OPEN_CAMERA}', $result);"
            binding.webview.evaluateJavascript(js, null)
        }
    }

    private fun updateNetworkStatus() {
        val result = NetworkPlugin(this).check()
        val data = result.optJSONObject("data")
        val isConnected = data?.optBoolean("isConnected", false) ?: false
        val type = data?.optString("type", "none") ?: "none"

        binding.tvStatus.text = if (isConnected) "Connected via $type" else "No internet connection"
    }

    override fun onDestroy() {
        binding.webview.destroy()
        super.onDestroy()
    }
}