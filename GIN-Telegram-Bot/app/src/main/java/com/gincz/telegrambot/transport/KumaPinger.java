package com.gincz.telegrambot.transport;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.util.Log;
import com.gincz.telegrambot.data.ConfigManager;
import com.gincz.telegrambot.data.OfflineQueue;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KumaPinger {
    private static final String TAG = "GIN-KumaPinger";
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public interface PingCallback {
        void onResult(boolean success, String statusInfo);
    }

    public static void pingAsync(final Context context, final PingCallback callback) {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                long start = System.currentTimeMillis();
                boolean success = pingSync(context);
                long latency = System.currentTimeMillis() - start;
                if (callback != null) {
                    callback.onResult(success, success ? ("OK (" + latency + "ms)") : "Failed");
                }
            }
        });
    }

    public static boolean pingSync(Context context) {
        ConfigManager config = ConfigManager.getInstance(context);
        String pushUrl = config.getKumaUrl();
        if (pushUrl == null || pushUrl.trim().isEmpty()) {
            Log.d(TAG, "Kuma Push URL is not configured, skipping ping");
            return false;
        }

        if (!TelegramClient.isNetworkAvailable(context)) {
            Log.w(TAG, "Network not available for Kuma ping");
            return false;
        }

        // Gather Telemetry
        int batteryPct = getBatteryPercentage(context);
        String chargeStatus = getChargingStatus(context);
        String wifiInfoStr = getWifiDetails(context);
        int queueCount = OfflineQueue.getInstance(context).getCount();

        String msgText = "OK | Bat: " + batteryPct + "% (" + chargeStatus + ") | WiFi: " + wifiInfoStr + " | Queue: " + queueCount;

        HttpURLConnection conn = null;
        try {
            long startTime = System.currentTimeMillis();
            String separator = pushUrl.contains("?") ? "&" : "?";
            String fullUrl = pushUrl.trim() + separator +
                    "status=up" +
                    "&msg=" + URLEncoder.encode(msgText, "UTF-8") +
                    "&ping=";

            // First connect without ping param to measure rough connection latency or calculate directly
            URL url = new URL(fullUrl + "50");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int code = conn.getResponseCode();
            long latency = System.currentTimeMillis() - startTime;
            Log.i(TAG, "Uptime Kuma ping response: " + code + " in " + latency + "ms (" + msgText + ")");

            // Flush pending messages if network is verified alive
            TelegramClient.flushQueueAsync(context);

            return code == 200;
        } catch (Exception e) {
            Log.e(TAG, "Error pinging Uptime Kuma: " + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    private static int getBatteryPercentage(Context context) {
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
        return -1;
    }

    private static String getChargingStatus(Context context) {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            if (batteryStatus != null) {
                int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
                boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                     status == BatteryManager.BATTERY_STATUS_FULL;
                if (isCharging) {
                    int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
                    if (chargePlug == BatteryManager.BATTERY_PLUGGED_USB) return "USB";
                    if (chargePlug == BatteryManager.BATTERY_PLUGGED_AC) return "AC";
                    return "Charging";
                }
            }
        } catch (Exception ignored) {}
        return "Discharging";
    }

    private static String getWifiDetails(Context context) {
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
                    return ssid + " (" + rssi + "dBm)";
                }
            }
        } catch (Exception ignored) {}
        return "Offline/Mobile";
    }
}
