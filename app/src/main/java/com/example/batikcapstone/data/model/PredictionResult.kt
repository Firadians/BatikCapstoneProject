package com.example.batikcapstone.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "prediction_results")
data class PredictionResult(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val result: String,
    val timestamp: Long,
    val imagePath: String // Add this line
)