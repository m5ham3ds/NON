package com.example.util

object Constants {
    const val LANG_ARABIC = "ar"
    const val LANG_ENGLISH = "en"
    const val LANG_SYSTEM = "system"

    const val THEME_LIGHT = "light"
    const val THEME_DARK = "dark"
    const val THEME_SYSTEM = "system"

    const val WEBVIEW_TIMEOUT_MS = 20000L
    const val FORM_READY_TIMEOUT_MS = 10000L
    const val SCREENSHOT_INTERVAL_MS = 2000L

    const val DEFAULT_DELAY_MS = 500L
    const val DEFAULT_THREAD_COUNT = 3
    const val MAX_CARDS_PER_BATCH = 10000

    const val DB_NAME = "wdmaster_db"

    const val NOTIFICATION_CHANNEL_TEST = "test_channel"
    const val NOTIFICATION_CHANNEL_RESULT = "result_channel"
    const val NOTIFICATION_ID_TEST = 1001
    const val NOTIFICATION_ID_RESULT = 1002

    const val EXPORT_DIR = "exports"
    const val LOG_FILE_NAME = "app_logs.txt"
    const val MAX_LOG_SIZE_BYTES = 5 * 1024 * 1024L // 5MB
}
