package com.example.batikcapstone.ui.settings

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.batikcapstone.R
import java.util.*

class LanguageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        val englishButton = findViewById<Button>(R.id.btn_english)
        val indonesiaButton = findViewById<Button>(R.id.btn_indonesia)
        val backbtn = findViewById<ImageButton>(R.id.btn_back)

        englishButton.setOnClickListener { changeLanguage("en") }
        indonesiaButton.setOnClickListener { changeLanguage("id") }
        backbtn.setOnClickListener { finishWithResult() }
    }

    private fun changeLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val configuration = Configuration()
        configuration.locale = locale
        baseContext.resources.updateConfiguration(
            configuration,
            baseContext.resources.displayMetrics
        )
    }

    private fun finishWithResult() {
        val data = Intent().apply {
            // Pass the selected language code back to the previous activity
            val selectedLanguageCode = getCurrentLanguageCode()
            putExtra("languageCode", selectedLanguageCode)
        }
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    private fun getCurrentLanguageCode(): String {
        val configuration = resources.configuration
        return configuration.locale.language
    }
}