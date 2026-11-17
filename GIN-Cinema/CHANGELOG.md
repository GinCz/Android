# 📜 GIN-Cinema: Release Changelog

> **Project:** GIN-Cinema Android by Vladimir Bulantsev + AI  
> **Repository:** [GinCz/Android ↗](https://github.com/GinCz/Android/tree/main/GIN-Cinema)  
> **Author:** Vladimir Bulantsev ([GinCz ↗](https://github.com/GinCz))  
> **Scope:** Full interactive version log for Android TV, Google TV, and mobile devices.

---

## 🔬 Version History & Architecture Log

*(Click on any release header below to expand details)*

<details open>
<summary><b>🔥 Release v118 (2026-11-18) — High-Definition Instant Seek Previews [Current Release]</b></summary>
<br>

* **Key Capabilities:**
  - **Instant Parallel 3-Card Seek Previews:** The target, previous (-10s), and next (+10s) preview cards are now loaded concurrently in parallel, rendering immediately upon completion without delay or UI blocking.
  - **High-Definition 320x180 (16:9) Resolution:** Upgraded thumbnail extraction resolution from 140x78 to 320x180 pixels, delivering crisp, detailed preview images on 1080p and 4K displays.
  - **Balanced HLS Preview Variant:** Automatically targets ~1.4 Mbps 480p/720p streams instead of low-bitrate 240p/360p streams, eliminating pixelation and macroblock artifacts.
  - **Fast-Path Keyframe Decoding:** Extracts the initial IDR keyframe at -1L in ~20ms, bypassing EOF scan timeouts and decoder lockups on Android TV chipsets (Amlogic, Rockchip, Realtek).
  - **Multi-CDN Media Proxy Support:** Whitelisted Sambray and Ukrtelcdn CDN pools on backend, ensuring seamless segment fetching and 0ms cached retrieval.

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
