# 🚀 GIN-TV-Launcher: High-Performance Android TV & Smart TV Home Interface

<div align="center">

![Platform](https://img.shields.io/badge/Platform-Android%206.0+%20%7C%20Smart%20TV%20%7C%20Google%20TV-3DDC84?logo=android&logoColor=white)
![UI](https://img.shields.io/badge/UI-Leanback%20D--Pad%20Optimized-00E5FF)
![Release](https://img.shields.io/badge/Release-v022-brightgreen)
![License](https://img.shields.io/badge/License-Freeware%20%7C%20Free%20to%20Use-blue)

**Ultra-fast, ad-free, and resource-efficient launcher engineered for Android TV boxes, Google TV, FireTV, and Smart TVs (MediaTek, Vestel, Xiaomi, TCL, Sony). Sub-25MB RAM footprint, instant D-pad navigation, dynamic 1080p thematic wallpapers, integrated night sleep timer, and dual network status sentinels.**

[⬇️ Download GIN-TV-Launcher.apk (v022)](#-quick-download--latest-release-v022) • [Key Features](#-key-features) • [Changelog](CHANGELOG.md)

</div>

---

## 🚀 Quick Download & Latest Release (v022)

<div align="center">

### 📺 [⬇️ Download GIN-TV-Launcher.apk from High-Speed CDN](https://gincz.com/GIN-TV-Launcher_v022.apk)
*Standalone Production APK (4.9 MB) • Signed Release • Android 6.0+ (API 23–35)*

**Alternative Direct Mirror:** [GitHub Raw Release (v022)](https://github.com/GinCz/Android/raw/main/GIN-Launcher/GIN-TV-Launcher.apk)

</div>

> [!NOTE]
> **Strict Single-Release Policy:** We maintain strictly **the latest, thoroughly tested production release**. Outdated legacy builds are permanently retired to guarantee compatibility, hardware security, and smooth auto-updates.

---

## 🌟 Key Features

### 1. 🖧 Windows 10 Dual Network Status Sentinels (LAN & Wi-Fi)
* **Windows 10 Design Language:** Pixel-perfect vector drawables modeled directly on Windows 10 taskbar iconography — true concentric 90-degree radiating curved arcs for Wi-Fi and the iconic monitor with left-side vertical RJ-45 cable/plug for wired Ethernet.
* **Smart 30% Semi-Transparency:** When disconnected, Ethernet or Wi-Fi smoothly dims to a subtle 30% opacity (`alpha = 0.30`) without red strikethrough lines.
* **Crisp Active Connectivity:** When connected, Ethernet and Wi-Fi render in crisp 100% opaque Windows 10 white (`#FFFFFF`).
* **Zero-Polling Network Callback:** Leverages Android's native `ConnectivityManager.NetworkCallback` to reflect cable unplugging or Wi-Fi reconnects in under 100 milliseconds without consuming background CPU cycles.

### 2. 🌙 Quick Nighttime Sleep Timer (30–180 min)
* **Unified Action Toolbar:** Integrated as the 6th button in the primary left toolbar (`[ ⭐ ] [ ▦ ] [ 🧹 ] [ ⎋ ] [ ⚙ ] [ 🌙 ]`), cleanly decoupled from right-side information widgets.
* **One-Click Dropdown Menu:** Dedicated `🌙` action button on the top bar. Pressing OK instantly opens the quick selector:
  * `30 minutes`
  * `60 minutes (1 hour)`
  * `90 minutes (1.5 hours)`
  * `120 minutes (2 hours)`
  * `180 minutes (3 hours)`
* **Zero Confirmation Friction:** Selecting an interval immediately arms the timer without annoying secondary dialogs.
* **Active Ticking Badge:** The button lights up with a **vibrant amber-yellow glow (`#FFD600`)** and dynamically displays the real-time minute countdown (e.g. `🌙 90m` ➔ `🌙 89m`...).
* **Multi-Tier TV Standby Execution:** When the timer elapses, the launcher initiates hardware TV Standby via Accessibility Lock Screen, native Android TV keyevents (`KEYCODE_SLEEP` 223 / `KEYCODE_POWER` 26), and deep matrix blackouts.

### 3. 🧹 Instant RAM Cleaner & Memory Optimization
* **Dedicated 3rd Action Button:** Placed prominently in the top bar (`[ ⭐ ] [ ▦ ] [ 🧹 ] [ ⎋ ] [ ⚙ ]`).
* **One-Touch Memory Flush:** Purges cached background app tasks, invokes Android VM garbage collection (`System.gc()`), and performs a 360-degree rotation animation.
* **Informative Toast Metrics:** Instantly reports reclaimed RAM and remaining system memory (e.g. *“Freed RAM: 240 MB | Available: 890 MB / 1850 MB”*).

### 4. 🖼️ 12 Curated 1080p Wallpaper Themes (3-Hour Rotation)
* **Custom Thematic Categories:** Access the Wallpaper Picker inside the Settings Control Center:
  1. 🌲 **Nature** (Misty mountains, serene alpine lakes, lush pine forests)
  2. 🌸 **Flowers** (Cherry blossom sakura, blooming gardens, vivid petals)
  3. 🌌 **Space** (Deep space nebulae, galaxies, celestial bodies)
  4. 🌍 **Earth** (Orbital views from space, grand canyons, island coastlines)
  5. 🎨 **Abstraction** (Modern 3D fluid waves, dark neon geometry)
  6. 🍥 **Naruto** (Konoha landmarks, iconic nightscapes, shinobi artwork)
  7. 🏙️ **Cities** (Tokyo night skylines, metropolis skyscrapers, urban lights)
  8. 🌊 **Ocean** (Deep turquoise waters, coastal tides, sunsets)
  9. 🚗 **Cars** (High-performance supercars, night automotive photography)
  10. 🦁 **Wildlife** (Big cats, raptors, majestic animals in habitat)
  11. 🎲 **Random Mix** (Cycles dynamically across all available categories)
  12. 🖥️ **Procedural Gradients** (Original zero-bandwidth dark shader themes)
* **Cinematic Crossfade Transition:** Seamless 2.2-second crossfade between backgrounds every 3 hours with automatic local disk caching for offline resilience.

### 5. ⏱️ OLED Screen Burn-In Protection & Auto-Sleep Engine
* **Configurable Screen Dimming:** Set inactivity dimming timeout to `1 min` (default), `2 min`, `3 min`, `5 min`, `10 min`, or `Off`.
* **Adjustable Dimming Brightness:** Customize residual brightness level to `Default (33%)`, `20%`, `30%`, or `50%`.
* **Deep Inactivity Sleep Mode:** Automatically sends the TV into Standby mode if forgotten on the home screen (`1h`, `2h`, `3h` (default), `4h`, `5h`, `6h`, or `Off`).

### 6. 📅 Dual Cultural Calendar (Name Day / Svátek)
* **Bilingual Celebration Badge:** Shows today's Russian *День Ангела* and Czech *Svátek* alongside European weather and date.
* **Toggle Visibility:** Can be toggled on or off directly within Launcher Settings.

### 7. ⎋ Native TV Input Panel (MediaTek & Android TV)
* Seamlessly switches to native TV inputs (HDMI 1, HDMI 2, HDMI 3, TV/Antenna, AV Composite) on MediaTek and Vestel Smart TVs without losing D-pad focus.

### 8. 🔄 Integrated Auto-Updater
* Background checks run 3 minutes after launcher launch and repeat every 3 hours.
* Instant manual check via Long Press on the Settings button or through the Launcher Version button in the Control Center.

---

## 🧭 D-Pad Navigation & Layout Map

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│ [⭐ Fav] [▦ All] [🧹 RAM] [⎋ Inputs] [⚙ Set]    [🖧 📶] [🌙 Sleep] [Namedays] [Date] [Clock] │
├────────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                        │
│   ┌────────┐   ┌────────┐   ┌────────┐   ┌────────┐   ┌────────┐   ┌────────┐   ┌────────┐ │
│   │ App 01 │   │ App 02 │   │ App 03 │   │ App 04 │   │ App 05 │   │ App 06 │   │ App 07 │ │
│   └────────┘   └────────┘   └────────┘   └────────┘   └────────┘   └────────┘   └────────┘ │
│                                                                                        │
│   ┌────────┐   ┌────────┐   ┌────────┐   ┌────────┐   ┌────────┐   ┌────────┐   ┌────────┐ │
│   │ App 08 │   │ App 09 │   │ App 10 │   │ App 11 │   │ App 12 │   │ App 13 │   │ App 14 │ │
│   └────────┘   └────────┘   └────────┘   └────────┘   └────────┘   └────────┘   └────────┘ │
│                                                                                        │
└────────────────────────────────────────────────────────────────────────────────────────┘
```

* **Hold OK on App Card:** Opens Card Management menu (Move position, App properties, Hide from Favorites, Uninstall).
* **Hold OK on Settings (⚙):** Opens GIN-Launcher Control Center.

---

## 📋 System Requirements

| Parameter | Minimum Requirement | Recommended |
| :--- | :--- | :--- |
| **Operating System** | Android 6.0 Marshmallow (API 23) | Android 9.0 – 14.0 (Android TV / Google TV) |
| **RAM** | 1.0 GB RAM | 2.0 GB+ RAM |
| **Storage** | 20 MB free space | 100 MB free space (for wallpaper cache) |
| **Input Device** | Standard IR / Bluetooth TV Remote (D-Pad) | Voice / Air Mouse / Gamepad |

---

## 🔒 Security & Source Code Governance

* **Privacy by Design:** This public repository distributes solely pre-compiled, verified, signed release APK binaries and comprehensive documentation.
* **Source Protection:** Full Kotlin source code, signing keystores, and Gradle environments are stored exclusively within the author's private repository ([Secret_Privat ↗](https://github.com/GinCz/Secret_Privat)) to prevent unauthorized repackaging or tampering.

---

## 👤 Author & Architecture

* **Author:** **Ing. Vladimir Bulantsev** ([GinCz ↗](https://github.com/GinCz))
* **Email:** `gin@volny.cz` | `gin.vladimir@gmail.com`
* **Distribution Hub:** [https://github.com/GinCz/Android ↗](https://github.com/GinCz/Android)

---

## 📄 License

This software is released free of charge (**Freeware**) for personal and home use. All rights reserved.
