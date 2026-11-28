package com.gincz.telegrambot.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Native On-Demand Update Engine for GIN-Telegram-Bot.
 * Features:
 * - Redundant high-availability JSON endpoints (gincz.com, ru-tv.eu)
 * - Version comparison with current package metadata
 * - Background APK downloading with ProgressDialog
 * - Native package installation supporting Android 5.0 through 15+
 * - Zero external libraries / 100% native Android SDK
 */
public class UpdateManager {

    private static final String TAG = "GIN-UpdateManager";

    private static final String[] MANIFEST_URLS = new String[]{
            "https://gincz.com/gin_telegram_bot_version.json",
            "https://eco-seo.cz/apk/telegram_bot/version.json"
    };

    public static class UpdateInfo {
        public final int versionCode;
        public final String versionName;
        public final String apkUrl;
        public final String changelog;

        public UpdateInfo(int versionCode, String versionName, String apkUrl, String changelog) {
            this.versionCode = versionCode;
            this.versionName = versionName;
            this.apkUrl = apkUrl;
            this.changelog = changelog;
        }
    }

    public static void checkForUpdates(final Activity activity, final boolean silent) {
        if (!silent) {
            Toast.makeText(activity, "Проверка обновлений GIN-Telegram-Bot...", Toast.LENGTH_SHORT).show();
        }

        final Handler mainHandler = new Handler(Looper.getMainLooper());

        new Thread(new Runnable() {
            @Override
            public void run() {
                final UpdateInfo updateInfo = fetchManifest();

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (activity == null || activity.isFinishing()) {
                            return;
                        }

                        if (updateInfo == null) {
                            if (!silent) {
                                Toast.makeText(activity, "Не удалось проверить обновления: проверьте сеть", Toast.LENGTH_LONG).show();
                            }
                            return;
                        }

                        long currentCode = getCurrentVersionCode(activity);
                        String currentName = getCurrentVersionName(activity);

                        if (updateInfo.versionCode > currentCode) {
                            showUpdateDialog(activity, updateInfo);
                        } else {
                            if (!silent) {
                                Toast.makeText(activity, "У вас установлена последняя версия (" + currentName + ")", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private static UpdateInfo fetchManifest() {
        long timestamp = System.currentTimeMillis();

        for (String baseEndpoint : MANIFEST_URLS) {
            HttpURLConnection conn = null;
            try {
                String sep = baseEndpoint.contains("?") ? "&" : "?";
                URL url = new URL(baseEndpoint + sep + "t=" + timestamp);
                conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(6000);
                conn.setReadTimeout(6000);
                conn.setUseCaches(false);
                conn.setRequestProperty("Cache-Control", "no-cache, no-store, must-revalidate");
                conn.setRequestProperty("Pragma", "no-cache");
                conn.setInstanceFollowRedirects(true);

                int code = conn.getResponseCode();
                if (code >= 200 && code < 300) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    reader.close();

                    JSONObject json = new JSONObject(sb.toString());
                    int vCode = json.optInt("versionCode", 0);
                    String vName = json.optString("versionName", "v" + vCode);
                    String apkUrl = json.optString("apkUrl", "");
                    String changelog = json.optString("changelog", "");

                    if (vCode > 0 && !apkUrl.isEmpty()) {
                        return new UpdateInfo(vCode, vName, apkUrl, changelog);
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, "Endpoint " + baseEndpoint + " failed: " + e.getMessage());
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
        }
        return null;
    }

    private static void showUpdateDialog(final Activity activity, final UpdateInfo updateInfo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Доступно обновление " + updateInfo.versionName);

        String message = (updateInfo.changelog != null && !updateInfo.changelog.trim().isEmpty())
                ? updateInfo.changelog + "\n\nОбновить сейчас?"
                : "Найдена новая версия " + updateInfo.versionName + ". Обновить сейчас?";

        builder.setMessage(message);
        builder.setPositiveButton("Обновить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startDownloadAndInstall(activity, updateInfo);
            }
        });
        builder.setNegativeButton("Позже", null);
        builder.setCancelable(true);
        builder.show();
    }

    private static void startDownloadAndInstall(final Activity activity, final UpdateInfo updateInfo) {
        final ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setTitle("Загрузка обновления");
        progressDialog.setMessage("Скачивание " + updateInfo.versionName + "...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final Handler mainHandler = new Handler(Looper.getMainLooper());

        new Thread(new Runnable() {
            @Override
            public void run() {
                File apkFile = null;
                HttpURLConnection conn = null;
                InputStream in = null;
                FileOutputStream out = null;
                boolean success = false;
                String errorMsg = null;

                try {
                    File extDir = activity.getExternalCacheDir();
                    File updatesDir = (extDir != null) ? new File(extDir, "updates") : new File(activity.getCacheDir(), "updates");
                    if (!updatesDir.exists()) {
                        updatesDir.mkdirs();
                    }
                    updatesDir.setReadable(true, false);
                    updatesDir.setExecutable(true, false);

                    apkFile = new File(updatesDir, "GIN-Telegram-Bot_" + updateInfo.versionName + ".apk");
                    if (apkFile.exists()) {
                        apkFile.delete();
                    }

                    String sep = updateInfo.apkUrl.contains("?") ? "&" : "?";
                    URL url = new URL(updateInfo.apkUrl + sep + "t=" + System.currentTimeMillis());
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(10000);
                    conn.setReadTimeout(30000);
                    conn.setInstanceFollowRedirects(true);
                    conn.setUseCaches(false);

                    int respCode = conn.getResponseCode();
                    if (respCode < 200 || respCode >= 300) {
                        throw new IllegalStateException("HTTP error " + respCode);
                    }

                    int contentLength = conn.getContentLength();
                    in = conn.getInputStream();
                    out = new FileOutputStream(apkFile);

                    byte[] buffer = new byte[8192];
                    int bytesRead;
                    long totalRead = 0;

                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                        totalRead += bytesRead;
                        if (contentLength > 0) {
                            final int percent = (int) ((totalRead * 100) / contentLength);
                            mainHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (progressDialog.isShowing()) {
                                        progressDialog.setProgress(percent);
                                    }
                                }
                            });
                        }
                    }
                    out.flush();
                    success = apkFile.length() > 5000;
                    if (success) {
                        apkFile.setReadable(true, false);
                    }
                } catch (Exception e) {
                    errorMsg = e.getMessage();
                    Log.e(TAG, "Download error", e);
                } finally {
                    try { if (out != null) out.close(); } catch (Exception ignored) {}
                    try { if (in != null) in.close(); } catch (Exception ignored) {}
                    if (conn != null) conn.disconnect();
                }

                final boolean finalSuccess = success;
                final File finalApkFile = apkFile;
                final String finalErrorMsg = errorMsg;

                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }

                        if (finalSuccess && finalApkFile != null && finalApkFile.exists()) {
                            installApk(activity, finalApkFile);
                        } else {
                            Toast.makeText(activity, "Ошибка скачивания: " + (finalErrorMsg != null ? finalErrorMsg : "Неизвестная ошибка"), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }

    public static void installApk(Activity activity, File apkFile) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!activity.getPackageManager().canRequestPackageInstalls()) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                            Uri.parse("package:" + activity.getPackageName()));
                    activity.startActivity(intent);
                    Toast.makeText(activity, "Разрешите установку из этого источника для обновления", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            apkFile.setReadable(true, false);
            File parent = apkFile.getParentFile();
            if (parent != null) {
                parent.setReadable(true, false);
                parent.setExecutable(true, false);
            }

            Uri apkUri = GenericFileProvider.getUriForFile(activity, apkFile);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Install error", e);
            Toast.makeText(activity, "Ошибка запуска установки: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static long getCurrentVersionCode(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                return pInfo.getLongVersionCode();
            } else {
                return pInfo.versionCode;
            }
        } catch (Exception e) {
            return 1;
        }
    }

    public static String getCurrentVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName != null ? pInfo.versionName : "v001";
        } catch (Exception e) {
            return "v001";
        }
    }
}
