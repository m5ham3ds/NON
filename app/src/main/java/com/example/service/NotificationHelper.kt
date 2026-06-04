package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.R
import com.example.presentation.MainActivity
import com.example.util.LocaleHelper
import timber.log.Timber

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_TEST_ID = "test_channel"
        const val CHANNEL_RESULT_ID = "result_channel"
        const val NOTIFICATION_ID = 1001
        const val RESULT_NOTIFICATION_ID = 1002
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val testChannel = NotificationChannel(
                CHANNEL_TEST_ID,
                context.getString(R.string.channel_test_name),
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = context.getString(R.string.channel_test_desc)
            }

            val resultChannel = NotificationChannel(
                CHANNEL_RESULT_ID,
                context.getString(R.string.channel_result_name),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.channel_result_desc)
            }

            notificationManager.createNotificationChannel(testChannel)
            notificationManager.createNotificationChannel(resultChannel)
        }
    }

    fun buildRunningNotification(state: ServiceState, forceStandard: Boolean = false): Notification {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Pause Action
        val pauseIntent = Intent(context, TestService::class.java).apply {
            action = if (state.isPaused) TestService.ACTION_RESUME else TestService.ACTION_PAUSE
        }
        val pausePendingIntent = PendingIntent.getService(
            context,
            1,
            pauseIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Cancel Action
        val cancelIntent = Intent(context, TestService::class.java).apply {
            action = TestService.ACTION_CANCEL
        }
        val cancelPendingIntent = PendingIntent.getService(
            context,
            2,
            cancelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isArabic = LocaleHelper.getPersistedLocale(context) == "ar"
        val titleSuffix = if (state.isPaused) {
            if (isArabic) " (موقوف مؤقتاً)" else " (Paused)"
        } else {
            ""
        }

        val baseTitle = context.getString(R.string.notification_title) + titleSuffix
        val baseContent = context.getString(
            R.string.notification_content,
            state.progress,
            state.total,
            state.successCount,
            state.failureCount
        )

        val pauseLabel = if (state.isPaused) {
            if (isArabic) "استئناف" else "Resume"
        } else {
            if (isArabic) "إيقاف مؤقت" else "Pause"
        }
        val cancelLabel = if (isArabic) "إيقاف" else "Cancel"

        val builder = NotificationCompat.Builder(context, CHANNEL_TEST_ID)
            .setSmallIcon(R.drawable.ic_router)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentTitle(baseTitle)
            .setContentText(baseContent)
            .setProgress(state.total, state.progress, false)

        if (forceStandard) {
            builder.addAction(android.R.drawable.ic_media_play, pauseLabel, pausePendingIntent)
            builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, cancelLabel, cancelPendingIntent)
        }

        if (!forceStandard) {
            try {
                val remoteViews = android.widget.RemoteViews(context.packageName, R.layout.notification_custom_test).apply {
                    setTextViewText(R.id.tv_title, baseTitle)
                    
                    val statusLabel = if (state.isPaused) {
                        if (isArabic) "موقوف مؤقتاً" else "Paused"
                    } else {
                        if (isArabic) "جاري الفحص النشط..." else "Active testing..."
                    }
                    setTextViewText(R.id.tv_status, statusLabel)
                    
                    val cardDisplay = if (state.currentCard.isEmpty()) "---" else state.currentCard
                    val detailsText = (if (isArabic) "البطاقة الحالية: " else "Current Card: ") + cardDisplay + " | " + baseContent
                    setTextViewText(R.id.tv_card_info, detailsText)
                    
                    val pct = if (state.total > 0) (state.progress * 100 / state.total) else 0
                    setTextViewText(R.id.tv_progress_percentage, "$pct%")
                    
                    setProgressBar(R.id.progress_bar, state.total, state.progress, false)
                    
                    setTextViewText(R.id.tv_success_count, state.successCount.toString())
                    setTextViewText(R.id.tv_failure_count, state.failureCount.toString())
                    setTextViewText(R.id.tv_total_count, state.total.toString())
                    
                    setTextViewText(R.id.tv_pause_action_label, pauseLabel)
                    
                    setOnClickPendingIntent(R.id.btn_pause_action, pausePendingIntent)
                    setOnClickPendingIntent(R.id.btn_cancel_action, cancelPendingIntent)
                }
                builder.setCustomContentView(remoteViews)
                builder.setCustomBigContentView(remoteViews)
                builder.setCustomHeadsUpContentView(remoteViews)
            } catch (e: Exception) {
                Timber.e(e, "Failed to inflate Custom RemoteViews inside builder, using standard fallback layout")
                builder.addAction(android.R.drawable.ic_media_play, pauseLabel, pausePendingIntent)
                builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, cancelLabel, cancelPendingIntent)
            }
        }

        return builder.build()
    }

    fun updateNotification(state: ServiceState) {
        try {
            notificationManager.notify(NOTIFICATION_ID, buildRunningNotification(state, forceStandard = false))
        } catch (e: Exception) {
            Timber.e(e, "Failed to post Custom Notification, retrying with pure standard layout")
            try {
                notificationManager.notify(NOTIFICATION_ID, buildRunningNotification(state, forceStandard = true))
            } catch (ex: Exception) {
                Timber.e(ex, "Failed to post standard notification fallback")
            }
        }
    }

    fun showResultNotification(card: String, isSuccess: Boolean) {
        val title = if (isSuccess) {
            context.getString(R.string.notification_success_title)
        } else {
            context.getString(R.string.notification_failure_title)
        }
        val body = context.getString(R.string.notification_result_body, card)

        val notification = NotificationCompat.Builder(context, CHANNEL_RESULT_ID)
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_router)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(RESULT_NOTIFICATION_ID, notification)
    }
}
