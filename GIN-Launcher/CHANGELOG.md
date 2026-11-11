# 📜 GIN-TV-Launcher: Release Changelog

All notable updates, fixes, and architectural evolutions for **GIN-TV-Launcher** are documented in this file.

---

### [v038] - 2026-11-08
* **feat(dynamic-key-filter):** Switched `HomeInterceptorService` to dynamic key event filtering:
  * Removed static `flagRequestFilterKeyEvents` from `accessibility_service_config.xml` (`flagDefault`).
  * `FLAG_REQUEST_FILTER_KEY_EVENTS` is attached dynamically at runtime via `setServiceInfo()` only when button mappings are active or button capture dialog is open.
  * When no buttons are remapped, `KeyboardInterceptor` is completely detached from the Android input subsystem, guaranteeing 100% native remote control responsiveness.
* **fix(volume-hold-auto-repeat):** Restored smooth continuous volume adjustment when holding down `Volume +` or `Volume -`:
  * Implemented continuous auto-repeat handling for `KEYCODE_VOLUME_UP`, `KEYCODE_VOLUME_DOWN`, and `KEYCODE_VOLUME_MUTE` in `RemoteKeyMapper`.
  * Routed key repeat events directly to `AudioManager.adjustSuggestedStreamVolume(..., AudioManager.FLAG_SHOW_UI)`, restoring smooth hardware-level volume stepping with visible OSD slider.
* **fix(remote-pairing-protection):** Added explicit exemption in `handleWindowPackageChanged` for `com.google.android.tv.remote` and `com.vestel.smartcenter` packages so that smartphone remote control pairing dialogs are never covered or dismissed.
* **deploy:** Successfully compiled and deployed to Vestel Android TV via ADB `127.0.0.1:15555` and uploaded to `gincz.com`.

---

### [v037] - 2026-11-08
* **perf(zero-jni-cleanup):** Completely purged heavy native VPN binaries (`libxray.so` 35MB, `libtun2socks.so` 12MB).
  * Reduced APK installer size from 36.5MB to 4.9MB (~9x lighter).
* **feat(native-adguard-dot):** Implemented lightweight zero-overhead ad blocking via native Android Private DNS (DNS-over-TLS) using `dns.adguard-dns.com` via `WRITE_SECURE_SETTINGS`.

---

### [v034] - 2026-11-07
* **ui(vector-icons):** Replaced missing Unicode glyph `⏻` (`U+23FB`), which rendered as an empty square/cross on TV fonts, with vector drawables `ic_power_off.xml` and `ic_reboot_action.xml` for crisp rendering across all display resolutions.
* **feat(native-power-controls):** Integrated direct TV system power dialog invocation via `HomeInterceptorService.showPowerDialog()` (`GLOBAL_ACTION_POWER_DIALOG`), giving users the native Android TV menu for Shut down and Restart.
* **feat(secure-settings-auto-enable):** Added automatic accessibility service activation via `WRITE_SECURE_SETTINGS` on launcher startup.
* **feat(interactive-power-fallback):** When accessibility service is not yet enabled, launcher displays an interactive guidance dialog instead of silently dismissing settings.

---

### [v033] - 2026-11-07
* **fix(vpn-state-sync):** Restored single-process architecture for `LauncherVpnService` by removing `android:process=":vpn"` from `AndroidManifest.xml`.
  * Eliminates cross-process memory caching discrepancies in Android's `SharedPreferencesImpl` where `MainActivity` was unable to observe status updates written by the isolated `:vpn` process.
  * Direct volatile in-memory state inspection in `LauncherVpnService` (`isVpnActive`, `isAdFilterActive`, `getActiveProfile`) guarantees instant and 100% accurate status reflection when exiting and re-entering launcher settings.
* **fix(tls-reality-sni-validation):** Implemented strict domain validation on SNI in `GinVpnHub`:
  * Explicitly rejects numeric IP addresses used as SNI targets, preventing Go TLS `x509: cannot validate certificate for <IP> because it doesn't contain any IP SANs` failures that blackholed tunnel traffic.
  * Embedded high-speed verified European fallback nodes in `BUILTIN_EU_NODES` (Czechia Oracle-49, Germany DE-222, Germany Oracle, FFPN) with unblocked Reality ports (8443, 443).

---

### [v032] - 2026-11-07
* **fix(vpn-process-isolation):** Isolated `LauncherVpnService` into a dedicated background process (`android:process=":vpn"` in `AndroidManifest.xml`).
  * Completely shields the main launcher process (`com.gincz.ginlauncher`) from native C/Go teardown routines.
  * Added re-entrancy and double-stop guards in `Tun2socks.kt` and `LauncherVpnService.kt` to eliminate JVM aborts on disconnect.
  * Turning off VPN now cleanly disconnects without ever exiting GIN-Launcher or reverting to the stock Google TV launcher.
* **feat(strict-eu-filtering):** Implemented `GinVpnHub.isStrictlyEuropean()` filter:
  * Restricts node selection strictly to certified European Union countries (DE, NL, PL, AT, FR, etc.).
  * Blacklists non-EU country tags (`RU`, `UZ`, `KZ`, `BY`, `US`) and non-EU TLDs (`.ru`, `.su`, `.by`, `.kz`, `.uz`).
  * Updated European Reality subscription list (`sub_EU.txt`) with 6 verified low-latency nodes in Germany, Netherlands, and Poland.
* **feat(shared-preferences-state):** Persisted VPN and AdGuard DNS modes to `gin_launcher_vpn_state` for multi-process safety and instant UI synchronization across settings dialogs.

---

### [v031] - 2026-11-07
* **feat(dual-arch-native):** Compiled and packaged dual-architecture native libraries (`arm64-v8a` + `armeabi-v7a`) for both Go `tun2socks` and Go `xray-core`.
  * Guarantees 100% native compatibility across all Android TV architectures (64-bit and 32-bit MediaTek, Realtek, Amlogic).
* **feat(embedded-vpn-core):** Integrated native Xray VLESS Reality client and AdGuard DNS tunnel directly into GIN-Launcher without requiring external helper tools or ADB configuration.

---

### [v027] - 2026-11-05
* **ui(settings):** Optimized Control Center Settings dialog layout into an ergonomic **2-Column Layout (`dialog_launcher_settings.xml`)**:
  * Prevents vertical overflow on 720p/1080p/4K TV screens: 4 rows per column (`Left Column`: Wallpapers, Night Mode, Screen Off, Sleep Timer; `Right Column`: Remote Buttons, Android Settings, Accessibility, Power/Reboot).
  * Compact 2x2 grid layout for Programmable Remote Buttons dialog (`dialog_remote_buttons.xml`), eliminating vertical clipping.
* **fix(uninstall):** Fixed application uninstallation failure from long-press OK menu:
  * Added `<uses-permission android:name="android.permission.REQUEST_DELETE_PACKAGES" />` and explicit `<queries>` intent filters for `android.intent.action.DELETE` / `UNINSTALL_PACKAGE`.
  * Added `HomeInterceptorService.pauseForUpdate()` prior to intent dispatch so the accessibility service does not intercept package uninstaller windows.
  * Added multi-tier fallback: `PackageInstaller.Session` / `ACTION_UNINSTALL_PACKAGE` / `ACTION_DELETE` with `Intent.FLAG_ACTIVITY_NEW_TASK`.
* **fix(accessibility):** Resolved Android 13/14 "Restricted Settings" (`ACCESS_RESTRICTED_SETTINGS`) causing accessibility toggle to flip off immediately:
  * Automated root grant via `appops set com.gincz.ginlauncher ACCESS_RESTRICTED_SETTINGS allow`.
  * Auto-activates `HomeInterceptorService` via `settings put secure enabled_accessibility_services`.
* **fix(remote-keys):** Mitigated OEM firmware key hijacking where vendor remote buttons (e.g. Red / YouTube button launching fake `com.android.vending` / Tubesky) pre-empt standard key events:
  * Integrated stock package detection (`handleWindowPackageChanged`) during key capture: auto-returns focus to GIN-Launcher and maps the slot to override the hijacked target package.
  * Disabled predatory pre-installed vendor package `com.android.vending` (Tubesky) via `pm disable-user --user 0`.

---

### [v026] - 2026-11-05
* **feat(remote-mapper):** Added **4 Programmable Remote Control Button Slots (`🎮 Remote Buttons (4 Slots)`)** in the GIN-Launcher Control Center settings (`dialog_launcher_settings.xml`, `dialog_remote_buttons.xml`, `RemoteKeyMapper.kt`):
  * **4 Dedicated Color/Function Slots:**
    1. `🔴 Slot 1 (Red / YouTube)`
    2. `🔵 Slot 2 (Blue / Prime Video)`
    3. `⚪ Slot 3 (White 1)`
    4. `⚪ Slot 4 (White 2)`
  * **Interactive 3-Step Programming Flow:** Select Slot (1–4) ➔ Press any target button on the remote control (`dialog_key_capture.xml`) ➔ Choose any installed application from the visual grid (e.g. `GIN-Tube`, `SmartTube`, `GIN-TV`, `GIN-Cinema`).
  * **Dual-Level Hardware Interception (`KeyEvent` + `ScanCode` + OEM Window Fallback):** Intercepts both standard Android `KeyEvent` / hardware `scanCode` globally via `HomeInterceptorService` (`flagRequestFilterKeyEvents`) and firmware-hardcoded OEM app launches (`TYPE_WINDOW_STATE_CHANGED`).
  * **Automatic Background App Killer (`🧹 Close all other apps on launch`):** Enabled by default. Pressing a programmed button automatically closes all other background/foreground apps (preserving `GIN-Launcher` and Google Cast `com.google.android.apps.mediashell`) before launching the selected app.

---

### [v025] - 2026-10-25
* **feat(power):** Added **Restart (`🔄 Reboot`)** and **Shutdown (`⏻ Power Off`)** buttons in the Control Center settings menu.
* **fix(cast):** Added Google Cast (`com.google.android.apps.mediashell`) safety invariant and self-healing check ensuring stock launcher (`com.google.android.tvlauncher`) is never disabled.

---

### [v024] - 2026-10-25
* **feat(favorites):** Replaced single-item add action in **All Apps** with interactive **Multi-Select Favorites Edit Mode (`⭐ Add / Remove from Favorites`)**:
  * Holding **OK** on any app in **All Apps** and choosing **`⭐ Add / Remove from Favorites`** enters multi-selection mode directly on the All Apps grid.
  * **Visual Status Differentiation:** Apps already in Favorites render in **100% vivid full color**, whereas apps not in Favorites render **grayscale and semi-transparent (`35%` opacity)**.
  * **Multi-Icon Toggle:** Navigate across the grid with D-Pad arrows and press **OK** on any number of icons to toggle them between Favorites (vivid color) and Hidden (grey semi-transparent); press **Back** to save and exit.

---

### [v023] - 2026-10-25
* **feat(favorites):** Added **"⭐ Add to Favorites"** option to the long-press OK context menu inside the **All Apps** tab, allowing previously hidden/removed apps to be restored to Favorites at any time.
* **refactor(menu):** Reordered the long-press OK application context menu (`dialog_app_menu.xml`) across both **Favorites** and **All Apps** tabs so **"ℹ️ App Properties"** is consistently positioned at the very bottom:
  1. `⇄  Move card`
  2. `👁️  Hide from Favorites` (in Favorites) / `⭐  Add to Favorites` (in All Apps)
  3. `🗑️  Uninstall app`
  4. `ℹ️  App properties` (bottom item)

---

### [v022] - 2026-10-25
* **refactor(toolbar):** Reordered top-left action toolbar so Settings (`⚙`) is the rightmost button (`[ ⭐ ] [ ▦ ] [ 🧹 ] [ ⎋ ] [ 🌙 ] [ ⚙ ]`).
* **feat(settings):** Both single click and long press on the Settings (`⚙`) button now directly open the unified GIN-Launcher Control Center menu (providing one-click access to Android System Settings as well as GIN-Launcher settings).
* **style(ui):** Replaced light-grey frosted backgrounds on app cards (`bg_app_item.xml`), top action buttons (`bg_top_btn.xml`, `bg_top_btn_active.xml`), and status pills (`bg_weather_pill.xml`) with dark smoked-glass surfaces (`#B30C1018`, ~30% transparency / 70% dark opacity) so colorful app icons and crisp white Windows 10 network indicators stand out with maximum contrast.

---

### [v021] - 2026-10-25
* **fix(updater):** Eliminated Cloudflare CDN edge caching (`cf-cache-status: HIT` on unversioned `GIN-TV-Launcher_latest.apk`) that previously caused legacy `v017` installations to download a cached `v017` binary during self-updates:
  * Pointed `version.json` `apkUrl` directly to the versioned release binary (`https://gincz.com/GIN-TV-Launcher_v021.apk`).
  * Configured Nginx `Cache-Control: no-store, no-cache, must-revalidate, max-age=0` (`cf-cache-status: BYPASS`) for `.apk` and `.json` endpoints.
  * Added cache-busting query timestamps and `useCaches = false` in `UpdateManager.kt`, restoring `Intent.ACTION_VIEW` as the primary Android TV installer flow.
* **feat(network):** Authentic Windows 10 taskbar network icons (`Segoe MDL2 Assets` geometry):
  * **Ethernet (`ic_net_ethernet.xml`):** Left-side vertical RJ-45 connector head and cable paired with right-side monitor and stand.
  * **Wi-Fi (`ic_net_wifi.xml`):** True concentric 90-degree circular arcs centered on the bottom signal dot.
  * **Pure Opacity States:** 100% white (`alpha = 1.0f`) when connected, 30% opacity (`alpha = 0.30f`) when disconnected, with zero red strikethrough overlays (`ic_slash.xml` removed).

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
* **fix(namedays):** Toggling the Name Day celebration badge to hidden (`Hidden`) now smoothly collapses only the namedays pill without displacing or hiding the sleep timer button or network status.
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
* **feat(namedays):** International Name Day celebrations with dual cultural holiday feeds.
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
