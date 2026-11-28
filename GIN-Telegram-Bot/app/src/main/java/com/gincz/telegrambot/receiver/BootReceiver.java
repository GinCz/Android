package com.gincz.telegrambot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.util.Log;
import com.gincz.telegrambot.data.ConfigManager;
import com.gincz.telegrambot.service.HeartbeatManager;
import com.gincz.telegrambot.transport.TelegramClient;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "GIN-BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) return;
        String action = intent.getAction();

        if (Intent.ACTION_BOOT_COMPLETED.equals(action) ||
            "android.intent.action.QUICKBOOT_POWERON".equals(action) ||
            Intent.ACTION_MY_PACKAGE_REPLACED.equals(action)) {

            Log.i(TAG, "System boot detected. Initializing GIN-Telegram-Bot services...");

            // Schedule recurring Uptime Kuma heartbeats
            HeartbeatManager.scheduleNext(context);

            ConfigManager config = ConfigManager.getInstance(context);
            if (!config.isServiceActive()) return;

            // Send Boot notification to Telegram
            String deviceName = config.getDeviceName();
            int batteryPct = getBatteryPercentage(context);
            String wifiDetails = getWifiDetails(context);

            String html = "🟢 <b>[" + TelegramClient.escapeHtml(deviceName) + " • Шлюз запущен]</b>\n" +
                    "Устройство успешно загрузилось. Службы перехвата SMS и Push активны.\n" +
                    "<b>Wi-Fi:</b> " + TelegramClient.escapeHtml(wifiDetails) + "\n" +
                    "<b>Батарея:</b> " + batteryPct + "% (USB)";

            TelegramClient.sendAsync(context, html, true, null);
        }
    }

    private int getBatteryPercentage(Context context) {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            if (batteryStatus != null) {
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                if (level >= 0 && scale > 0) {
                    return (int) ((level / (float) scale) * 100);
                }
            }
        } catch (Exception ignored) {}
        return 100;
    }

    private String getWifiDetails(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wm != null && wm.isWifiEnabled()) {
                WifiInfo info = wm.getConnectionInfo();
                if (info != null) {
                    String ssid = info.getSSID();
                    if (ssid != null && ssid.startsWith("\"") && ssid.endsWith("\"")) {
                        ssid = ssid.substring(1, ssid.length() - 1);
                    }
                    int rssi = info.getRssi();
                    return ssid + " (" + rssi + " dBm)";
                }
            }
        } catch (Exception ignored) {}
        return "Connected";
    }
}
