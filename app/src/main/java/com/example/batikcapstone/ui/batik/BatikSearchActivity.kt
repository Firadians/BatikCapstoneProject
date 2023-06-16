package com.example.batikcapstone.ui.batik

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.batikcapstone.R
import com.example.batikcapstone.data.adapter.BatikAdapter
import com.example.batikcapstone.data.model.Batik
import com.example.batikcapstone.data.model.News
import com.example.batikcapstone.databinding.ActivityBatikSearchBinding
import com.example.batikcapstone.ui.news.NewsDetailActivity
import com.google.firebase.firestore.FirebaseFirestore

class BatikSearchActivity : AppCompatActivity() {

    private val viewModel: BatikViewModel by viewModels()
    private lateinit var batikAdapter: BatikAdapter
    private lateinit var binding: ActivityBatikSearchBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var batikList: MutableList<Batik>
    private var searchQuery: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBatikSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = findViewById<RecyclerView>(R.id.rv_batik)

        batikList = mutableListOf()
        batikAdapter = BatikAdapter(batikList, true)
        firestore = FirebaseFirestore.getInstance()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = batikAdapter


        fetchDataFromDatabase()

        val searchView = binding.svNews
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                searchQuery = newText.trim()
                filterData()
                return true
            }
        })

        batikAdapter.setOnItemClickCallback(object : BatikAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Batik) {
                val intent = Intent(this@BatikSearchActivity, DetailBatikActivity::class.java)
                intent.putExtra("batik", data) // Pass the entire News object to the activity
                startActivity(intent)
                overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left)
            }
        })
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun filterData() {
        fetchDataFromDatabase()
    }

    private fun fetchDataFromDatabase() {
        firestore.collection("batikjenis").get().addOnSuccessListener { snapshot ->
            val tempList = mutableListOf<Batik>()
            for (doc in snapshot.documents) {
                val batik = doc.toObject(Batik::class.java)
                batik?.let {
                    if (searchQuery.isEmpty() || batik.name?.contains(
                            searchQuery,
                            ignoreCase = true
                        ) == true
                    ) {
                        tempList.add(it)
                    }
                }
            }
            batikList.clear()
            batikList.addAll(tempList)
            batikAdapter.notifyDataSetChanged()
        }.addOnFailureListener { exception ->
            // Handle the failure here
        }
    }
}