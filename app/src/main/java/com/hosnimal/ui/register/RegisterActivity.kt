package com.hosnimal.ui.register

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.hosnimal.LoginActivity
import com.hosnimal.R
import com.hosnimal.databinding.ActivityRegisterBinding
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    // Binding
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting DatePicker Dialog
        binding.btnDatePicker.setOnClickListener {
            val picker = MaterialDatePicker.Builder.datePicker()
                    .setTitleText(getString(R.string.input_birthday_date_picker))
                    .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                    .build()
            picker.show(supportFragmentManager, DATE_PICKER_TAG)
            picker.addOnPositiveButtonClickListener {
                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                binding.birthday.editText?.setText(dateFormat.format(it))
            }
        }

        // Setting Button Login Alternative
        binding.btnAlternativeLogin.setOnClickListener {
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
        }

        // Setting Button Register
        binding.btnRegister.setOnClickListener {
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
            val isNameFilled = isInputFilled(binding.fullName, getString(R.string.error_name))
            val isEmailFilled = isInputFilled(binding.email, getString(R.string.error_email))
            val isEmailValid = if (isEmailFilled) isEmailValid(binding.email, getString(R.string.error_email_valid)) else false
            val isPhoneFilled = isInputFilled(binding.phone, getString(R.string.error_phone))
            val isBirthDayFilled = isInputFilled(binding.birthday, getString(R.string.error_birthday))
            val isPasswordFilled = isInputFilled(binding.password, getString(R.string.error_password))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun isInputFilled(view: TextInputLayout, error: String) : Boolean {
        view.editText?.clearFocus()
        return if (TextUtils.isEmpty(view.editText?.text.toString().trim())) {
            view.error = error
            false
        } else {
            view.isErrorEnabled = false
            true
        }
    }

    private fun isEmailValid(view: TextInputLayout, error: String) : Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        view.editText?.clearFocus()
        return if (view.editText?.text.toString().trim().matches(emailPattern.toRegex())) {
            view.isErrorEnabled = false
            true
        } else {
            view.error = error
            false
        }
    }

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
    }
}