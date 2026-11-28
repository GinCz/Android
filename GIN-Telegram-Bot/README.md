# 📱 GIN-Telegram-Bot: Ultra-Lightweight Android SMS & App Push Gateway for Telegram

<div align="center">

![Platform](https://img.shields.io/badge/Platform-Android%205.0+-3DDC84?logo=android&logoColor=white)
![Release](https://img.shields.io/badge/Release-v001--plan-brightgreen)
![License](https://img.shields.io/badge/License-MIT%20%7C%20Open%20Source-blue)
![Size](https://img.shields.io/badge/Size-%3C%20200%20KB-orange)
![Trackers](https://img.shields.io/badge/Trackers-0%25%20Zero-red)

**High-performance, zero-bloat Android gateway that instantly forwards incoming SMS (Dual-SIM) and selected app push notifications (Banking, 2FA, Delivery) to your Telegram chat. Features Uptime Kuma push monitoring and GPS location reporting.**

[⬇️ Download APK](#-download--releases) • [Key Features](#-key-features) • [Architecture](docs/ARCHITECTURE.md) • [Changelog](CHANGELOG.md)

</div>

---

## 🚀 Download & Releases

<div align="center">

### 📱 [⬇️ Click Here to Download GIN-Telegram-Bot.apk](https://github.com/GinCz/Android/raw/main/GIN-Telegram-Bot/GIN-Telegram-Bot.apk)
*Ultra-compact standalone APK (< 200 KB) • Compatible with Android 5.0 through Android 15*

</div>

> [!NOTE]
> **Zero Bloat Guarantee:** 100% Free, Open-Source, Ad-Free, and Tracker-Free. No Google Play Services required.

---

## 🌟 Key Features

* **⚡ Sub-300ms Forwarding:** Incoming SMS messages are captured via SmsReceiver with maximum system priority and instantly dispatched to the official Telegram Bot API via native HTTP POST.
* **📶 Full Dual-SIM Awareness:** Automatically identifies and tags which physical SIM card received the message ([SIM 1] vs [SIM 2]).
* **🏦 Selective App Push Forwarding:** Intercepts push notifications from user-selected apps (e.g., Banking apps, 2FA authenticators, courier deliveries) using native NotificationListenerService.
* **💓 Uptime Kuma Push Heartbeat:** Periodically pings your self-hosted Uptime Kuma instance (every 15 minutes) with battery level, Wi-Fi RSSI signal strength, and queue status. Alerts you in Telegram if the phone loses connection or shuts down!
* **📍 On-Demand GPS Tracking:** Sends real-time Google Maps coordinates when requested or when battery drops below critical threshold.
* **🛡 Offline Resilient Queue:** In case of temporary Wi-Fi disconnection, incoming events are securely buffered in a local SQLite database and automatically flushed upon reconnection.
* **🪶 Tiny Footprint:** Weighs less than 200 KB and consumes only 3–5 MB of RAM (replacing bloated 45 MB third-party bots).

---

## ⚙️ Configuration & Quick Start

1. Create a bot using [@BotFather](https://t.me/BotFather) and get your BOT_TOKEN.
2. Get your personal CHAT_ID (e.g. from [@userinfobot](https://t.me/userinfobot)).
3. Launch **GIN-Telegram-Bot** on your Android device:
   - Enter your Bot Token and Chat ID.
   - Set device prefix (e.g., [My-Gateway]).
   - (Optional) Enter your Uptime Kuma Push URL.
   - In the **Apps Checklist**, tick the applications whose push notifications you want forwarded.
   - Tap **Save & Start Service**.

---

## 🏷 Hashtags & Topics

#Android #TelegramBot #SMSGateway #NotificationForwarder #DualSIM #OpenSource #UptimeKuma #ZeroBloat #Kotlin #Java #DevOps #Privacy #Minimalist

---

## 📜 License

Distributed under the **MIT License**. Free for personal and commercial use.  
Author: [Vladimir Bulantsev (@GinCz)](https://github.com/GinCz)