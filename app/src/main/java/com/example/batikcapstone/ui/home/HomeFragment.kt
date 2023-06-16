package com.example.batikcapstone.ui.home

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.batikcapstone.CameraResultActivity
import com.example.batikcapstone.R
import com.example.batikcapstone.data.adapter.BatikAdapter
import com.example.batikcapstone.data.adapter.NewsAdapter
import com.example.batikcapstone.data.local.PredictionResultDao
import com.example.batikcapstone.data.local.PredictionResultDatabase
import com.example.batikcapstone.data.model.Batik
import com.example.batikcapstone.data.model.News
import com.example.batikcapstone.data.model.PredictionResult
import com.example.batikcapstone.databinding.FragmentHomeBinding
import com.example.batikcapstone.ui.about.AboutActivity
import com.example.batikcapstone.ui.batik.BatikSearchActivity
import com.example.batikcapstone.ui.batik.DetailBatikActivity
import com.example.batikcapstone.ui.guide.PopupDataHandler
import com.example.batikcapstone.ui.history.HistoryActivity
import com.example.batikcapstone.ui.news.NewsDetailActivity
import com.example.batikcapstone.ui.settings.LanguageActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var firestore: FirebaseFirestore
    private lateinit var batikAdapter: BatikAdapter
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsList: MutableList<News>
    private lateinit var batikList: MutableList<Batik>
    private lateinit var navController: NavController
    private lateinit var predictionResultsLiveData: LiveData<List<PredictionResult>>
    private lateinit var predictionResultDao: PredictionResultDao
    private lateinit var sharedPreferences: SharedPreferences


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedPreferences = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val isFirstTime = sharedPreferences.getBoolean(PREF_FIRST_TIME, DEFAULT_FIRST_TIME)

        if (isFirstTime) {
            sharedPreferences.edit().putBoolean(PREF_FIRST_TIME, false).apply()
            view.post {
                showCustomPopup()
            }
        }

        database = FirebaseDatabase.getInstance()

        newsList = mutableListOf()
        newsAdapter = NewsAdapter(newsList, false)
        binding.rvNews.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        binding.rvNews.adapter = newsAdapter

        batikList = mutableListOf()
        batikAdapter = BatikAdapter(batikList, true)
        firestore = FirebaseFirestore.getInstance()
        binding.rvBatik.layoutManager = LinearLayoutManager(requireContext())
        binding.rvBatik.adapter = batikAdapter

        navController = findNavController()
        // Call the function to fetch data from the Realtime Database
        fetchDataFromDatabase()



        // Get an instance of the PredictionResultDatabase
        val predictionResultDatabase = PredictionResultDatabase.getDatabase(requireContext())

        // Initialize the predictionResultDao property
        predictionResultDao = predictionResultDatabase.predictionResultDao()

        // Initialize the predictionResultsLiveData property
        predictionResultsLiveData = predictionResultDao.getAllPredictionResults()

        predictionResultsLiveData.observe(viewLifecycleOwner, Observer { predictionResults ->
            // Get the most recent prediction result from the list
            val recentPredictionResult = predictionResults.firstOrNull()

            // Display the recent prediction result in the TextView
            binding.tvRecentName.text = recentPredictionResult?.result ?: getString(R.string.home_fragment_recent_title)

// Load the recent picture into the ImageView using the imagePath field
            recentPredictionResult?.imagePath?.let { imagePath ->
                val file = File(imagePath)
                if (file.exists()) {
                    Glide.with(requireContext())
                        .load(file)
                        .into(binding.ivRecentImage)
                } else {
                    // If the image file doesn't exist, show a placeholder image
                    Glide.with(requireContext())
                        .load(R.drawable.baseline_image_24) // Replace with the placeholder image resource ID
                        .into(binding.ivRecentImage)
                }
            } ?: run {
                // If the imagePath is null, show a placeholder image
                Glide.with(requireContext())
                    .load(R.drawable.baseline_error_24) // Replace with the placeholder image resource ID
                    .into(binding.ivRecentImage)
            }
        })

        binding.tvRecentDetail.setOnClickListener {
            Intent(requireActivity(), HistoryActivity::class.java).also { intent ->
                startActivity(intent)
            }
        }

        binding.svHome.setOnClickListener {
            Intent(requireActivity(), BatikSearchActivity::class.java).also { intent ->
                startActivity(intent)

            }
        }
        binding.cameraButton.setOnClickListener {
            Intent(requireActivity(), CameraResultActivity::class.java).also { intent ->
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.slide_from_left, R.anim.slide_from_right)
            }
        }

        binding.btnGuide.setOnClickListener {
            showCustomPopup()
        }

        binding.btnHistory.setOnClickListener {
            Intent(requireActivity(), HistoryActivity::class.java).also { intent ->
                startActivity(intent)
            }
        }

        binding.btnAbout.setOnClickListener {
            Intent(requireActivity(), AboutActivity::class.java).also { intent ->
                startActivity(intent)
                requireActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left)
            }
        }

        binding.btnSetting.setOnClickListener {
            val intent = Intent(android.provider.Settings.ACTION_LOCALE_SETTINGS)
            startActivity(intent)

        }


        newsAdapter.setOnItemClickCallback(object : NewsAdapter.OnItemClickCallback {
            override fun onItemClicked(data: News) {
                val intent = Intent(requireContext(), NewsDetailActivity::class.java)
                intent.putExtra("news", data) // Pass the entire News object to the activity
                startActivity(intent)
            }
        })

        batikAdapter.setOnItemClickCallback(object : BatikAdapter.OnItemClickCallback {
            override fun onItemClicked(data: Batik) {
                val intent = Intent(requireContext(), DetailBatikActivity::class.java)
                intent.putExtra("batik", data) // Pass the entire News object to the activity
                startActivity(intent)
            }
        })

        return view
    }

    private fun showCustomPopup() {
        val dataHandler = PopupDataHandler(requireContext())
        val inflater = dataHandler.getInflater()
        val view = inflater.inflate(R.layout.popup_app_guide, null)

        val popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        // Set any desired properties for the popup window
        //popupWindow.animationStyle = R.style.PopupAnimation
        popupWindow.isOutsideTouchable = false
        popupWindow.isFocusable = true

        // Get the UI elements inside the popup layout
        val prevButton = view.findViewById<Button>(R.id.btn_prev)
        val nextButton = view.findViewById<Button>(R.id.btn_next)
        val contentTextView = view.findViewById<TextView>(R.id.message)
        val imageView = view.findViewById<ImageView>(R.id.img_guide)
        val currentIndex = view.findViewById<TextView>(R.id.tv_current_page)

        // Set initial content
        contentTextView.text = dataHandler.getCurrentContent()
        imageView.setImageResource(dataHandler.getCurrentImage())
        currentIndex.text = "${dataHandler.getCurrentPageCount()} / ${dataHandler.getTotalPageCount()}"

        // Hide or show previous button based on the current page index
        when (dataHandler.currentIndex) {
            0 -> prevButton.visibility = View.GONE
            else -> prevButton.visibility = View.VISIBLE
        }

        binding.svHome.setOnClickListener(null)

        // Handle previous button click event
        prevButton.setOnClickListener {
            if (dataHandler.currentIndex > 0) {
                dataHandler.currentIndex--
                contentTextView.text = dataHandler.getCurrentContent()
                imageView.setImageResource(dataHandler.getCurrentImage())
                currentIndex.text = "${dataHandler.getCurrentPageCount()} / ${dataHandler.getTotalPageCount()}"

                // Hide or show previous button based on the updated current page index
                when (dataHandler.currentIndex) {
                    0 -> prevButton.visibility = View.GONE
                    else -> prevButton.visibility = View.VISIBLE
                }

                // Change button text to "Next" if it was "Finish" on the last page
                if (nextButton.text == getString(R.string.guide_finish)) {
                    nextButton.text = getString(R.string.btc_next)
                }
            }
        }

        // Handle next button click event
        nextButton.setOnClickListener {
            if (dataHandler.currentIndex == dataHandler.contents.size - 1) {
                // Last content reached, dismiss the popup
                popupWindow.dismiss()
            } else {
                dataHandler.currentIndex++
                contentTextView.text = dataHandler.getCurrentContent()
                imageView.setImageResource(dataHandler.getCurrentImage())
                currentIndex.text = "${dataHandler.getCurrentPageCount()} / ${dataHandler.getTotalPageCount()}"

                // Change button text to "Finish" on the second-to-last page
                if (dataHandler.currentIndex == dataHandler.contents.size - 1) {
                    prevButton.text = getString(R.string.back)
                    nextButton.text = getString(R.string.guide_finish)
                }

                // Show the previous button
                prevButton.visibility = View.VISIBLE
            }
        }

        // Show the popup window at a specific location on the screen
        val anchorView = binding.viewPopup
        popupWindow.showAtLocation(anchorView, Gravity.CENTER, 0, 0)
    }
    private fun fetchDataFromDatabase() {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("news")
        databaseRef.limitToFirst(3).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<News>()
                for (newsSnapshot in snapshot.children) {
                    val news = newsSnapshot.getValue(News::class.java)
                    news?.let {
                            tempList.add(it)
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

        firestore.collection("batikjenis").get().addOnSuccessListener { snapshot ->
            val tempList = mutableListOf<Batik>()
            for (doc in snapshot.documents) {
                val batik = doc.toObject(Batik::class.java)
                batik?.let {
                    tempList.add(it)

                }
            }
            batikList.clear()
            batikList.addAll(tempList)
            batikAdapter.notifyDataSetChanged()
        }.addOnFailureListener { exception ->
            // Handle the failure here
        }
    }

    companion object {
        private const val PREF_FIRST_TIME = "first_time"
        private const val DEFAULT_FIRST_TIME = true
    }


}