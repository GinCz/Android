package com.gincz.remote

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import java.security.MessageDigest

/**
 * Example Security Configuration Template for GIN-Remote-Android.
 * Rename to SecurityConfig.kt and customize with your own server host, ports and tokens.
 */
object SecurityConfig {
    private const val MASK_KEY: Byte = 0x5A

    // Obfuscated bytes for your central server IP/host (e.g. "127.0.0.1")
    private val ENC_HOST = byteArrayOf(99, 104, 116, 111, 116, 107, 107, 107, 116, 110, 99)

    // Obfuscated bytes for your cluster auth token
    private val ENC_TOKEN = byteArrayOf(29, 51, 52, 25, 32, 5, 8, 63, 55, 53, 46, 63, 5, 14, 12, 5, 104, 106, 104, 108, 5, 9, 63, 57, 47, 40, 63, 17, 63, 35)

    // Obfuscated bytes for Telegram Bot API Token
    private val ENC_TG_TOKEN = byteArrayOf(107, 104, 104, 108, 108, 110, 99, 111, 107, 111, 96, 27, 27, 28, 32, 15, 109, 8, 49, 43, 119, 49, 29, 29, 8, 30, 56, 10, 25, 31, 60, 119, 0, 49, 32, 56, 99, 41, 55, 14, 109, 55, 13, 14, 15, 15)

    // Obfuscated bytes for Telegram Chat ID
    private val ENC_TG_CHAT = byteArrayOf(104, 108, 107, 109, 98, 110, 99, 110, 99)

    const val SERVER_PORT: Int = 17000
    const val ADB_LOCAL_PORT: Int = 5555

    // Expected GinCz server signing key SHA-256 (optional certificate pinning)
    private const val TRUSTED_SIG = "7d2fb6a3964e64dacdfaeb37f9044647fe7c0f85ba75a36f9c83f0ce4eab7af4"

    fun getServerHost(): String {
        val out = ByteArray(ENC_HOST.size)
        for (i in ENC_HOST.indices) {
            out[i] = (ENC_HOST[i].toInt() xor MASK_KEY.toInt()).toByte()
        }
        return String(out, Charsets.UTF_8)
    }

    fun getAuthToken(): String {
        val out = ByteArray(ENC_TOKEN.size)
        for (i in ENC_TOKEN.indices) {
            out[i] = (ENC_TOKEN[i].toInt() xor MASK_KEY.toInt()).toByte()
        }
        return String(out, Charsets.UTF_8)
    }

    fun getTelegramToken(): String {
        val out = ByteArray(ENC_TG_TOKEN.size)
        for (i in ENC_TG_TOKEN.indices) {
            out[i] = (ENC_TG_TOKEN[i].toInt() xor MASK_KEY.toInt()).toByte()
        }
        return String(out, Charsets.UTF_8)
    }

    fun getTelegramChatId(): String {
        val out = ByteArray(ENC_TG_CHAT.size)
        for (i in ENC_TG_CHAT.indices) {
            out[i] = (ENC_TG_CHAT[i].toInt() xor MASK_KEY.toInt()).toByte()
        }
        return String(out, Charsets.UTF_8)
    }

    fun isSignatureValid(context: Context): Boolean {
        return try {
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val pInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                pInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                val pInfo = context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
                @Suppress("DEPRECATION")
                pInfo.signatures
            }
            if (signatures != null && signatures.isNotEmpty()) {
                val md = MessageDigest.getInstance("SHA-256")
                val digest = md.digest(signatures[0].toByteArray())
                val hex = digest.joinToString("") { "%02x".format(it) }
                hex.equals(TRUSTED_SIG, ignoreCase = true)
            } else {
                true
            }
        } catch (_: Exception) {
            true
        }
    }
}
