package com.garudahacks.falconnect.view.main.community

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.addTextChangedListener
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.databinding.FragmentCommunityBinding
import com.garudahacks.falconnect.model.Community
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class CommunityFragment : Fragment(), CommunityAdapter.AdapterListener {

    private lateinit var binding: FragmentCommunityBinding
    private lateinit var adapter: CommunityAdapter
    private val db by lazy { Firebase.firestore }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommunityBinding.inflate(inflater, container, false)

        adapter = CommunityAdapter(arrayListOf(), this)
        binding.rvCommunity.adapter = adapter

        getCommunity()

        binding.etPost.addTextChangedListener {
            val isPostEmpty = it.isNullOrEmpty()
            updateShareButtonState(isPostEmpty)
        }

        val isPostEmpty = binding.etPost.text.isNullOrEmpty()
        updateShareButtonState(isPostEmpty)

        binding.postShare.setOnClickListener {
            startActivity(Intent(activity, ShareActivity::class.java))
        }

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(activity, ShareActivity::class.java))
        }

        binding.btnImage.setOnClickListener {
            Toast.makeText(requireContext(), "Sorry this feature is not available at this time", Toast.LENGTH_SHORT).show()
        }

        binding.btnExpression.setOnClickListener {
            Toast.makeText(requireContext(), "Sorry this feature is not available at this time", Toast.LENGTH_SHORT).show()
        }

        binding.btnCamera.setOnClickListener {
            Toast.makeText(requireContext(), "Sorry this feature is not available at this time", Toast.LENGTH_SHORT).show()
        }

        binding.btnShare.setOnClickListener {
            Toast.makeText(requireContext(), "Sorry for not being able to post at this time", Toast.LENGTH_SHORT).show()
        }

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.backgroundPrimary)
        activity?.window?.let { window ->
            ViewCompat.getWindowInsetsController(window.decorView)?.let { controller ->
                controller.isAppearanceLightStatusBars = true
            }
        }

        return binding.root
    }

    private fun getCommunity() {
        binding.progressBar.visibility = View.VISIBLE
        val communities: ArrayList<Community> = arrayListOf()
        db.collection("community")
            .get()
            .addOnSuccessListener { result ->
                binding.progressBar.visibility = View.GONE
                result.forEach { doc ->
                    val totalLike = (doc.data["totalLike"] as? Long)?.toInt() ?: 0
                    val totalComment = (doc.data["totalComment"] as? Long)?.toInt() ?: 0
                    val totalBookmark = (doc.data["totalBookmark"] as? Long)?.toInt() ?: 0

                    communities.add(
                        Community(
                            id = doc.reference.id,
                            fullname = doc.data["fullname"].toString(),
                            status = doc.data["status"].toString(),
                            profileImageUrl = doc.data["profileImageUrl"].toString(),
                            photoStatusUrl = doc.data["photoStatusUrl"].toString(),
                            totalLike = totalLike,
                            totalComment = totalComment,
                            totalBookmark = totalBookmark,
                            createdAt = doc.data["createdAt"] as Timestamp
                        )
                    )
                }
                adapter.setData(communities)
            }
            .addOnFailureListener { exception ->
                binding.progressBar.visibility = View.GONE
            }
    }

    override fun onClick(communities: Community) {
        Toast.makeText(requireContext(), "Post details not yet available", Toast.LENGTH_SHORT).show()
    }

    private fun updateShareButtonState(isEmpty: Boolean) {
        if (isEmpty) {
            binding.btnShare.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary200)
            binding.btnShare.isEnabled = false
        } else {
            binding.btnShare.backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.primary500)
            binding.btnShare.isEnabled = true
        }
    }

}