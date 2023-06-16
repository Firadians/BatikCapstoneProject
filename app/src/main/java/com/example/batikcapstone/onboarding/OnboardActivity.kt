package com.example.batikcapstone.onboarding

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.example.batikcapstone.MainActivity
import com.example.batikcapstone.R
import com.example.batikcapstone.databinding.ActivityOnboardBinding
import com.google.android.material.tabs.TabLayoutMediator

class OnboardActivity : AppCompatActivity() {
    private lateinit var mViewPager: ViewPager2
    private lateinit var btnBack: Button
    private lateinit var btnNext: Button
    private lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding: ActivityOnboardBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)


        sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        // Check if onboarding has already been shown
        if (isOnboardingShown()) {
            navigateToMain()
        } else {
            // Set the flag indicating onboarding has been shown
            setOnboardingShown(true)
        }

        mViewPager = binding.viewPager
        mViewPager.adapter = OnboardingViewPagerAdapter(this, this)
        mViewPager.offscreenPageLimit = 1
        btnBack = binding.btnPreviousStep
        btnNext = binding.btnNextStep
        mViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == 2) {
                    btnNext.text = getText(R.string.finish)
                }
                else if (position == 0){
                    btnBack.visibility = View.GONE
                }
                else {
                    btnBack.visibility = View.VISIBLE
                    btnBack.text = getText(R.string.back)
                    btnNext.text = getText(R.string.next)
                }
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
            override fun onPageScrollStateChanged(arg0: Int) {}
        })
        TabLayoutMediator(binding.pageIndicator, mViewPager) { _, _ -> }.attach()

        btnNext.setOnClickListener {
            if (getItem() == 2) {
                navigateToMain()
            } else {
                mViewPager.setCurrentItem(getItem() + 1, true)
            }
        }

        btnBack.setOnClickListener {
            if (getItem() == 0) {
                finish()
            } else {
                mViewPager.setCurrentItem(getItem() - 1, true)
            }
        }
    }

    private fun getItem(): Int {
        return mViewPager.currentItem
    }


    private fun isOnboardingShown(): Boolean {
        return sharedPreferences.getBoolean("isOnboardingShown", false)
    }

    private fun setOnboardingShown(isShown: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isOnboardingShown", isShown)
        editor.apply()
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}