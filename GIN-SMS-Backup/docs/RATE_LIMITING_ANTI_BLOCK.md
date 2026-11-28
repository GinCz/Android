# 🛡️ Rate Limiting & Anti-Blocking Engine — GIN-SMS-Backup

> **Techniques and protocols implemented in GIN-SMS-Backup to prevent Google IMAP rate-limit flags and account lockouts.**

---

## 1. Google IMAP Rate Limiting Challenges

Google enforces strict undocumented throughput ceilings on `imap.gmail.com:993`:
* **Rapid Re-authentication:** Opening new TLS sessions for individual messages triggers automated security flags (`[ALERT] Web login required`).
* **Burst Concurrency:** Uploading multiple messages concurrently causes `UNAVAILABLE` or `SERVERBUG` throttling.

---

## 2. Defensive Countermeasures

### 2.1. Batch Connection Reuse
Instead of opening a new TCP socket per SMS, **GIN-SMS-Backup** maintains a single TLS session when flushing queued messages:
```
Open TLS ➔ Authenticate ➔ Upload Msg 1 ➔ Upload Msg 2 ➔ ... ➔ Logout ➔ Close Socket
```

### 2.2. Enforced 3-Second Pacing Delay
Between consecutive `APPEND` calls in a batch, the client sleeps for 3,000 milliseconds:
```java
Thread.sleep(3000); // 3-second pacing delay to respect Google IMAP limits
```

### 2.3. Exponential Backoff on Transient Errors
If the server returns `TEMPORARY ERROR` or `TRYAGAIN`:
1. Wait 30 seconds before retry.
2. If it fails again, wait 60 seconds, then 120 seconds.
3. Abort the batch after 3 consecutive failures to protect account standing.

---

## 🏷 Topics & Tags

#RateLimiting #AntiFlood #GoogleIMAP #TLSConnection #NetworkDefense #HighAvailability
