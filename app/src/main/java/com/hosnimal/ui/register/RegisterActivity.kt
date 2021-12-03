package com.hosnimal.ui.register

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.hosnimal.App
import com.hosnimal.LoginActivity
import com.hosnimal.ui.main.MainActivity
import com.hosnimal.R
import com.hosnimal.databinding.ActivityRegisterBinding
import com.hosnimal.preferences.UserPreferences
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    // Binding
    private var _binding: ActivityRegisterBinding? = null
    private val binding get() = _binding!!

    // DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)

    // Preferences
    private lateinit var preferences: UserPreferences

    // ViewModel
    private lateinit var viewModel: RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting Preferences
        preferences = UserPreferences.getInstance(dataStore)

        // Setting ViewModel
        viewModel = ViewModelProvider(this, RegisterViewModelFactory(application, preferences))[RegisterViewModel::class.java]

        // Setting DatePicker Dialog
        binding.btnDatePicker.setOnClickListener {
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.input_birthday_date_picker))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
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
            // Close Keyboard
            val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)

            // Check Input
            val isNameFilled = isInputFilled(binding.fullName, getString(R.string.error_name))
            val isEmailFilled = isInputFilled(binding.email, getString(R.string.error_email))
            val isEmailValid = if (isEmailFilled) isEmailValid(binding.email, getString(R.string.error_email_valid)) else false
            val isUserRegistered = if (isEmailValid) isEmailRegistered(binding.email, getString(R.string.error_email_registered)) else false
            val isPhoneFilled = isInputFilled(binding.phone, getString(R.string.error_phone))
            val isBirthDayFilled = isInputFilled(binding.birthday, getString(R.string.error_birthday))
            val isPasswordFilled = isInputFilled(binding.password, getString(R.string.error_password))

            // Checking
            if (isNameFilled and isEmailFilled and isEmailValid and isPhoneFilled and isBirthDayFilled and isPasswordFilled and !isUserRegistered) {
                // Create dateFormat
                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())

                // Save data to database
                viewModel.register(
                    userName = binding.fullName.editText?.text.toString(),
                    userEmail = binding.email.editText?.text.toString().lowercase(),
                    userPhone = binding.phone.editText?.text.toString(),
                    userBirthDay = binding.birthday.editText?.text.toString(),
                    userPassword = binding.password.editText?.text.toString(),
                    userCreatedAt = dateFormat.format(Date())
                )

                // Intent to MainActivity
                val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    private fun isInputFilled(view: TextInputLayout, error: String) : Boolean {
        view.editText?.clearFocus()
        return if (TextUtils.isEmpty(view.editText?.text.toString().trim())) {
            view.isErrorEnabled = true
            view.error = error
            false
        } else {
            view.isErrorEnabled = false
            true
        }
    }

    private fun isEmailValid(view: TextInputLayout, error: String) : Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        return if (view.editText?.text.toString().lowercase().trim().matches(emailPattern.toRegex())) {
            view.isErrorEnabled = false
            true
        } else {
            view.isErrorEnabled = true
            view.error = error
            false
        }
    }

    private fun isEmailRegistered(view: TextInputLayout, error: String) : Boolean {
        return if (viewModel.isRegistered(view.editText?.text.toString().lowercase().trim())) {
            view.isErrorEnabled = true
            view.error = error
            true
        } else {
            view.isErrorEnabled = false
            false
        }
    }

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
    }
}