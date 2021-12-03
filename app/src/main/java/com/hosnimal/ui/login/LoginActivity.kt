package com.hosnimal.ui.login

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
import com.google.android.material.textfield.TextInputLayout
import com.hosnimal.App
import com.hosnimal.R
import com.hosnimal.databinding.ActivityLoginBinding
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.ui.main.MainActivity
import com.hosnimal.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    // Binding
    private var _binding: ActivityLoginBinding? = null
    private val binding get() = _binding!!

    // DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)

    // Preferences
    private lateinit var preferences: UserPreferences

    // ViewModel
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setting Preferences
        preferences = UserPreferences.getInstance(dataStore)

        // Setting ViewModel
        viewModel = ViewModelProvider(this, LoginViewModelFactory(application, preferences))[LoginViewModel::class.java]

        // Setting Button Login
        binding.btnLogin.setOnClickListener {
            // Close Keyboard
            closeKeyboard()

            // Check Input
            val isEmailFilled = isInputFilled(binding.email, getString(R.string.error_email))
            val isEmailValid = if (isEmailFilled) isEmailValid(binding.email, getString(R.string.error_email_valid)) else false
            val isUserRegistered = if (isEmailValid) isEmailRegistered(binding.email, getString(R.string.error_email_not_registered)) else false
            val isPasswordFilled = isInputFilled(binding.password, getString(R.string.error_password))
            val isPasswordMatch = if (isPasswordFilled and isUserRegistered) isPasswordMatch(binding.email.editText?.text.toString(), binding.password, getString(R.string.error_password_not_match)) else false


            // Checking
            if (isEmailFilled and isEmailValid and isUserRegistered and isPasswordFilled and isPasswordMatch) {
                // Login to Database
                viewModel.login(email = binding.email.editText?.text.toString())

                // Intent to MainActivity
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                finish()
            }
        }

        // Setting Button Register Alternative
        binding.btnAlternativeRegister.setOnClickListener {
            // Close Keyboard
            closeKeyboard()

            // Intent to RegisterActivity
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            finish()
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
            view.isErrorEnabled = false
            true
        } else {
            view.isErrorEnabled = true
            view.error = error
            false
        }
    }

    private fun isPasswordMatch(email: String, view: TextInputLayout, error: String) : Boolean {
        return if (viewModel.isUserPasswordMatch(email, view.editText?.text.toString())) {
            view.isErrorEnabled = false
            true
        } else {
            view.isErrorEnabled = true
            view.error = error
            false
        }

    }

    private fun closeKeyboard() {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}