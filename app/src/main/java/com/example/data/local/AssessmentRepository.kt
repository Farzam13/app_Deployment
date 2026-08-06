package com.example.data.local

import kotlinx.coroutines.flow.Flow

class AssessmentRepository(private val db: AppDatabase) {
    val consultations: Flow<List<ConsultationEntity>> = db.consultationDao().getAllConsultations()
    val weightLogs: Flow<List<WeightLogEntity>> = db.weightLogDao().getAllWeightLogs()
    val history: Flow<List<AssessmentHistoryEntity>> = db.assessmentHistoryDao().getAllHistory()

    suspend fun saveConsultation(name: String, phone: String, contextStr: String, notes: String): Long {
        return db.consultationDao().insertConsultation(
            ConsultationEntity(
                patientName = name,
                phoneNumber = phone,
                toolSummaryContext = contextStr,
                notes = notes
            )
        )
    }

    suspend fun deleteConsultation(id: Long) {
        db.consultationDao().deleteConsultation(id)
    }

    suspend fun saveWeightLog(
        heightCm: Double,
        surgeryWeightKg: Double,
        currentWeightKg: Double,
        bmi: Double,
        procedureName: String
    ): Long {
        return db.weightLogDao().insertWeightLog(
            WeightLogEntity(
                heightCm = heightCm,
                surgeryWeightKg = surgeryWeightKg,
                currentWeightKg = currentWeightKg,
                bmi = bmi,
                procedureName = procedureName
            )
        )
    }

    suspend fun saveHistory(toolId: String, summary: String, status: String): Long {
        return db.assessmentHistoryDao().insertHistory(
            AssessmentHistoryEntity(
                toolId = toolId,
                summary = summary,
                status = status
            )
        )
    }
}
