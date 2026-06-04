package com.example.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import timber.log.Timber

class NotificationActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action ?: return
        Timber.d("NotificationActionReceiver received action: $action")
        
        val serviceIntent = Intent(context, TestService::class.java).apply {
            this.action = action
        }
        try {
            // Since the service is already running, we can directly call startService
            // to send control intents without triggering background service constraints.
            context.startService(serviceIntent)
        } catch (e: Exception) {
            Timber.e(e, "Failed to send action using standard startService, falling back to startForegroundService")
            try {
                ContextCompat.startForegroundService(context, serviceIntent)
            } catch (ex: Exception) {
                Timber.e(ex, "Failed to send foreground service action from NotificationActionReceiver")
            }
        }
    }
}
