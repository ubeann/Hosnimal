package com.hosnimal.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.hosnimal.helper.AESEncryption
import com.hosnimal.helper.Event
import com.hosnimal.model.User
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)
    private val _notificationText = MutableLiveData<Event<String>>()
    val notificationText: LiveData<Event<String>> = _notificationText

    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

    fun isRegistered(email: String): Boolean = mUserRepository.isUserRegistered(email)

    fun isUserPasswordMatch(emailUser: String, oldPassword: String): Boolean {
        // Get User Data
        val user = mUserRepository.getUserByEmail(emailUser)

        // Return
        return AESEncryption.decrypt(user.password.toString()) == oldPassword
    }

    fun forgetUserLogin(isForget: Boolean) {
        viewModelScope.launch {
            preferences.forgetUserLogin(!isForget)
        }
    }

    fun updateUser(emailOld: String, userName: String, userEmail: String, userPhone: String, userBirthDay: String) {
        // Get User Data
        val user = mUserRepository.getUserByEmail(emailOld)

        // Update User Data
        user.name = userName
        user.email = userEmail
        user.phone = userPhone
        user.birthday = userBirthDay

        // Send data to Database
        mUserRepository.update(user)

        // Send data to Setting
        viewModelScope.launch {
            preferences.saveUserSetting(userName, userEmail, userPhone, userBirthDay, user.createdAt.toString())
        }

        // Send notification
        _notificationText.value = Event("Data user ${user.name} berhasil disimpan pada database")
    }

    fun updateUserPassword(emailUser: String, newPassword: String) {
        // Get User Data
        val user = mUserRepository.getUserByEmail(emailUser)

        // Update User Data
        user.password = AESEncryption.encrypt(newPassword)

        // Send Data to Database
        mUserRepository.update(user)

        // Send notification
        _notificationText.value = Event("Password user ${user.name} berhasil diganti")
    }
}