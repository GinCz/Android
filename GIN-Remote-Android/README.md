# 📱 GIN-Remote-Android (v017)
> **Universal Remote Management & Reverse ADB Bridge for Android 5.0–15+ (TVs, TV Sticks, Tablets & Mobile Devices)**  
> **Author:** Vladimir Bulantsev (@GinCz) | **Platform:** Android (minSdk 21, compileSdk 35)

---

## 🚀 Overview

`GIN-Remote-Android` is a resilient, lightweight reverse ADB bridge and screen mirroring client. It allows seamless, secure administrative remote access and troubleshooting across Android devices without requiring public IP addresses, router port forwards, or third-party cloud services.

### ✨ Key Features

1. **Universal Android Compatibility (minSdk 21):** Runs natively on Android 5.0 (Lollipop) all the way to Android 15+.
2. **On-Demand Mobile Resource Economy:** Does not keep persistent background loops on mobile phones. Runs when started by the user and fully frees memory upon exit (`killProcess`).
3. **On-Demand Screen Mirroring:** Screen capture (`MediaProjection`) is decoupled from the connection tunnel and activated strictly via a dedicated button, saving battery and CPU cycles.
4. **Automated Telegram Session Alerts:** Dispatches instant HTML alerts to the administrator's Telegram bot when an authorized device establishes connection.
5. **Adaptive UI:** Responsive layout supporting both horizontal 1080p/4K TV screens (D-Pad leanback navigation) and vertical smartphone screens.
6. **Encrypted Reverse Tunnel:** Low-latency socket bridge relaying ADB commands back to the master controller (`Oracle-49:17000`).

---

## 🏗️ Architecture

```
[Android Device: TV / Smartphone / Stick]
          │
          │ (Encrypted TCP Tunnel :17000)
          ▼
   [Oracle-49 Gateway]
          ▲
          │ (ADB Shell / Control)
   [Administrator / Web Studio]
```

## 🛠️ Building from Source

```bash
# Clone the repository
git clone https://github.com/GinCz/Android.git
cd Android/GIN-Remote-Android

# Configure your server settings
cp app/src/main/java/com/gincz/remote/SecurityConfig.example.kt app/src/main/java/com/gincz/remote/SecurityConfig.kt

# Build Release APK
./gradlew assembleRelease
```

---

## 📜 License & Copyright

Copyright © 2026 Vladimir Bulantsev (`VladiMIR+AI`). All rights reserved.
