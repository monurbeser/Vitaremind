package com.vitaremind.app.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.vitaremind.app.data.datastore.UserPreferencesDataStore
import com.vitaremind.app.worker.WaterReminderWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: UserPreferencesDataStore,
    private val workManager: WorkManager
) : ViewModel() {

    val dailyGoalMl: StateFlow<Int> = prefs.dailyGoalMl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 2000)

    val waterReminderIntervalH: StateFlow<Int> = prefs.waterReminderIntervalH
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 2)

    val waterReminderStartHour: StateFlow<Int> = prefs.waterReminderStartHour
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 8)

    val waterReminderEndHour: StateFlow<Int> = prefs.waterReminderEndHour
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 22)

    val medicineSnoozeMinutes: StateFlow<Int> = prefs.medicineSnoozeMinutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 10)

    val medicineSoundEnabled: StateFlow<Boolean> = prefs.medicineSoundEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), true)

    val themePreference: StateFlow<String> = prefs.themePreference
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "system")

    // ── Setters ───────────────────────────────────────────────────────────────

    fun setDailyGoal(ml: Int) = viewModelScope.launch { prefs.setDailyGoalMl(ml) }

    fun setWaterReminderInterval(hours: Int) {
        viewModelScope.launch {
            prefs.setWaterReminderIntervalH(hours)
            if (hours == 0) {
                workManager.cancelUniqueWork(WaterReminderWorker.WORK_NAME)
            } else {
                val request = PeriodicWorkRequestBuilder<WaterReminderWorker>(
                    hours.toLong(), TimeUnit.HOURS
                ).build()
                workManager.enqueueUniquePeriodicWork(
                    WaterReminderWorker.WORK_NAME,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    request
                )
            }
        }
    }

    fun setWaterReminderStartHour(hour: Int) =
        viewModelScope.launch { prefs.setWaterReminderStartHour(hour) }

    fun setWaterReminderEndHour(hour: Int) =
        viewModelScope.launch { prefs.setWaterReminderEndHour(hour) }

    fun setMedicineSnoozeMinutes(minutes: Int) =
        viewModelScope.launch { prefs.setMedicineSnoozeMinutes(minutes) }

    fun setMedicineSoundEnabled(enabled: Boolean) =
        viewModelScope.launch { prefs.setMedicineSoundEnabled(enabled) }

    fun setThemePreference(theme: String) =
        viewModelScope.launch { prefs.setThemePreference(theme) }

    fun resetAllData() {
        viewModelScope.launch {
            workManager.cancelUniqueWork(WaterReminderWorker.WORK_NAME)
            workManager.cancelAllWorkByTag("medicine_reminder")
            prefs.clearAll()
        }
    }
}
