package com.hosnimal.ui.register

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hosnimal.helper.AESEncryption
import com.hosnimal.helper.Event
import com.hosnimal.model.User
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.repository.UserRepository
import kotlinx.coroutines.launch

class RegisterViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)
    private val _notificationText = MutableLiveData<Event<String>>()
    val notificationText: LiveData<Event<String>> = _notificationText

    fun register(userName: String, userEmail: String, userPhone: String, userBirthDay: String, userPassword: String) {
        // Insert to database
        val user = User(
            name = userName,
            email = userEmail,
            phone = userPhone,
            birthday = userBirthDay,
            password = AESEncryption.encrypt(userPassword)
        )
        mUserRepository.insert(user)
        viewModelScope.launch {
            preferences.saveUserSetting(mUserRepository.getUserByEmail(userEmail))
        }
    }

    fun isRegistered(email: String): Boolean = mUserRepository.isUserRegistered(email)
}