# 📜 GIN-IP: Release Changelog

> **Project:** GIN-IP Android by Vladimir Bulantsev + AI  
> **Repository:** [GinCz/Android ↗](https://github.com/GinCz/Android/tree/main/GIN-IP)  
> **Author:** Vladimir Bulantsev ([GinCz ↗](https://github.com/GinCz))  
> **Scope:** Comprehensive version log for the network diagnostics, geolocation, and intelligent Speed Test utility.

---

## 🔬 Version History & Architecture Log

*(Click on any release header below to expand details)*

<details open>
<summary><b>🔥 Release v018 (2026-11-08) — Intelligent Speed Test with ISP Matching & Multi-Stream [Current Release]</b></summary>
<br>

* **Key Capabilities:**
  - **ISP Matching First:** Automatically resolves active carrier name and ASN (`INTERCONNECT s.r.o.`, `Vodafone`, `CETIN`, `O2`). When carrier test nodes are present, prioritizes on-net servers over overloaded third-party nodes.
  - **3-Position Target Node Selector:**
    - `⚡ AUTO (ISP)`: Dynamically selects nearest on-net carrier server to measure true connection tier bandwidth (default).
    - `🇪🇺 DE (222)`: Direct latency and throughput benchmark to private server in Germany (NetCup).
    - `🇷🇺 RU (109)`: Direct latency and throughput benchmark to private server in Russia (FastVDS Moscow).
  - **Top-5 Ping Race Fallback:** When no local on-net server is detected, executes multi-packet ICMP/HTTP ping race against 5 nearest hosts with auto-elimination of stalled nodes.
  - **Multi-Stream Progressive Download:** Progressively scales chunk sizes (4.4 MB to 24.1 MB) across concurrent threads to saturate high-speed Gigabit connections (up to 1 Gbps).
  - **Interface Update:** Subtitle telemetry displays active server and latency (`★ INTERCONNECT s.r.o. (Prague) — 2 ms`), with one-tap clipboard report export.

</details>

<details>
<summary><b>🚀 Release v017 (2026-10-18) — Footer Signature & Visual Polish</b></summary>
<br>

* **Key Capabilities:**
  - Clean sand-yellow signature text `VladiMIR+AI ||| v017` pinned to bottom edge.
  - Removed redundant border framing on footer button for a cleaner Android TV interface.

</details>

<details>
<summary><b>🚀 Release v016 (2026-10-14) — Dynamic Localization & City Deduplication</b></summary>
<br>

* **Key Capabilities:**
  - Instant live localization switching for interface labels and region names without app restart.
  - Resolved city name deduplication in geolocation strings.

</details>

<details>
<summary><b>🚀 Release v010 (2026-10-08) — Baseline Release of GIN-IP</b></summary>
<br>

* **Key Capabilities:**
  - Lightweight Material 3 UI with pure OLED dark theme.
  - Real-time display of local LAN/Wi-Fi IP, default gateway, and hardware MAC address.

</details>
