package com.example.batikcapstone.onboarding

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import com.example.batikcapstone.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {
    private lateinit var title: String
    private lateinit var description: String
    private lateinit var imageResource: Bitmap
    private lateinit var tvTitle: AppCompatTextView
    private lateinit var tvDescription: AppCompatTextView
    private lateinit var image: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            title = requireArguments().getString(ARG_PARAM1)!!
            description = requireArguments().getString(ARG_PARAM2)!!
            imageResource = requireArguments().getParcelable(ARG_PARAM3)!!
        }
    }

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        val view = binding.root
        tvTitle = binding.textOnboardingTitle
        tvDescription = binding.textOnboardingDescription
        image = binding.imageOnboarding
        tvTitle.text = title
        tvDescription.text = description
        image.setImageDrawable(BitmapDrawable(resources, imageResource))
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
        private const val ARG_PARAM3 = "param3"

        fun newInstance(
            title: String?,
            description: String?,
            imageResource: Bitmap
        ): OnboardingFragment {
            val fragment = OnboardingFragment()
            val args = Bundle().apply {
                putString(ARG_PARAM1, title)
                putString(ARG_PARAM2, description)
                putParcelable(ARG_PARAM3, imageResource)
            }
            fragment.arguments = args
            return fragment
        }
    }
}