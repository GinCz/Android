# 📜 GIN-TV: Release Changelog

> **Project:** GIN-TV Android by Vladimir Bulantsev + AI  
> **Repository:** [GinCz/Android ↗](https://github.com/GinCz/Android/tree/main/GIN-TV)  
> **Author:** Vladimir Bulantsev ([GinCz ↗](https://github.com/GinCz))  
> **Scope:** Full interactive version log for the online TV and IPTV player.

---

## 🔬 Version History & Architecture Log

*(Click on any release header below to expand details)*

<details open>
<summary><b>🔥 Release v091 (2026-11-24) — Autonomous Silicon Hardware GIN-ID & Elimination of IPC Binder Deadlocks [Current Release]</b></summary>
<br>

* **Key Capabilities:**
  - **Elimination of Cross-App IPC Deadlocks:** Completely eradicated synchronous `ContentResolver` queries to `cz.gin.cinema.pin_provider`. Resolved the startup crash and ANR freeze occurring after app data wipes when both apps synchronously waited on Binder transactions for each other.
  - **Pure Silicon Deterministic GIN-ID:** Generates the canonical 9-digit device PIN (`XXX-XXX-XXX`) strictly and locally from CPU silicon traits (`/proc/cpuinfo`, SoC eFuse, CPU architecture) and motherboard serial in <= 0.001s with zero inter-process overhead.
  - **Zero-Crash Resilience:** Immediate launch without dependence on external apps or background services, preserving full stability even under fresh installs or cold data resets.

</details>

<details>
<summary><b>🔥 Release v085 (2026-11-19) — Default Free EU Playlist, 1-Year Trial & Strict Subscription Enforcement [Current Release]</b></summary>
<br>

* **Key Capabilities:**
  - **Default European Playlist & 1-Year Subscription:** Newly connected devices are automatically assigned the free European playlist (`free_eu`) and a 1-year subscription trial (`paid_until = now + 365 days`). Device pairing is immediately activated with admin Telegram alerts and subscription management actions.
  - **Strict Subscription Protection:** Devices without an active subscription cannot access channels or catalog. The app displays a dedicated fullscreen "🔴 НЕТ ПОДПИСКИ" panel with device PIN, expiration info, and "Check Subscription" / "Settings" buttons. Playback is strictly prevented on both client and player levels.
  - **Server-Side API 403 Enforcement:** The server enforces valid subscription on `/api/gin-tv/v1/playlist` and `/api/gin-tv/v1/web/catalog`, returning HTTP 403 Forbidden when expired or unpaid.

</details>

<details>
<summary><b>📦 Release v084 (2026-11-19) — 2-Step DPAD Remote Navigation & Long-Press Favorites</b></summary>
<br>

* **Key Capabilities:**
  - **2-Step Navigation for Budget Remotes:** Designed for TV sticks with basic remotes (no CH+/CH- buttons). In full screen, the first press of **UP** opens the playback/quick action menu, while a second press of **UP** immediately switches to the next channel. Pressing **DOWN** closes the menu if open, or switches to the previous channel if the menu is already closed.
  - **Long-Press OK to Toggle Favorite:** On channels in the list, users without number keys can hold OK to quickly add or remove channels from favorites.

</details>

<details>
<summary><b>📦 Release v083 (2026-11-19) — Non-Blocking Error Overlay, Fast Channel Hopping & Permissive SSL</b></summary>
<br>

* **Key Capabilities:**
  - **Non-blocking Channel Error Notification:** Removed the modal popup dialog that intercepted remote focus. Channels that fail to buffer now display a clean, non-intrusive overlay with a sad emoji 😕 and retry hint without stealing remote control focus.
  - **Instant Remote Navigation:** Users can hop to the next/previous channel immediately via DPAD UP/DOWN or CHANNEL UP/DOWN without dismissing any windows. DPAD LEFT/OK opens the full channel list.
  - **Permissive SSL for IPTV CDNs:** Injected permissive SSL engine (`SSLSocketFactory` + wildcard `HostnameVerifier`) to natively play external IPTV streams with mismatched domain certificates (such as `stream.mcquack.net`).

</details>

<details>
<summary><b>📦 Release v082 (2026-11-17) — Buffer Menu & Family Status Badges</b></summary>
<br>

* **Key Capabilities:**
  - Enhanced buffer configuration menu with high-contrast frame.
  - Device/family pairing status badge improvements.

</details>

<details>
<summary><b>📦 Release v077 (2026-10-15) — HLS Buffer Optimization</b></summary>
  - Dynamic electronic program guide (EPG XMLTV) with automated updates.

</details>

<details>
<summary><b>📺 Release v055 (2026-10-10) — D-Pad Remote Navigation for Smart TVs</b></summary>
<br>

* **Key Capabilities:**
  - Slide-out side drawer channel list categorized by genres (Movies, News, Discovery, Sports, Music).
  - Direct numeric keypad navigation for instant channel hopping via remote control.

</details>
