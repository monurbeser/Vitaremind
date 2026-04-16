package com.vitaremind.app.data.repository

import com.vitaremind.app.data.datastore.UserPreferencesDataStore
import com.vitaremind.app.data.local.dao.WaterDao
import com.vitaremind.app.data.local.entity.WaterLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WaterRepository @Inject constructor(
    private val waterDao: WaterDao,
    private val prefsDataStore: UserPreferencesDataStore
) {
    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun today(): String = LocalDate.now().format(dateFormatter)

    fun fromDate(daysAgo: Int): String =
        LocalDate.now().minusDays(daysAgo.toLong()).format(dateFormatter)

    suspend fun addWater(amountMl: Int) {
        waterDao.insertLog(
            WaterLog(
                date = today(),
                amountMl = amountMl,
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteLog(id: Long) = waterDao.deleteLog(id)

    fun getTodayLogs(): Flow<List<WaterLog>> = waterDao.getLogsForDate(today())

    fun getTodayTotal(): Flow<Int?> = waterDao.getTotalForDate(today())

    fun getLast7DaysLogs(): Flow<List<WaterLog>> = waterDao.getLogsLast7Days(fromDate(7))

    val dailyGoalMl: Flow<Int> = prefsDataStore.dailyGoalMl

    suspend fun setDailyGoal(ml: Int) = prefsDataStore.setDailyGoalMl(ml)

    suspend fun getStartHour(): Int = prefsDataStore.waterReminderStartHour.first()
    suspend fun getEndHour(): Int = prefsDataStore.waterReminderEndHour.first()
}
