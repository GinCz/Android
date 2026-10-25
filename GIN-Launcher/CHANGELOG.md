# 📜 GIN-TV-Launcher: Release Changelog

All notable updates, fixes, and architectural evolutions for **GIN-TV-Launcher** are documented in this file.

---

### [v020] - 2026-10-25
* **feat(ui):** Enlarged application icons and reduced inner card margins:
  * Increased favorite card icons from 76dp to 94dp (`item_app.xml`), reducing empty perimeter borders inside the card container for prominent TV display visibility.
  * Increased compact all-apps icons from 56dp to 74dp (`item_app_compact.xml`).
  * Adjusted card container paddings and minimum heights for optimal alignment.
* **style(ui):** Card background opacity increased from ~20% to 40% (`#66FFFFFF` gradient in `bg_app_item.xml`), delivering distinct frosted glass outlines with enhanced readability against dynamic wallpapers.
* **feat(network):** Refined native Windows 10 network status icons:
  * Crisp monitor screen with stand and RJ-45 cable connector for Ethernet LAN.
  * Triple radiating concentric arc fan with base dot for Wi-Fi.
  * Scaled icon footprint to 24dp for improved distance legibility on large screens.

---

### [v019] - 2026-10-25
* **refactor(ui):** Integrated Quick Sleep Timer (🌙) into the primary left action toolbar (`[ ⭐ ] [ ▦ ] [ 🧹 ] [ ⎋ ] [ ⚙ ] [ 🌙 ]`), cleanly separating user actions from the right-hand system info cluster.
* **fix(namedays):** Toggling the Name Day / Svátek badge to hidden (`Скрыто`) now smoothly collapses only the namedays pill without displacing or hiding the sleep timer button or network status.
* **feat(network):** Replaced legacy Unicode and emoji symbols with crisp, native Windows 10 VectorDrawables:
  * **Wi-Fi:** Authentic Windows 10 radiating curved arc fan (`ic_net_wifi.xml`). When disconnected, renders at 30% semi-transparency without harsh red strikethrough lines. Glows vivid cyan (`#00E5FF`, 100% opacity) upon connection.
  * **Ethernet:** Iconic Windows 10 monitor with stand and RJ-45 plug connector (`ic_net_ethernet.xml`). Shines bright cyan (`#00E5FF`, 100% opacity) when wired network is connected, and rests at 30% semi-transparency when disconnected.

---

### [v018] - 2026-10-25
* **fix(updater):** Resolved self-updating failure on Android TV where update installer aborted after downloading.
  * Migrated primary installation pipeline to Android's official `PackageInstaller.Session` API, streaming APK bytes directly into system staging memory to completely bypass `FileProvider` permission drops and scoped storage constraints.
  * Implemented `InstallReceiver` to intercept system installation state changes and automatically handle `STATUS_PENDING_USER_ACTION`, launching the native TV confirmation dialog.
  * Added temporary suspension mechanism (`pauseForUpdate`) in `HomeInterceptorService` to prevent the accessibility service from erroneously refocusing the launcher when the package installer launches.
  * Declared required `<queries>` intent filters and package names in `AndroidManifest.xml` for package installer visibility under target SDK 35 (Android 15).
  * Added TV-compatible fallback routines for unknown app source permissions and silent root install (`su -c pm install -r`) where available.

---

### [v017] - 2026-10-25
* **feat(network):** Concurrent dual network status sentinels for both Ethernet LAN (`🖧`) and Wi-Fi (`📶`) with real-time connectivity callback and visual red strike-through overlay (`/`) on disconnection.
* **feat(sleep):** Quick night sleep timer on the status bar with instant interval selection (30, 60, 90, 120, 180 min) without secondary confirmations. Features glowing amber-yellow active indicator (`#FFD600`) and live minute-by-minute countdown badge.
* **feat(ram):** One-tap RAM Cleaner placed 3rd in the top navigation bar (`[ ⭐ ] [ ▦ ] [ 🧹 ] [ ⎋ ] [ ⚙ ]`). Flushes background processes, invokes garbage collection, and shows reclaimed memory metrics.
* **feat(wallpaper):** 12 curated thematic wallpaper collections (Nature, Flowers, Space, Earth, Abstraction, Naruto, Cities, Ocean, Cars, Animals, Random, Procedural) with automatic 3-hour rotation, smooth 2.2-second crossfade, and offline disk caching.
* **feat(naruto):** High-resolution Naruto wallpaper pack hosted directly on high-speed CDN (`https://gincz.com/wallpapers/naruto/`).

---

### [v016] - 2026-10-25
* **fix(navigation):** Resolved card swap bug during consecutive app reordering in Favorites by replacing stale closure bindings with dynamic adapter position lookups and full data synchronization upon move completion.
* **refactor(ui):** Reorganized top status bar. Moved action buttons to the left and shifted the information cluster (Namedays, Date, Weather, Digital Clock) flush to the right margin.
* **feat(settings):** Long press on Settings (⚙) now opens the GIN-Launcher Control Center dialog.
* **feat(dimming):** Added customizable idle screen dimming (1 to 10 min) and variable residual brightness levels (20%, 30%, 50%, or 33% default).
* **feat(standby):** Deep TV standby sleep mode after inactivity (1 to 6 hours, default 3 hours) utilizing Accessibility Lock Screen and native keyevents.
* **feat(namedays):** Settings toggle to show or hide the dual cultural Name Day / Svátek badge.

---

### [v015] - 2026-10-24
* **feat(namedays):** Russian *День Ангела* prioritized as primary text with Czech *Svátek* nested below.
* **feat(filter):** Implemented system package filter to automatically hide headless OEM background services (ACBridge, SmartCenter, Katniss).

---

### [v014] - 2026-10-24
* **fix(focus):** TV remote D-Pad navigation loop prevention between top action bar and app grid.
* **feat(theme):** Procedural dark gradient themes with smooth crossfade.

---

### [v013] - 2026-10-23
* **feat(updater):** Automated background update checking engine via `version.json` with self-install workflow.

---

### [v001 - v012] - 2026-10-22
* Initial architecture, Leanback grid foundation, MediaTek TV inputs integration, favorites hiding engine.
