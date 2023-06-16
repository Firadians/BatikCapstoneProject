package com.example.batikcapstone.onboarding

import android.content.Context
import android.graphics.BitmapFactory
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.batikcapstone.R

class OnboardingViewPagerAdapter (fragmentActivity: FragmentActivity,
                                  private val context: Context
) :
FragmentStateAdapter(fragmentActivity) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> OnboardingFragment.newInstance(
                context.resources.getString(R.string.title_onboarding_1),
                context.resources.getString(R.string.description_onboarding_1),
                BitmapFactory.decodeResource(context.resources, R.drawable.onboard1)
            )
            1 -> OnboardingFragment.newInstance(
                context.resources.getString(R.string.title_onboarding_2),
                context.resources.getString(R.string.description_onboarding_2),
                BitmapFactory.decodeResource(context.resources, R.drawable.onboard2)
            )
            else -> OnboardingFragment.newInstance(
                context.resources.getString(R.string.title_onboarding_3),
                context.resources.getString(R.string.description_onboarding_3),
                BitmapFactory.decodeResource(context.resources, R.drawable.onboard3)
            )
        }
    }

    override fun getItemCount(): Int {
        return 3
    }
}