package com.gincz.telegrambot.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import com.gincz.telegrambot.data.ConfigManager;
import com.gincz.telegrambot.transport.KumaPinger;
import com.gincz.telegrambot.transport.TelegramClient;

public class HeartbeatManager extends BroadcastReceiver {
    private static final String TAG = "GIN-HeartbeatManager";
    public static final String ACTION_HEARTBEAT = "com.gincz.telegrambot.ACTION_HEARTBEAT";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent == null || !ACTION_HEARTBEAT.equals(intent.getAction())) return;

        Log.i(TAG, "Heartbeat alarm triggered. Pinging Uptime Kuma and flushing offline queue...");
        KumaPinger.pingAsync(context, null);
        TelegramClient.flushQueueAsync(context);

        // Reschedule next exact alarm if needed on newer Android or maintain cycle
        scheduleNext(context);
    }

    public static void scheduleNext(Context context) {
        ConfigManager config = ConfigManager.getInstance(context);
        int intervalMin = config.getKumaIntervalMinutes();
        long triggerAt = System.currentTimeMillis() + (intervalMin * 60L * 1000L);

        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(context, HeartbeatManager.class);
        intent.setAction(ACTION_HEARTBEAT);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pi = PendingIntent.getBroadcast(context, 1001, intent, flags);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                am.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            } else {
                am.set(AlarmManager.RTC_WAKEUP, triggerAt, pi);
            }
            Log.d(TAG, "Next Heartbeat scheduled in " + intervalMin + " minutes");
        } catch (Exception e) {
            Log.e(TAG, "Failed to schedule Heartbeat: " + e.getMessage());
        }
    }

    public static void cancel(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (am == null) return;

        Intent intent = new Intent(context, HeartbeatManager.class);
        intent.setAction(ACTION_HEARTBEAT);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pi = PendingIntent.getBroadcast(context, 1001, intent, flags);
        am.cancel(pi);
        Log.d(TAG, "Heartbeat cancelled");
    }
}
