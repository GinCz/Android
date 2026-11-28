# 📝 Changelog — GIN-Telegram-Bot

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

---

## [v002] - 2026-11-28

### Added
- **On-Demand In-App Update Engine:** Implemented pure native `UpdateManager` and zero-dependency `GenericFileProvider` enabling manual one-tap APK updates without Google Play or external libraries across Android 5.0 through 15+.
- **Signature Version Button:** Pinned `VladiMIR+AI ||| v002` action button at the bottom of settings interface, triggering real-time version manifest polling, progress dialog download, and native package installer launch.
- **Redundant Update Mirrors:** Deployed HTTPS manifests and binary distributions to high-availability endpoints (`https://gincz.com/gin_telegram_bot_version.json` and `https://eco-seo.cz/apk/telegram_bot/version.json`).

### Fixed & Improved
- **Dedicated WakeLock in `TelegramClient.sendAsync`:** Acquired a temporary partial WakeLock during asynchronous Telegram API calls to prevent CPU sleep during lockscreen/Doze modes.

---

## [v001] - 2026-11-28

### Added
- **Native SMS Gateway:** High-priority `SmsReceiver` with multipart message reassembly and Dual-SIM carrier slot detection.
- **Push Notification Listener:** `NotificationListenerService` capturing status-bar alerts from user-whitelisted financial and 2FA applications.
- **Direct Telegram HTTP Client:** Native TLS 1.2+ transport using `HttpURLConnection` with zero third-party dependencies.
- **Offline Message Buffer:** SQLite-based `OfflineQueue` for temporary caching and automatic retry when network connectivity is disrupted.
- **Uptime Kuma Integration:** Periodic push heartbeat monitoring reporting battery level, Wi-Fi RSSI, and queue metrics every 15 minutes.
- **Settings Activity:** Clean single-screen interface for configuring Bot Token, Chat ID, SIM names, Push URL, and app whitelist.
- **R8 FullMode Shrinking:** Ultra-optimized 30 KB release binary with zero third-party libraries.
