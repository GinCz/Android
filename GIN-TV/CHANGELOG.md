# 📜 GIN-TV: Release Changelog

> **Project:** GIN-TV Android by Vladimir Bulantsev + AI  
> **Repository:** [GinCz/Android ↗](https://github.com/GinCz/Android/tree/main/GIN-TV)  
> **Author:** Vladimir Bulantsev ([GinCz ↗](https://github.com/GinCz))  
> **Scope:** Full interactive version log for the online TV and IPTV player.

---

## 🔬 Version History & Architecture Log

*(Click on any release header below to expand details)*

<details open>
<summary><b>🔥 Release v084 (2026-11-19) — 2-Step DPAD Remote Navigation & Long-Press Favorites [Current Release]</b></summary>
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
