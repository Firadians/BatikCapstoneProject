package com.example.batikcapstone.data.local

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.batikcapstone.data.model.PredictionResult

@Dao
interface PredictionResultDao {
    @Query("SELECT * FROM prediction_results ORDER BY timestamp DESC")
    fun getAllPredictionResults(): LiveData<List<PredictionResult>>

    @Query("SELECT * FROM prediction_results ORDER BY timestamp DESC LIMIT 1")
    fun getMostRecentPrediction(): PredictionResult?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPredictionResult(result: PredictionResult)

    @Delete
    fun deletePredictionResult(result: PredictionResult)

    @Query("DELETE FROM prediction_results")
    fun deleteAllPredictionResults()
}