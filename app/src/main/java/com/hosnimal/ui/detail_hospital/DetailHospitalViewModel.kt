package com.hosnimal.ui.detail_hospital

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hosnimal.helper.Event
import com.hosnimal.model.Hospital
import com.hosnimal.model.User
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.repository.HospitalRepository
import com.hosnimal.repository.UserRepository

class DetailHospitalViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mHospitalRepository: HospitalRepository = HospitalRepository(application)
    private val mUserRepository: UserRepository = UserRepository(application)
    private val _notificationText = MutableLiveData<Event<String>>()
    val notificationText: LiveData<Event<String>> = _notificationText

    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

    fun isRegistered(email: String): Boolean = mUserRepository.isUserRegistered(email)

    fun getHospital(id: Int): LiveData<Hospital> = mHospitalRepository.getHospital(id)
}