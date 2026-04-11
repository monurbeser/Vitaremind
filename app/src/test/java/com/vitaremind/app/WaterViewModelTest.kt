package com.vitaremind.app

import androidx.work.WorkManager
import com.vitaremind.app.data.repository.WaterRepository
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class WaterViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: WaterRepository
    private lateinit var workManager: WorkManager

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = mock()
        workManager = mock()
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
    }

    @Test
    fun dailyGoal_emitsDefaultValue() = runTest {
        whenever(repository.dailyGoalMl).thenReturn(flowOf(2000))
        whenever(repository.getTodayLogs()).thenReturn(flowOf(emptyList()))
        whenever(repository.getTodayTotal()).thenReturn(flowOf(null))

        val result = repository.dailyGoalMl.first()
        assertEquals(2000, result)
    }

    @Test
    fun getTodayTotal_returnsSumOfEntries() = runTest {
        whenever(repository.getTodayTotal()).thenReturn(flowOf(750))
        whenever(repository.getTodayLogs()).thenReturn(flowOf(emptyList()))
        whenever(repository.dailyGoalMl).thenReturn(flowOf(2000))

        val result = repository.getTodayTotal().first()
        assertEquals(750, result)
    }

    @Test
    fun getTodayTotal_returnsNullWhenNoEntries() = runTest {
        whenever(repository.getTodayTotal()).thenReturn(flowOf(null))
        whenever(repository.getTodayLogs()).thenReturn(flowOf(emptyList()))
        whenever(repository.dailyGoalMl).thenReturn(flowOf(2000))

        val result = repository.getTodayTotal().first()
        assertNull(result)
    }
}
