package com.gincz.remote

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.view.WindowManager
import java.io.ByteArrayOutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class ScreenCaptureManager(
    private val context: Context,
    private val mediaProjection: MediaProjection,
    private val deviceId: String
) {
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null
    private val networkExecutor = Executors.newSingleThreadExecutor()

    @Volatile
    private var isRunning = false
    private var lastSendTime = 0L
    private var hasLoggedSuccess = false

    companion object {
        @Volatile
        var activeInstance: ScreenCaptureManager? = null
            private set
    }

    fun start() {
        if (isRunning) return
        isRunning = true
        activeInstance = this

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val metrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        wm.defaultDisplay.getRealMetrics(metrics)

        val origW = metrics.widthPixels
        val origH = metrics.heightPixels

        // Target width 540 for mobile devices (bandwidth and battery friendly)
        val minDim = origW.coerceAtMost(origH)
        val scale = if (minDim > 540) 540f / minDim else 1.0f
        val targetW = (origW * scale).toInt() and 0xFFFFFFFE.toInt()
        val targetH = (origH * scale).toInt() and 0xFFFFFFFE.toInt()
        val density = metrics.densityDpi

        RemoteForegroundService.addLog("[⚡] Запуск захвата экрана: ${targetW}x${targetH} (DPI: $density)")

        imageReader = ImageReader.newInstance(targetW, targetH, PixelFormat.RGBA_8888, 2)
        handlerThread = HandlerThread("GIN_ScreenCapture").apply { start() }
        handler = Handler(handlerThread!!.looper)

        try {
            mediaProjection.registerCallback(object : MediaProjection.Callback() {
                override fun onStop() {
                    stop()
                }
            }, handler)
        } catch (_: Throwable) {}

        try {
            virtualDisplay = mediaProjection.createVirtualDisplay(
                "GIN-Remote-Capture",
                targetW, targetH, density,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader!!.surface, null, handler
            )
        } catch (e: Throwable) {
            RemoteForegroundService.addLog("[-] Ошибка createVirtualDisplay: ${e.message}")
            stop()
            return
        }

        imageReader!!.setOnImageAvailableListener({ reader ->
            val now = System.currentTimeMillis()
            // ~3-4 frames per second is optimal for remote assistance preview
            if (now - lastSendTime < 280) {
                val drop = reader.acquireLatestImage()
                drop?.close()
                return@setOnImageAvailableListener
            }

            var image: Image? = null
            try {
                image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener
                val planes = image.planes
                val buffer = planes[0].buffer
                val pixelStride = planes[0].pixelStride
                val rowStride = planes[0].rowStride
                val rowPadding = rowStride - pixelStride * targetW

                val bitmap = Bitmap.createBitmap(
                    targetW + rowPadding / pixelStride,
                    targetH,
                    Bitmap.Config.ARGB_8888
                )
                bitmap.copyPixelsFromBuffer(buffer)

                val cropped = if (rowPadding > 0) {
                    Bitmap.createBitmap(bitmap, 0, 0, targetW, targetH)
                } else {
                    bitmap
                }

                val out = ByteArrayOutputStream()
                cropped.compress(Bitmap.CompressFormat.JPEG, 65, out)
                val jpegBytes = out.toByteArray()

                bitmap.recycle()
                if (cropped != bitmap) cropped.recycle()

                lastSendTime = now
                uploadFrame(jpegBytes)
            } catch (e: Exception) {
                // Ignore transient frame buffer errors
            } finally {
                image?.close()
            }
        }, handler)
    }

    private fun uploadFrame(jpegBytes: ByteArray) {
        networkExecutor.execute {
            for (endpoint in arrayOf("https://remote.gincz.com/api/device_frame", "http://92.5.111.49:8096/api/device_frame")) {
                try {
                    val url = URL(endpoint)
                    val conn = url.openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.doOutput = true
                    conn.connectTimeout = 3000
                    conn.readTimeout = 3000
                    conn.setRequestProperty("Content-Type", "image/jpeg")
                    conn.setRequestProperty("X-Auth-Token", SecurityConfig.getAuthToken())
                    conn.setRequestProperty("X-Device-Id", deviceId)

                    conn.outputStream.use { it.write(jpegBytes) }
                    val code = conn.responseCode
                    conn.disconnect()
                    if (code == 200) {
                        if (!hasLoggedSuccess) {
                            hasLoggedSuccess = true
                            RemoteForegroundService.addLog("[✅] Поток экрана успешно передается на Oracle-49!")
                        }
                        break
                    }
                } catch (_: Exception) {}
            }
        }
    }

    fun stop() {
        isRunning = false
        if (activeInstance == this) activeInstance = null
        try { virtualDisplay?.release() } catch (_: Throwable) {}
        try { imageReader?.close() } catch (_: Throwable) {}
        try { handlerThread?.quitSafely() } catch (_: Throwable) {}
        try { mediaProjection.stop() } catch (_: Throwable) {}
        RemoteForegroundService.addLog("[ℹ️] Захват экрана остановлен")
    }
}
