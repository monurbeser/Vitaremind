package com.vitaremind.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dosage: String,
    val color: Int,                  // ARGB color int (e.g. Color.Red.toArgb())
    val reminderTimes: String,       // JSON array string e.g. ["08:00","20:00"]
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
