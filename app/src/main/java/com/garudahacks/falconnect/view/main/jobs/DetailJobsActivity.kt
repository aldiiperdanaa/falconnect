package com.garudahacks.falconnect.view.main.jobs

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.databinding.ActivityDetailJobsBinding
import com.garudahacks.falconnect.model.Jobs
import com.garudahacks.falconnect.view.BaseActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

class DetailJobsActivity : BaseActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityDetailJobsBinding
    private lateinit var mMap: GoogleMap
    private val jobsId by lazy { intent.getStringExtra("id") }
    private val db by lazy { com.google.firebase.Firebase.firestore }
    private lateinit var jobs: Jobs


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailJobsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setNavigationBarColor()
        setStatusBarTransparent()

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            insets
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        binding.detailJob.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            if (scrollY > 0) {
                setStatusBarWhite()
            } else {
                setStatusBarTransparent()
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onStart() {
        super.onStart()
        if (jobsId != null && jobsId!!.isNotEmpty()) {
            detailJobs()
        } else {
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (jobsId != null && jobsId!!.isNotEmpty()) {
            detailJobs()
        } else {
            finish()
        }
    }

    private fun detailJobs() {
        db.collection("jobs")
            .document(jobsId!!)
            .get()
            .addOnSuccessListener { result ->
                jobs = Jobs(
                    id = result.id,
                    roleJob = result["roleJob"].toString(),
                    companyName = result["companyName"].toString(),
                    category = result["category"].toString(),
                    city = result["city"].toString(),
                    salary = result["salary"].toString(),
                    description = result["description"].toString(),
                    createdAt = result["createdAt"] as Timestamp,
                    isDisability = result["isDisability"] as? Boolean ?: false,
                    isBad = result["isBad"] as? Boolean ?: false,
                    latitude = result["latitude"].toString(),
                    longitude = result["longitude"].toString(),
                )

                val date = jobs.createdAt?.toDate()
                val sdf = SimpleDateFormat("d MMM, yyyy", Locale.getDefault())
                val formattedDate = sdf.format(date)

                binding.roleJob.text = jobs.roleJob
                binding.companyName.text = jobs.companyName
                binding.category.text = jobs.category
                binding.city.text = jobs.city
                binding.salary.text = jobs.salary
                binding.description.text = jobs.description
                binding.createdAt.text = formattedDate
                binding.containerIsDisability.visibility = if (jobs.isDisability == true) View.VISIBLE else View.GONE
                binding.containerIsBad.visibility = if (jobs.isBad == true) View.VISIBLE else View.GONE

                // Update Map Location
                val latitude = jobs.latitude.toDoubleOrNull()
                val longitude = jobs.longitude.toDoubleOrNull()
                if (latitude != null && longitude != null) {
                    val locationCompany = LatLng(latitude, longitude)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(locationCompany)
                            .title(jobs.companyName)
                    )
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationCompany, 15f))
                }

                Glide.with(this@DetailJobsActivity)
                    .load(jobs.companyImg)
                    .placeholder(R.drawable.img_placeholder)
                    .into(binding.companyImg)

            }
            .addOnFailureListener { exception -> }
    }

    private fun setNavigationBarColor() {
        val color = ContextCompat.getColor(this, R.color.backgroundSecondary)
        window.navigationBarColor = color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
    }

    private fun setStatusBarWhite() {
        window.statusBarColor = ContextCompat.getColor(this, R.color.backgroundSecondary)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }

    private fun setStatusBarTransparent() {
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.decorView.windowInsetsController?.setSystemBarsAppearance(
                0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
    }
}