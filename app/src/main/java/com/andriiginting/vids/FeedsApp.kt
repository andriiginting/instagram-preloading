package com.andriiginting.vids

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class FeedsApp : Application() {

    override fun onCreate() {
        super.onCreate()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "feeds",
                getString(R.string.app_name),
                NotificationManager.IMPORTANCE_HIGH
            )
            channel.description = "Feeds Download"
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}