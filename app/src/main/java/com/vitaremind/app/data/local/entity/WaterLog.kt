package com.vitaremind.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_log")
data class WaterLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,        // format: yyyy-MM-dd
    val amountMl: Int,
    val timestamp: Long      // System.currentTimeMillis()
)
