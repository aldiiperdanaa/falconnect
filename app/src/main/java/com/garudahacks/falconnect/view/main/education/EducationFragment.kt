package com.garudahacks.falconnect.view.main.education

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.databinding.FragmentCommunityBinding
import com.garudahacks.falconnect.databinding.FragmentEducationBinding
import com.garudahacks.falconnect.view.main.community.CommunityAdapter
import com.garudahacks.falconnect.view.main.home.HomeCourseAdapter
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EducationFragment : Fragment() {

    private lateinit var binding: FragmentEducationBinding
    private lateinit var adapter: HomeCourseAdapter
    private val db by lazy { Firebase.firestore }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentEducationBinding.inflate(inflater, container, false)

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.backgroundPrimary)
        activity?.window?.let { window ->
            ViewCompat.getWindowInsetsController(window.decorView)?.let { controller ->
                controller.isAppearanceLightStatusBars = true
            }
        }

        return binding.root
    }
}