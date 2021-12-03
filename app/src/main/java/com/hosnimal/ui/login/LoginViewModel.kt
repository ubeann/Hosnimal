package com.hosnimal.ui.login

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hosnimal.helper.AESEncryption
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)

    fun isRegistered(email: String): Boolean = mUserRepository.isUserRegistered(email)

    fun isUserPasswordMatch(emailUser: String, oldPassword: String): Boolean {
        // Get User Data
        val user = mUserRepository.getUserByEmail(emailUser)

        // Return
        return AESEncryption.decrypt(user.password.toString()) == oldPassword
    }

    fun login(email: String) {
        // Get User Data
        val user = mUserRepository.getUserByEmail(email)

        // Save User Data to Setting
        viewModelScope.launch {
            preferences.saveUserSetting(
                userName = user.name.toString(),
                userEmail = user.email.toString(),
                userPhone = user.phone.toString(),
                userBirthDay = user.birthday.toString(),
                userCreatedAt = user.createdAt.toString()
            )
        }
    }
}