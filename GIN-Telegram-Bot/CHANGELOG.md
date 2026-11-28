# 📝 История изменений (CHANGELOG) — GIN-Telegram-Bot

Все изменения проекта документируются в данном файле.

---

## [v002] — 2026-11-28
### Исправлено и Улучшено
- **Собственный `WakeLock` для `TelegramClient.sendAsync`:** Метод `sendAsync` теперь гарантированно удерживает процессор Android в бодрствующем состоянии на время выполнения сетевого запроса к `api.telegram.org` (таймаут 10 сек). Это исключает засыпание фонового потока после быстрого завершения синхронного `SmsReceiver.onReceive()`.

---

## [v001] — 2026-11-28
### Добавлено
- Полноценная реализация ультралегкого (30 КБ) нативного шлюза `GIN-Telegram-Bot` для замены `Remote Bot Pro`.
- Каркас Gradle-проекта на чистом Android SDK без сторонних фреймворков (`minSdk 21`, `targetSdk 35`, `compileSdk 35`) с оптимизацией Google R8.
- Модуль `SmsReceiver`: перехват SMS с приоритетом 2147483647, автоопределение слота SIM 1/SIM 2 (`SubscriptionManager` + MediaTek extras) и HTML-форматирование.
- Модуль `NotificationListener`: фоновый системный сервис с белым списком банковских и государственных приложений (`ru.tinkoff.activities`, `ru.sberbankmobile`, `ru.gosuslugi.eskiapp` и др.), фильтрацией системного шума и дедупликацией (30 сек).
- Модуль `KumaPinger` и `HeartbeatManager`: периодический пинг сервера Uptime Kuma раз в 15 минут через точный `AlarmManager` с передачей телеметрии (уровень заряда, статус зарядки, SSID и RSSI Wi-Fi, размер офлайн-очереди).
- Модуль `OfflineQueue`: отказоустойчивая локальная база данных SQLite для буферизации сообщений при сбое Wi-Fi и их автоматического сброса в Telegram при восстановлении связи.
- Модуль `TelegramClient`: нативный легковесный транспорт через `HttpURLConnection` для прямой отправки в Telegram Bot API.
- Пользовательский интерфейс `MainActivity`: ввод параметров Bot Token, Chat ID, префикса устройства, названий SIM, Uptime Kuma URL, кнопки проверки связи и диалог выбора приложений с чекбоксами.
- Скрипт автоматизированной сборки и развертывания `build_deploy.sh` с выкладкой в Samba DE-222 (`/storage/user/APK/`) и MEGA Cloud (`MEGA/WIN-FLASH/ANDROID/GIN_APK/`).
