package com.hosnimal.ui.detail_hospital

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hosnimal.helper.Event
import com.hosnimal.model.Hospital
import com.hosnimal.model.Reservation
import com.hosnimal.model.User
import com.hosnimal.model.relational.HospitalReservation
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.repository.HospitalRepository
import com.hosnimal.repository.ReservationRepository
import com.hosnimal.repository.UserRepository
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.format.DateTimeFormatter
import java.util.*

class DetailHospitalViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mHospitalRepository: HospitalRepository = HospitalRepository(application)
    private val mUserRepository: UserRepository = UserRepository(application)
    private val mReservationRepository: ReservationRepository = ReservationRepository(application)
    private val _notificationText = MutableLiveData<Event<String>>()
    val notificationText: LiveData<Event<String>> = _notificationText

    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

    fun getUserIdByEmail(email: String): Int = mUserRepository.getUserByEmail(email).id

    fun isRegistered(email: String): Boolean = mUserRepository.isUserRegistered(email)

    fun isReservationFilled(hospitalId: Int, date: OffsetDateTime, time: OffsetTime): Boolean = mReservationRepository.isReservationFilled(hospitalId, time.atDate(date.toLocalDate()), time.atDate(date.toLocalDate()).plusHours(1L))

    fun getHospital(id: Int): LiveData<Hospital> = mHospitalRepository.getHospital(id)

    fun getHospitalReservation(hospitalId: Int): LiveData<List<HospitalReservation>> = mReservationRepository.getHospitalReservations(hospitalId)

    fun placeReservation(emailUser: String, hospital: Hospital, date: OffsetDateTime, time: OffsetTime){
        // Get Data User
        val user = mUserRepository.getUserByEmail(emailUser)

        // Calculate Date and Time Reservation
        val startReservation = time.atDate(date.toLocalDate())

        // Create Data Reservation
        val data = Reservation(
            userId = user.id,
            hospitalId = hospital.id,
            start = startReservation,
            end = startReservation.plusHours(1L)
        )

        // Send Data Reservation to Database
        mReservationRepository.insert(data)

        // Send notification
        _notificationText.value = Event("Berhasil reservasi pada ${startReservation.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm", Locale("in", "ID")))} sampai pukul ${time.plusHours(1L).format(DateTimeFormatter.ofPattern("HH:mm", Locale("in", "ID")))}")
    }

    fun cancelReservation(reservation: HospitalReservation) {
        // Save data of reservation
        val start = reservation.detailReservation.start
        val end = reservation.detailReservation.end

        // Delete Reservation on Database
        mReservationRepository.delete(reservation.detailReservation)

        // Send notification
        _notificationText.value = Event("Berhasil membatalkan reservasi pada ${start.format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy HH:mm", Locale("in", "ID")))} sampai pukul ${end.format(DateTimeFormatter.ofPattern("HH:mm", Locale("in", "ID")))}")
    }
}