package com.example.batikcapstone.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.batikcapstone.R
import com.example.batikcapstone.data.adapter.PredictionAdapter
import com.example.batikcapstone.data.local.PredictionResultDao
import com.example.batikcapstone.data.local.PredictionResultDatabase
import com.example.batikcapstone.data.model.PredictionResult
import com.example.batikcapstone.databinding.ActivityHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding
    private lateinit var predictionAdapter: PredictionAdapter
    private lateinit var predictionResultDao: PredictionResultDao
    private lateinit var timer: Timer
    private lateinit var originalPredictionResults: List<PredictionResult>

    private var isAscendingSort = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        predictionResultDao = PredictionResultDatabase.getDatabase(this).predictionResultDao()

        initRecyclerView()
        observePredictionResults()
        setupEraseButton()
        setupBackButton()
        setupSortButton()
        startAutoEraseTimer()
        setupSearchView()

    }

    private fun initRecyclerView() {
        predictionAdapter = PredictionAdapter()
        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = predictionAdapter
        }
    }

    private fun setupSearchView() {
        binding.svHistory.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val query = newText?.toLowerCase(Locale.getDefault()) ?: ""
                val filteredResults = originalPredictionResults.filter {
                    it.result.toLowerCase(Locale.getDefault()).contains(query)
                }
                predictionAdapter.setData(filteredResults)
                return true
            }
        })
    }

    private fun observePredictionResults() {
        predictionResultDao.getAllPredictionResults().observe(this, Observer { predictionResults ->
            originalPredictionResults = predictionResults
            val sortedResults = if (isAscendingSort) {
                predictionResults.sortedBy { it.timestamp }
            } else {
                predictionResults.sortedByDescending { it.timestamp }
            }
            predictionAdapter.setData(sortedResults)
        })
    }

    private fun setupBackButton() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun setupEraseButton() {
        binding.fabErase.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun erasePredictionResults() {
        GlobalScope.launch(Dispatchers.IO) {
            predictionResultDao.deleteAllPredictionResults()
        }
    }

    private fun showDeleteConfirmationDialog() {
        if (predictionAdapter.itemCount == 0) {
            Toast.makeText(this, getString(R.string.history_activity_dialog_nodata), Toast.LENGTH_SHORT).show()
            return
        }

        val alertDialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.history_activity_dialog_title))
            .setMessage(getString(R.string.history_activity_dialog_message))
            .setPositiveButton(getString(R.string.history_activity_dialog_positive)) { _, _ ->
                erasePredictionResults()
            }
            .setNegativeButton(getString(R.string.history_activity_dialog_negative), null)
            .create()

        alertDialog.show()
    }

    private fun setupSortButton() {
        binding.btnChoice.setOnClickListener {
            isAscendingSort = !isAscendingSort
            val sortText = if (isAscendingSort) "Sort Ascending" else "Sort Descending"

            observePredictionResults()
        }
    }

    private fun startAutoEraseTimer() {
        timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                erasePredictionResults()
            }
        }
        val delay = 168L * 60L * 60L * 1000L // 168 hours in milliseconds
        timer.schedule(task, Date(System.currentTimeMillis() + delay))
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}