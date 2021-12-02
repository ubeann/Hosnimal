package com.hosnimal.ui.on_boarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hosnimal.preferences.OnBoardingPreferences

class OnBoardingViewModelFactory (private val pref: OnBoardingPreferences) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnBoardingViewModel::class.java)) {
            return OnBoardingViewModel(pref) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}