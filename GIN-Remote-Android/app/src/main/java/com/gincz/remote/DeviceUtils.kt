package com.gincz.remote

import android.content.Context
import android.os.Build
import android.provider.Settings
import java.security.MessageDigest

object DeviceUtils {

    /**
     * Permanent Widevine DRM Hardware Unique ID (survives OS re-installs, factory resets, firmware updates).
     * Widevine device key is burned into the silicon OTP/TrustZone at factory.
     */
    private fun getWidevineUniqueId(): ByteArray? {
        return try {
            val widevineUuid = java.util.UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L)
            val mediaDrm = android.media.MediaDrm(widevineUuid)
            val bytes = mediaDrm.getPropertyByteArray(android.media.MediaDrm.PROPERTY_DEVICE_UNIQUE_ID)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                mediaDrm.close()
            } else {
                @Suppress("DEPRECATION")
                mediaDrm.release()
            }
            if (bytes != null && bytes.isNotEmpty()) bytes else null
        } catch (_: Throwable) {
            null
        }
    }

    /**
     * Deterministic, permanent 9-digit Hardware ID (XXX-XXX-XXX).
     * Priority 1: Widevine DRM silicon hardware ID (unalterable even across OS reinstalls).
     * Priority 2: ANDROID_ID + Build info fallback.
     * Canonical algorithm across GIN-Cinema, GIN-TV, GIN-Remote and GIN-Chat.
     */
    fun getHardwareDeviceId(context: Context): String {
        return try {
            val widevineBytes = getWidevineUniqueId()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = if (widevineBytes != null && widevineBytes.isNotEmpty()) {
                md.digest(widevineBytes)
            } else {
                val androidId = Settings.Secure.getString(
                    context.contentResolver,
                    Settings.Secure.ANDROID_ID
                ).orEmpty()
                val raw = "${androidId}|${Build.MANUFACTURER}|${Build.MODEL}|${Build.BOARD}"
                md.digest(raw.toByteArray(Charsets.UTF_8))
            }
            var num = 0L
            for (i in 0 until 8) {
                num = (num shl 8) or (digest[i].toLong() and 0xFF)
            }
            num = Math.abs(num)
            val code = (num % 900_000_000L) + 100_000_000L
            val s = code.toString().padStart(9, '0')
            "${s.substring(0, 3)}-${s.substring(3, 6)}-${s.substring(6, 9)}"
        } catch (_: Exception) {
            "100-000-001"
        }
    }
}
