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
import com.garudahacks.falconnect.model.Jobs
import com.garudahacks.falconnect.view.main.community.CommunityAdapter
import com.garudahacks.falconnect.view.main.community.ShareActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem

class JobsFragment : Fragment(), JobsAdapter.AdapterListener {

    private lateinit var binding: FragmentJobsBinding
    private lateinit var adapter: JobsAdapter
    private val db by lazy { Firebase.firestore }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentJobsBinding.inflate(inflater, container, false)

        adapter = JobsAdapter(arrayListOf(), this)
        binding.rvJobs.adapter = adapter

        getJobs()

        return binding.root
    }

    private fun getJobs() {
        binding.progressBar.visibility = View.VISIBLE
        val news: ArrayList<Jobs> = arrayListOf()
        db.collection("jobs")
            .get()
            .addOnSuccessListener { result ->
                binding.progressBar.visibility = View.GONE
                result.forEach { doc ->
                    news.add(
                        Jobs(
                            id = doc.reference.id,
                            category = doc.data["category"].toString(),
                            city = doc.data["city"].toString(),
                            companyImg = doc.data["companyImg"].toString(),
                            companyName = doc.data["companyName"].toString(),
                            description = doc.data["description"].toString(),
                            latitude = doc.data["latitude"].toString(),
                            longitude = doc.data["longitude"].toString(),
                            roleJob = doc.data["roleJob"].toString(),
                            salary = doc.data["salary"].toString(),
                            isDisability = doc.data["isDisability"] as? Boolean ?: false,
                            isBad = doc.data["isBad"] as? Boolean ?: false,
                            createdAt = doc.data["createdAt"] as Timestamp
                        )
                    )
                }
                adapter.setData(news)
            }
            .addOnFailureListener { exception ->
                binding.progressBar.visibility = View.GONE
            }
    }

    override fun onClick(news: Jobs) {
        val intent = Intent(requireContext(), DetailJobsActivity::class.java).apply {
            putExtra("id", news.id)
        }
        startActivity(intent)
    }
}