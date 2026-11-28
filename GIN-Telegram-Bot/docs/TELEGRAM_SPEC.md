# 💬 Telegram Message Specification — GIN-Telegram-Bot

> **HTML message formatting standards, Dual-SIM tagging, sanitization rules, and outbound Telegram Bot payload structures.**

---

## 1. Incoming SMS Message Formatting

Outbound Telegram notifications use Telegram's native HTML parse mode (`parse_mode=HTML`) for maximum readability, clean monospace formatting of senders and codes, and instant mobile glanceability.

### 📩 SMS Template (SIM 1):
```html
📩 <b>[SIM 1 • Operator A]</b> <i>10:45:12</i>
<b>На номер:</b> <code>+19870001122</code>
<b>От:</b> <code>+19876543210</code>

Your verification code is: <b>849201</b>. Do not share this code with anyone.
```

### 📩 SMS Template (SIM 2):
```html
📩 <b>[SIM 2 • Operator B]</b> <i>10:46:05</i>
<b>На номер:</b> <code>+19870003344</code>
<b>От:</b> <code>BANK_ALERT</code>

Card *4829: Transaction of $45.20 at GROCERY STORE. Available balance: $1,240.50.
```

---

## 2. App Push Notification Formatting

Every push notification forwarded by `NotificationListenerService` includes an application badge, timestamp, title, and body text.

### 🏦 Banking / Financial Push:
```html
🏦 <b>[MyBank • Push]</b> <i>11:15:22</i>
<b>Title:</b> Purchase Approved: $32.50
<b>Text:</b> Supermarket Store #102. Remaining balance: $2,840.10
```

### 🏛 Government & 2FA Services:
```html
🏛 <b>[Auth Service • Push]</b> <i>12:00:10</i>
<b>Title:</b> New Login Request
<b>Text:</b> Login attempt detected from Web Browser. Tap to verify.
```

---

## 3. System & Telemetry Alerts

### 🚀 Service Online / Boot Alert:
```html
🚀 <b>[GIN-Telegram-Bot] Gateway Started</b>
<b>Device:</b> <code>DOOGEE X3</code>
<b>Android:</b> <code>5.1 (API 22)</code>
<b>Queue:</b> <code>0 pending</code>
<b>Status:</b> All SMS & Notification listeners active.
```

### 🔋 Battery / Low Power Warning:
```html
⚠️ <b>[GIN-Telegram-Bot] Low Battery Warning</b>
<b>Device:</b> <code>DOOGEE X3</code>
<b>Level:</b> <code>14% (Discharging)</code>
<b>Notice:</b> Please connect the gateway device to AC power.
```

---

## 4. HTML Escaping Protocol

All raw strings received from cellular broadcasts and third-party notifications are automatically sanitized to prevent Telegram HTML parse errors:

```java
private static String escapeHtml(String text) {
    if (text == null) return "";
    return text.replace("&", "&amp;")
               .replace("<", "&lt;")
               .replace(">", "&gt;");
}
```

---

## 🏷 Topics & Tags

#Android #TelegramBot #TelegramGateway #SMSForwarder #DualSIM #HTMLParsing #ZeroBloat #OpenSource
