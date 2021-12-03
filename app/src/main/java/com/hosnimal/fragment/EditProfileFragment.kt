package com.hosnimal.fragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.hosnimal.App
import com.hosnimal.R
import com.hosnimal.databinding.FragmentEditProfileBinding
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.ui.main.MainViewModel
import com.hosnimal.ui.main.MainViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class EditProfileFragment : Fragment() {
    // Binding
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!

    // DataStore
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = App.DATA_STORE_KEY)

    // Preferences
    private lateinit var preferences: UserPreferences

    // ViewModel
    private lateinit var viewModel: MainViewModel

    // User Data
    private lateinit var emailUser: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Setting Preferences
        preferences = UserPreferences.getInstance(requireActivity().dataStore)

        // Set ViewModel
        viewModel = ViewModelProvider(this, MainViewModelFactory(requireActivity().application, preferences))[MainViewModel::class.java]

        // Inflate the layout for this fragment
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe User Data
        viewModel.getUserSetting().observe(viewLifecycleOwner, { user ->
            with(binding) {
                fullName.editText?.setText(user.name)
                email.editText?.setText(user.email)
                emailUser = user.email.toString()
                phone.editText?.setText(user.phone)
                birthday.editText?.setText(user.birthday)
            }
        })

        // Observe Notification
        viewModel.notificationText.observe(viewLifecycleOwner, {
            it.getContentIfNotHandled()?.let { text ->
                Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
            }
        })

        // Setting DatePicker Dialog
        binding.btnDatePicker.setOnClickListener {
            val constraintsBuilder = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
            val picker = MaterialDatePicker.Builder.datePicker()
                .setTitleText(getString(R.string.input_birthday_date_picker))
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build())
                .build()
            picker.show(requireActivity().supportFragmentManager, DATE_PICKER_TAG)
            picker.addOnPositiveButtonClickListener {
                val dateFormat = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                binding.birthday.editText?.setText(dateFormat.format(it))
            }
        }

        // Setting Button Save Bio
        binding.btnBioSave.setOnClickListener {
            // Close Keyboard
            closeKeyboard()

            // Check Input
            val isNameFilled = isInputFilled(binding.fullName, getString(R.string.error_name))
            val isEmailFilled = isInputFilled(binding.email, getString(R.string.error_email))
            val isEmailValid = if (isEmailFilled) isEmailValid(binding.email, getString(R.string.error_email_valid)) else false
            val isNotEmailUser = if (isEmailValid) isNotEmailUser(binding.email.editText?.text.toString()) else false
            val isUserRegistered = if (isEmailValid and isNotEmailUser) isEmailRegistered(binding.email, getString(R.string.error_email_registered)) else false
            val isPhoneFilled = isInputFilled(binding.phone, getString(R.string.error_phone))
            val isBirthDayFilled = isInputFilled(binding.birthday, getString(R.string.error_birthday))


            // Checking
            if (isNameFilled and isEmailFilled and isEmailValid and isPhoneFilled and isBirthDayFilled and !isUserRegistered) {
                // Update user data
                with(binding) {
                    viewModel.updateUser(
                        emailOld = emailUser,
                        userName = fullName.editText?.text.toString(),
                        userEmail = email.editText?.text.toString(),
                        userPhone = phone.editText?.text.toString(),
                        userBirthDay = birthday.editText?.text.toString()
                    )
                }
            }
        }

        // Setting Button Save Password
        binding.btnPasswordSave.setOnClickListener {
            // Close Keyboard
            closeKeyboard()

            // Check Input
            val isOldPasswordFilled = isInputFilled(binding.passwordOld, getString(R.string.error_password_old))
            val isOldPasswordMatch = if (isOldPasswordFilled) isOldPasswordMatch(binding.passwordOld, getString(R.string.error_password_old_not_match)) else false
            val isNewPasswordFilled = isInputFilled(binding.passwordNew, getString(R.string.error_password_new))

            // Checking
            if  (isOldPasswordFilled and isOldPasswordMatch and isNewPasswordFilled) {
                viewModel.updateUserPassword(emailUser, binding.passwordNew.editText?.text.toString())
            }
        }

        // Setting Button Back
        binding.btnBack.setOnClickListener {
            closeKeyboard()
            requireActivity().onBackPressed()
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

    private fun isOldPasswordMatch(view: TextInputLayout, error: String) : Boolean {
        return if (viewModel.isUserPasswordMatch(emailUser, view.editText?.text.toString())) {
            view.isErrorEnabled = false
            true
        } else {
            view.isErrorEnabled = true
            view.error = error
            false
        }

    }

    private fun isNotEmailUser(email: String) : Boolean = emailUser != email

    private fun closeKeyboard() {
        val inputMethodManager = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }

    companion object {
        private const val DATE_PICKER_TAG = "DatePicker"
    }
}