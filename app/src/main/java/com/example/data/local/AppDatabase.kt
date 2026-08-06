package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ConsultationEntity::class, WeightLogEntity::class, AssessmentHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun consultationDao(): ConsultationDao
    abstract fun weightLogDao(): WeightLogDao
    abstract fun assessmentHistoryDao(): AssessmentHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "drk_assessment_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
