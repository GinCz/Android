# 📜 GIN-Cinema: Release Changelog

> **Project:** GIN-Cinema Android by Vladimir Bulantsev + AI  
> **Repository:** [GinCz/Android ↗](https://github.com/GinCz/Android/tree/main/GIN-Cinema)  
> **Author:** Vladimir Bulantsev ([GinCz ↗](https://github.com/GinCz))  
> **Scope:** Full interactive version log for Android TV, Google TV, and mobile devices.

---

## 🔬 Version History & Architecture Log

*(Click on any release header below to expand details)*

<details open>
<summary><b>🔥 Release v124 (2026-11-19) — Cross-Device Sync of Source, Voice-Over & Position [Current Release]</b></summary>
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
