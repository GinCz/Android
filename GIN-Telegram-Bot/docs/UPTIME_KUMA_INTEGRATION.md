# 💓 Uptime Kuma Push Monitor Integration (Heartbeat Gateway)

> **Continuous health monitoring and telemetry reporting for the SMS & Notification Gateway via Uptime Kuma.**

---

## 1. Push Monitor Architecture

Because the Android gateway runs behind carrier NAT and residential Wi-Fi, incoming polling is unfeasible. We leverage Uptime Kuma's **Push Monitor** mechanism:

```
+------------------------------------+          HTTPS GET / POST           +---------------------------------+
|      Android Gateway Device        |  -------------------------------->  |       Uptime Kuma Server        |
|  (AlarmManager: Every 15 minutes)  |   Push URL + Heartbeat Telemetry   |     (kuma.yourdomain.com)       |
+------------------------------------+                                     +---------------------------------+
                                                                                           |
                                                                         If no ping received within 20 mins:
                                                                                           v
                                                                           🔴 Instant Alert to Administrator:
                                                                              "SMS Gateway is DOWN"
```

1. Create a monitor with type **Push** in your Uptime Kuma dashboard.
2. Configure the heartbeat interval (e.g. 900 seconds / 15 minutes).
3. Copy the generated push URL into GIN-Telegram-Bot:
   ```
   https://kuma.yourdomain.com/api/push/<MONITOR_TOKEN>?status=up&msg=OK&ping=
   ```

---

## 2. Telemetry Parameters

Each heartbeat request sends diagnostic metrics in the query string:

| Parameter | Type | Purpose | Example Value |
|---|---|---|---|
| `status` | string | Gateway health state | `up` |
| `msg` | string | Compact diagnostic summary | `OK \| Bat: 100% (AC) \| WiFi: -42dBm \| Q: 0` |
| `ping` | integer | Network round-trip latency (ms) | `38` |

### Full URL Payload Example:
```
https://kuma.yourdomain.com/api/push/sampleToken123?status=up&msg=OK+Bat:100%25(AC)+WiFi:-42dBm+Q:0&ping=38
```

---

## 3. Battery & Wi-Fi Metrics Collection

```java
// Battery level & power source
Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
int pct = (int) ((level / (float) scale) * 100);

// Wi-Fi signal strength
WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
int rssi = wifiManager.getConnectionInfo().getRssi();
```

---

## 🏷 Topics & Tags

#UptimeKuma #Heartbeat #PushMonitor #ServerMonitoring #AndroidTelemetry #DevOps #HighAvailability
