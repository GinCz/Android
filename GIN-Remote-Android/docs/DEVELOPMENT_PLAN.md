# 📋 GIN-Remote-Android: Architecture & Development Plan (v017)

> **Project:** `GIN-Remote-Android`  
> **Repository:** [GinCz/Android](https://github.com/GinCz/Android)  
> **Author:** Vladimir Bulantsev (@GinCz)  
> **Version:** `v017` (Universal release for Android 5.0–15+)  
> **Date:** 2026-11-28  

---

## 🎯 Project Goals

`GIN-Remote-Android` is a lightweight, high-performance remote management and ADB bridge client designed for universal compatibility across Android Smart TVs, TV sticks, tablets, and mobile smartphones running Android 5.0 (API 21) through Android 15+.

### Key Architectural Pillars:
1. **Universal Android Support (`minSdk = 21`):** Native compatibility from legacy Android 5.0 (Lollipop) up to modern Android 15.
2. **Strict On-Demand Execution (Zero RAM Waste):** Completely avoids background daemon persistence on phones. The app runs only when needed by the user. Pressing "Exit" kills the process (`killProcess`), freeing 100% of memory.
3. **On-Demand Screen Mirroring:** Screen capture (`MediaProjection`) is decoupled from the connection process and activated strictly upon user request via a dedicated button, saving CPU cycles on low-RAM devices.
4. **Automated Telegram Session Alerts:** Automatically notifies the administrator in Telegram when an authorized remote session connects.
5. **Secure Reverse ADB Tunnel:** Encrypted socket connection back to the central server for zero-config remote troubleshooting, log analysis, package installation, and cache cleaning.

---

## 📅 Step-by-Step Implementation Roadmap

1. **Build System & SDK Upgrade:**
   - Set `minSdk = 21`, `targetSdk = 33`, `compileSdk = 35`.
   - Update version code to `17` (`v017`).
2. **OS Version Adaptation:**
   - Graceful backward compatibility for `NotificationChannel` (API 26+) and `startForegroundService`.
   - Backward compatibility for `AccessibilityService` gesture dispatch (API 24+) with ADB fallback.
3. **UI Layout Adaptability:**
   - Seamlessly adaptive layout for both landscape 1080p/4K TVs and portrait 480p/1080p mobile devices.
4. **Telegram Notification Engine:**
   - Lightweight asynchronous HTTP client delivering connection events directly to Telegram bot chat.
5. **Deployment & Verification:**
   - Strict 2-target delivery: Samba share `\\s.gincz.com\user\APK\` and MEGA Cloud `/MEGA/WIN-FLASH/ANDROID/GIN_APK/`.
   - Hardware validation on low-RAM physical Android devices.
