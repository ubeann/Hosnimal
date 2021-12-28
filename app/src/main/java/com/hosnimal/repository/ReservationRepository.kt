package com.hosnimal.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.hosnimal.database.HosnimalDatabase
import com.hosnimal.database.dao.ReservationDao
import com.hosnimal.model.Reservation
import com.hosnimal.model.relational.HospitalReservation
import com.hosnimal.model.relational.UserReservation
import kotlinx.coroutines.runBlocking
import java.time.OffsetDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ReservationRepository(application: Application) {
    private val mReservationDao: ReservationDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = HosnimalDatabase.getDatabase(application)
        mReservationDao = db.reservationDao()
    }

    fun isReservationFilled(hospitalId: Int, reservationStart: OffsetDateTime, reservationEnd: OffsetDateTime): Boolean = runBlocking { mReservationDao.checkReservation(hospitalId, reservationStart, reservationEnd) }

    fun getUserReservations(userId: Int): LiveData<List<UserReservation>> = mReservationDao.getUserReservations(userId)

    fun getHospitalReservations(hospitalId: Int): LiveData<List<HospitalReservation>> = mReservationDao.getHospitalReservations(hospitalId)

    fun insert(vararg reservation: Reservation) {
        executorService.execute { mReservationDao.insert(*reservation) }
    }

    fun delete(vararg reservation: Reservation) {
        executorService.execute { mReservationDao.delete(*reservation) }
    }
}