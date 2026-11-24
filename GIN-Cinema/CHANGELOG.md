# 📜 GIN-Cinema: Release Changelog

> **Project:** GIN-Cinema Android by Vladimir Bulantsev + AI  
> **Repository:** [GinCz/Android ↗](https://github.com/GinCz/Android/tree/main/GIN-Cinema)  
> **Author:** Vladimir Bulantsev ([GinCz ↗](https://github.com/GinCz))  
> **Scope:** Full interactive version log for Android TV, Google TV, and mobile devices.

---

## 🔬 Version History & Architecture Log

*(Click on any release header below to expand details)*

<details open>
<summary><b>🔥 Release v136 (2026-11-24) — Autonomous Silicon Hardware GIN-ID & Elimination of IPC Binder Deadlocks [Current Release]</b></summary>
<br>

* **Key Capabilities:**
  - **Elimination of Cross-App IPC Deadlocks:** Completely eradicated synchronous `ContentResolver` calls to `com.gincz.gintv.pin_provider`. Fixed immediate launch crashes caused by circular Binder locks when app data was cleared.
  - **Local Silicon Hardware Fingerprint:** Computes the 9-digit GIN-ID directly from CPU silicon features and bootloader motherboard serials locally in 0.001s without invoking external application providers.
  - **Full Production Deployment:** Successfully deployed across production servers (Oracle-49, DE-222 ru-tv.eu, Samba `/storage/user/APK`, MEGA Cloud 02) with all 5 live update checks fully verified.

</details>

<details>
<summary><b>🔥 Release v131 (2026-11-24) — Seamless 3-Row D-Pad Remote Navigation & Voiceover Inspector for Every Video Source [Current Release]</b></summary>
<br>

* **Key Capabilities:**
  - **Seamless D-Pad Traversal (Left / Right / Up / Down):** Resolved focus locking on Android TV remote controls in the in-player "Озвучка" menu. Fixed autoFocus stealing from lower voiceover lists; directional D-Pad navigation now moves naturally left and right across 3 columns of sources, wraps between rows, and allows jumping straight into the voiceovers list on D-Pad Down or OK.
  - **Provider Grouping & Voiceover Counter:** Intelligently groups sources into recognized providers (`⚡ CDNVideoHub`, `🇷🇺 HDRezka`, `🍿 Collaps`, `🍿 DelivEmbed`, `🇨🇿 Videasy`, `🇨🇿 MultiEmbed`, `🇬🇧 VidSrc`) with instant voiceover count badges (e.g. `⚡ CDNVideoHub (4)`).
  - **Comprehensive Voiceover Inspector:** Moving focus to any source in the upper grid instantly displays all available voiceovers and translations in the bottom half of the screen (multi-studio dubs, internal ExoPlayer audio tracks, API translations). Clicking any voiceover switches audio track or source seamlessly while preserving playback timecode.

</details>

<details>
<summary><b>🔥 Release v130 (2026-11-23) — Default Rating-Descending Sort & Interactive Sorting Mode Chips</b></summary>
<br>

* **Key Capabilities:**
  - **Default Rating-Descending Sort:** Search and filter catalog results on both Android TV (`TvSearchScreen.kt`) and Android Phone (`SearchScreen.kt`) now automatically sort movies from highest rating to lowest. Low-rated releases (e.g. 3.8) are pushed down to the end of the catalog, ensuring premier, acclaimed movies occupy top positions.
  - **Intelligent Weighting & Anti-Abuse:** Evaluates ratings taking vote counts into account (`voteCount >= 10`), preventing obscure single-vote 10.0 entries from hijacking top spots while reliably showcasing legitimate blockbusters.
  - **Interactive Sorting Mode Chips:** Added a dedicated 5th row "Сортировка" with mode chips: `⭐ По рейтингу` (default), `🔥 По популярности` (TMDB popularity order), `📅 По новизне` (release date order).
  - **Full D-Pad Remote Navigation:** Smooth focus traversal between country chips, sort chips, search query input, and movie cards grid.

</details>

<details>
<summary><b>🔥 Release v129 (2026-11-23) — Dual Action Buttons, Video Quality Filtration (&ge;480p) & 3-Row Sources Grid with Voiceover Inspector</b></summary>
<br>

* **Key Capabilities:**
  - **Dual Action Row in Detail Cards:** Split the wide "Continue" button into two separate buttons side-by-side on both Android TV (`TvDetailScreen.kt`) and Android Phone (`DetailScreen.kt`): "Продолжить просмотр" on the left and dedicated "Источники" on the right for instant provider switching.
  - **Video Quality Ladder Hardening:** Removed obsolete and unplayable low resolutions (144p, 240p, 360p) from player quality menus (`PlayerMenus.kt`, `PlayerTrackMenu.kt`). Floor quality is strictly locked at 480p minimum.
  - **3-Row / 3-Column Sources Grid & Voiceover Inspector:** Overhauled the player "Озвучка" menu: top half displays source provider buttons neatly arranged in a 3-column / 3-row grid with flag emojis and active indicator; bottom half interactively reveals all voiceovers and audio tracks available for the focused/clicked source.

</details>

<details>
<summary><b>🔥 Release v128 (2026-11-23) — Complete Mobile & TV Settings Parity & Mobile Quality Selection</b></summary>
<br>

* **Key Capabilities:**
  - **100% Settings Parity (Android Phone vs Android TV):** Added all 3 missing settings cards to mobile `ProfileScreen.kt` using sleek horizontally scrollable FilterChips with GinGold accent:
    1. Default video quality (Авто, 1080p, 720p, 480p)
    2. Startup screen (Главная, Избранное, Досмотреть, История, Поиск)
    3. OLED Screen Saver & Idle Dimmer timeout (1 мин, 2 мин, 5 мин, Выкл)
    4. Seek step duration (5с, 10с, 20с, 30с)
  - **Mobile Player Default Quality Support:** Integrated ExoPlayer track selection parameters in `PhonePlayerScreen.kt` honoring user preference on launch.
  - **Seamless Navigation & Dimmer Binding:** Wired up phone startup screen navigation and idle dimmer listeners in phone UI.

</details>

<details>
<summary><b>🔥 Release v127 (2026-11-21) — Manual Source Selection Dialog & WebView Error Fallback</b></summary>
<br>

* **Key Capabilities:**
  - **Manual Source Selection Dialog:** Fast balancer switching directly during playback.
  - **Faster Stream Sniffer:** Streamlined media extraction with automatic WebView fallback.

</details>

<details>
<summary><b>🔥 Release v126 (2026-11-21) — Security Hardening, HMAC Device Auth & WebView Isolation</b></summary>
<br>

* **Key Capabilities:**
  - **HMAC Device Authentication:** Cryptographically signed device tokens for IDOR prevention.
  - **WebView Hardening:** Disabled local file access and external window creation in player webviews.

</details>

<details>
<summary><b>🔥 Release v125 (2026-11-20) — Playback Race Condition Resolution & SSRF Defense</b></summary>
<br>

* **Key Capabilities:**
  - **Playback Race Condition Fix:** Atomic state tracking for episode switching and playlist resolution.
  - **Strict Private Network SSRF Filtering:** Safeguarded internal endpoints from direct player probes.

</details>

<details>
<summary><b>🔥 Release v124 (2026-11-19) — Cross-Device Sync of Source, Voice-Over & Position</b></summary>
<br>

* **Key Capabilities:**
  - **Cross-Device Source & Voice-Over Persistence:** Selected media provider, source, and audio track/voice-over are now synced to the backend along with the playback timestamp.
  - **Seamless Resume:** Opening a movie or series from Favorites, History, or "Continue Watching" on TV or mobile automatically resolves the exact previous source and selects the matching ExoPlayer audio track.
  - **Immediate Progress Flush:** Playback position, active voice-over, and source are persisted continuously during playback and immediately on exit or source switch.

</details>

<details>
<summary><b>🔥 Release v123 (2026-11-19) — TV Movie Cards Border Refinement & Elegant Gold Contour</b></summary>
<br>

* **Key Capabilities:**
  - **33% Slimmer Focused Card Border:** Active focused card border reduced from 3.dp to `2.dp` (`TvCardFocusedBorderWidth`), preserving the rich 3D gold gradient (`GinGoldBrush`) while eliminating visual bulkiness.
  - **Delicate 0.6.dp Gold Outline for Unfocused Cards:** All unfocused movie cards are now contoured with a subtle, razor-thin `0.6.dp` gold border (`TvCardUnfocusedBorderBrush` with 45–65% opacity), more than 3x thinner than the focused card. Renders as a crisp 1-pixel outline on 1080p and 4K TV displays.
  - **Universal UI Application:** Seamlessly applied across `TvHomeScreen` shelves, TOP-200 more card, `TvCategoryScreen` grid, `TvSearchScreen` results, `TvPersonScreen` filmography, `LibraryScreens` (Favorites, History, Unfinished), and `DetailRows` similar movies.

</details>

</details>

<details>
<summary><b>🔥 Release v117 (2026-11-07) — Intelligent Quality Filter & CamRip Elimination</b></summary>
<br>

* **Key Capabilities:**
  - **Automated Low-Quality Stream Filtering:** Integrated multi-tier quality filter preventing the display and playback of video streams below 720p (CAMRip, TS, TeleSync, 350p).
  - **Theatrical-Only Release Blocking:** Catalog and recommendation shelves now verify official digital release dates via TMDB metadata. Theatrical-only or in-production movies are hidden until high-bitrate WEB-DL/BDRip releases become available.
  - **Audio Track Matching Integrity:** Fixed fallback mismatch where missing audio tracks on a selected balancer erroneously switched to unrelated titles with similar names.

</details>

<details>
<summary><b>🔥 Release v114 (2026-10-14) — Stream Engine Optimization & Seamless Updates</b></summary>
<br>

* **Key Capabilities:**
  - Upgraded internal CDN mirror resolver for smooth 4K and 1080p Ultra bitrate playback.
  - Improved adaptive HLS/DASH streaming resilience under variable mobile network bandwidth.
  - Synchronized version reporting and seamless OTA update checking via API.

</details>

<details>
<summary><b>📺 Release v113 (2026-10-13) — Native D-Pad Navigation for Android TV</b></summary>
<br>

* **Key Capabilities:**
  - Full remote control D-Pad navigation tuning for Android TV boxes (Xiaomi Mi Box, Google TV, Smart TV).
  - Smooth card focus animations with subtle zoom and border glow.

</details>

<details>
<summary><b>⚡ Release v108 (2026-10-10) — Hardware ExoPlayer Decoding</b></summary>
<br>

* **Key Capabilities:**
  - Transitioned to customized ExoPlayer profile with forced hardware H.264/HEVC/AV1 acceleration.
  - Minimal CPU overhead on Smart TV processors with zero overheating or thermal throttling.

</details>
