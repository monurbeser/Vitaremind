package com.vitaremind.app.ui.medicine

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitaremind.app.data.local.entity.DoseLog
import com.vitaremind.app.data.local.entity.Medicine
import com.vitaremind.app.data.repository.MedicineRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MedicineViewModel @Inject constructor(
    private val repository: MedicineRepository
) : ViewModel() {

    val medicines: StateFlow<List<Medicine>> = repository.getAllActiveMedicines()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val todayDoses: StateFlow<List<DoseLog>> = repository.getTodayDoseLogs()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _addMedicineCount = MutableStateFlow(0)
    val addMedicineCount: StateFlow<Int> = _addMedicineCount

    // Signal to show interstitial ad (after every 3rd add)
    private val _showInterstitial = MutableSharedFlow<Unit>()
    val showInterstitial: SharedFlow<Unit> = _showInterstitial.asSharedFlow()

    fun addMedicine(medicine: Medicine) {
        viewModelScope.launch {
            repository.addMedicine(medicine)
            val newCount = _addMedicineCount.value + 1
            _addMedicineCount.value = newCount
            if (newCount % 3 == 0) {
                _showInterstitial.emit(Unit)
            }
        }
    }

    fun deleteMedicine(medicine: Medicine) {
        viewModelScope.launch { repository.deleteMedicine(medicine) }
    }

    fun markDoseTaken(doseLog: DoseLog) {
        viewModelScope.launch { repository.markDoseTaken(doseLog) }
    }

    fun skipDose(doseLog: DoseLog) {
        viewModelScope.launch { repository.skipDose(doseLog) }
    }
}
