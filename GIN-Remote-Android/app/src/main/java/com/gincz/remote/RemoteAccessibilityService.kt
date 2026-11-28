package com.gincz.remote

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent

class RemoteAccessibilityService : AccessibilityService() {

    companion object {
        @Volatile
        var instance: RemoteAccessibilityService? = null
            private set

        val isEnabled: Boolean get() = instance != null

        fun isAccessibilitySettingsOn(context: Context): Boolean {
            try {
                val serviceName = "${context.packageName}/${RemoteAccessibilityService::class.java.canonicalName}"
                val enabledServices = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
                ) ?: return false
                return enabledServices.contains(serviceName)
            } catch (_: Exception) {
                return false
            }
        }

        fun getRealScreenDimensions(): Pair<Float, Float> {
            val service = instance ?: return Pair(1080f, 2340f)
            val wm = service.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val metrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            wm.defaultDisplay.getRealMetrics(metrics)
            return Pair(metrics.widthPixels.toFloat(), metrics.heightPixels.toFloat())
        }

        fun performTapRatio(ratioX: Float, ratioY: Float): Boolean {
            val (realW, realH) = getRealScreenDimensions()
            val actualX = if (ratioX <= 1.0f) ratioX * realW else ratioX
            val actualY = if (ratioY <= 1.0f) ratioY * realH else ratioY
            return performTap(actualX, actualY)
        }

        fun performSwipeRatio(rx1: Float, ry1: Float, rx2: Float, ry2: Float, durationMs: Long = 300L): Boolean {
            val (realW, realH) = getRealScreenDimensions()
            val ax1 = if (rx1 <= 1.0f) rx1 * realW else rx1
            val ay1 = if (ry1 <= 1.0f) ry1 * realH else ry1
            val ax2 = if (rx2 <= 1.0f) rx2 * realW else rx2
            val ay2 = if (ry2 <= 1.0f) ry2 * realH else ry2
            return performSwipe(ax1, ay1, ax2, ay2, durationMs)
        }

        fun performTap(x: Float, y: Float): Boolean {
            val service = instance
            if (service != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val path = Path().apply { moveTo(x, y) }
                val stroke = GestureDescription.StrokeDescription(path, 0, 50)
                val gesture = GestureDescription.Builder().addStroke(stroke).build()
                return service.dispatchGesture(gesture, null, null)
            }
            return try {
                Runtime.getRuntime().exec(arrayOf("sh", "-c", "input tap ${x.toInt()} ${y.toInt()}"))
                true
            } catch (_: Throwable) {
                false
            }
        }

        fun performSwipe(startX: Float, startY: Float, endX: Float, endY: Float, durationMs: Long = 300L): Boolean {
            val service = instance
            if (service != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                val path = Path().apply {
                    moveTo(startX, startY)
                    lineTo(endX, endY)
                }
                val stroke = GestureDescription.StrokeDescription(path, 0, durationMs.coerceAtLeast(100L))
                val gesture = GestureDescription.Builder().addStroke(stroke).build()
                return service.dispatchGesture(gesture, null, null)
            }
            return try {
                Runtime.getRuntime().exec(arrayOf("sh", "-c", "input swipe ${startX.toInt()} ${startY.toInt()} ${endX.toInt()} ${endY.toInt()} $durationMs"))
                true
            } catch (_: Throwable) {
                false
            }
        }

        fun performGlobal(action: Int): Boolean {
            return instance?.performGlobalAction(action) ?: false
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        RemoteForegroundService.addLog("[✅] Служба управления Accessibility активна (Тапы и жесты)")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        if (instance == this) instance = null
        RemoteForegroundService.addLog("[ℹ️] Служба управления Accessibility остановлена")
    }
}
