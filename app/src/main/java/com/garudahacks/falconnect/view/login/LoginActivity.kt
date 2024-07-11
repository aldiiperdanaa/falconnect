package com.garudahacks.falconnect.view.login

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.view.WindowInsetsController
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.garudahacks.falconnect.databinding.ActivityLoginBinding
import com.garudahacks.falconnect.local.preference.PreferenceManager
import com.garudahacks.falconnect.view.BaseActivity
import com.garudahacks.falconnect.view.signup.SignupActivity
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.model.User
import com.garudahacks.falconnect.util.timestampToString
import com.garudahacks.falconnect.view.main.MainActivity

class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val db by lazy { Firebase.firestore }
    private val pref by lazy { PreferenceManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavigationBarColor()

        ViewCompat.setOnApplyWindowInsetsListener(binding.login) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.btnShowPassword.setOnClickListener {
            togglePasswordVisibility(binding.etPassword, it as ImageButton)
        }

        binding.btnLogin.setOnClickListener {
            if (isRequired()) login()
        }

        binding.btnSignup.setOnClickListener {
            val loginIntent = Intent(this, SignupActivity::class.java)
            startActivity(loginIntent)
        }

        binding.etIdentification.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(textWatcher)
        updateLoginButtonState()
    }

    private fun togglePasswordVisibility(editText: EditText, button: ImageButton) {
        val selectionStart = editText.selectionStart
        val selectionEnd = editText.selectionEnd
        if (editText.transformationMethod == PasswordTransformationMethod.getInstance()) {
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
            button.setImageResource(R.drawable.ic_eye)
        } else {
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
            button.setImageResource(R.drawable.ic_eye_slash)
        }
        editText.setSelection(selectionStart, selectionEnd)
    }

    private fun progress(progress: Boolean) {
        binding.alertLogin.visibility = View.GONE
        when (progress) {
            true -> {
                binding.btnLogin.text = getString(R.string.loading)
                val disabledColor = resources.getColor(R.color.primary200, null)
                binding.btnLogin.backgroundTintList = ColorStateList.valueOf(disabledColor)
                binding.btnLogin.isEnabled = false
            }
            false -> {
                binding.btnLogin.text = getString(R.string.log_in)
                val enabledColor = resources.getColor(R.color.primary500, null)
                binding.btnLogin.backgroundTintList = ColorStateList.valueOf(enabledColor)
                binding.btnLogin.isEnabled = true
            }
        }
    }

    private fun isRequired(): Boolean {
        return (
                binding.etIdentification.text.toString().isNotEmpty() &&
                        binding.etPassword.text.toString().isNotEmpty()
                )
    }

    private fun login() {
        progress(true)
        db.collection("user")
            .whereEqualTo("identification", binding.etIdentification.text.toString())
            .whereEqualTo("password", binding.etPassword.text.toString())
            .get()
            .addOnSuccessListener { result ->
                progress(false)
                if (result.isEmpty) binding.alertLogin.visibility = View.VISIBLE
                else {
                    result.forEach{ document ->
                        saveSession(
                            User(
                                identification = document.data["identification"].toString(),
                                fullname = document.data["fullname"].toString(),
                                date = document.data["date"] as Timestamp,
                                phone = document.data["phone"].toString(),
                                email = document.data["email"].toString(),
                                password = document.data["password"].toString(),
                                created = document.data["created"] as Timestamp
                            )
                        )
                    }
                    Toast.makeText(applicationContext,
                        getString(R.string.alert_log_in_success), Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            updateLoginButtonState()
        }
        override fun afterTextChanged(s: Editable?) {}
    }

    private fun updateLoginButtonState() {
        val isEnabled = isRequired()
        binding.btnLogin.isEnabled = isEnabled
        val backgroundColor = if (isEnabled) {
            resources.getColor(R.color.primary400, null)
        } else {
            resources.getColor(R.color.primary200, null)
        }
        binding.btnLogin.backgroundTintList = ColorStateList.valueOf(backgroundColor)
    }

    private fun saveSession(user: User) {
        Log.e("LoginActivity", user.toString())
        pref.put("pref_is_login", 1)
        pref.put("pref_identification", user.identification)
        pref.put("pref_fullname", user.fullname)
        pref.put("pref_date", timestampToString(user.date)!!)
        pref.put("pref_phone", user.phone)
        pref.put("pref_email", user.email)
        pref.put("pref_created", timestampToString(user.created)!!)
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
