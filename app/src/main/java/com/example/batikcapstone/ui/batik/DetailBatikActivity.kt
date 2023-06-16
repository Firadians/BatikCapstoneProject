package com.example.batikcapstone.ui.batik

import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.batikcapstone.R
import com.example.batikcapstone.data.model.Batik
import com.example.batikcapstone.data.model.News
import com.example.batikcapstone.databinding.ActivityDetailBatikBinding
import com.example.batikcapstone.databinding.ActivityNewsDetailBinding

class DetailBatikActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBatikBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBatikBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data = intent.getParcelableExtra<Batik>("batik")

        binding.detailTitle.text = data!!.name
        binding.detailOrigin.text = data!!.origin
        binding.detailDesc.text = data!!.description
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
        Glide.with(this)
            .load(data.display1)
            .placeholder(R.drawable.baseline_error_24)
            .error(R.drawable.baseline_error_24)
            .into(binding.display1)
        Glide.with(this)
            .load(data.display2)
            .placeholder(R.drawable.baseline_error_24)
            .error(R.drawable.baseline_error_24)
            .into(binding.display2)

        binding.btnBackDetail.setOnClickListener {
            navigateBack()
        }
    }
    private fun navigateBack() {
        // Handle the navigation logic to go back to the previous activity
        // For example, using finish() to close the current activity
        finish()
    }
}