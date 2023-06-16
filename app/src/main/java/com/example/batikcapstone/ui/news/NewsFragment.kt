package com.example.batikcapstone.ui.news

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.batikcapstone.R
import com.example.batikcapstone.data.adapter.NewsAdapter
import com.example.batikcapstone.data.model.News
import com.example.batikcapstone.databinding.FragmentNewsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class NewsFragment : Fragment() {
    private lateinit var database: FirebaseDatabase
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var binding: FragmentNewsBinding
    private lateinit var newsList: MutableList<News>
    private var searchQuery: String = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNewsBinding.inflate(inflater, container, false)
        val view = binding.root

        database = FirebaseDatabase.getInstance()

        newsList = mutableListOf()
        newsAdapter = NewsAdapter(newsList, true)
        binding.rvNews.layoutManager = LinearLayoutManager(requireContext())
        binding.rvNews.adapter = newsAdapter


        // Call the function to fetch data from the Realtime Database
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

        newsAdapter.setOnItemClickCallback(object : NewsAdapter.OnItemClickCallback {
            override fun onItemClicked(data: News) {
                val intent = Intent(requireContext(), NewsDetailActivity::class.java)
                intent.putExtra("news", data) // Pass the entire News object to the activity
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left)
            }
        })
        return view
    }

    private fun filterData() {
        fetchDataFromDatabase()
    }

    private fun fetchDataFromDatabase() {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("news")

        databaseRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<News>()
                for (newsSnapshot in snapshot.children) {
                    val news = newsSnapshot.getValue(News::class.java)
                    news?.let {
                        if (searchQuery.isEmpty() || news.name?.contains(searchQuery, ignoreCase = true) == true) {
                            tempList.add(it)
                        }
                    }
                }
                newsList.clear()
                newsList.addAll(tempList)
                newsAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error here
            }
        })
    }
}