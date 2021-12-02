package com.hosnimal.ui.register

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hosnimal.helper.AESEncryption
import com.hosnimal.model.User
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)

    fun register(userName: String, userEmail: String, userPhone: String, userBirthDay: String, userPassword: String, userCreatedAt: String) {
        // Save to setting
        viewModelScope.launch {
            preferences.saveUserSetting(userName, userEmail, userPhone, userBirthDay, userCreatedAt)
        }

        // Insert to database
        val user = User(
            name = userName,
            email = userEmail,
            phone = userPhone,
            birthday = userBirthDay,
            password = AESEncryption.encrypt(userPassword),
            createdAt = userCreatedAt
        )
        mUserRepository.insert(user)
    }

    fun isRegistered(email: String): Boolean = mUserRepository.isUserRegistered(email)
}