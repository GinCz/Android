package com.gincz.remote

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.Inet4Address
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.Executors

class RemoteForegroundService : Service() {

    enum class State {
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }

    interface Listener {
        fun onStateChanged(state: State)
        fun onLogAdded(logLine: String)
    }

    companion object {
        const val ACTION_START = "com.gincz.remote.ACTION_START"
        const val ACTION_STOP = "com.gincz.remote.ACTION_STOP"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "gin_remote_bridge_channel"

        val SERVER_HOST: String get() = SecurityConfig.getServerHost()
        val SERVER_PORT: Int get() = SecurityConfig.SERVER_PORT
        val ADB_LOCAL_PORT: Int get() = SecurityConfig.ADB_LOCAL_PORT
        val AUTH_TOKEN: String get() = SecurityConfig.getAuthToken()

        @Volatile
        var currentState: State = State.DISCONNECTED
            private set

        @Volatile
        var activeInstance: RemoteForegroundService? = null
            private set

        private val listeners = CopyOnWriteArrayList<Listener>()
        private val logHistory = ArrayList<String>()
        private const val MAX_LOGS = 300

        fun stopServiceCleanly(context: Context) {
            try {
                activeInstance?.stopBridge()
            } catch (_: Throwable) {}
            try {
                ScreenCaptureManager.activeInstance?.stop()
            } catch (_: Throwable) {}
            try {
                context.stopService(Intent(context, RemoteForegroundService::class.java))
            } catch (_: Throwable) {}
            try {
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                nm?.cancel(NOTIFICATION_ID)
            } catch (_: Throwable) {}
            currentState = State.DISCONNECTED
            for (l in listeners) {
                l.onStateChanged(State.DISCONNECTED)
            }
        }

        fun addListener(l: Listener) {
            listeners.add(l)
            l.onStateChanged(currentState)
            synchronized(logHistory) {
                for (line in logHistory) {
                    l.onLogAdded(line)
                }
            }
        }

        fun removeListener(l: Listener) {
            listeners.remove(l)
        }

        fun addLog(msg: String) {
            val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            val formatted = "[$time] $msg"
            synchronized(logHistory) {
                if (logHistory.size >= MAX_LOGS) {
                    logHistory.removeAt(0)
                }
                logHistory.add(formatted)
            }
            for (l in listeners) {
                l.onLogAdded(formatted)
            }
        }
    }

    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceRunning = false
    private val executor = Executors.newCachedThreadPool()
    private var controlSocket: Socket? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        activeInstance = this
        try {
            createNotificationChannel()
            val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
            wakeLock = pm?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "GIN-Remote::WakeLock")?.apply {
                setReferenceCounted(false)
            }
        } catch (_: Throwable) {}
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val isTv = try {
            packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_LEANBACK)
        } catch (_: Throwable) {
            false
        }
        val action = intent?.action ?: if (isTv) ACTION_START else ACTION_STOP
        when (action) {
            ACTION_START -> {
                if (!isServiceRunning) {
                    startForegroundServiceInternal()
                }
                return if (isTv) START_STICKY else START_NOT_STICKY
            }
            ACTION_STOP -> {
                stopBridge()
                stopSelf()
                return START_NOT_STICKY
            }
        }
        return START_NOT_STICKY
    }

    private fun startForegroundServiceInternal() {
        isServiceRunning = true
        try {
            wakeLock?.acquire(24 * 60 * 60 * 1000L) // 24 hours
        } catch (_: Throwable) {}

        try {
            val notification = buildNotification("Подключение к Oracle-49...")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    startForeground(
                        NOTIFICATION_ID,
                        notification,
                        ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
                    )
                } catch (_: Throwable) {
                    startForeground(NOTIFICATION_ID, notification)
                }
            } else {
                startForeground(NOTIFICATION_ID, notification)
            }
        } catch (e: Throwable) {
            addLog("[-] Предупреждение службы уведомлений: ${e.message}")
        }

        executor.execute { runBridgeLoop() }
        ensureAdbTcpListening()
    }

    private fun ensureAdbTcpListening() {
        executor.execute {
            try {
                var isListening = false
                try {
                    Socket().use { s ->
                        s.connect(InetSocketAddress("127.0.0.1", ADB_LOCAL_PORT), 1000)
                        isListening = true
                    }
                } catch (_: Throwable) {
                    isListening = false
                }

                if (!isListening) {
                    val suExists = listOf("/system/bin/su", "/system/xbin/su", "/sbin/su", "/vendor/bin/su")
                        .any { java.io.File(it).exists() }
                    if (suExists) {
                        addLog("[⚡] Порт 5555 закрыт, активация TCP ADB через встроенный Root...")
                        val process = Runtime.getRuntime().exec(arrayOf("su", "0", "sh", "-c", "setprop persist.adb.tcp.port 5555; setprop service.adb.tcp.port 5555; stop adbd; start adbd"))
                        process.waitFor(3, java.util.concurrent.TimeUnit.SECONDS)
                        addLog("[✅] Команда активации TCP ADB (порт 5555) выполнена через Root")
                    } else {
                        addLog("[ℹ️] Локальный порт 5555 закрыт (Root не обнаружен, требуется adb tcpip 5555)")
                    }
                }
            } catch (e: Throwable) {
                addLog("[⚠️] Ошибка при проверке/активации ADB TCP: ${e.message}")
            }
        }
    }

    private fun updateState(newState: State) {
        currentState = newState
        for (l in listeners) {
            l.onStateChanged(newState)
        }
        if (!isServiceRunning) {
            try {
                val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
                nm?.cancel(NOTIFICATION_ID)
            } catch (_: Throwable) {}
            return
        }
        val text = when (newState) {
            State.CONNECTED -> "Подключено к Oracle-49 (92.5.111.49:17000)"
            State.CONNECTING -> "Подключение к шлюзу Oracle-49..."
            State.DISCONNECTED -> "Переподключение к Oracle-49..."
        }
        try {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            nm?.notify(NOTIFICATION_ID, buildNotification(text))
        } catch (_: Throwable) {}
    }

    private fun runBridgeLoop() {
        val vName = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (_: Exception) {
            "v017"
        }
        addLog("[⚡] Запуск службы GIN-Remote $vName")
        addLog("[ℹ️] Шлюз: $SERVER_HOST:$SERVER_PORT | Локальный ADB: 127.0.0.1:$ADB_LOCAL_PORT")

        while (isServiceRunning) {
            updateState(State.CONNECTING)
            addLog("[⏳] Подключение к центральному серверу Oracle-49...")

            var socket: Socket? = null
            try {
                socket = Socket()
                socket.tcpNoDelay = true
                socket.keepAlive = true
                socket.connect(InetSocketAddress(SERVER_HOST, SERVER_PORT), 10000)
                controlSocket = socket

                val out = socket.getOutputStream()
                val reader = BufferedReader(InputStreamReader(socket.getInputStream(), "UTF-8"))

                val pin = DeviceUtils.getHardwareDeviceId(this)
                val deviceName = "${Build.MANUFACTURER}_${Build.MODEL}_${pin}".replace(" ", "_")
                val helloMsg = "HELLO $AUTH_TOKEN $deviceName\n"
                out.write(helloMsg.toByteArray(Charsets.UTF_8))
                out.flush()

                val ack = reader.readLine()
                if (ack != null && ack.trim() == "HELLO_OK") {
                    updateState(State.CONNECTED)
                    addLog("[✅] Соединение с Oracle-49 успешно установлено!")
                    addLog("[🔑] GIN PIN устройства: $pin")
                    addLog("[✅] Устройство авторизовано: '$deviceName'")
                    addLog("[✅] Туннель активен: Доступно для удаленного управления")

                    // Send Telegram Alert
                    val deviceLabel = "${Build.MANUFACTURER} ${Build.MODEL}".trim()
                    val localIp = getLocalIpAddress()
                    sendTelegramAlert(deviceLabel, localIp, pin)

                    // Start Ping Thread
                    executor.execute {
                        try {
                            while (isServiceRunning && controlSocket == socket && !socket.isClosed) {
                                Thread.sleep(15000)
                                out.write("PING\n".toByteArray(Charsets.UTF_8))
                                out.flush()
                            }
                        } catch (_: Exception) {}
                    }

                    // Main command loop
                    var line = reader.readLine()
                    while (isServiceRunning && line != null) {
                        val cmd = line.trim()
                        if (cmd.startsWith("OPEN ")) {
                            val sid = cmd.substring(5).trim()
                            executor.execute { handleStream(sid) }
                        } else if (cmd.startsWith("TAP ")) {
                            val parts = cmd.split(" ")
                            if (parts.size >= 3) {
                                val x = parts[1].toFloatOrNull() ?: 0f
                                val y = parts[2].toFloatOrNull() ?: 0f
                                val ok = RemoteAccessibilityService.performTapRatio(x, y)
                                if (ok) addLog("[⚡] Тап по экрану: ($x, $y)")
                            }
                        } else if (cmd.startsWith("SWIPE ")) {
                            val parts = cmd.split(" ")
                            if (parts.size >= 5) {
                                val x1 = parts[1].toFloatOrNull() ?: 0f
                                val y1 = parts[2].toFloatOrNull() ?: 0f
                                val x2 = parts[3].toFloatOrNull() ?: 0f
                                val y2 = parts[4].toFloatOrNull() ?: 0f
                                val dur = parts.getOrNull(5)?.toLongOrNull() ?: 300L
                                val ok = RemoteAccessibilityService.performSwipeRatio(x1, y1, x2, y2, dur)
                                if (ok) addLog("[⚡] Свайп: ($x1,$y1)->($x2,$y2)")
                            }
                        } else if (cmd.startsWith("KEY ")) {
                            val key = cmd.substring(4).trim().lowercase()
                            when (key) {
                                "back" -> RemoteAccessibilityService.performGlobal(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_BACK)
                                "home" -> RemoteAccessibilityService.performGlobal(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_HOME)
                                "recents" -> RemoteAccessibilityService.performGlobal(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_RECENTS)
                                "notifications" -> RemoteAccessibilityService.performGlobal(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS)
                                "power" -> {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                        RemoteAccessibilityService.performGlobal(android.accessibilityservice.AccessibilityService.GLOBAL_ACTION_LOCK_SCREEN)
                                    }
                                }
                                "up" -> RemoteAccessibilityService.performSwipeRatio(0.5f, 0.7f, 0.5f, 0.3f, 250L)
                                "down" -> RemoteAccessibilityService.performSwipeRatio(0.5f, 0.3f, 0.5f, 0.7f, 250L)
                                "left" -> RemoteAccessibilityService.performSwipeRatio(0.8f, 0.5f, 0.2f, 0.5f, 250L)
                                "right" -> RemoteAccessibilityService.performSwipeRatio(0.2f, 0.5f, 0.8f, 0.5f, 250L)
                                "ok", "center" -> RemoteAccessibilityService.performTapRatio(0.5f, 0.5f)
                                "volup", "volume_up" -> {
                                    val am = getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
                                    am?.adjustSuggestedStreamVolume(android.media.AudioManager.ADJUST_RAISE, android.media.AudioManager.USE_DEFAULT_STREAM_TYPE, android.media.AudioManager.FLAG_SHOW_UI)
                                }
                                "voldown", "volume_down" -> {
                                    val am = getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
                                    am?.adjustSuggestedStreamVolume(android.media.AudioManager.ADJUST_LOWER, android.media.AudioManager.USE_DEFAULT_STREAM_TYPE, android.media.AudioManager.FLAG_SHOW_UI)
                                }
                                "mute" -> {
                                    val am = getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
                                    am?.adjustSuggestedStreamVolume(android.media.AudioManager.ADJUST_TOGGLE_MUTE, android.media.AudioManager.USE_DEFAULT_STREAM_TYPE, android.media.AudioManager.FLAG_SHOW_UI)
                                }
                                "play", "play_pause" -> {
                                    val am = getSystemService(Context.AUDIO_SERVICE) as? android.media.AudioManager
                                    val eventDown = android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
                                    val eventUp = android.view.KeyEvent(android.view.KeyEvent.ACTION_UP, android.view.KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
                                    am?.dispatchMediaKeyEvent(eventDown)
                                    am?.dispatchMediaKeyEvent(eventUp)
                                }
                            }
                            addLog("[⚡] Клавиша: $key")
                        } else if (cmd.startsWith("OPEN_APP_SETTINGS ")) {
                            val pkg = cmd.substring(18).trim()
                            if (pkg.isNotEmpty()) {
                                try {
                                    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = android.net.Uri.parse("package:$pkg")
                                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    }
                                    startActivity(intent)
                                    addLog("[⚡] Открыты системные настройки для: $pkg")
                                } catch (e: Exception) {
                                    addLog("[-] Ошибка открытия настроек $pkg: ${e.message}")
                                }
                            }
                        } else if (cmd == "LIST_PACKAGES") {
                            executor.execute {
                                try {
                                    val pm = packageManager
                                    val pkgs = pm.getInstalledPackages(0)
                                    val list = pkgs.map {
                                        val appInfo = it.applicationInfo
                                        val isSys = appInfo != null && (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
                                        "${it.packageName}|${if (isSys) "SYS" else "USER"}"
                                    }
                                    val data = list.joinToString(",")
                                    out.write("PACKAGES $data\n".toByteArray(Charsets.UTF_8))
                                    out.flush()
                                    addLog("[ℹ️] Список установленных пакетов (${pkgs.size}) передан на сервер")
                                } catch (e: Exception) {
                                    addLog("[-] Ошибка получения списка пакетов: ${e.message}")
                                }
                            }
                        } else if (cmd == "PONG") {
                            // Heartbeat ok
                        }
                        line = reader.readLine()
                    }
                } else {
                    addLog("[❌] Ошибка авторизации сервера: $ack")
                    addLog("[❌] Доступ отклонен: Неверный ключ безопасности (XOR Token)")
                }
            } catch (e: java.net.UnknownHostException) {
                if (isServiceRunning) {
                    addLog("[❌] Ошибка DNS: Не удалось найти сервер $SERVER_HOST")
                    addLog("[❌] Причина: Отсутствует интернет или сбой DNS провайдера")
                }
            } catch (e: java.net.ConnectException) {
                if (isServiceRunning) {
                    addLog("[❌] Ошибка подключения: Сервер $SERVER_HOST:$SERVER_PORT недоступен")
                    addLog("[❌] Причина: Порт заблокирован (Фаервол роутера / РКН / Сервер выключен)")
                }
            } catch (e: java.net.SocketTimeoutException) {
                if (isServiceRunning) {
                    addLog("[❌] Превышен таймаут ответа сервера (10с): $SERVER_HOST")
                    addLog("[❌] Причина: Медленная сеть, фильтрация TCP пакетов или блокировка провайдером")
                }
            } catch (e: Exception) {
                if (isServiceRunning) {
                    addLog("[❌] Разрыв связи: ${e.message ?: e.javaClass.simpleName}")
                    addLog("[❌] Причина: Нестабильная сеть или принудительный сброс TCP")
                }
            } finally {
                closeQuietly(socket)
                controlSocket = null
            }

            if (isServiceRunning) {
                val isTv = try {
                    packageManager.hasSystemFeature(android.content.pm.PackageManager.FEATURE_LEANBACK)
                } catch (_: Throwable) {
                    false
                }
                updateState(State.DISCONNECTED)
                if (isTv) {
                    addLog("[🔄] Автопереподключение через 5 секунд...")
                    try {
                        Thread.sleep(5000)
                    } catch (_: InterruptedException) {
                        break
                    }
                } else {
                    addLog("[ℹ️] Режим On-Demand: связь завершена. Для подключения нажмите кнопку")
                    isServiceRunning = false
                    break
                }
            }
        }

        updateState(State.DISCONNECTED)
        addLog("[ℹ️] Служба GIN-Remote остановлена")
    }

    private fun sendTelegramAlert(deviceLabel: String, localIp: String, pin: String) {
        executor.execute {
            try {
                val token = SecurityConfig.getTelegramToken()
                val chatId = SecurityConfig.getTelegramChatId()
                val text = "🟢 <b>GIN-Remote Android</b> [<code>v017</code>]\n" +
                        "<b>Устройство:</b> $deviceLabel ($localIp)\n" +
                        "<b>PIN устройства:</b> <code>$pin</code>\n" +
                        "<b>Статус:</b> Мост подключен к Oracle-49. Доступно для обслуживания и установки приложений."

                val postData = "chat_id=" + java.net.URLEncoder.encode(chatId, "UTF-8") +
                        "&parse_mode=HTML&disable_web_page_preview=true&text=" + java.net.URLEncoder.encode(text, "UTF-8")
                val url = URL("https://api.telegram.org/bot$token/sendMessage")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.doOutput = true
                conn.connectTimeout = 5000
                conn.readTimeout = 5000
                conn.outputStream.use { os ->
                    os.write(postData.toByteArray(Charsets.UTF_8))
                    os.flush()
                }
                val code = conn.responseCode
                if (code == 200) {
                    addLog("[📢] Оповещение о сессии отправлено в Telegram")
                } else {
                    addLog("[⚠️] Ошибка отправки Telegram (HTTP $code)")
                }
            } catch (e: Throwable) {
                addLog("[⚠️] Сбой отправки Telegram: ${e.message}")
            }
        }
    }

    private fun getLocalIpAddress(): String {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val intf = interfaces.nextElement()
                val addrs = intf.inetAddresses
                while (addrs.hasMoreElements()) {
                    val addr = addrs.nextElement()
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress ?: "127.0.0.1"
                    }
                }
            }
        } catch (_: Exception) {}
        return "127.0.0.1"
    }

    private fun handleStream(sid: String) {
        var serverSock: Socket? = null
        var adbSock: Socket? = null
        try {
            serverSock = Socket()
            serverSock.tcpNoDelay = true
            serverSock.connect(InetSocketAddress(SERVER_HOST, SERVER_PORT), 10000)
            val sOut = serverSock.getOutputStream()
            sOut.write("STREAM $sid\n".toByteArray(Charsets.UTF_8))
            sOut.flush()

            adbSock = Socket()
            adbSock.tcpNoDelay = true
            try {
                adbSock.connect(InetSocketAddress("127.0.0.1", ADB_LOCAL_PORT), 5000)
            } catch (connectEx: Exception) {
                val suExists = listOf("/system/bin/su", "/system/xbin/su", "/sbin/su", "/vendor/bin/su")
                    .any { java.io.File(it).exists() }
                if (suExists) {
                    addLog("[⚡] Повторная попытка: активация TCP ADB через Root...")
                    try {
                        val p = Runtime.getRuntime().exec(arrayOf("su", "0", "sh", "-c", "setprop persist.adb.tcp.port 5555; setprop service.adb.tcp.port 5555; stop adbd; start adbd"))
                        p.waitFor(2, java.util.concurrent.TimeUnit.SECONDS)
                        Thread.sleep(1000)
                    } catch (_: Throwable) {}
                    adbSock = Socket()
                    adbSock.tcpNoDelay = true
                    adbSock.connect(InetSocketAddress("127.0.0.1", ADB_LOCAL_PORT), 5000)
                } else {
                    throw connectEx
                }
            }

            addLog("[✅] Сессия ADB ($sid) активна: 127.0.0.1:$ADB_LOCAL_PORT ⇄ Oracle-49")

            val sIn = serverSock.getInputStream()
            val aIn = adbSock.getInputStream()
            val aOut = adbSock.getOutputStream()

            val f1 = executor.submit { pipe(sIn, aOut) }
            val f2 = executor.submit { pipe(aIn, sOut) }

            f1.get()
            f2.get()
        } catch (e: Exception) {
            if (RemoteAccessibilityService.isEnabled) {
                addLog("[ℹ️] ADB сокет 5555 недоступен (активен режим Accessibility)")
            } else {
                addLog("[❌] Сбой потока управления ADB ($sid): ${e.message}")
            }
        } finally {
            closeQuietly(serverSock)
            closeQuietly(adbSock)
            addLog("[ℹ️] Сессия ADB ($sid) завершена")
        }
    }

    private fun pipe(input: InputStream, output: OutputStream) {
        val buf = ByteArray(16384)
        try {
            var n: Int
            while (input.read(buf).also { n = it } != -1) {
                output.write(buf, 0, n)
                output.flush()
            }
        } catch (_: Exception) {
        } finally {
            try { output.close() } catch (_: Exception) {}
        }
    }

    private fun stopBridge() {
        isServiceRunning = false
        currentState = State.DISCONNECTED
        for (l in listeners) {
            l.onStateChanged(State.DISCONNECTED)
        }
        try { ScreenCaptureManager.activeInstance?.stop() } catch (_: Throwable) {}
        closeQuietly(controlSocket)
        controlSocket = null
        if (wakeLock?.isHeld == true) {
            try { wakeLock?.release() } catch (_: Exception) {}
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try { stopForeground(STOP_FOREGROUND_REMOVE) } catch (_: Throwable) {}
        } else {
            @Suppress("DEPRECATION")
            try { stopForeground(true) } catch (_: Throwable) {}
        }
        try {
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            nm?.cancel(NOTIFICATION_ID)
        } catch (_: Throwable) {}
    }

    private fun closeQuietly(socket: Socket?) {
        try {
            socket?.close()
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        stopBridge()
        if (activeInstance == this) {
            activeInstance = null
        }
        super.onDestroy()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "GIN-Remote Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Фоновая служба туннеля GIN-Remote"
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
            nm?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(contentText: String): Notification {
        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            flags
        )

        val vName = try {
            packageManager.getPackageInfo(packageName, 0).versionName
        } catch (_: Exception) {
            "v017"
        }

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationCompat.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            NotificationCompat.Builder(this)
        }

        return builder
            .setContentTitle("GIN-Remote $vName")
            .setContentText(contentText)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
}
