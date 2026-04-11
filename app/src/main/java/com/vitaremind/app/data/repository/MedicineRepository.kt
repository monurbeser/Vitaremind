package com.vitaremind.app.data.repository

import com.vitaremind.app.data.local.dao.DoseLogDao
import com.vitaremind.app.data.local.dao.MedicineDao
import com.vitaremind.app.data.local.entity.DoseLog
import com.vitaremind.app.data.local.entity.DoseStatus
import com.vitaremind.app.data.local.entity.Medicine
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicineRepository @Inject constructor(
    private val medicineDao: MedicineDao,
    private val doseLogDao: DoseLogDao
) {
    fun getAllActiveMedicines(): Flow<List<Medicine>> =
        medicineDao.getAllActiveMedicines()

    fun getMedicineById(id: Long): Flow<Medicine?> =
        medicineDao.getMedicineById(id)

    suspend fun addMedicine(medicine: Medicine): Long =
        medicineDao.insertMedicine(medicine)

    suspend fun updateMedicine(medicine: Medicine) =
        medicineDao.updateMedicine(medicine)

    suspend fun deleteMedicine(medicine: Medicine) {
        doseLogDao.deleteLogsForMedicine(medicine.id)
        medicineDao.deleteMedicine(medicine)
    }

    fun getTodayDoseLogs(): Flow<List<DoseLog>> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val startOfDay = cal.timeInMillis
        val endOfDay = startOfDay + 24 * 60 * 60 * 1000L
        return doseLogDao.getDoseLogsForDay(startOfDay, endOfDay)
    }

    suspend fun markDoseTaken(doseLog: DoseLog) {
        doseLogDao.updateDoseLog(
            doseLog.copy(
                status  = DoseStatus.TAKEN,
                takenAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun skipDose(doseLog: DoseLog) {
        doseLogDao.updateDoseLog(doseLog.copy(status = DoseStatus.SKIPPED))
    }

    suspend fun scheduleDoseLog(medicineId: Long, scheduledTime: Long): Long =
        doseLogDao.insertDoseLog(
            DoseLog(medicineId = medicineId, scheduledTime = scheduledTime)
        )

    suspend fun getDoseLogById(id: Long): DoseLog? =
        doseLogDao.getDoseLogById(id)
}
