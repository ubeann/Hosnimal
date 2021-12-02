package com.hosnimal.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.hosnimal.model.User
import com.hosnimal.preferences.UserPreferences
import kotlinx.coroutines.launch

class MainViewModel(private val preferences: UserPreferences) : ViewModel() {
    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

    fun forgetUserLogin(isForget: Boolean) {
        viewModelScope.launch {
            preferences.forgetUserLogin(!isForget)
        }
    }
}