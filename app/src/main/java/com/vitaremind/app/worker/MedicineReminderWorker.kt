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
import com.vitaremind.app.data.datastore.UserPreferencesDataStore
import com.vitaremind.app.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class MedicineReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val prefsDataStore: UserPreferencesDataStore
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

        val soundEnabled = prefsDataStore.medicineSoundEnabled.first()

        val notification = NotificationCompat.Builder(
            applicationContext, NotificationHelper.MEDICINE_CHANNEL_ID
        )
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(medicineName)
            .setContentText("Time to take your $dosage")
            .setStyle(NotificationCompat.BigTextStyle().bigText("Time to take your $dosage"))
            .setPriority(
                if (soundEnabled) NotificationCompat.PRIORITY_HIGH
                else NotificationCompat.PRIORITY_LOW
            )
            .setSilent(!soundEnabled)
            .setAutoCancel(true)
            .setContentIntent(openIntent)
            .addAction(buildSnoozeAction(notifId, medicineName, dosage, doseLogId))
            .build()

        val nm = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(notifId, notification)
        return Result.success()
    }

    private fun buildSnoozeAction(
        notifId: Int,
        medicineName: String,
        dosage: String,
        doseLogId: Long
    ): NotificationCompat.Action {
        val snoozeIntent = android.content.Intent(
            applicationContext, com.vitaremind.app.SnoozeReceiver::class.java
        ).apply {
            putExtra("notif_id", notifId)
            putExtra(KEY_MEDICINE_NAME, medicineName)
            putExtra(KEY_DOSAGE, dosage)
            putExtra(KEY_DOSE_LOG_ID, doseLogId)
        }
        val snoozePi = android.app.PendingIntent.getBroadcast(
            applicationContext,
            notifId + 10000,
            snoozeIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or
                android.app.PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Action(0, "Snooze", snoozePi)
    }
}
