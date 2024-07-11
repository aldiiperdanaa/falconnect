package com.garudahacks.falconnect.view.main.jobs

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.databinding.FragmentCommunityBinding
import com.garudahacks.falconnect.databinding.FragmentJobsBinding
import com.garudahacks.falconnect.view.main.community.CommunityAdapter
import com.garudahacks.falconnect.view.main.community.ShareActivity

class JobsFragment : Fragment() {

    private lateinit var binding: FragmentJobsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobsBinding.inflate(inflater, container, false)
        return binding.root
    }

}