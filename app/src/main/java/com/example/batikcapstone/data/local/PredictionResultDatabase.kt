package com.example.batikcapstone.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.batikcapstone.data.model.PredictionResult

@Database(entities = [PredictionResult::class], version = 1)
abstract class PredictionResultDatabase : RoomDatabase() {
    abstract fun predictionResultDao(): PredictionResultDao

    companion object {
        private const val DATABASE_NAME = "prediction_results"

        @Volatile
        private var INSTANCE: PredictionResultDatabase? = null

        fun getDatabase(context: Context): PredictionResultDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PredictionResultDatabase::class.java,
                    DATABASE_NAME
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}