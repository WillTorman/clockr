package com.thortech.clockr.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "time_entries")
data class TimeEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String = "",
    val startTime: Long,
    val endTime: Long? = null,
    val projectName: String,
    val synced: Boolean = false
) {
    // No-argument constructor for Firestore
    constructor() : this(0, "", 0, null, "", false)

    fun toMap(): Map<String, Any?> {
        return mapOf(
            "userId" to userId,
            "startTime" to startTime,
            "endTime" to endTime,
            "projectName" to projectName
        )
    }
}
