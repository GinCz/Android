package com.gincz.telegrambot.data;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ConfigManager {
    private static final String PREF_NAME = "gin_tg_bot_prefs";
    private static ConfigManager instance;
    private final SharedPreferences prefs;

    private static final String KEY_BOT_TOKEN = "key_bot_token";
    private static final String KEY_CHAT_ID = "key_chat_id";
    private static final String KEY_DEVICE_NAME = "key_device_name";
    private static final String KEY_KUMA_URL = "key_kuma_url";
    private static final String KEY_KUMA_INTERVAL = "key_kuma_interval";
    private static final String KEY_WHITELIST = "key_whitelisted_apps";
    private static final String KEY_SIM1_NAME = "key_sim1_name";
    private static final String KEY_SIM2_NAME = "key_sim2_name";
    private static final String KEY_SERVICE_ACTIVE = "key_service_active";

    private ConfigManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized ConfigManager getInstance(Context context) {
        if (instance == null) {
            instance = new ConfigManager(context);
        }
        return instance;
    }

    public String getBotToken() {
        return prefs.getString(KEY_BOT_TOKEN, "");
    }

    public void setBotToken(String token) {
        prefs.edit().putString(KEY_BOT_TOKEN, token != null ? token.trim() : "").apply();
    }

    public String getChatId() {
        return prefs.getString(KEY_CHAT_ID, "");
    }

    public void setChatId(String chatId) {
        prefs.edit().putString(KEY_CHAT_ID, chatId != null ? chatId.trim() : "").apply();
    }

    public String getDeviceName() {
        return prefs.getString(KEY_DEVICE_NAME, "DOOGEE X3");
    }

    public void setDeviceName(String name) {
        prefs.edit().putString(KEY_DEVICE_NAME, name != null ? name.trim() : "DOOGEE X3").apply();
    }

    public String getKumaUrl() {
        return prefs.getString(KEY_KUMA_URL, "");
    }

    public void setKumaUrl(String url) {
        prefs.edit().putString(KEY_KUMA_URL, url != null ? url.trim() : "").apply();
    }

    public int getKumaIntervalMinutes() {
        return prefs.getInt(KEY_KUMA_INTERVAL, 15);
    }

    public void setKumaIntervalMinutes(int minutes) {
        prefs.edit().putInt(KEY_KUMA_INTERVAL, Math.max(5, minutes)).apply();
    }

    public String getSim1Name() {
        return prefs.getString(KEY_SIM1_NAME, "МТС");
    }

    public void setSim1Name(String name) {
        prefs.edit().putString(KEY_SIM1_NAME, name != null ? name.trim() : "МТС").apply();
    }

    public String getSim2Name() {
        return prefs.getString(KEY_SIM2_NAME, "МегаФон");
    }

    public void setSim2Name(String name) {
        prefs.edit().putString(KEY_SIM2_NAME, name != null ? name.trim() : "МегаФон").apply();
    }

    public boolean isServiceActive() {
        return prefs.getBoolean(KEY_SERVICE_ACTIVE, true);
    }

    public void setServiceActive(boolean active) {
        prefs.edit().putBoolean(KEY_SERVICE_ACTIVE, active).apply();
    }

    public Set<String> getWhitelistedPackages() {
        Set<String> set = prefs.getStringSet(KEY_WHITELIST, null);
        if (set == null) {
            Set<String> defaults = new HashSet<>();
            // Popular Russian banks by default
            defaults.add("ru.tinkoff.activities");
            defaults.add("com.idamob.tinkoff.android");
            defaults.add("ru.sberbankmobile");
            defaults.add("ru.vtb24.mobilebanking");
            defaults.add("ru.alfabank.mobile.android");
            defaults.add("ru.raiffeisennews");
            defaults.add("ru.gazprombank.android.mobilebank.app");
            defaults.add("ru.sberbank.sberid");
            defaults.add("ru.gosuslugi.eskiapp");
            return defaults;
        }
        return new HashSet<>(set);
    }

    public void setWhitelistedPackages(Set<String> packages) {
        prefs.edit().putStringSet(KEY_WHITELIST, new HashSet<>(packages)).apply();
    }

    public boolean isPackageWhitelisted(String packageName) {
        if (packageName == null) return false;
        return getWhitelistedPackages().contains(packageName);
    }
}
