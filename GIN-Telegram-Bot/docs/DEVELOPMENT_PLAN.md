# 📋 Development Roadmap — GIN-Telegram-Bot

> **Milestones and engineering roadmap: from architecture design to field deployment.**

---

## Phase 1: Environment & Gradle Scaffolding
- [x] **1.1.** Gradle project setup supporting Android 5.0 Lollipop (`minSdkVersion 21`) up to modern Android (`compileSdk 35`).
- [x] **1.2.** Lean `build.gradle.kts` configuration with zero heavy external dependencies, enforcing Google R8 full-mode optimization for an ultra-compact ~30 KB binary.
- [x] **1.3.** Continuous delivery scripts with automated SHA-256 verification and Samba / Cloud mirror distribution.
- [x] **1.4.** Minimal `AndroidManifest.xml` declaring strictly essential runtime permissions:
  - `RECEIVE_BOOT_COMPLETED` (gateway auto-start upon device boot);
  - `RECEIVE_SMS`, `READ_SMS`, `READ_PHONE_STATE` (SMS interception and Dual-SIM carrier identification);
  - `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE` (direct Telegram HTTPS dispatch and Kuma heartbeat);
  - `BIND_NOTIFICATION_LISTENER_SERVICE` (financial & 2FA push forwarding);
  - `WAKE_LOCK` (short-lived partial CPU wake lock during active cellular events).

---

## Phase 2: Interception Engines (SMS & Push)
- [x] **2.1.** **SMS Interceptor Module (`SmsReceiver`):**
  - High-priority (`Integer.MAX_VALUE`) broadcast receiver;
  - Reassembles multipart PDU segments (`SmsMessage.createFromPdu`);
  - Resolves active carrier SIM slot index via Android `SubscriptionManager` and MediaTek vendor hardware extras (`slot`, `simId`);
  - Formats HTML notification payloads with sender, SIM tag, and timestamp.
- [x] **2.2.** **Push Notification Forwarder (`NotificationListener`):**
  - Subclasses native `NotificationListenerService`;
  - Filters packages against user-configured whitelist;
  - Extracts notification title (`EXTRA_TITLE`) and body (`EXTRA_TEXT`, `EXTRA_BIG_TEXT`);
  - Deduplication engine with a 30-second sliding window to suppress duplicate alerts.

---

## Phase 3: Network Transport, Local Queue & Health Sentinel
- [x] **3.1.** **Telegram Client (`TelegramClient`):**
  - Direct asynchronous HTTPS POST calls via standard Java `HttpURLConnection`;
  - Automated HTML character escaping (`&`, `<`, `>`);
  - Network timeout detection and seamless offline handoff.
- [x] **3.2.** **Offline Local Queue (`OfflineQueue`):**
  - SQLite persistent buffer storing pending events during cellular / Wi-Fi outages;
  - Automatic queue flush in chronological order upon connectivity restoration.
- [x] **3.3.** **Uptime Kuma Telemetry Sentinel (`KumaPinger` & `HeartbeatManager`):**
  - Exact 15-minute background timer via `AlarmManager`;
  - Heartbeat metrics payload: battery level, power source (AC/USB/Battery), Wi-Fi RSSI (dBm), and queue size.

---

## Phase 4: User Interface & Administration
- [x] **4.1.** Configuration UI (`MainActivity`):
  - Bot Token, Chat ID, and Device Identifier inputs;
  - Custom SIM 1 and SIM 2 carrier label fields;
  - Uptime Kuma Push URL configuration;
  - Instant connectivity test buttons (`Test Telegram`, `Test Uptime Kuma`).
- [x] **4.2.** Whitelist Selection Dialog:
  - Dynamic searchable package list with native app icons and display labels;
  - Multi-select toggles to manage the push forwarding whitelist.
- [x] **4.3.** System Shortcut:
  - 1-click button to launch `ACTION_NOTIFICATION_LISTENER_SETTINGS`.

---

## Phase 5: Testing & Production Deployment
- [x] **5.1.** Release APK compilation and verification (`v001`, 30 KB).
- [x] **5.2.** Automated deployment to Samba storage and cloud mirrors.
- [x] **5.3.** Physical installation and field testing on dedicated gateway hardware (DOOGEE X3).
- [x] **5.4.** Uptime Kuma push monitor activation and end-to-end alert verification.

---

## 🏷 Topics & Tags

#AndroidDevelopment #Roadmap #MicroServices #TelegramBot #SMSGateway #DevOps #OpenSource
