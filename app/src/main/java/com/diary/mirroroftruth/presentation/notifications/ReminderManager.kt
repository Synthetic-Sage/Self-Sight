package com.diary.mirroroftruth.presentation.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object ReminderManager {
    const val PREFS_NAME = "reminder_prefs"
    const val KEY_REMINDER_HOUR = "reminder_hour"
    const val KEY_REMINDER_MINUTE = "reminder_minute"
    const val KEY_REMINDER_ENABLED = "reminder_enabled"

    fun scheduleReminder(context: Context, hour: Int, minute: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // If time is in the past, schedule for tomorrow
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        } catch (e: SecurityException) {
            // Permission not granted for exact alarms on Android 12+
            // Fallback to inexact alarm if needed, or prompt user.
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun saveReminderTime(context: Context, hour: Int, minute: Int, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putInt(KEY_REMINDER_HOUR, hour)
            .putInt(KEY_REMINDER_MINUTE, minute)
            .putBoolean(KEY_REMINDER_ENABLED, enabled)
            .apply()

        if (enabled) {
            scheduleReminder(context, hour, minute)
        } else {
            cancelReminder(context)
        }
    }
}
