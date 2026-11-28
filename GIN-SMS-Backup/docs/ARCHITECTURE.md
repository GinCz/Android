# 🏗 Architecture & System Design — GIN-SMS-Backup

> **Technical overview of direct IMAP synchronization, pacing mechanics, and offline queue resilience.**

---

## 1. High-Level Flowchart

`mermaid
flowchart TD
    subgraph Device ["Android Phone"]
        subgraph Triggers ["System Triggers"]
            SMS["Incoming SMS (SIM 1 / SIM 2)"]
            BOOT["Boot Completed (Auto-Start)"]
            NET["Network Online Event"]
        end

        subgraph Interceptors ["Native Interceptors"]
            SR["SmsBroadcastReceiver\n(Priority: 2147483647)"]
            SVC["ImapSyncService\n(Foreground Service + WakeLock)"]
        end

        subgraph Storage ["Local Storage"]
            DB[("SQLite Database\n(Pending SMS Queue)")]
        end

        subgraph Core ["Engine Core"]
            MIME["RFC 2822 MIME Builder"]
            RATE["RateLimiter Engine\n(3.0s Delay & Reusing TLS)"]
            IMAP["Native SSLSocket IMAP Client"]
        end

        SMS --> SR -->|Atomic Write| DB
        BOOT --> SVC
        NET --> SVC

        SVC -->|Poll Pending| DB --> MIME --> RATE --> IMAP
    end

    subgraph Google ["Google Cloud"]
        GMAIL["Gmail IMAP Server\n(imap.gmail.com:993)\nFolder: 'SMS'"]
    end

    IMAP -->|SSL/TLS 1.2+| GMAIL
`

---

## 2. Core Modules

1. **SmsBroadcastReceiver:**
   - Intercepts incoming SMS immediately and writes them to local SQLite database in single-digit milliseconds.
   - Wakes up ImapSyncService without performing blocking network operations inside the receiver.
2. **ImapSyncService:**
   - Foreground Service with low-priority notification ensuring survival under low RAM conditions.
   - Holds a temporary partial WakeLock while uploading messages to prevent CPU suspension.
3. **RateLimiter:**
   - Enforces a 3.0-second pause between successive APPEND commands.
   - Reuses a single authenticated TLS socket across batches of queued messages.
   - Implements Exponential Backoff when encountering temporary network hiccups or Google server limits.
4. **ImapTlsClient:**
   - Direct lightweight socket implementation via standard Java SSLSocketFactory.
   - Executes standard RFC 3501 commands: LOGIN, SELECT, APPEND, and LOGOUT.