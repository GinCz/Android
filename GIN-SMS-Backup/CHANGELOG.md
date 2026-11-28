# 📝 Changelog — GIN-SMS-Backup

All notable changes to this project will be documented in this file.

---

## [v001-plan] — 2026-11-28
### Added
- Initial open-source public specification and architecture design.
- Direct Gmail IMAP synchronization over native TLS 1.2+ (imap.gmail.com:993).
- Zero Google Play Services dependencies (eliminating 200+ MB RAM bloat).
- Anti-flood rate limiting engine with 3-second pacing between APPEND operations.
- Single TLS session connection reuse for batched message uploads.
- RFC 2822 email format generator with Dual-SIM slot tagging.