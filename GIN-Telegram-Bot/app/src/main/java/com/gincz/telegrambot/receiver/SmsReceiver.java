package com.gincz.telegrambot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.telephony.SmsMessage;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import com.gincz.telegrambot.data.ConfigManager;
import com.gincz.telegrambot.transport.TelegramClient;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "GIN-SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || !"android.provider.Telephony.SMS_RECEIVED".equals(intent.getAction())) {
            return;
        }

        ConfigManager config = ConfigManager.getInstance(context);
        if (!config.isServiceActive()) {
            Log.d(TAG, "Service is disabled in settings, ignoring SMS");
            return;
        }

        // Acquire WakeLock for 5 seconds to ensure network dispatch finishes
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = null;
        if (pm != null) {
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GIN-TelegramBot:SmsWakeLock");
            wakeLock.acquire(5000);
        }

        try {
            Bundle bundle = intent.getExtras();
            if (bundle == null) return;

            Object[] pdus = (Object[]) bundle.get("pdus");
            if (pdus == null || pdus.length == 0) return;

            String format = bundle.getString("format");

            // Extract SIM Slot / Operator
            int slotIndex = detectSimSlot(context, intent, bundle);
            String simName = (slotIndex == 1) ? config.getSim2Name() : config.getSim1Name();
            String simNumber = (slotIndex == 1) ? config.getSim2Number() : config.getSim1Number();
            String slotTag = (slotIndex == 1) ? "SIM 2" : "SIM 1";

            String simLabel;
            if (simName != null && !simName.trim().isEmpty()) {
                simLabel = "[" + slotTag + " • " + simName.trim() + "]";
            } else {
                simLabel = "[" + slotTag + "]";
            }

            // Concatenate multi-part SMS messages by sender
            Map<String, StringBuilder> messagesBySender = new HashMap<>();
            long timestamp = System.currentTimeMillis();

            for (Object pdu : pdus) {
                SmsMessage sms;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    sms = SmsMessage.createFromPdu((byte[]) pdu, format);
                } else {
                    sms = SmsMessage.createFromPdu((byte[]) pdu);
                }

                if (sms != null) {
                    String sender = sms.getDisplayOriginatingAddress();
                    if (sender == null) sender = "Unknown";
                    String body = sms.getDisplayMessageBody();
                    if (body == null) body = "";

                    timestamp = sms.getTimestampMillis();
                    if (timestamp <= 0) timestamp = System.currentTimeMillis();

                    StringBuilder sb = messagesBySender.get(sender);
                    if (sb == null) {
                        sb = new StringBuilder();
                        messagesBySender.put(sender, sb);
                    }
                    sb.append(body);
                }
            }

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String timeStr = timeFormat.format(new Date(timestamp));

            for (Map.Entry<String, StringBuilder> entry : messagesBySender.entrySet()) {
                String sender = entry.getKey();
                String fullText = entry.getValue().toString();

                StringBuilder html = new StringBuilder();
                html.append("📩 <b>").append(TelegramClient.escapeHtml(simLabel)).append("</b> <i>").append(timeStr).append("</i>\n");

                if (simNumber != null && !simNumber.trim().isEmpty()) {
                    html.append("<b>На номер:</b> <code>").append(TelegramClient.escapeHtml(simNumber.trim())).append("</code>\n");
                }

                html.append("<b>От:</b> <code>").append(TelegramClient.escapeHtml(sender)).append("</code>\n\n");
                html.append(TelegramClient.escapeHtml(fullText));

                Log.i(TAG, "Forwarding SMS from " + sender + " (" + simLabel + (simNumber != null && !simNumber.trim().isEmpty() ? " -> " + simNumber.trim() : "") + ")");
                TelegramClient.sendAsync(context, html.toString(), true, null);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error handling incoming SMS: " + e.getMessage(), e);
        } finally {
            if (wakeLock != null && wakeLock.isHeld()) {
                try {
                    wakeLock.release();
                } catch (Exception ignored) {}
            }
        }
    }

    private int detectSimSlot(Context context, Intent intent, Bundle bundle) {
        // Method 1: Check MediaTek & Standard Intent Extras
        int slot = -1;
        String[] slotKeys = {"slot", "simId", "sim_id", "sim_slot", "phone", "slot_id", "slot_idx"};
        for (String key : slotKeys) {
            if (bundle.containsKey(key)) {
                try {
                    slot = bundle.getInt(key, -1);
                    if (slot >= 0) {
                        Log.d(TAG, "Detected SIM slot via bundle key '" + key + "': " + slot);
                        return slot;
                    }
                } catch (Exception ignored) {}
            }
        }

        // Method 2: Android 5.1+ (API 22) SubscriptionManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                int subId = bundle.getInt("subscription", -1);
                if (subId == -1) subId = bundle.getInt("sub_id", -1);
                if (subId >= 0) {
                    SubscriptionManager sm = SubscriptionManager.from(context);
                    SubscriptionInfo subInfo = sm.getActiveSubscriptionInfo(subId);
                    if (subInfo != null) {
                        slot = subInfo.getSimSlotIndex();
                        Log.d(TAG, "Detected SIM slot via SubscriptionInfo: " + slot);
                        return slot;
                    }
                }
            } catch (SecurityException ignored) {
                // If READ_PHONE_STATE is missing or denied
            } catch (Exception ignored) {}
        }

        // Fallback: Default to SIM 1 (slot 0)
        return 0;
    }
}
