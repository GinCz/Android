package com.gincz.telegrambot;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.gincz.telegrambot.data.ConfigManager;
import com.gincz.telegrambot.data.OfflineQueue;
import com.gincz.telegrambot.service.HeartbeatManager;
import com.gincz.telegrambot.service.NotificationListener;
import com.gincz.telegrambot.transport.KumaPinger;
import com.gincz.telegrambot.transport.TelegramClient;
import com.gincz.telegrambot.util.UpdateManager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends Activity {

    private TextView tvMaskedToken;
    private Button btnEditToken;
    private EditText etChatId;
    private EditText etDeviceName;
    private EditText etSim1Name;
    private EditText etSim1Number;
    private EditText etSim2Name;
    private EditText etSim2Number;
    private EditText etKumaUrl;
    private Switch switchService;
    private TextView tvNotifStatus;
    private TextView tvQueueStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMaskedToken = findViewById(R.id.tv_masked_token);
        btnEditToken = findViewById(R.id.btn_edit_token);
        btnEditToken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditTokenDialog();
            }
        });
        etChatId = findViewById(R.id.et_chat_id);
        etDeviceName = findViewById(R.id.et_device_name);
        etSim1Name = findViewById(R.id.et_sim1_name);
        etSim1Number = findViewById(R.id.et_sim1_number);
        etSim2Name = findViewById(R.id.et_sim2_name);
        etSim2Number = findViewById(R.id.et_sim2_number);
        etKumaUrl = findViewById(R.id.et_kuma_url);
        switchService = findViewById(R.id.switch_service);
        tvNotifStatus = findViewById(R.id.tv_notif_status);
        tvQueueStatus = findViewById(R.id.tv_queue_status);

        Button btnSave = findViewById(R.id.btn_save);
        Button btnTestTg = findViewById(R.id.btn_test_tg);
        Button btnTestKuma = findViewById(R.id.btn_test_kuma);
        Button btnWhitelist = findViewById(R.id.btn_whitelist);
        Button btnNotifPermission = findViewById(R.id.btn_notif_permission);
        Button btnUpdateSignature = findViewById(R.id.btn_update_signature);

        String currentVer = UpdateManager.getCurrentVersionName(this);
        btnUpdateSignature.setText(String.format(getString(R.string.footer_signature), currentVer));
        btnUpdateSignature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateManager.checkForUpdates(MainActivity.this, false);
            }
        });

        loadSettings();
        requestSmsPermissions();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                Toast.makeText(MainActivity.this, "Настройки сохранены!", Toast.LENGTH_SHORT).show();
            }
        });

        btnTestTg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                sendTestTelegramMessage();
            }
        });

        btnTestKuma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                testKumaPing();
            }
        });

        btnNotifPermission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS));
            }
        });

        btnWhitelist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAppWhitelistDialog();
            }
        });

        switchService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigManager.getInstance(MainActivity.this).setServiceActive(isChecked);
                if (isChecked) {
                    HeartbeatManager.scheduleNext(MainActivity.this);
                } else {
                    HeartbeatManager.cancel(MainActivity.this);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateServiceAndQueueStatus();
        Button btnUpdateSignature = findViewById(R.id.btn_update_signature);
        if (btnUpdateSignature != null) {
            String ver = UpdateManager.getCurrentVersionName(this);
            btnUpdateSignature.setText(String.format(getString(R.string.footer_signature), ver));
        }
    }

    private void loadSettings() {
        ConfigManager config = ConfigManager.getInstance(this);
        updateMaskedTokenDisplay();
        etChatId.setText(config.getChatId());
        etDeviceName.setText(config.getDeviceName());
        etSim1Name.setText(config.getSim1Name());
        etSim1Number.setText(config.getSim1Number());
        etSim2Name.setText(config.getSim2Name());
        etSim2Number.setText(config.getSim2Number());
        etKumaUrl.setText(config.getKumaUrl());
        switchService.setChecked(config.isServiceActive());
    }

    private void saveSettings() {
        ConfigManager config = ConfigManager.getInstance(this);
        config.setChatId(etChatId.getText().toString());
        config.setDeviceName(etDeviceName.getText().toString());
        config.setSim1Name(etSim1Name.getText().toString());
        config.setSim1Number(etSim1Number.getText().toString());
        config.setSim2Name(etSim2Name.getText().toString());
        config.setSim2Number(etSim2Number.getText().toString());
        config.setKumaUrl(etKumaUrl.getText().toString());
        config.setServiceActive(switchService.isChecked());

        if (switchService.isChecked()) {
            HeartbeatManager.scheduleNext(this);
        }
    }

    private void updateMaskedTokenDisplay() {
        String token = ConfigManager.getInstance(this).getBotToken();
        tvMaskedToken.setText(maskToken(token));
    }

    private String maskToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return "(ключ не задан)";
        }
        token = token.trim();
        if (token.length() <= 14) {
            return "••••••••••••";
        }
        int prefixLen = Math.min(10, token.length() / 3);
        int suffixLen = Math.min(6, token.length() / 4);
        String prefix = token.substring(0, prefixLen);
        String suffix = token.substring(token.length() - suffixLen);
        return prefix + "••••••••••••" + suffix;
    }

    private void showEditTokenDialog() {
        final ConfigManager config = ConfigManager.getInstance(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Редактирование Telegram Bot Token");

        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
        input.setText(config.getBotToken());
        if (input.getText().length() > 0) {
            input.setSelection(input.getText().length());
        }
        input.setHint("123456789:ABCdefGhIJKlmNoPQRsTUVwxyZ");
        input.setTextSize(15);
        input.setPadding(36, 28, 36, 28);

        builder.setView(input);
        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newToken = input.getText().toString().trim();
                config.setBotToken(newToken);
                updateMaskedTokenDisplay();
                Toast.makeText(MainActivity.this, "Токен бота сохранен!", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void updateServiceAndQueueStatus() {
        boolean isGranted = isNotificationServiceEnabled();
        if (isGranted) {
            tvNotifStatus.setText(R.string.notif_status_granted);
            tvNotifStatus.setTextColor(getResources().getColor(R.color.status_green));
        } else {
            tvNotifStatus.setText(R.string.notif_status_denied);
            tvNotifStatus.setTextColor(getResources().getColor(R.color.status_red));
        }

        int queueCount = OfflineQueue.getInstance(this).getCount();
        tvQueueStatus.setText("Очередь офлайн-сообщений: " + queueCount + " шт.");
    }

    private boolean isNotificationServiceEnabled() {
        String pkgName = getPackageName();
        final String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
        if (!TextUtils.isEmpty(flat)) {
            final String[] names = flat.split(":");
            for (String name : names) {
                final ComponentName cn = ComponentName.unflattenFromString(name);
                if (cn != null && TextUtils.equals(pkgName, cn.getPackageName())) {
                    return true;
                }
            }
        }
        return false;
    }

    private void requestSmsPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] perms = {
                    "android.permission.RECEIVE_SMS",
                    "android.permission.READ_SMS",
                    "android.permission.READ_PHONE_STATE"
            };
            List<String> needed = new ArrayList<>();
            for (String p : perms) {
                if (checkSelfPermission(p) != PackageManager.PERMISSION_GRANTED) {
                    needed.add(p);
                }
            }
            if (!needed.isEmpty()) {
                requestPermissions(needed.toArray(new String[0]), 101);
            }
        }
    }

    private void sendTestTelegramMessage() {
        ConfigManager cfg = ConfigManager.getInstance(this);
        String deviceName = cfg.getDeviceName();
        String sim1 = cfg.getSim1Name();
        String sim1Num = cfg.getSim1Number();
        String sim2 = cfg.getSim2Name();
        String sim2Num = cfg.getSim2Number();

        StringBuilder sb = new StringBuilder();
        sb.append("🚀 <b>[").append(TelegramClient.escapeHtml(deviceName)).append(" • Тест связи]</b>\n");
        sb.append("Шлюз GIN-Telegram-Bot успешно настроен и готов к приёму входящих сообщений.\n\n");
        sb.append("<b>Конфигурация SIM:</b>\n");
        sb.append("• <b>SIM 1:</b> ").append(TelegramClient.escapeHtml(sim1));
        if (sim1Num != null && !sim1Num.trim().isEmpty()) {
            sb.append(" (<code>").append(TelegramClient.escapeHtml(sim1Num.trim())).append("</code>)");
        }
        sb.append("\n• <b>SIM 2:</b> ").append(TelegramClient.escapeHtml(sim2));
        if (sim2Num != null && !sim2Num.trim().isEmpty()) {
            sb.append(" (<code>").append(TelegramClient.escapeHtml(sim2Num.trim())).append("</code>)");
        }
        sb.append("\n\n<b>Статус:</b> Связь с Telegram Bot API подтверждена.");

        String html = sb.toString();

        Toast.makeText(this, "Отправка тестового сообщения...", Toast.LENGTH_SHORT).show();
        TelegramClient.sendAsync(this, html, false, new TelegramClient.SendCallback() {
            @Override
            public void onResult(final boolean success, final String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            Toast.makeText(MainActivity.this, "✅ Сообщение доставлено в Telegram!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "❌ Ошибка отправки: " + (error != null ? error : "Неизвестно"), Toast.LENGTH_LONG).show();
                        }
                        updateServiceAndQueueStatus();
                    }
                });
            }
        });
    }

    private void testKumaPing() {
        Toast.makeText(this, "Отправка пинга в Uptime Kuma...", Toast.LENGTH_SHORT).show();
        KumaPinger.pingAsync(this, new KumaPinger.PingCallback() {
            @Override
            public void onResult(final boolean success, final String statusInfo) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (success) {
                            Toast.makeText(MainActivity.this, "✅ Uptime Kuma: " + statusInfo, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "❌ Uptime Kuma пинг не удался", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    // App Whitelist Dialog
    private static class AppItem {
        final String label;
        final String packageName;
        final Drawable icon;
        boolean selected;

        AppItem(String label, String packageName, Drawable icon, boolean selected) {
            this.label = label;
            this.packageName = packageName;
            this.icon = icon;
            this.selected = selected;
        }
    }

    private void showAppWhitelistDialog() {
        final PackageManager pm = getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        final ConfigManager config = ConfigManager.getInstance(this);
        final Set<String> currentWhitelist = config.getWhitelistedPackages();

        final List<AppItem> appItems = new ArrayList<>();
        for (ApplicationInfo ai : packages) {
            // Ignore own app
            if (ai.packageName.equals(getPackageName())) continue;

            String label = pm.getApplicationLabel(ai).toString();
            Drawable icon = pm.getApplicationIcon(ai);
            boolean isChecked = currentWhitelist.contains(ai.packageName);
            appItems.add(new AppItem(label, ai.packageName, icon, isChecked));
        }

        // Sort: checked first, then alphabetically
        Collections.sort(appItems, new Comparator<AppItem>() {
            @Override
            public int compare(AppItem o1, AppItem o2) {
                if (o1.selected != o2.selected) {
                    return o1.selected ? -1 : 1;
                }
                return o1.label.compareToIgnoreCase(o2.label);
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Выбор приложений для пересылки");

        ListView listView = new ListView(this);
        final BaseAdapter adapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return appItems.size();
            }

            @Override
            public Object getItem(int position) {
                return appItems.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.item_app_whitelist, parent, false);
                }
                final AppItem item = appItems.get(position);
                ImageView ivIcon = convertView.findViewById(R.id.iv_app_icon);
                TextView tvLabel = convertView.findViewById(R.id.tv_app_label);
                TextView tvPackage = convertView.findViewById(R.id.tv_app_package);
                CheckBox cbSelected = convertView.findViewById(R.id.cb_app_selected);

                ivIcon.setImageDrawable(item.icon);
                tvLabel.setText(item.label);
                tvPackage.setText(item.packageName);
                cbSelected.setChecked(item.selected);

                return convertView;
            }
        };

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AppItem item = appItems.get(position);
                item.selected = !item.selected;
                adapter.notifyDataSetChanged();
            }
        });

        builder.setView(listView);
        builder.setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Set<String> newWhitelist = new HashSet<>();
                for (AppItem item : appItems) {
                    if (item.selected) {
                        newWhitelist.add(item.packageName);
                    }
                }
                config.setWhitelistedPackages(newWhitelist);
                Toast.makeText(MainActivity.this, "Выбрано приложений: " + newWhitelist.size(), Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }
}
