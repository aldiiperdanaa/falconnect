package com.garudahacks.falconnect.view.main.education

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.databinding.FragmentEducationBinding
import com.garudahacks.falconnect.model.Course
import com.garudahacks.falconnect.util.GridSpacingItemDecoration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.NumberFormat
import java.util.Locale

class EducationFragment : Fragment(), CourseAdapter.AdapterListener {

    private lateinit var binding: FragmentEducationBinding
    private val db by lazy { Firebase.firestore }
    private lateinit var adapterCourse: CourseAdapter

    companion object {
        private const val GRID_SPAN_COUNT = 2
        private const val GRID_SPACING_DP = 12
    }

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

        setupRecyclerView()
        getCourse()

        return binding.root
    }

    private fun setupRecyclerView() {
        adapterCourse = CourseAdapter(arrayListOf(), this)
        binding.rvCourse.layoutManager = GridLayoutManager(context, GRID_SPAN_COUNT)
        binding.rvCourse.adapter = adapterCourse

        val spacingInPixels = (GRID_SPACING_DP * resources.displayMetrics.density).toInt()
        binding.rvCourse.addItemDecoration(GridSpacingItemDecoration(GRID_SPAN_COUNT, spacingInPixels, true))
    }

    private fun getCourse() {
        binding.progressBar.visibility = View.VISIBLE
        val courses: ArrayList<Course> = arrayListOf()
        db.collection("courses")
            .get()
            .addOnSuccessListener { result ->
                binding.progressBar.visibility = View.GONE
                result.forEach { doc ->
                    val priceString = doc.data["price"].toString()
                    val price = priceString.toIntOrNull() ?: 0
                    val rating = (doc.data["rating"] as? Long)?.toInt() ?: 0

                    val formatter = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                    formatter.maximumFractionDigits = 0
                    val formattedPrice = formatter.format(price)

                    courses.add(
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
                adapterCourse.setData(courses)
            }
            .addOnFailureListener { exception ->
                binding.progressBar.visibility = View.GONE
            }
    }

    override fun onClick(courses: Course) {
        Toast.makeText(requireContext(), "Detail course not yet available", Toast.LENGTH_SHORT).show()
    }
}