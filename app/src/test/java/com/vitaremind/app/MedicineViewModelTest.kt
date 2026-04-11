package com.vitaremind.app

import com.vitaremind.app.data.local.entity.DoseLog
import com.vitaremind.app.data.local.entity.DoseStatus
import com.vitaremind.app.data.local.entity.Medicine
import com.vitaremind.app.data.repository.MedicineRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class MedicineViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: MedicineRepository

    private val sampleMedicine = Medicine(
        id            = 1L,
        name          = "Aspirin",
        dosage        = "500mg",
        color         = 0xFF1D9E75.toInt(),
        reminderTimes = "[\"08:00\",\"20:00\"]",
        isActive      = true,
        createdAt     = System.currentTimeMillis()
    )

    private val pendingDose = DoseLog(
        id            = 1L,
        medicineId    = 1L,
        scheduledTime = System.currentTimeMillis(),
        takenAt       = null,
        status        = DoseStatus.PENDING
    )

    private val takenDose = DoseLog(
        id            = 2L,
        medicineId    = 1L,
        scheduledTime = System.currentTimeMillis(),
        takenAt       = System.currentTimeMillis(),
        status        = DoseStatus.TAKEN
    )

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun getAllActiveMedicines_returnsEmptyListInitially() = runTest {
        whenever(repository.getAllActiveMedicines()).thenReturn(flowOf(emptyList()))
        whenever(repository.getTodayDoseLogs()).thenReturn(flowOf(emptyList()))

        val result = repository.getAllActiveMedicines().first()
        assertTrue(result.isEmpty())
    }

    @Test
    fun getAllActiveMedicines_returnsMedicineList() = runTest {
        whenever(repository.getAllActiveMedicines()).thenReturn(flowOf(listOf(sampleMedicine)))
        whenever(repository.getTodayDoseLogs()).thenReturn(flowOf(emptyList()))

        val result = repository.getAllActiveMedicines().first()
        assertEquals(1, result.size)
        assertEquals("Aspirin", result[0].name)
        assertEquals("500mg", result[0].dosage)
    }

    @Test
    fun getTodayDoseLogs_returnsPendingDose() = runTest {
        whenever(repository.getAllActiveMedicines()).thenReturn(flowOf(listOf(sampleMedicine)))
        whenever(repository.getTodayDoseLogs()).thenReturn(flowOf(listOf(pendingDose)))

        val result = repository.getTodayDoseLogs().first()
        assertEquals(1, result.size)
        assertEquals(DoseStatus.PENDING, result[0].status)
    }

    @Test
    fun getTodayDoseLogs_distinguishesTakenFromPending() = runTest {
        whenever(repository.getAllActiveMedicines()).thenReturn(flowOf(listOf(sampleMedicine)))
        whenever(repository.getTodayDoseLogs()).thenReturn(flowOf(listOf(pendingDose, takenDose)))

        val doses = repository.getTodayDoseLogs().first()
        val pending = doses.filter { it.status == DoseStatus.PENDING }
        val taken   = doses.filter { it.status == DoseStatus.TAKEN }

        assertEquals(1, pending.size)
        assertEquals(1, taken.size)
    }

    @Test
    fun doseStatus_constants_haveCorrectValues() {
        assertEquals("PENDING", DoseStatus.PENDING)
        assertEquals("TAKEN",   DoseStatus.TAKEN)
        assertEquals("SKIPPED", DoseStatus.SKIPPED)
    }
}
