package com.vitaremind.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vitaremind.app.data.local.entity.WaterLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: WaterLog)

    @Query("DELETE FROM water_log WHERE id = :id")
    suspend fun deleteLog(id: Long)

    @Query("SELECT * FROM water_log WHERE date = :date ORDER BY timestamp DESC")
    fun getLogsForDate(date: String): Flow<List<WaterLog>>

    @Query("SELECT SUM(amountMl) FROM water_log WHERE date = :date")
    fun getTotalForDate(date: String): Flow<Int?>

    @Query("SELECT * FROM water_log WHERE date >= :fromDate ORDER BY date DESC, timestamp DESC")
    fun getLogsLast7Days(fromDate: String): Flow<List<WaterLog>>
}
