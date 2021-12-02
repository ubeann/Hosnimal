package com.hosnimal.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hosnimal.preferences.OnBoardingPreferences
import com.hosnimal.preferences.UserPreferences

class SplashViewModel  (private val onBoarding: OnBoardingPreferences, private val user: UserPreferences) : ViewModel() {
    fun getOnBoardingSetting(): LiveData<Boolean> = onBoarding.getOnBoardingSetting().asLiveData()

    fun getUserLogin(): LiveData<Boolean> = user.getUserIsLogin().asLiveData()
}