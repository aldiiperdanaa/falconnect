package com.garudahacks.falconnect.view.register

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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.databinding.ActivityRegisterBinding
import com.garudahacks.falconnect.view.BaseActivity
import com.garudahacks.falconnect.view.login.LoginActivity
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : BaseActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavigationBarColor()

        ViewCompat.setOnApplyWindowInsetsListener(binding.register) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnRegister.setOnClickListener {
            if (isRequired()) checkIdentification()
        }

        binding.btnLogin.setOnClickListener {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }

        binding.etIdentification.addTextChangedListener(textWatcher)
        updateRegisterButtonState()
    }

    private fun progress(progress: Boolean) {
        binding.alertRegister.visibility = View.GONE
        when (progress) {
            true -> {
                binding.btnRegister.text = getString(R.string.loading)
                val disabledColor = resources.getColor(R.color.primary200, null)
                binding.btnRegister.backgroundTintList = ColorStateList.valueOf(disabledColor)
                binding.btnRegister.isEnabled = false
            }
            false -> {
                binding.btnRegister.text = getString(R.string.next)
                val enabledColor = resources.getColor(R.color.primary400, null)
                binding.btnRegister.backgroundTintList = ColorStateList.valueOf(enabledColor)
                binding.btnRegister.isEnabled = true
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
            .whereEqualTo("identification", binding.etIdentification.text.toString())
            .get()
            .addOnSuccessListener { result ->
                progress(false)
                if (result.isEmpty) {
                    val intent = Intent(this, AccountSetupActivity::class.java)
                    intent.putExtra("identification", identification)
                    startActivity(intent)
                }
                else binding.alertRegister.visibility = View.VISIBLE
            }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateRegisterButtonState()
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun updateRegisterButtonState() {
        val isEnabled = isRequired()
        binding.btnRegister.isEnabled = isEnabled
        val backgroundColor = if (isEnabled) {
            resources.getColor(R.color.primary400, null)
        } else {
            resources.getColor(R.color.primary200, null)
        }
        binding.btnRegister.backgroundTintList = ColorStateList.valueOf(backgroundColor)
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
