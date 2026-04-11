package com.vitaremind.app.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.vitaremind.app.data.local.dao.DoseLogDao
import com.vitaremind.app.data.local.dao.MedicineDao
import com.vitaremind.app.data.local.dao.WaterDao
import com.vitaremind.app.data.local.entity.DoseLog
import com.vitaremind.app.data.local.entity.Medicine
import com.vitaremind.app.data.local.entity.WaterLog

@Database(
    entities = [WaterLog::class, Medicine::class, DoseLog::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun waterDao(): WaterDao
    abstract fun medicineDao(): MedicineDao
    abstract fun doseLogDao(): DoseLogDao

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS medicines (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        dosage TEXT NOT NULL,
                        color INTEGER NOT NULL,
                        reminderTimes TEXT NOT NULL,
                        isActive INTEGER NOT NULL DEFAULT 1,
                        createdAt INTEGER NOT NULL
                    )
                """.trimIndent())

                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS dose_logs (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        medicineId INTEGER NOT NULL,
                        scheduledTime INTEGER NOT NULL,
                        takenAt INTEGER,
                        status TEXT NOT NULL DEFAULT 'PENDING',
                        FOREIGN KEY (medicineId) REFERENCES medicines(id) ON DELETE CASCADE
                    )
                """.trimIndent())

                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS index_dose_logs_medicineId ON dose_logs(medicineId)"
                )
            }
        }
    }
}
