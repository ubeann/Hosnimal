package com.hosnimal.ui.on_boarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class OnBoardingViewModel (private val pref: OnBoardingPreferences) : ViewModel() {
    fun saveOnBoardingSetting(isOnBoardingCleared: Boolean) {
        viewModelScope.launch {
            pref.saveOnBoardingSetting(isOnBoardingCleared)
        }
    }
}