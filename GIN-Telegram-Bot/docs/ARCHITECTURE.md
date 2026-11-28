# 🏗 Architecture & System Design — GIN-Telegram-Bot

> **Technical overview of event interception, queue resilience, and Telegram dispatch pipelines.**

---

## 1. High-Level Flowchart

`mermaid
flowchart TD
    subgraph Device ["Android Phone"]
        subgraph Triggers ["System Triggers"]
            SMS["Incoming SMS (SIM 1 / SIM 2)"]
            PUSH["Selected App Push (Bank, 2FA)"]
            TMR["Heartbeat Timer (every 15 min)"]
        end

        subgraph Interceptors ["Native Interceptors"]
            SR["SmsBroadcastReceiver\n(Priority: 2147483647)"]
            NLS["NotificationListenerService\n(Whitelist Filter)"]
            HBM["HeartbeatManager\n(AlarmManager)"]
        end

        subgraph Core ["Engine Core"]
            FMT["HTML Message Formatter"]
            Q["Local SQLite Queue"]
            HTTP["Native HttpURLConnection Client"]
        end

        SMS --> SR --> FMT
        PUSH --> NLS --> FMT
        TMR --> HBM --> FMT

        FMT --> Q --> HTTP
    end

    subgraph Cloud ["External Endpoints"]
        TG["Telegram Bot API\n(api.telegram.org)"]
        KUMA["Uptime Kuma\n(Push Monitor)"]
    end

    HTTP -->|Direct POST| TG
    HTTP -->|Push GET Ping| KUMA
`

---

## 2. Core Modules

1. **SmsBroadcastReceiver:**
   - Registers for ndroid.provider.Telephony.SMS_RECEIVED with maximum priority (2147483647).
   - Extracts sender phone number, message text (reassembling multi-part PDU segments), and hardware SIM slot index.
2. **NotificationListenerService:**
   - Checks incoming status bar notifications against user-configured package whitelist.
   - Filters out ongoing system noises (media players, download bars, charging status).
3. **HeartbeatManager:**
   - Issues periodic GET request to Uptime Kuma Push URL every 15 minutes.
   - Attaches diagnostic metrics: battery percentage, power source, and Wi-Fi RSSI signal level.
4. **OfflineQueue & HttpDispatcher:**
   - Uses zero third-party libraries (no OkHttp/Retrofit overhead).
   - Direct TLS socket communication via native HttpURLConnection.
   - Buffered SQLite queue guarantees zero message loss during temporary network drops.