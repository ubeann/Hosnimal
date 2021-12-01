package com.hosnimal.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hosnimal.ui.on_boarding.OnBoardingPreferences

class SplashViewModel  (private val pref: OnBoardingPreferences) : ViewModel() {
    fun getOnBoardingSetting(): LiveData<Boolean> = pref.getOnBoardingSetting().asLiveData()
}