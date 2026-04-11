package com.vitaremind.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.vitaremind.app.data.local.entity.DoseLog
import kotlinx.coroutines.flow.Flow

@Dao
interface DoseLogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDoseLog(log: DoseLog): Long

    @Update
    suspend fun updateDoseLog(log: DoseLog)

    @Query("""
        SELECT * FROM dose_logs
        WHERE scheduledTime >= :startOfDayMs AND scheduledTime < :endOfDayMs
        ORDER BY scheduledTime ASC
    """)
    fun getDoseLogsForDay(startOfDayMs: Long, endOfDayMs: Long): Flow<List<DoseLog>>

    @Query("SELECT * FROM dose_logs WHERE medicineId = :medicineId AND status = 'PENDING'")
    fun getPendingDosesForMedicine(medicineId: Long): Flow<List<DoseLog>>

    @Query("DELETE FROM dose_logs WHERE medicineId = :medicineId")
    suspend fun deleteLogsForMedicine(medicineId: Long)

    @Query("SELECT * FROM dose_logs WHERE id = :id")
    suspend fun getDoseLogById(id: Long): DoseLog?
}
