package com.diary.mirroroftruth.presentation.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.diary.mirroroftruth.R

/**
 * BroadcastReceiver responsible for triggering the daily journal reminder notification.
 * It is triggered by the AlarmManager at a time specified by the user in Settings.
 */
class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // 1. Initialize NotificationManager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "daily_reminder_channel"

        // 2. Create Notification Channel (required for Android 8.0+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Daily Journal Reminders",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminds you to write your daily reflection"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // 3. Setup Deep Link Intent
        // This allows the notification to open the Journal screen directly using the "mirror://journal" URI scheme.
        // Deep links are handled in the MirrorNavGraph and AndroidManifest.
        val deepLinkIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("mirror://journal")
        ).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            deepLinkIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 4. Build and Display the Notification
        val notification = NotificationCompat.Builder(context, channelId)
            // Use a default icon since we don't know the exact drawable name, android.R.drawable.ic_dialog_info works
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Time to Reflect 📖")
            .setContentText("How did your day go? Take a moment to write in Self Sight.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)

        // 5. Automatic Rescheduling
        // Since AlarmManager alarms can be cleared on reboot or system updates, we reschedule
        // the alarm for the next day here. This ensures the chain of reminders remains persistent.
        val prefs = context.getSharedPreferences(ReminderManager.PREFS_NAME, Context.MODE_PRIVATE)
        if (prefs.getBoolean(ReminderManager.KEY_REMINDER_ENABLED, false)) {
            val hour = prefs.getInt(ReminderManager.KEY_REMINDER_HOUR, 20)
            val minute = prefs.getInt(ReminderManager.KEY_REMINDER_MINUTE, 0)
            ReminderManager.scheduleReminder(context, hour, minute)
        }
    }
}
