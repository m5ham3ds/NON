package com.example.util

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AppLogger {
    private const val MAX_LOG_SIZE = 5 * 1024 * 1024L // 5MB
    private val logScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var logFile: File? = null

    fun init(context: Context) {
        logFile = File(context.filesDir, "app_logs.txt")
    }

    fun log(level: String, tag: String, message: String, throwable: Throwable? = null) {
        when (level) {
            "ERROR" -> Timber.tag(tag).e(throwable, message)
            "WARN" -> Timber.tag(tag).w(message)
            else -> Timber.tag(tag).d(message)
        }
        logScope.launch {
            writeToFile(level, tag, message, throwable)
        }
    }

    private fun writeToFile(level: String, tag: String, message: String, throwable: Throwable?) {
        val file = logFile ?: return
        try {
            if (file.exists() && file.length() > MAX_LOG_SIZE) {
                val lines = file.readLines()
                file.writeText(lines.drop(lines.size / 2).joinToString("\n"))
            }
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                .format(Date())
            file.appendText("[$timestamp][$level][$tag] $message\n")
            throwable?.let { file.appendText("  Exception: ${it.message}\n") }
        } catch (_: Exception) {}
    }
}
