package com.vitaremind.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaremind.app.data.local.entity.DoseLog
import com.vitaremind.app.data.local.entity.DoseStatus
import com.vitaremind.app.data.repository.MedicineRepository
import com.vitaremind.app.data.repository.WaterRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val waterRepository: WaterRepository,
    private val medicineRepository: MedicineRepository
) : ViewModel() {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val dayLabelFormatter = DateTimeFormatter.ofPattern("EEE", Locale.getDefault())

    val totalToday: StateFlow<Int> = waterRepository.getTodayTotal()
        .map { it ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    val dailyGoal: StateFlow<Int> = waterRepository.dailyGoalMl
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 2000)

    /** List of (dayLabel e.g. "Mon", amountMl) for the last 7 days, oldest first */
    val weeklyWaterData: StateFlow<List<Pair<String, Int>>> =
        waterRepository.getLast7DaysLogs()
            .map { logs ->
                val today = LocalDate.now()
                (6 downTo 0).map { daysAgo ->
                    val date    = today.minusDays(daysAgo.toLong())
                    val label   = date.format(dayLabelFormatter)
                    val dateStr = date.format(dateFormatter)
                    val total   = logs
                        .filter { it.date == dateStr }
                        .sumOf { it.amountMl }
                    label to total
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    /** How many consecutive days the daily goal was met (including today) */
    val currentStreak: StateFlow<Int> =
        combine(waterRepository.getLast7DaysLogs(), waterRepository.dailyGoalMl) { logs, goal ->
            val today = LocalDate.now()
            var streak = 0
            for (daysAgo in 0..6) {
                val date    = today.minusDays(daysAgo.toLong())
                val dateStr = date.format(dateFormatter)
                val total   = logs.filter { it.date == dateStr }.sumOf { it.amountMl }
                if (total >= goal) streak++ else break
            }
            streak
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    /** Up to 3 PENDING doses for today */
    val nextDoses: StateFlow<List<DoseLog>> = medicineRepository.getTodayDoseLogs()
        .map { doses ->
            doses
                .filter { it.status == DoseStatus.PENDING }
                .sortedBy { it.scheduledTime }
                .take(3)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val medicines = medicineRepository.getAllActiveMedicines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
