package com.gincz.remote

import android.app.Activity
import android.app.ActivityManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import java.net.Inet4Address
import java.net.NetworkInterface

class MainActivity : AppCompatActivity(), RemoteForegroundService.Listener {

    companion object {
        private const val REQUEST_MEDIA_PROJECTION = 2026
    }

    private lateinit var tvStatusText: TextView
    private lateinit var viewStatusDot: View
    private lateinit var btnToggle: Button
    private lateinit var btnExit: Button
    private lateinit var btnAccessibility: Button
    private lateinit var btnCapture: Button
    private lateinit var tvLogs: TextView
    private lateinit var scrollLogs: ScrollView
    private lateinit var btnVersion: Button

    private lateinit var tvDeviceModel: TextView
    private lateinit var tvDeviceOs: TextView
    private lateinit var tvDeviceRam: TextView
    private lateinit var tvDeviceCpu: TextView
    private lateinit var tvDeviceNet: TextView

    private lateinit var updateManager: UpdateManager
    private val mainHandler = Handler(Looper.getMainLooper())
    private val telemetryRunnable = object : Runnable {
        override fun run() {
            updateTelemetry()
            updateAccessibilityState()
            mainHandler.postDelayed(this, 3000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        super.onCreate(savedInstanceState)

        // Security check: verify untampered signature
        if (!SecurityConfig.isSignatureValid(this)) {
            Toast.makeText(this, "Security error: Invalid app signature", Toast.LENGTH_LONG).show()
            finishAffinity()
            return
        }

        try {
            setContentView(R.layout.activity_main)

            tvStatusText = findViewById(R.id.tvStatusText)
            viewStatusDot = findViewById(R.id.viewStatusDot)
            btnToggle = findViewById(R.id.btnToggle)
            btnExit = findViewById(R.id.btnExit)
            btnAccessibility = findViewById(R.id.btnAccessibility)
            btnCapture = findViewById(R.id.btnCapture)
            tvLogs = findViewById(R.id.tvLogs)
            scrollLogs = findViewById(R.id.scrollLogs)
            btnVersion = findViewById(R.id.btnVersion)

            btnCapture.setOnClickListener {
                if (ScreenCaptureManager.activeInstance != null) {
                    ScreenCaptureManager.activeInstance?.stop()
                    updateCaptureState()
                } else {
                    requestScreenCapture()
                }
            }

            btnExit.setOnClickListener {
                exitApplicationCompletely()
            }

            tvDeviceModel = findViewById(R.id.tvDeviceModel)
            tvDeviceOs = findViewById(R.id.tvDeviceOs)
            tvDeviceRam = findViewById(R.id.tvDeviceRam)
            tvDeviceCpu = findViewById(R.id.tvDeviceCpu)
            tvDeviceNet = findViewById(R.id.tvDeviceNet)

            val vName = try {
                packageManager.getPackageInfo(packageName, 0).versionName
            } catch (_: Exception) {
                "v017"
            }
            btnVersion.text = "GIN-Remote $vName • Проверить обновления (OK)"

            updateTelemetry()
            updateAccessibilityState()

            updateManager = UpdateManager(this)
            updateManager.start()

            btnToggle.setOnClickListener {
                if (RemoteForegroundService.currentState == RemoteForegroundService.State.CONNECTED ||
                    RemoteForegroundService.currentState == RemoteForegroundService.State.CONNECTING) {
                    RemoteForegroundService.stopServiceCleanly(this)
                    updateCaptureState()
                } else {
                    requestMobileAssistAndStart()
                }
                btnToggle.post {
                    btnToggle.requestFocus()
                }
            }

            btnAccessibility.setOnClickListener {
                try {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    startActivity(intent)
                    Toast.makeText(this, "Включите службу GIN-Remote для управления", Toast.LENGTH_LONG).show()
                } catch (_: Exception) {
                    Toast.makeText(this, "Откройте Настройки -> Спец. возможности -> GIN-Remote", Toast.LENGTH_LONG).show()
                }
            }

            btnVersion.setOnClickListener {
                updateManager.checkNow()
            }

            if (RemoteForegroundService.currentState == RemoteForegroundService.State.DISCONNECTED) {
                val isTv = packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
                if (isTv) {
                    startBridgeService()
                } else if (ScreenCaptureManager.activeInstance == null) {
                    // Clean up any stale notifications when app opens in disconnected state on phone
                    RemoteForegroundService.stopServiceCleanly(this)
                }
            }

            btnToggle.post {
                btnToggle.requestFocus()
            }
        } catch (e: Throwable) {
            Toast.makeText(this, "Init error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun exitApplicationCompletely() {
        try {
            mainHandler.removeCallbacksAndMessages(null)
        } catch (_: Throwable) {}
        try {
            if (::updateManager.isInitialized) {
                updateManager.stop()
            }
        } catch (_: Throwable) {}
        try {
            RemoteForegroundService.removeListener(this)
        } catch (_: Throwable) {}
        try {
            RemoteForegroundService.stopServiceCleanly(this)
        } catch (_: Throwable) {}
        try {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            nm?.cancelAll()
        } catch (_: Throwable) {}
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAndRemoveTask()
            }
        } catch (_: Throwable) {}
        try {
            finishAffinity()
        } catch (_: Throwable) {}

        Thread {
            try {
                Thread.sleep(150)
            } catch (_: Throwable) {}
            try {
                android.os.Process.killProcess(android.os.Process.myPid())
            } catch (_: Throwable) {}
            kotlin.system.exitProcess(0)
        }.start()
    }

    private fun isTvDevice(): Boolean {
        return packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
    }

    private fun requestScreenCapture() {
        try {
            val mpm = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            startActivityForResult(mpm.createScreenCaptureIntent(), REQUEST_MEDIA_PROJECTION)
        } catch (e: Exception) {
            Toast.makeText(this, "MediaProjection error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun requestMobileAssistAndStart() {
        startBridgeService()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_MEDIA_PROJECTION) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                try {
                    val mpm = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                    val mp = mpm.getMediaProjection(resultCode, data)
                    if (mp != null) {
                        val pin = DeviceUtils.getHardwareDeviceId(this)
                        val deviceName = "${Build.MANUFACTURER}_${Build.MODEL}_${pin}".replace(" ", "_")
                        val captureManager = ScreenCaptureManager(this, mp, deviceName)
                        captureManager.start()
                        updateCaptureState()
                        RemoteForegroundService.addLog("[✅] Трансляция экрана активирована!")
                        Toast.makeText(this, "Трансляция экрана запущена!", Toast.LENGTH_SHORT).show()
                    } else {
                        RemoteForegroundService.addLog("[-] MediaProjection вернул null")
                    }
                } catch (e: Throwable) {
                    RemoteForegroundService.addLog("[-] Ошибка MediaProjection: ${e.message}")
                }
            } else {
                RemoteForegroundService.addLog("[⚠️] Запрос трансляции экрана отклонен пользователем")
            }
        }
    }

    private fun updateCaptureState() {
        try {
            if (ScreenCaptureManager.activeInstance != null) {
                btnCapture.text = "✅ Трансляция экрана: Активна"
                btnCapture.setTextColor(Color.parseColor("#4ade80"))
            } else {
                btnCapture.text = "📸 Трансляция экрана: Запустить"
                btnCapture.setTextColor(Color.parseColor("#38bdf8"))
            }
        } catch (_: Throwable) {}
    }

    private fun updateAccessibilityState() {
        try {
            val isOn = RemoteAccessibilityService.isAccessibilitySettingsOn(this)
            if (isOn) {
                btnAccessibility.text = "✅ Управление жестами: Активно"
                btnAccessibility.setTextColor(Color.parseColor("#4ade80"))
            } else {
                btnAccessibility.text = "⚠️ Включить управление жестами (Спец. возможности)"
                btnAccessibility.setTextColor(Color.parseColor("#fbbf24"))
            }
            updateCaptureState()
        } catch (_: Throwable) {}
    }

    private fun updateTelemetry() {
        try {
            tvDeviceModel.text = "Модель: ${Build.MANUFACTURER} ${Build.MODEL}"
            tvDeviceOs.text = "ОС: Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

            // RAM
            val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val mi = ActivityManager.MemoryInfo()
            am.getMemoryInfo(mi)
            val totalMb = mi.totalMem / (1024 * 1024)
            val availMb = mi.availMem / (1024 * 1024)
            val usedMb = totalMb - availMb
            val ramPercent = if (totalMb > 0) (usedMb * 100) / totalMb else 0
            tvDeviceRam.text = "ОЗУ: $usedMb МБ / $totalMb МБ ($ramPercent% исп.)"

            // CPU
            val cores = Runtime.getRuntime().availableProcessors()
            val is64 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Build.SUPPORTED_64_BIT_ABIS.isNotEmpty()
            } else false
            val arch = if (is64) "64-bit ARM" else "32-bit ARM"
            tvDeviceCpu.text = "ЦП: $cores ядра • $arch"

            // Network IP
            val ip = getLocalIpAddress()
            tvDeviceNet.text = "Сеть: $ip"
        } catch (_: Throwable) {}
    }

    private fun getLocalIpAddress(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val intf = en.nextElement()
                val enumIpAddr = intf.inetAddresses
                while (enumIpAddr.hasMoreElements()) {
                    val inetAddress = enumIpAddr.nextElement()
                    if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                        return "${inetAddress.hostAddress} (${intf.name})"
                    }
                }
            }
        } catch (_: Exception) {}
        return "127.0.0.1"
    }

    private fun startBridgeService() {
        try {
            val intent = Intent(this, RemoteForegroundService::class.java).apply {
                action = RemoteForegroundService.ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
        } catch (_: Throwable) {
            try {
                startService(Intent(this, RemoteForegroundService::class.java).apply {
                    action = RemoteForegroundService.ACTION_START
                })
            } catch (_: Throwable) {}
        }
    }

    override fun onResume() {
        super.onResume()
        updateAccessibilityState()
        updateCaptureState()
        btnToggle.post {
            btnToggle.requestFocus()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            btnToggle.post {
                btnToggle.requestFocus()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        tvLogs.text = ""
        RemoteForegroundService.addListener(this)
        mainHandler.post(telemetryRunnable)
    }

    override fun onStop() {
        mainHandler.removeCallbacks(telemetryRunnable)
        RemoteForegroundService.removeListener(this)
        super.onStop()
    }

    override fun onDestroy() {
        try {
            updateManager.stop()
        } catch (_: Throwable) {}
        super.onDestroy()
    }

    override fun onStateChanged(state: RemoteForegroundService.State) {
        mainHandler.post {
            when (state) {
                RemoteForegroundService.State.CONNECTED -> {
                    tvStatusText.text = getString(R.string.status_connected)
                    viewStatusDot.setBackgroundResource(R.drawable.dot_connected)
                    btnToggle.text = getString(R.string.btn_disconnect)
                    btnToggle.setBackgroundResource(R.drawable.btn_disconnect_bg)
                }
                RemoteForegroundService.State.CONNECTING -> {
                    tvStatusText.text = getString(R.string.status_connecting)
                    viewStatusDot.setBackgroundResource(R.drawable.dot_connecting)
                    btnToggle.text = getString(R.string.btn_disconnect)
                    btnToggle.setBackgroundResource(R.drawable.btn_disconnect_bg)
                }
                RemoteForegroundService.State.DISCONNECTED -> {
                    tvStatusText.text = getString(R.string.status_disconnected)
                    viewStatusDot.setBackgroundResource(R.drawable.dot_disconnected)
                    btnToggle.text = getString(R.string.btn_connect)
                    btnToggle.setBackgroundResource(R.drawable.btn_connect_bg)
                }
            }
            btnToggle.post {
                btnToggle.requestFocus()
            }
        }
    }

    override fun onLogAdded(logLine: String) {
        mainHandler.post {
            tvLogs.append(logLine + "\n")
            scrollLogs.post {
                scrollLogs.scrollTo(0, tvLogs.bottom)
            }
        }
    }
}
