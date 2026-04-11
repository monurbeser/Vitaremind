package com.vitaremind.app.ui.water

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.vitaremind.app.data.local.entity.WaterLog
import com.vitaremind.app.data.repository.WaterRepository
import com.vitaremind.app.worker.WaterReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class WaterViewModel @Inject constructor(
    private val repository: WaterRepository,
    private val workManager: WorkManager
) : ViewModel() {

    val todayLogs: StateFlow<List<WaterLog>> = repository.getTodayLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val totalToday: StateFlow<Int> = repository.getTodayTotal()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val dailyGoal: StateFlow<Int> = repository.dailyGoalMl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 2000)

    fun addWater(amountMl: Int) {
        viewModelScope.launch { repository.addWater(amountMl) }
    }

    fun deleteLog(id: Long) {
        viewModelScope.launch { repository.deleteLog(id) }
    }

    fun scheduleWaterReminders(intervalHours: Int = 2) {
        val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(
            intervalHours.toLong(), TimeUnit.HOURS
        )
            .setConstraints(Constraints.Builder().build())
            .build()

        workManager.enqueueUniquePeriodicWork(
            WaterReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun cancelWaterReminders() {
        workManager.cancelUniqueWork(WaterReminderWorker.WORK_NAME)
    }
}
