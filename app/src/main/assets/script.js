// Maps plugin name -> the <p> element id that shows its result
const RESULT_ELEMENT_MAP = {
  PLGN_IS_NETWORK_AVAILABLE: 'result-network',
  PLGN_DEV_DETAILS: 'result-device',
  PLGN_GET_IP: 'result-ip',
  PLGN_APP_VERSION: 'result-version',
  PLGN_CRNT_LOCALE: 'result-locale',
  PLGN_APP_SIZE_INFO: 'result-appsize',
  PLGN_CHECK_GPS_STATUS: 'result-gps',
  PLGN_CHECK_ACCESSIBILITY_SETTINGS: 'result-accessibility',
  PLGN_CHECK_BIOMETRIC: 'result-biometric',
  PLGN_OPEN_CAMERA: 'result-camera'
};

function isBridgeAvailable() {
  return typeof window.PlugWebBridge !== 'undefined';
}

// Called by button clicks. params is optional.
function callPlugin(pluginName, params) {
  if (!isBridgeAvailable()) {
    showResult(pluginName, false, 'Bridge not available. Open inside the PlugWeb app.');
    return;
  }
  showResult(pluginName, true, 'Running...');
  window.PlugWebBridge.callPlugin(pluginName, JSON.stringify(params || {}));
}

// Called directly by native code via evaluateJavascript
window.onPluginResult = function (pluginName, result) {
  if (result && result.success) {
    showResult(pluginName, true, JSON.stringify(result.data));
  } else {
    showResult(pluginName, false, result ? result.error : 'Unknown error');
  }
};

function showResult(pluginName, success, text) {
  const el = document.getElementById(RESULT_ELEMENT_MAP[pluginName]);
  if (!el) return;
  el.textContent = text;
  el.className = success ? '' : 'error';
}

window.addEventListener('DOMContentLoaded', function () {
  document.getElementById('bridge-status').textContent = isBridgeAvailable()
    ? 'Bridge connected'
    : 'Bridge not detected (open in app)';
});