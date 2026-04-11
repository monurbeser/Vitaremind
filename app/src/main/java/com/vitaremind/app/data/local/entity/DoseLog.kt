package com.vitaremind.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "dose_logs",
    foreignKeys = [
        ForeignKey(
            entity = Medicine::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("medicineId")]
)
data class DoseLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val medicineId: Long,
    val scheduledTime: Long,
    val takenAt: Long? = null,
    val status: String = DoseStatus.PENDING
)

object DoseStatus {
    const val PENDING = "PENDING"
    const val TAKEN   = "TAKEN"
    const val SKIPPED = "SKIPPED"
}
