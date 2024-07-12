package com.garudahacks.falconnect.view.news

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.databinding.ActivityDetailNewsBinding
import com.garudahacks.falconnect.model.News
import com.garudahacks.falconnect.view.BaseActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Locale

class DetailNewsActivity : BaseActivity() {
    private val binding by lazy { ActivityDetailNewsBinding.inflate(layoutInflater) }
    private val newsId by lazy { intent.getStringExtra("id") }
    private val db by lazy { com.google.firebase.Firebase.firestore }
    private lateinit var news: News

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            insets
        }

        binding.scrollView.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val backgroundOpacity = Color.parseColor("#1A000000")
            if (scrollY > 0) {
                binding.statusBar.setBackgroundColor(resources.getColor(R.color.backgroundSecondary))
                binding.navbar.setBackgroundColor(resources.getColor(R.color.backgroundSecondary))
                binding.btnBack.backgroundTintList = ContextCompat.getColorStateList(this, R.color.backgroundSecondary)
                binding.btnShare.backgroundTintList = ContextCompat.getColorStateList(this, R.color.backgroundSecondary)
                binding.btnBookmark.backgroundTintList = ContextCompat.getColorStateList(this, R.color.backgroundSecondary)
                binding.titleNavbar.visibility = View.VISIBLE
            } else {
                binding.statusBar.setBackgroundColor(resources.getColor(android.R.color.transparent))
                binding.navbar.setBackgroundColor(resources.getColor(android.R.color.transparent))
                binding.btnBack.backgroundTintList = ColorStateList.valueOf(backgroundOpacity)
                binding.btnShare.backgroundTintList = ColorStateList.valueOf(backgroundOpacity)
                binding.btnBookmark.backgroundTintList = ColorStateList.valueOf(backgroundOpacity)
                binding.titleNavbar.visibility = View.GONE
            }
        }

        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        if (newsId != null && newsId!!.isNotEmpty()) {
            detailNews()
        } else {
            finish()
        }
    }

    private fun detailNews() {
        db.collection("news")
            .document(newsId!!)
            .get()
            .addOnSuccessListener { result ->
                news = News(
                    id = result.id,
                    title = result["title"].toString(),
                    photoUrl = result["photoUrl"].toString(),
                    sourceLogo = result["sourceLogo"].toString(),
                    source = result["source"].toString(),
                    author = result["author"].toString(),
                    description = result["description"].toString(),
                    createdAt = result["createdAt"] as Timestamp
                )

                val date = news.createdAt?.toDate()
                val sdf = SimpleDateFormat("d MMM, yyyy", Locale.getDefault())
                val formattedDate = sdf.format(date)

                binding.title.text = news.title
                binding.source.text = news.source
                binding.author.text = news.author
                binding.description.text = news.description
                binding.timestamp.text = formattedDate

                Glide.with(this@DetailNewsActivity)
                    .load(news.photoUrl)
                    .placeholder(R.drawable.img_placeholder)
                    .into(binding.photoUrl)

                Glide.with(this@DetailNewsActivity)
                    .load(news.sourceLogo)
                    .placeholder(R.drawable.img_placeholder)
                    .into(binding.sourceLogo)
            }
            .addOnFailureListener { exception -> }
    }
}
