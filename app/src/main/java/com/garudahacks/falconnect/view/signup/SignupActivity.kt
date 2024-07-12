package com.garudahacks.falconnect.view.signup

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.databinding.ActivitySignupBinding
import com.garudahacks.falconnect.view.BaseActivity
import com.garudahacks.falconnect.view.login.LoginActivity
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : BaseActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavigationBarColor()

        ViewCompat.setOnApplyWindowInsetsListener(binding.signup) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnSignup.setOnClickListener {
            if (isRequired()) {
                checkIdentification()
            }
        }

        binding.btnLogin.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        binding.etIdentification.addTextChangedListener(textWatcher)
        updateSignupButtonState()
    }

    private fun progress(progress: Boolean) {
        binding.alertSignup.visibility = View.GONE
        when (progress) {
            true -> {
                binding.btnSignup.text = getString(R.string.loading)
                val disabledColor = resources.getColor(R.color.primary200, null)
                binding.btnSignup.backgroundTintList = ColorStateList.valueOf(disabledColor)
                binding.btnSignup.isEnabled = false
            }
            false -> {
                binding.btnSignup.text = getString(R.string.next)
                val enabledColor = resources.getColor(R.color.primary400, null)
                binding.btnSignup.backgroundTintList = ColorStateList.valueOf(enabledColor)
                binding.btnSignup.isEnabled = true
            }
        }
    }

    private fun isRequired(): Boolean {
        return (binding.etIdentification.text.toString().isNotEmpty())
    }

    private fun checkIdentification() {
        progress(true)
        val identification = binding.etIdentification.text.toString()
        db.collection("user")
            .whereEqualTo("identification", identification)
            .get()
            .addOnSuccessListener { result ->
                progress(false)
                if (result.isEmpty) {
                    checkPoliceData(identification)
                } else {
                    binding.alertSignup.visibility = View.VISIBLE
                }
            }
    }

    private fun checkPoliceData(identification: String) {
        progress(true)
        db.collection("police_data")
            .whereEqualTo("identification", identification)
            .get()
            .addOnSuccessListener { result ->
                progress(false)
                val intent = Intent(this, AccountSetupActivity::class.java)
                intent.putExtra("identification", identification)
                if (result.isEmpty) {
                    intent.putExtra("police_data", false)
                }
                else {
                    intent.putExtra("police_data", true)
                }
                startActivity(intent)
            }
            .addOnFailureListener {
                progress(false)
                Toast.makeText(this, "Failed to check police data", Toast.LENGTH_SHORT).show()
            }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateSignupButtonState()
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun updateSignupButtonState() {
        val isEnabled = isRequired()
        binding.btnSignup.isEnabled = isEnabled
        val backgroundColor = if (isEnabled) {
            resources.getColor(R.color.primary400, null)
        } else {
            resources.getColor(R.color.primary200, null)
        }
        binding.btnSignup.backgroundTintList = ColorStateList.valueOf(backgroundColor)
    }

    private fun setNavigationBarColor() {
        val navBarColor = ContextCompat.getColor(this, R.color.backgroundSecondary)
        val statusBarColor = ContextCompat.getColor(this, R.color.backgroundPrimary)
        window.navigationBarColor = navBarColor
        window.statusBarColor = statusBarColor
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
}
