package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val fullName: String,
    val birthDate: String,    // Format: DD/MM/YYYY or similar
    val notes: String = "",   // Optional clinical notes or description of archiving
    val registrationTimestamp: Long = System.currentTimeMillis()
)
