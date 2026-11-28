# 📱 Android Applications & Smart TV Suite

> **Official Showcase & Distribution Repository:** [GinCz/Android ↗](https://github.com/GinCz/Android)  
> **Author & Architect:** Ing. Vladimir Bulantsev ([GinCz ↗](https://github.com/GinCz)) & Anti-Gravity AI  
> **Target Platforms:** Android 7.0+ • Android TV • Google TV • FireTV • Smart TV Boxes  
> **Policy:** 100% Free • Ad-Free • Zero Trackers • Hardware-Accelerated Playback  

---

## 🧭 Official Applications Catalog & Direct Downloads

Official public repository of production-ready Android and Smart TV applications created by **Vladimir Bulantsev (GinCz) + AI**.

Each application is distributed as a single standalone APK build without third-party advertisements, tracking SDKs, or forced subscriptions:

| Application | Target Platform | Latest Release | Direct Download (.apk) | SHA-256 Checksum |
| :--- | :--- | :---: | :--- | :--- |
| 📱 **[GIN-Telegram-Bot ↗](GIN-Telegram-Bot/)** | All Android Devices (Phone/Tablet) | `v001` | [⬇️ **Download GIN-Telegram-Bot_v001.apk**](https://gincz.com/GIN-Telegram-Bot_v001.apk) | 30 KB |
| ✉️ **[GIN-SMS-Backup ↗](GIN-SMS-Backup/)** | All Android Devices (Phone/Tablet) | `v001` | [⬇️ **Download GIN-SMS-Backup_v001.apk**](https://github.com/GinCz/Android/tree/main/GIN-SMS-Backup) | 62 KB |
| 🚀 **[GIN-Launcher ↗](GIN-Launcher/)** | Android TV / Google TV / FireTV | `v053` | [⬇️ **Download GIN-TV-Launcher.apk**](https://raw.githubusercontent.com/GinCz/Android/main/GIN-Launcher/GIN-TV-Launcher.apk) | `d7da91130050...` |
| 🎬 **[GIN-Cinema ↗](GIN-Cinema/)** | Android TV / Tablets / Phones | `v114` | [⬇️ **Download GIN-Cinema.apk**](https://raw.githubusercontent.com/GinCz/Android/main/GIN-Cinema/GIN-Cinema.apk) | `40dad63dedc5...` |
| 📺 **[GIN-TV ↗](GIN-TV/)** | Android TV / Smart TV / Mobile | `v077` | [⬇️ **Download GIN-TV.apk**](https://raw.githubusercontent.com/GinCz/Android/main/GIN-TV/GIN-TV.apk) | `ddd075718b01...` |
| 🌐 **[GIN-IP ↗](GIN-IP/)** | All Android Devices (Phone/TV) | `v013` | [⬇️ **Download GIN-IP.apk**](https://raw.githubusercontent.com/GinCz/Android/main/GIN-IP/GIN-IP.apk) | `18b8f62b5789...` |
| 📦 **[GIN-APP ↗](GIN-APP/)** | Android TV / Mobile Hub | `v006` | [⬇️ **Download GIN-APP.apk**](https://raw.githubusercontent.com/GinCz/Android/main/GIN-APP/GIN-APP.apk) | `55ae6d199abd...` |
| 📊 **[Ultarix-Monitor ↗](Ultarix-Monitor/)** | Android TV / Telemetry Daemon | `v002` | [⬇️ **Download Ultarix-Monitor.apk**](https://raw.githubusercontent.com/GinCz/Android/main/Ultarix-Monitor/Ultarix-Monitor_v002.apk) | `687e10cdbebf...` |

---

## ⚡ Application Highlights

### 📱 [GIN-Telegram-Bot](GIN-Telegram-Bot/) — Ultra-Lightweight SMS & Push Gateway
- **Sub-300ms Telegram Dispatch:** Direct native HTTP POST forwarding for incoming SMS with Dual-SIM identification.
- **Selective App Push Forwarding:** Intercepts push notifications from user-selected apps (banking, 2FA, logistics).
- **Uptime Kuma Heartbeat:** Automatic periodic push pings (every 15 min) reporting battery, Wi-Fi RSSI, and queue health.
- **Zero Google Play Overhead:** Operates independently of Google services, consuming under 5MB RAM and < 200KB disk.

### ✉️ [GIN-SMS-Backup](GIN-SMS-Backup/) — Standalone Gmail IMAP Archiver
- **Direct IMAP TLS Sync:** Archives SMS to Gmail folder SMS directly over imap.gmail.com:993 via Java SSL sockets.
- **Pacing & Anti-Block Engine:** Enforces safe 3.0s delays between uploads and reuses open TLS sessions to prevent Google bans.
- **Dual-SIM & Threading Support:** RFC 2822 compliant MIME headers preserve conversation threads and tag SIM slots.
- **Never Sleeps:** Resilient Foreground Service architecture with offline SQLite queue.

### 🚀 [GIN-TV-Launcher](GIN-Launcher/) — Leanback Smart TV Home Interface
- **Ultra-Lightweight Footprint:** Sub-25MB RAM consumption, optimized for low-end MediaTek / Amlogic chipsets.
- **D-Pad Native:** Smooth 60 FPS remote control navigation with customizable quick-launch shortcuts.
- **Live Status Sentinels:** Integrated Windows 10 style LAN/Wi-Fi connection sentinels and real-time clock.
- **Sleep Timer:** Auto-standby sleep timer designed for bedtime streaming.
- **Dynamic Theming:** 12 curated high-definition wallpapers with ambient transitions.

### 🎬 [GIN-Cinema](GIN-Cinema/) — Online Cinema Streaming Client
- **ExoPlayer Engine:** Native hardware-accelerated HEVC/H.264/AV1 4K HDR video decoding.
- **Family Sync Ecosystem:** Fast family PIN synchronization (`XXX-XXX-XXX`) across TV, mobile, and desktop.
- **Zero Ads:** Clean, uninterrupted playback without popups or commercial interruptions.
- **Voice & D-Pad Search:** Instant title lookups with curated "TOP-33" recommendation shelves.

### 📺 [GIN-TV](GIN-TV/) — Ultra-Fast IPTV & Live Streaming Client
- **Sub-300ms Channel Switching:** Instant stream buffering and channel zapping without UI freeze.
- **Interactive EPG Guide:** Comprehensive electronic program guide with real-time schedule alignment.
- **Adaptive Bitrate (ABR):** Smooth playback on both mobile LTE and fiber optic networks.

### 🌐 [GIN-IP](GIN-IP/) — Network Diagnostics Utility
- **One-Tap Diagnostics:** External IP, ISP provider, Autonomous System Number (ASN), and geolocation details.
- **DNS Latency Benchmark:** Real-time round-trip latency checks to major DNS resolvers.
- **Clean Clipboard Export:** Copy network metrics with a single tap.

### 📦 [GIN-APP](GIN-APP/) — Ecosystem Hub & Auto-Updater
- **Unified Portal:** Explore, install, and update all GinCz suite applications in one place.
- **Silent Self-Updater:** Automatically checks for and installs updated APK releases.

---

## 🔒 Verification & Release Integrity

Verify the cryptographic SHA-256 hash of downloaded APK files prior to installation:

```bash
# Verify checksum on Linux / macOS:
sha256sum GIN-TV-Launcher.apk

# Verify checksum on Windows (PowerShell):
Get-FileHash -Algorithm SHA256 .\GIN-TV-Launcher.apk
```

| Package Name | File | SHA-256 Hash |
| :--- | :--- | :--- |
| `GIN-TV-Launcher` | `GIN-TV-Launcher.apk` | `d7da9113005098c1871428ce1f9f59a77edad98e98840e55ff27ae514dfb61ab` |
| `GIN-Cinema` | `GIN-Cinema.apk` | `40dad63dedc5ae93c0ca1066004000dc32451ed6ded0558eed34a917c0659563` |
| `GIN-TV` | `GIN-TV.apk` | `ddd075718b01dbfe64766ad9a3dd7640982acf012b6a398da1c3faf7ca5e2c0c` |
| `GIN-IP` | `GIN-IP.apk` | `18b8f62b57899370cc55895e0acb788e380d01e82397b00aaf80c62defe41d64` |
| `GIN-APP` | `GIN-APP.apk` | `55ae6d199abd1dd4c9dd11ba5927eb7248e70dca78e4fe29b62d36ac03432df8` |
| `Ultarix-Monitor` | `Ultarix-Monitor_v002.apk` | `687e10cdbebf4d9822aa71faf1d69e5cd36515b2669bc1ed8ae129d659fedba8` |

---

## 📋 Version History & Changelog

<details>
<summary><b>Click to expand full suite version history</b></summary>

### v2026-11 (Latest)
* **GIN-Launcher v053:** Leanback TV home interface with real-time network sentinels, 60fps D-pad performance.
* **GIN-Cinema v114:** Hardware-accelerated 4K HDR playback and cross-device family sync.
* **GIN-TV v077:** Ultra-fast sub-300ms live IPTV channel switching and EPG guide.
* **Ultarix-Monitor v002:** Android TV hardware telemetry and background thermal monitoring.
* **Integrity Audit:** Re-verified and published cryptographic SHA-256 hashes for all production APK files.

</details>

---

## 📦 Distribution & Source Code Policy

- **Single Active Release Standard:** Each directory maintains strictly **one active production APK file** with a stable filename for seamless self-updating.
- **Proprietary Source Repositories:** Full Kotlin, Java, and Gradle build pipelines are securely maintained in private vaults ([Secret_Privat ↗](https://github.com/GinCz/Secret_Privat)).

---

## 🏷️ Keywords, Topics & Search Metadata

`#android-tv` `#google-tv` `#firetv` `#leanback-launcher` `#smart-tv-apps` `#exoplayer-4k` `#iptv-player` `#ad-free-streaming` `#android-telemetry` `#ultarix-monitor` `#kotlin-android` `#clean-architecture`

---

<div align="center">

<h3>= Engineered by VladiMIR + AI =</h3>

<a href="https://github.com/GinCz" target="_blank" rel="noopener noreferrer"><img src="https://img.shields.io/badge/GitHub-GinCz-181717?style=flat&logo=github&logoColor=white" alt="GitHub" /></a>
&nbsp;&nbsp;<b>|||</b>&nbsp;&nbsp;
<a href="https://www.linkedin.com/in/gincz/" target="_blank" rel="noopener noreferrer"><img src="https://img.shields.io/badge/LinkedIn-Vladimir%20Bulantsev-0A66C2?style=flat&logo=linkedin&logoColor=white" alt="LinkedIn" /></a>

<br/><br/>

<sub>Android Suite &copy; 2026. Ing. Vladimir Bulantsev</sub>

</div>
