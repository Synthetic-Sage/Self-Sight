package com.diary.mirroroftruth.presentation.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == "android.intent.action.QUICKBOOT_POWERON") {
            val prefs = context.getSharedPreferences(ReminderManager.PREFS_NAME, Context.MODE_PRIVATE)
            val enabled = prefs.getBoolean(ReminderManager.KEY_REMINDER_ENABLED, false)
            if (enabled) {
                val hour = prefs.getInt(ReminderManager.KEY_REMINDER_HOUR, 20)
                val minute = prefs.getInt(ReminderManager.KEY_REMINDER_MINUTE, 0)
                ReminderManager.scheduleReminder(context, hour, minute)
            }
        }
    }
}
