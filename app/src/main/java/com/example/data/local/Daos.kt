package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsultationDao {
    @Query("SELECT * FROM consultations ORDER BY createdAt DESC")
    fun getAllConsultations(): Flow<List<ConsultationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsultation(consultation: ConsultationEntity): Long

    @Query("DELETE FROM consultations WHERE id = :id")
    suspend fun deleteConsultation(id: Long)
}

@Dao
interface WeightLogDao {
    @Query("SELECT * FROM weight_logs ORDER BY dateLogged DESC")
    fun getAllWeightLogs(): Flow<List<WeightLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightLog(log: WeightLogEntity): Long

    @Query("DELETE FROM weight_logs WHERE id = :id")
    suspend fun deleteWeightLog(id: Long)
}

@Dao
interface AssessmentHistoryDao {
    @Query("SELECT * FROM assessment_history ORDER BY timestamp DESC LIMIT 50")
    fun getAllHistory(): Flow<List<AssessmentHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: AssessmentHistoryEntity): Long
}
