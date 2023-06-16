package com.example.batikcapstone.splashscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.batikcapstone.R
import com.example.batikcapstone.onboarding.OnboardActivity

class SplashscreenActivity : AppCompatActivity() {
    private val SPLASH_DELAY = 3000 // Delay in milliseconds (3 seconds)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)

        // Handler to delay the screen change
        Handler().postDelayed({
            val intent = Intent(this, OnboardActivity::class.java)
            startActivity(intent)
            finish()
        }, SPLASH_DELAY.toLong())
    }
}