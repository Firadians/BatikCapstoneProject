package com.example.batikcapstone.data.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.batikcapstone.data.model.PredictionResult
import com.example.batikcapstone.R
import java.text.SimpleDateFormat
import java.util.*

class PredictionAdapter : RecyclerView.Adapter<PredictionAdapter.PredictionViewHolder>() {
    private val predictionResults: MutableList<PredictionResult> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PredictionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_prediction_result, parent, false)
        return PredictionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PredictionViewHolder, position: Int) {
        val predictionResult = predictionResults[position]
        holder.bind(predictionResult)
    }

    override fun getItemCount(): Int = predictionResults.size

    fun setData(data: List<PredictionResult>) {
        predictionResults.clear()
        predictionResults.addAll(data)
        notifyDataSetChanged()
    }

    inner class PredictionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val predictionText: TextView = itemView.findViewById(R.id.tv_prediction)
        private val timestampText: TextView = itemView.findViewById(R.id.tv_timestamp)
        private val historyImage: ImageView = itemView.findViewById(R.id.iv_image_taken)
//        private val recentImage: ImageView = itemView.findViewById(R.id.iv_recentImage)
  //      private val recentPrediction: TextView = itemView.findViewById(R.id.tv_recentName)

        fun bind(predictionResult: PredictionResult) {
            predictionText.text = predictionResult.result
            val timestamp = formatTimestamp(predictionResult.timestamp)
            val fullTimestamp = "Date taken: $timestamp" // Add "Timestamp: " before the formatted timestamp
            timestampText.text = fullTimestamp
            // Load and display the image
            Glide.with(itemView)
                .load(predictionResult.imagePath)
                .into(historyImage)

            // Display the recent picture and prediction result
//            Glide.with(itemView)
//                .load(predictionResult.imagePath)
//                .into(recentImage)
//            recentPrediction.text = predictionResult.result
        }

        private fun formatTimestamp(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = Date(timestamp)
            return dateFormat.format(date)
        }
    }
}