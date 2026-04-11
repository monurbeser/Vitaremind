package com.vitaremind.app

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.vitaremind.app.data.local.db.AppDatabase
import com.vitaremind.app.data.local.entity.WaterLog
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class WaterRepositoryTest {

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
    }

    @After
    fun teardown() {
        db.close()
    }

    @Test
    fun insertLog_and_getLogsForDate_returnsCorrectEntry() = runTest {
        val dao = db.waterDao()
        val log = WaterLog(date = "2024-01-15", amountMl = 250, timestamp = 1000L)
        dao.insertLog(log)
        val result = dao.getLogsForDate("2024-01-15").first()
        assertEquals(1, result.size)
        assertEquals(250, result[0].amountMl)
    }

    @Test
    fun getTotalForDate_returnsCorrectSum() = runTest {
        val dao = db.waterDao()
        dao.insertLog(WaterLog(date = "2024-01-15", amountMl = 200, timestamp = 1000L))
        dao.insertLog(WaterLog(date = "2024-01-15", amountMl = 350, timestamp = 2000L))
        dao.insertLog(WaterLog(date = "2024-01-16", amountMl = 500, timestamp = 3000L))
        val total = dao.getTotalForDate("2024-01-15").first()
        assertEquals(550, total)
    }

    @Test
    fun getTotalForDate_returnsNullWhenNoEntries() = runTest {
        val dao = db.waterDao()
        val total = dao.getTotalForDate("2099-12-31").first()
        assertNull(total)
    }

    @Test
    fun deleteLog_removesEntry() = runTest {
        val dao = db.waterDao()
        val log = WaterLog(date = "2024-01-15", amountMl = 300, timestamp = 1000L)
        dao.insertLog(log)
        val inserted = dao.getLogsForDate("2024-01-15").first()
        assertEquals(1, inserted.size)
        dao.deleteLog(inserted[0].id)
        val afterDelete = dao.getLogsForDate("2024-01-15").first()
        assertEquals(0, afterDelete.size)
    }
}
