package com.garudahacks.falconnect.view.main.profile

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.databinding.FragmentProfileBinding
import com.garudahacks.falconnect.local.preference.PreferenceManager
import com.garudahacks.falconnect.util.PrefUtil
import com.garudahacks.falconnect.view.WelcomeActivity

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private val pref by lazy { PreferenceManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val identification = pref.getString(PrefUtil.pref_identification)
        val fullname = pref.getString(PrefUtil.pref_fullname)
        val initials = getInitials(fullname)
        binding.identification.text = identification
        binding.fullname.text = fullname
        binding.initial.text = initials

        binding.logout.setOnClickListener {
            pref.clear()
            Toast.makeText(requireContext(), "Account successfully signed out", Toast.LENGTH_SHORT).show()
            startActivity(
                Intent(requireActivity(), WelcomeActivity::class.java)
                    .addFlags(
                        Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK or
                                Intent.FLAG_ACTIVITY_NEW_TASK
                    )
            )
            requireActivity().finish()
        }
    }

    private fun getInitials(fullname: String?): String {
        if (fullname.isNullOrEmpty()) return ""

        val names = fullname.trim().split("\\s+".toRegex()).toTypedArray()
        val builder = StringBuilder()

        for (name in names) {
            if (builder.length >= 2) {
                break
            }
            if (name.isNotEmpty()) {
                builder.append(name[0].toUpperCase())
            }
        }

        return builder.toString()
    }
}