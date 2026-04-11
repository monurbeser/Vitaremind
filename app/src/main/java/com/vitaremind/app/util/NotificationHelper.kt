package com.vitaremind.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

object NotificationHelper {

    const val WATER_CHANNEL_ID = "water_reminders"
    const val MEDICINE_CHANNEL_ID = "medicine_reminders"

    fun createNotificationChannels(context: Context) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val waterChannel = NotificationChannel(
            WATER_CHANNEL_ID,
            "Water Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Periodic reminders to drink water"
        }

        val medicineChannel = NotificationChannel(
            MEDICINE_CHANNEL_ID,
            "Medicine Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Reminders to take your medications"
        }

        notificationManager.createNotificationChannels(listOf(waterChannel, medicineChannel))
    }
}
