package com.example.batikcapstone.ui.news

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.batikcapstone.R
import com.example.batikcapstone.data.model.News
import com.example.batikcapstone.databinding.ActivityNewsDetailBinding

class NewsDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewsDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<News>("news")

        binding.detailTitle.text = data!!.name
        val formattedDescription = data!!.description?.replace("\n", "<br>")
        val styledDescription = Html.fromHtml(formattedDescription)
        binding.detailDesc.text = styledDescription
        binding.detailPostDate.text = data!!.postDate
        Glide.with(this)
            .load(data.photoUrl)
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    binding.constraintLayout4.background = resource
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Do nothing
                }
            })

        binding.btnBack.setOnClickListener {
            navigateBack()
        }
    }
    private fun navigateBack() {
        // Handle the navigation logic to go back to the previous activity
        // For example, using finish() to close the current activity
        finish()
    }
}