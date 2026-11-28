# ✉️ GIN-SMS-Backup: Lightweight Android SMS to Gmail IMAP Archiver

<div align="center">

![Platform](https://img.shields.io/badge/Platform-Android%205.0+-3DDC84?logo=android&logoColor=white)
![Release](https://img.shields.io/badge/Release-v001--plan-brightgreen)
![License](https://img.shields.io/badge/License-MIT%20%7C%20Open%20Source-blue)
![Size](https://img.shields.io/badge/Size-%3C%20250%20KB-orange)
![Google Play Dep](https://img.shields.io/badge/Google%20Play%20Services-None%20(Zero)-success)

**Ultra-lightweight, standalone Android application that securely synchronizes incoming SMS messages into your Gmail account via direct IMAP TLS (imap.gmail.com:993). Built with anti-blocking pacing, connection reuse, and zero Google Play Services dependencies.**

[⬇️ Download APK](#-download--releases) • [Key Features](#-key-features) • [Architecture](docs/ARCHITECTURE.md) • [Changelog](CHANGELOG.md)

</div>

---

## 🚀 Download & Releases

<div align="center">

### ✉️ [⬇️ Click Here to Download GIN-SMS-Backup.apk](https://github.com/GinCz/Android/raw/main/GIN-SMS-Backup/GIN-SMS-Backup.apk)
*Ultra-compact standalone APK (< 250 KB) • Compatible with Android 5.0 through Android 15*

</div>

> [!NOTE]
> **Complete Google Independence:** Unlike legacy backup apps, this application does NOT require Google Play Services or Firebase JobDispatcher. It connects directly over native Java SSL sockets.

---

## 🌟 Key Features

* **📬 Native Gmail IMAP Sync:** Backs up SMS messages directly into your Gmail SMS folder/label via standard RFC 3501 APPEND command over TLS 1.2+ (imap.gmail.com:993).
* **🛡 Anti-Flood & Rate Limiting Engine:** Enforces safe 3.0-second pauses between message uploads to strictly respect Google's IMAP rate limits and prevent temporary account lockouts.
* **⚡ SSL Connection Reusing:** Uploads batches of queued messages within a single established TLS session, preventing rapid connection-cycling flags on Google mail servers.
* **📶 Dual-SIM Tagging:** Automatically detects which SIM card received the message and tags email subject lines accordingly ([SIM 1] vs [SIM 2]).
* **🧵 Gmail Threading Compatibility:** Generates RFC 2822 compliant MIME emails with standardized message headers, ensuring conversation threading in the Gmail web and mobile interface.
* **🔋 Never Sleeps:** Runs as an unkillable Foreground Service with temporary partial WakeLock held strictly during active TLS transfer sessions.
* **🪶 Ultra-Low RAM Footprint:** Weighs under 250 KB and consumes only 3–5 MB of RAM.

---

## ⚙️ Configuration & Quick Start

1. Enable IMAP in your Gmail settings.
2. Generate an **App Password** (16 characters) in your Google Account Security settings.
3. Launch **GIN-SMS-Backup** on your device:
   - Enter your Gmail address (e.g. your_email@gmail.com).
   - Enter your 16-character Google App Password.
   - (Optional) Adjust pacing delay (default: 3 seconds).
   - Tap **Test IMAP Connection** to verify.
   - Tap **Enable Backup Service**.

---

## 🏷 Hashtags & Topics

#Android #Gmail #IMAP #SMSBackup #EmailSync #DualSIM #OpenSource #ZeroBloat #Privacy #Minimalist #Kotlin #Java #RFC3501

---

## 📜 License

Distributed under the **MIT License**. Free for personal and commercial use.  
Author: [Vladimir Bulantsev (@GinCz)](https://github.com/GinCz)