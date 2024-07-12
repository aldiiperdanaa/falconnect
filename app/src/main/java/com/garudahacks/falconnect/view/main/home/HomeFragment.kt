package com.garudahacks.falconnect.view.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.databinding.FragmentHomeBinding
import com.garudahacks.falconnect.local.preference.PreferenceManager
import com.garudahacks.falconnect.model.Course
import com.garudahacks.falconnect.model.News
import com.garudahacks.falconnect.util.PrefUtil
import com.garudahacks.falconnect.util.SpacingItemDecoration
import com.garudahacks.falconnect.util.TimeOfDay
import com.garudahacks.falconnect.util.timeOfDay
import com.garudahacks.falconnect.view.main.community.ShareActivity
import com.garudahacks.falconnect.view.news.DetailNewsActivity
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment(),
    HomeNewsAdapter.AdapterListener,
    HomeCourseAdapter.AdapterListener {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapterNews: HomeNewsAdapter
    private lateinit var adapterCourse: HomeCourseAdapter
    private val db by lazy { Firebase.firestore }
    private val pref by lazy { PreferenceManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        adapterNews = HomeNewsAdapter(arrayListOf(), this)
        binding.rvNews.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvNews.adapter = adapterNews

        adapterCourse = HomeCourseAdapter(arrayListOf(), this)
        binding.rvCourse.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCourse.adapter = adapterCourse

        val spacingInPixels = 20
        val edgeSpacingInPixels = 32
        binding.rvNews.addItemDecoration(SpacingItemDecoration(spacingInPixels, edgeSpacingInPixels))
        binding.rvCourse.addItemDecoration(SpacingItemDecoration(spacingInPixels, edgeSpacingInPixels))

        getNews()
        getCourse()
        setupTimeOfDay()
        setupImageCarousel()

        activity?.window?.statusBarColor = ContextCompat.getColor(requireContext(), R.color.primary500)
        activity?.window?.let { window ->
            ViewCompat.getWindowInsetsController(window.decorView)?.let { controller ->
                controller.isAppearanceLightStatusBars = false // Text and icons are white
            }
        }

        return binding.root
    }

    private fun setupImageCarousel() {
        val carouselItems = listOf(
            R.drawable.img_advertisement_2,
            R.drawable.img_advertisement_1,
        ).map { CarouselItem(imageDrawable = it) }

        binding.carousel.setData(carouselItems)
    }

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
                adapterNews.setData(news)
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

    private fun getCourse() {
        val course: ArrayList<Course> = arrayListOf()
        db.collection("courses")
            .limit(5)
            .get()
            .addOnSuccessListener { result ->
                result.forEach { doc ->
                    val priceString = doc.data["price"].toString()
                    val price = priceString.toIntOrNull() ?: 0
                    val rating = (doc.data["rating"] as? Long)?.toInt() ?: 0

                    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                    formatter.maximumFractionDigits = 0
                    val formattedPrice = formatter.format(price)

                    course.add(
                        Course(
                            id = doc.reference.id,
                            title = doc.data["title"].toString(),
                            imageCourse = doc.data["imageCourse"].toString(),
                            instructor = doc.data["instructor"].toString(),
                            job = doc.data["job"].toString(),
                            price = formattedPrice,
                            rating = rating,
                        )
                    )
                }
                adapterCourse.setData(course)
            }
    }

    override fun onClick(course: Course) {
        val intent = Intent(requireContext(), ShareActivity::class.java).apply {
            putExtra("id", course.id)
        }
        startActivity(intent)
    }
}
