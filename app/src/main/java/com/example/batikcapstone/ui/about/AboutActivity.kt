package com.example.batikcapstone.ui.about

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import com.example.batikcapstone.R
import com.example.batikcapstone.databinding.ActivityAboutBinding
import com.example.batikcapstone.databinding.ActivityNewsDetailBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding : ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val formattedDescription = getString(R.string.about_desc).replace("\n", "<br>")
        val styledDescription = Html.fromHtml(formattedDescription)
        binding.textView.text = styledDescription

        binding.btnBackDetail.setOnClickListener {
            finish()
        }
    }
}