package com.vitaremind.app.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vitaremind.app.data.repository.WaterRepository
import com.vitaremind.app.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class WaterReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val waterRepository: WaterRepository
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val WORK_NAME = "water_reminder_work"
        private const val NOTIFICATION_ID = 1001
    }

    override suspend fun doWork(): Result {
        val totalMl = waterRepository.getTodayTotal().first() ?: 0
        val goal = waterRepository.dailyGoalMl.first()
        val remaining = (goal - totalMl).coerceAtLeast(0)

        val body = when {
            totalMl == 0 -> "You haven't tracked any water yet today. Start now!"
            totalMl >= goal -> "Amazing! You've reached your daily goal of ${goal}ml 🎉"
            else -> "You've had ${totalMl}ml today. ${remaining}ml more to reach your goal!"
        }

        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(
            applicationContext,
            NotificationHelper.WATER_CHANNEL_ID
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Time to drink water! 💧")
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
        return Result.success()
    }
}
