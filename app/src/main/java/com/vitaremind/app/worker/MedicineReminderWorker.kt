package com.vitaremind.app.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vitaremind.app.MainActivity
import com.vitaremind.app.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class MedicineReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    companion object {
        const val KEY_MEDICINE_NAME = "medicine_name"
        const val KEY_DOSAGE        = "dosage"
        const val KEY_DOSE_LOG_ID   = "dose_log_id"
        const val KEY_MEDICINE_ID   = "medicine_id"
    }

    override suspend fun doWork(): Result {
        val medicineName = inputData.getString(KEY_MEDICINE_NAME) ?: return Result.failure()
        val dosage       = inputData.getString(KEY_DOSAGE)        ?: return Result.failure()
        val doseLogId    = inputData.getLong(KEY_DOSE_LOG_ID, -1L)
        val notifId      = (doseLogId % Int.MAX_VALUE).toInt().coerceAtLeast(2000)

        val openIntent = PendingIntent.getActivity(
            applicationContext,
            notifId,
            Intent(applicationContext, MainActivity::class.java),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, NotificationHelper.MEDICINE_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(medicineName)
            .setContentText("Time to take your $dosage")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Time to take your $dosage"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openIntent)
            .build()

        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(notifId, notification)
        return Result.success()
    }
}
