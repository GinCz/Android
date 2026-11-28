# 📝 Changelog — GIN-Telegram-Bot

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

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
