package com.hosnimal.ui.splash

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hosnimal.model.User
import com.hosnimal.preferences.OnBoardingPreferences
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.repository.UserRepository

class SplashViewModel  (application: Application, private val onBoarding: OnBoardingPreferences, private val user: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)

    fun getOnBoardingSetting(): LiveData<Boolean> = onBoarding.getOnBoardingSetting().asLiveData()

    fun getUserLogin(): LiveData<Boolean> = user.getUserIsLogin().asLiveData()

    fun getUserSetting(): LiveData<User> = user.getUserSetting().asLiveData()

    fun isRegistered(email: String): Boolean = mUserRepository.isUserRegistered(email)
}