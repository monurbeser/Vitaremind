package com.vitaremind.app.util

import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.vitaremind.app.data.local.entity.Medicine
import com.vitaremind.app.data.repository.MedicineRepository
import com.vitaremind.app.worker.MedicineReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicineScheduler @Inject constructor(
    private val workManager: WorkManager,
    private val medicineRepository: MedicineRepository
) {
    /**
     * Parse reminderTimes JSON string like ["08:00","20:00"] into list of "HH:mm" strings.
     */
    private fun parseReminderTimes(json: String): List<String> {
        return try {
            json.trim().removePrefix("[").removeSuffix("]")
                .split(",")
                .map { it.trim().removeSurrounding("\"") }
                .filter { it.contains(":") }
        } catch (e: Exception) { emptyList() }
    }

    private fun computeDelayMs(timeStr: String): Long {
        val parts = timeStr.split(":")
        if (parts.size != 2) return -1L
        val hour   = parts[0].toIntOrNull() ?: return -1L
        val minute = parts[1].toIntOrNull() ?: return -1L

        val now    = System.currentTimeMillis()
        val cal    = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        if (cal.timeInMillis <= now) {
            cal.add(Calendar.DAY_OF_YEAR, 1) // schedule for tomorrow if time passed
        }
        return cal.timeInMillis - now
    }

    suspend fun scheduleAllReminders(medicines: List<Medicine>) {
        // Cancel all existing medicine reminder work
        workManager.cancelAllWorkByTag("medicine_reminder")

        medicines.filter { it.isActive }.forEach { medicine ->
            val times = parseReminderTimes(medicine.reminderTimes)
            times.forEach { timeStr ->
                val delayMs = computeDelayMs(timeStr)
                if (delayMs < 0) return@forEach

                // Create a dose log for this scheduled time
                val scheduledTimeMs = System.currentTimeMillis() + delayMs
                val doseLogId = medicineRepository.scheduleDoseLog(medicine.id, scheduledTimeMs)

                val data = Data.Builder()
                    .putString(MedicineReminderWorker.KEY_MEDICINE_NAME, medicine.name)
                    .putString(MedicineReminderWorker.KEY_DOSAGE, medicine.dosage)
                    .putLong(MedicineReminderWorker.KEY_DOSE_LOG_ID, doseLogId)
                    .putLong(MedicineReminderWorker.KEY_MEDICINE_ID, medicine.id)
                    .build()

                val workRequest = OneTimeWorkRequestBuilder<MedicineReminderWorker>()
                    .setInitialDelay(delayMs, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .addTag("medicine_reminder")
                    .build()

                val workName = "medicine_${medicine.id}_${timeStr.replace(":", "")}"
                workManager.enqueueUniqueWork(
                    workName,
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
            }
        }
    }

    fun cancelAllReminders() {
        workManager.cancelAllWorkByTag("medicine_reminder")
    }
}
