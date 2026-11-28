package com.gincz.remote

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.core.content.FileProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class UpdateManager(private val context: Context) {

    private var job: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main)

    companion object {
        private const val VERSION_URL = "https://gincz.com/gin_remote_version.json"
        private const val INITIAL_DELAY_MS = 2 * 60 * 1000L // 2 min
        private const val PERIODIC_INTERVAL_MS = 3 * 60 * 60 * 1000L // 3 hours
    }

    fun start() {
        job?.cancel()
        job = scope.launch {
            delay(INITIAL_DELAY_MS)
            checkForUpdate(silent = true)
            while (isActive) {
                delay(PERIODIC_INTERVAL_MS)
                checkForUpdate(silent = true)
            }
        }
    }

    fun stop() {
        job?.cancel()
    }

    fun checkNow() {
        scope.launch {
            Toast.makeText(context, "Проверка обновлений GIN-Remote...", Toast.LENGTH_SHORT).show()
            checkForUpdate(silent = false)
        }
    }

    private suspend fun checkForUpdate(silent: Boolean) {
        val currentCode = getCurrentVersionCode()

        val updateInfo = withContext(Dispatchers.IO) {
            fetchUpdateInfo()
        } ?: run {
            if (!silent) {
                Toast.makeText(context, "Не удалось проверить обновления", Toast.LENGTH_SHORT).show()
            }
            return
        }

        if (updateInfo.versionCode > currentCode && updateInfo.apkUrl.isNotEmpty()) {
            Toast.makeText(
                context,
                "Найдено обновление: ${updateInfo.versionName}. Загрузка...",
                Toast.LENGTH_LONG
            ).show()

            val success = withContext(Dispatchers.IO) {
                downloadApk(updateInfo.apkUrl)
            }

            if (success) {
                Toast.makeText(
                    context,
                    "Обновление ${updateInfo.versionName} загружено. Запуск установки...",
                    Toast.LENGTH_LONG
                ).show()
                installApk()
            } else {
                if (!silent) {
                    Toast.makeText(context, "Ошибка скачивания обновления", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            if (!silent) {
                Toast.makeText(context, "У вас установлена последняя версия (${updateInfo.versionName})", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun fetchUpdateInfo(): UpdateInfo? {
        var conn: HttpURLConnection? = null
        return try {
            val sep = if (VERSION_URL.contains("?")) "&" else "?"
            val url = URL("${VERSION_URL}${sep}t=${System.currentTimeMillis()}")
            conn = url.openConnection() as HttpURLConnection
            conn.useCaches = false
            conn.setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate")
            conn.setRequestProperty("Pragma", "no-cache")
            conn.connectTimeout = 5000
            conn.readTimeout = 5000
            conn.instanceFollowRedirects = true

            if (conn.responseCode == 200) {
                val jsonStr = conn.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(jsonStr)
                UpdateInfo(
                    versionCode = json.optInt("versionCode", 0),
                    versionName = json.optString("versionName", ""),
                    apkUrl = json.optString("apkUrl", ""),
                    changelog = json.optString("changelog", "")
                )
            } else {
                null
            }
        } catch (_: Exception) {
            null
        } finally {
            conn?.disconnect()
        }
    }

    private fun downloadApk(downloadUrl: String): Boolean {
        var conn: HttpURLConnection? = null
        var input: InputStream? = null
        var output: FileOutputStream? = null

        return try {
            val destFile = File(context.cacheDir, "ginremote_update.apk")
            if (destFile.exists()) {
                destFile.delete()
            }

            val sep = if (downloadUrl.contains("?")) "&" else "?"
            val url = URL("${downloadUrl}${sep}t=${System.currentTimeMillis()}")
            conn = url.openConnection() as HttpURLConnection
            conn.useCaches = false
            conn.setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate")
            conn.setRequestProperty("Pragma", "no-cache")
            conn.connectTimeout = 10000
            conn.readTimeout = 30000
            conn.instanceFollowRedirects = true

            if (conn.responseCode != 200) {
                return false
            }

            input = conn.inputStream
            output = FileOutputStream(destFile)

            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (input.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
            output.flush()

            destFile.length() > 20_000 // Valid APK
        } catch (_: Exception) {
            false
        } finally {
            try { output?.close() } catch (_: Exception) {}
            try { input?.close() } catch (_: Exception) {}
            conn?.disconnect()
        }
    }

    private fun installApk() {
        val apkFile = File(context.cacheDir, "ginremote_update.apk")
        if (!apkFile.exists()) return

        val apkUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }
        context.startActivity(intent)
    }

    private fun getCurrentVersionCode(): Long {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                pInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                pInfo.versionCode.toLong()
            }
        } catch (_: Exception) {
            0L
        }
    }

    data class UpdateInfo(
        val versionCode: Int,
        val versionName: String,
        val apkUrl: String,
        val changelog: String
    )
}
