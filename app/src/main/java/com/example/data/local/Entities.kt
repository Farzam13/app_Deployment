package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "consultations")
data class ConsultationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientName: String,
    val phoneNumber: String,
    val toolSummaryContext: String,
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "weight_logs")
data class WeightLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val heightCm: Double,
    val surgeryWeightKg: Double,
    val currentWeightKg: Double,
    val bmi: Double,
    val procedureName: String,
    val dateLogged: Long = System.currentTimeMillis()
)

@Entity(tableName = "assessment_history")
data class AssessmentHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val toolId: String,
    val summary: String,
    val status: String,
    val timestamp: Long = System.currentTimeMillis()
)
