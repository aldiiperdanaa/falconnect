package com.garudahacks.falconnect.view.main.home

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.databinding.FragmentHomeBinding
import com.garudahacks.falconnect.local.preference.PreferenceManager
import com.garudahacks.falconnect.model.News
import com.garudahacks.falconnect.util.PrefUtil
import com.garudahacks.falconnect.util.SpacingItemDecoration
import com.garudahacks.falconnect.util.TimeOfDay
import com.garudahacks.falconnect.util.timeOfDay
import com.garudahacks.falconnect.view.news.DetailNewsActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import java.util.Date

class HomeFragment : Fragment(), HomeNewsAdapter.AdapterListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: HomeNewsAdapter
    private val db by lazy { Firebase.firestore }
    private val pref by lazy { PreferenceManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        adapter = HomeNewsAdapter(arrayListOf(), this)
        binding.rvNews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvNews.adapter = adapter

        val spacingInPixels = 20
        val edgeSpacingInPixels = 32
        binding.rvNews.addItemDecoration(SpacingItemDecoration(spacingInPixels, edgeSpacingInPixels))

        getNews()
        setupTimeOfDay()

        return binding.root
    }

//    private fun setupImageCarousel() {
//        val carouselItems = listOf(
//            R.drawable.img_placeholder,
//            R.drawable.img_placeholder,
//            R.drawable.img_placeholder
//        ).map { CarouselItem(imageDrawable = it) }
//
//        binding.carousel.setData(carouselItems)
//    }

    private fun getNews() {
        val news: ArrayList<News> = arrayListOf()
        db.collection("news")
            .limit(5)
            .get()
            .addOnSuccessListener { result ->
                result.forEach { doc ->
                    news.add(
                        News(
                            id = doc.reference.id,
                            title = doc.data["title"].toString(),
                            photoUrl = doc.data["photoUrl"].toString(),
                        )
                    )
                }
                adapter.setData(news)
            }
    }

    override fun onClick(news: News) {
        val intent = Intent(requireContext(), DetailNewsActivity::class.java).apply {
            putExtra("id", news.id)
        }
        startActivity(intent)
    }

    private fun setupTimeOfDay() {
        val currentTimeOfDay = Date().timeOfDay()
        val day: String
        when (currentTimeOfDay) {
            TimeOfDay.MORNING -> {
                day = "Good Morning"
            }
            TimeOfDay.DAY -> {
                day = "Good Day"
            }
            TimeOfDay.AFTERNOON -> {
                day = "Good Evening"
            }
            TimeOfDay.NIGHT -> {
                day = "Good Night"
            }
        }

        binding.greeting.text = day

        val fullname = pref.getString(PrefUtil.pref_fullname)
        binding.fullname.text = fullname
    }
}