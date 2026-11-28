# 📬 Gmail IMAP Protocol Specification — GIN-SMS-Backup

> **RFC 3501 IMAP over TLS and RFC 2822 MIME standards for synchronizing SMS conversations into Google Gmail.**

---

## 1. Connection Parameters

* **IMAP Host:** `imap.gmail.com`
* **IMAP Port:** `993` (SSL / TLS)
* **Encryption Protocol:** TLS 1.2+
* **Authentication Method:** Google App Password (16 characters) via `LOGIN` command.
* **Target Mailbox Folder:** `SMS`

---

## 2. IMAP Session Lifecycle

```
Client                                                  Gmail Server
  |                                                          |
  |  ----------------- TLS 1.2 Handshake ----------------->  |
  |  <---------------- OK imap.gmail.com Ready ------------  |
  |                                                          |
  |  A01 LOGIN "your_email@gmail.com" "xxxx xxxx xxxx xxxx"  |
  |  ----------------------------------------------------->  |
  |  <---------------- A01 OK Authenticated ---------------  |
  |                                                          |
  |  A02 SELECT "SMS"                                        |
  |  ----------------------------------------------------->  |
  |  <---------------- A02 OK [READ-WRITE] SELECT completed - |
  |                                                          |
  |  A03 APPEND "SMS" (\Seen) {MIME_BYTE_COUNT}              |
  |  ----------------------------------------------------->  |
  |  <---------------- + Go ahead -------------------------  |
  |                                                          |
  |  [RFC 2822 Raw MIME Stream]                              |
  |  ----------------------------------------------------->  |
  |  <---------------- A03 OK APPEND completed ------------  |
  |                                                          |
  |  A04 LOGOUT                                              |
  |  ----------------------------------------------------->  |
  |  <---------------- A04 OK LOGOUT completed ------------  |
```

---

## 3. RFC 2822 MIME Message Header Template

```email
From: "+19876543210" <+19876543210@sms-backup.local>
To: "User" <your_email@gmail.com>
Subject: SMS with +19876543210 [SIM 1]
Date: Fri, 28 Nov 2026 10:45:12 +0100
Message-ID: <1764321912.482910@sms-backup.local>
X-smssync-address: +19876543210
X-smssync-type: 1
X-smssync-sim: 0
MIME-Version: 1.0
Content-Type: text/plain; charset=UTF-8
Content-Transfer-Encoding: 8bit

Your security code is 849201.
```

---

## 🏷 Topics & Tags

#Gmail #IMAP #RFC3501 #RFC2822 #SMSBackup #TLS #OpenSource
