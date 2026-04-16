package com.vitaremind.app

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.vitaremind.app.data.datastore.UserPreferencesDataStore
import com.vitaremind.app.worker.MedicineReminderWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class SnoozeReceiver : BroadcastReceiver() {

    @Inject lateinit var workManager: WorkManager
    @Inject lateinit var prefsDataStore: UserPreferencesDataStore

    override fun onReceive(context: Context, intent: Intent) {
        val notifId      = intent.getIntExtra("notif_id", -1)
        val medicineName = intent.getStringExtra(MedicineReminderWorker.KEY_MEDICINE_NAME)
            ?: return
        val dosage       = intent.getStringExtra(MedicineReminderWorker.KEY_DOSAGE)
            ?: return
        val doseLogId    = intent.getLongExtra(MedicineReminderWorker.KEY_DOSE_LOG_ID, -1L)

        if (notifId != -1) {
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
            nm.cancel(notifId)
        }

        val snoozeMinutes = runBlocking {
            prefsDataStore.medicineSnoozeMinutes.first()
        }

        val request = OneTimeWorkRequestBuilder<MedicineReminderWorker>()
            .setInitialDelay(snoozeMinutes.toLong(), TimeUnit.MINUTES)
            .setInputData(
                workDataOf(
                    MedicineReminderWorker.KEY_MEDICINE_NAME to medicineName,
                    MedicineReminderWorker.KEY_DOSAGE        to dosage,
                    MedicineReminderWorker.KEY_DOSE_LOG_ID   to doseLogId
                )
            )
            .addTag("medicine_reminder")
            .build()
        workManager.enqueue(request)
    }
}
