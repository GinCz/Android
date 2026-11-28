# 📱 GIN-Telegram-Bot: Ultra-Lightweight Android SMS & Push Gateway

<div align="center">

![Platform](https://img.shields.io/badge/Platform-Android%205.0+-3DDC84?logo=android&logoColor=white)
![Release](https://img.shields.io/badge/Release-v006-brightgreen)
![APK Size](https://img.shields.io/badge/APK%20Size-30%20KB-orange)
![RAM Footprint](https://img.shields.io/badge/RAM-3--5%20MB-blue)
![External Deps](https://img.shields.io/badge/External%20Dependencies-0%20(Zero)-success)
![License](https://img.shields.io/badge/License-MIT%20%7C%20Open%20Source-blue)

**High-performance, ultra-compact native Android application that forwards incoming SMS messages and financial push notifications directly to Telegram in real time, featuring integrated Uptime Kuma heartbeat monitoring.**

[Key Features](#-key-features) • [Comparison](#-comparison-with-legacy-bots) • [Architecture](docs/ARCHITECTURE.md) • [Telegram Spec](docs/TELEGRAM_SPEC.md) • [Heartbeat Monitor](docs/UPTIME_KUMA_INTEGRATION.md) • [Changelog](CHANGELOG.md)

</div>

---

## 🎯 Purpose & Overview

**GIN-Telegram-Bot** was engineered from scratch to replace bloated, unstable legacy background bots (such as Remote Bot Pro, which exceeded 45 MB and ran 160+ background threads with advertising SDKs).

Built strictly on native Android APIs and optimized with Google R8, the entire production APK weighs just **30 KB** and consumes **< 5 MB of RAM**, running reliably 24/7 on dedicated gateway devices even on minimal hardware (e.g. Android 5.1 / 1 GB RAM).

---

## 🌟 Key Features

* **⚡ Real-Time SMS Interception:** Highest-priority broadcast receiver (`priority=2147483647`) catches incoming SMS instantly and forwards formatted text to Telegram via HTTPS in under 300 ms.
* **📶 Dual-SIM Identification:** Automatically detects active SIM slot index via Android `SubscriptionManager` and MediaTek vendor hardware extras, tagging notifications as `[SIM 1 • Operator A]` or `[SIM 2 • Operator B]`.
* **🔔 Bank & 2FA Push Notification Forwarding:** Uses Android's `NotificationListenerService` to capture and forward banking transactions, OTP codes, and government alerts with a custom app whitelist and 30-second deduplication engine.
* **💓 Integrated Uptime Kuma Heartbeat:** Automatically pings your self-hosted Uptime Kuma push monitor every 15 minutes, transmitting battery level, charging status, Wi-Fi signal strength (RSSI), and queue depth.
* **🛡 Resilient Offline Queue:** When Wi-Fi or cellular connectivity drops, incoming messages are safely buffered in a lightweight local SQLite database and automatically flushed to Telegram once connection is restored.
* **🪶 Ultra-Low Resource Usage:** Record-breaking **30 KB APK size**, zero external libraries, zero analytics, zero Google Play Services dependencies.

---

## 💎 Comparison with Legacy Bots

| Feature | Legacy Solutions (e.g. Remote Bot Pro) | GIN-Telegram-Bot v001 |
|---|---|---|
| **APK Size** | **45.5 MB** | **30 KB** (1,500x smaller!) |
| **RAM Consumption** | ~26–35 MB (160+ threads) | **~3–5 MB** (1–2 threads) |
| **Third-Party Bloat** | Ads, Analytics, Firebase, Crashlytics | **0% Bloat** (Clean Native SDK) |
| **Network Architecture** | Long Polling `getUpdates` (drains battery) | **Event-Driven Push** (instant HTTPS POST) |
| **Dual-SIM Support** | Unreliable / Incomplete | **Full Dual-SIM Slot Tagging** |
| **Health Telemetry** | None | **Uptime Kuma Push Monitor** |
| **Latency** | 2–8 seconds | **< 300 milliseconds** |

---

## ⚙️ Quick Start & Configuration

### 1. Build & Installation
```bash
# Clone repository
git clone https://github.com/GinCz/Android.git
cd Android/GIN-Telegram-Bot

# Build Release APK
./gradlew assembleRelease

# Install via ADB to connected device
adb install -r -d app/build/outputs/apk/release/app-release.apk
```

### 2. Required Permissions
Upon installation, grant the following permissions:
- **SMS & Phone State:** For reading incoming SMS and determining active SIM slot.
- **Notification Access:** Under `Settings > Special App Access > Notification Access`, enable `GIN-Telegram-Bot`.
- **Battery Optimization:** Whitelist the application from battery saver / background restrictions.

### 3. In-App Configuration
Launch the app and enter:
* **Telegram Bot Token:** Your bot token from `@BotFather` (e.g. `123456789:ABCdefGhIJKlmNoPQRsTUVwxyZ`).
* **Telegram Chat ID:** Your numeric chat ID (e.g. `123456789`).
* **SIM Labels:** Custom names for SIM 1 and SIM 2 (e.g. `Carrier 1` and `Carrier 2`).
* **Uptime Kuma Push URL:** *(Optional)* Your Kuma push monitor URL.
* Tap **Save Settings** and then **Test Telegram** to verify connectivity.

---

## 📁 Repository Structure

```
GIN-Telegram-Bot/
├── app/                               # Native Android application module
│   ├── src/main/
│   │   ├── java/com/gincz/telegrambot/
│   │   │   ├── MainActivity.java      # Settings UI & test trigger interface
│   │   │   ├── data/                  # ConfigManager & OfflineQueue (SQLite)
│   │   │   ├── receiver/              # SmsReceiver & BootReceiver
│   │   │   ├── service/               # NotificationListener & HeartbeatManager
│   │   │   └── transport/             # TelegramClient & KumaPinger
│   │   ├── res/                       # Minimal layout & string resources
│   │   └── AndroidManifest.xml        # Minimal required permissions
│   ├── build.gradle.kts               # Module build configuration with R8 shrinking
│   └── proguard-rules.pro             # Proguard / R8 optimization rules
├── docs/                              # Technical documentation
│   ├── ARCHITECTURE.md                # System component architecture & data flow
│   ├── TELEGRAM_SPEC.md               # HTML message templates & payload schemas
│   └── UPTIME_KUMA_INTEGRATION.md     # Push monitor heartbeat setup
├── CHANGELOG.md                       # Release notes and version history
├── build.gradle.kts                   # Root build script
├── settings.gradle.kts                # Project settings
└── README.md                          # Project documentation
```

---

## 🔒 Security & Privacy Notice

* **Direct End-to-End Delivery:** Messages are transmitted directly from the device to official Telegram API servers (`https://api.telegram.org`) over TLS 1.2+. No intermediary proxy servers or third-party cloud services are involved.
* **Zero Analytics:** The application does not collect, track, or transmit any user analytics, device identifiers, or diagnostics to external servers.

---

## 🏷 Topics & Hashtags

#Android #TelegramBot #SMSGateway #PushGateway #NotificationListener #DualSIM #UptimeKuma #DevOps #MicroServices #OpenSource #Kotlin #Java #Minimalism #ZeroBloat
