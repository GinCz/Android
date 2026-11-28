package com.gincz.telegrambot.transport;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.gincz.telegrambot.data.ConfigManager;
import com.gincz.telegrambot.data.OfflineQueue;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TelegramClient {
    private static final String TAG = "GIN-TelegramClient";
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    public interface SendCallback {
        void onResult(boolean success, String error);
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnected();
        }
        return false;
    }

    public static void sendAsync(final Context context, final String htmlMessage, final boolean canEnqueue, final SendCallback callback) {
        android.os.PowerManager pm = (android.os.PowerManager) context.getSystemService(Context.POWER_SERVICE);
        final android.os.PowerManager.WakeLock wakeLock = (pm != null) ?
                pm.newWakeLock(android.os.PowerManager.PARTIAL_WAKE_LOCK, "GIN-TelegramBot:SendAsyncWakeLock") : null;
        if (wakeLock != null) {
            try {
                wakeLock.acquire(10000);
            } catch (Exception ignored) {}
        }

        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean success = sendSync(context, htmlMessage, canEnqueue);
                    if (callback != null) {
                        callback.onResult(success, success ? null : "Network error or offline");
                    }
                } finally {
                    if (wakeLock != null && wakeLock.isHeld()) {
                        try {
                            wakeLock.release();
                        } catch (Exception ignored) {}
                    }
                }
            }
        });
    }

    public static boolean sendSync(Context context, String htmlMessage, boolean canEnqueue) {
        ConfigManager config = ConfigManager.getInstance(context);
        String botToken = config.getBotToken();
        String chatId = config.getChatId();

        if (botToken.isEmpty() || chatId.isEmpty()) {
            Log.w(TAG, "Cannot send: Telegram Bot Token or Chat ID is empty");
            if (canEnqueue) {
                OfflineQueue.getInstance(context).enqueue(htmlMessage);
            }
            return false;
        }

        if (!isNetworkAvailable(context)) {
            Log.w(TAG, "No network connection. Enqueueing message");
            if (canEnqueue) {
                OfflineQueue.getInstance(context).enqueue(htmlMessage);
            }
            return false;
        }

        HttpURLConnection conn = null;
        try {
            URL url = new URL("https://api.telegram.org/bot" + botToken + "/sendMessage");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(6000);
            conn.setReadTimeout(6000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");

            String postData = "chat_id=" + URLEncoder.encode(chatId, "UTF-8") +
                    "&parse_mode=HTML" +
                    "&disable_web_page_preview=true" +
                    "&text=" + URLEncoder.encode(htmlMessage, "UTF-8");

            byte[] postDataBytes = postData.getBytes(StandardCharsets.UTF_8);
            conn.setFixedLengthStreamingMode(postDataBytes.length);

            OutputStream os = conn.getOutputStream();
            os.write(postDataBytes);
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                Log.i(TAG, "Telegram message sent successfully");
                // Attempt to flush offline queue if there are pending messages
                flushQueueAsync(context);
                return true;
            } else {
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                Log.e(TAG, "Telegram API error (" + responseCode + "): " + sb.toString());
                if (canEnqueue) {
                    OfflineQueue.getInstance(context).enqueue(htmlMessage);
                }
                return false;
            }
        } catch (Exception e) {
            Log.e(TAG, "Network exception sending to Telegram: " + e.getMessage());
            if (canEnqueue) {
                OfflineQueue.getInstance(context).enqueue(htmlMessage);
            }
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static void flushQueueAsync(final Context context) {
        EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                flushQueueSync(context);
            }
        });
    }

    public static void flushQueueSync(Context context) {
        if (!isNetworkAvailable(context)) return;
        OfflineQueue queue = OfflineQueue.getInstance(context);
        int pending = queue.getCount();
        if (pending == 0) return;

        Log.i(TAG, "Flushing " + pending + " pending messages from offline queue...");
        List<OfflineQueue.QueueItem> items = queue.peekAll(20);
        for (OfflineQueue.QueueItem item : items) {
            boolean sent = sendSync(context, item.message, false);
            if (sent) {
                queue.delete(item.id);
            } else {
                Log.w(TAG, "Failed to send queued item " + item.id + ", pausing flush");
                break;
            }
            try {
                Thread.sleep(300); // Prevent hitting Telegram rate limits
            } catch (InterruptedException ignored) {}
        }
    }

    public static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;");
    }
}
