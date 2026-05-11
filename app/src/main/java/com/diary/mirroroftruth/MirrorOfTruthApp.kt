package com.diary.mirroroftruth

import android.app.Application
import android.content.Intent
import dagger.hilt.android.HiltAndroidApp
import java.io.PrintWriter
import java.io.StringWriter

@HiltAndroidApp
class MirrorOfTruthApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            val sw = StringWriter()
            throwable.printStackTrace(PrintWriter(sw))
            val intent = Intent(this, CrashActivity::class.java).apply {
                putExtra("EXTRA_STACK_TRACE", sw.toString())
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            android.os.Process.killProcess(android.os.Process.myPid())
            System.exit(10)
        }
    }
}
