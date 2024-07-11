package com.garudahacks.falconnect.view.signup

import android.app.DatePickerDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.view.WindowInsetsController
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.garudahacks.falconnect.R
import com.garudahacks.falconnect.databinding.ActivityAccountSetupBinding
import com.garudahacks.falconnect.model.User
import com.garudahacks.falconnect.view.BaseActivity
import com.garudahacks.falconnect.view.login.LoginActivity
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AccountSetupActivity : BaseActivity() {

    private lateinit var binding: ActivityAccountSetupBinding
    private val db by lazy { FirebaseFirestore.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAccountSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setNavigationBarColor()

        val identification = intent.getStringExtra("identification").toString()
        setStyledSubheading(identification)

        ViewCompat.setOnApplyWindowInsetsListener(binding.accountSetup) { v, insets ->
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

        binding.btnShowPasswordConfirmation.setOnClickListener {
            togglePasswordVisibility(binding.etPasswordConfirmation, it as ImageButton)
        }

        binding.btnSignup.setOnClickListener {
            if (isRequired()) addUser()
        }

        binding.etFullname.addTextChangedListener(textWatcher)
        binding.etDate.addTextChangedListener(textWatcher)
        binding.etPhone.addTextChangedListener(textWatcher)
        binding.etPassword.addTextChangedListener(textWatcher)
        binding.etPasswordConfirmation.addTextChangedListener(textWatcher)
        updateRegisterButtonState()

        // Tambahkan click listener untuk DatePicker
        binding.etDate.setOnClickListener {
            showDatePickerDialog(binding.etDate)
        }
    }

    private fun showDatePickerDialog(etDate: EditText) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                etDate.setText(dateFormat.format(calendar.time))
            },
            year, month, day
        )

        datePickerDialog.show()
    }

    private fun progress(progress: Boolean) {
        binding.alertPassword.visibility = View.GONE
        when (progress) {
            true -> {
                binding.btnSignup.text = getString(R.string.loading)
                val disabledColor = resources.getColor(R.color.primary200, null)
                binding.btnSignup.backgroundTintList = ColorStateList.valueOf(disabledColor)
                binding.btnSignup.isEnabled = false
            }
            false -> {
                binding.btnSignup.text = getString(R.string.sign_up)
                val enabledColor = resources.getColor(R.color.primary400, null)
                binding.btnSignup.backgroundTintList = ColorStateList.valueOf(enabledColor)
                binding.btnSignup.isEnabled = true
            }
        }
    }

    private fun isRequired(): Boolean {
        return (
                binding.etFullname.text.toString().isNotEmpty() &&
                        binding.etDate.text.toString().isNotEmpty() &&
                        binding.etPhone.text.toString().isNotEmpty() &&
                        binding.etPassword.text.toString().isNotEmpty() &&
                        binding.etPasswordConfirmation.text.toString().isNotEmpty()
                )
    }

    private fun validatePassword(): Boolean {
        val password = binding.etPassword.text.toString()
        val confirmPassword = binding.etPasswordConfirmation.text.toString()

        if (password != confirmPassword) {
            binding.alertPassword.visibility = View.VISIBLE
            return false
        } else {
            binding.alertPassword.visibility = View.GONE
            return true
        }
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

    private fun addUser() {
        if (!validatePassword()) {
            return
        }
        progress(true)
        val identification = intent.getStringExtra("identification").toString()
        val fullname = binding.etFullname.text.toString()
        val dateString = binding.etDate.text.toString()
        val phone = binding.etPhone.text.toString()
        val email = binding.etEmail.text.toString()
        val password = binding.etPassword.text.toString()

        val date: Timestamp = try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val parsedDate = dateFormat.parse(dateString)
            Timestamp(parsedDate!!)
        } catch (e: Exception) {
            Timestamp.now()
        }

        val user = User(
            identification = identification,
            fullname = fullname,
            date = date,
            phone = phone,
            email = email,
            password = password,
            created = Timestamp.now()
        )

        db.collection("user")
            .add(user)
            .addOnSuccessListener {
                progress(false)
                Toast.makeText(applicationContext,
                    getString(R.string.alert_sign_up_success), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
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
        binding.btnSignup.isEnabled = isEnabled
        val backgroundColor = if (isEnabled) {
            resources.getColor(R.color.primary500, null)
        } else {
            resources.getColor(R.color.primary200, null)
        }
        binding.btnSignup.backgroundTintList = ColorStateList.valueOf(backgroundColor)
    }

    private fun setStyledSubheading(identification: String) {
        val fullText = "One step away! You will be logged in with $identification"
        val spannableString = SpannableString(fullText)
        val start = fullText.indexOf(identification)
        val end = start + identification.length

        val color = ContextCompat.getColor(this, R.color.primary500)
        spannableString.setSpan(ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.signupSubheading.text = spannableString
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
