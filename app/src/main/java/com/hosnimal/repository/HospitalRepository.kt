package com.hosnimal.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.hosnimal.database.HosnimalDatabase
import com.hosnimal.database.dao.HospitalDao
import com.hosnimal.model.Hospital
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class HospitalRepository (application: Application) {
    private val mHospitalDao: HospitalDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = HosnimalDatabase.getDatabase(application)
        mHospitalDao = db.hospitalDao()
    }

    fun getAllHospital(): LiveData<List<Hospital>> = mHospitalDao.getAllHospitals()

    fun getTopHospital(qty: Int): LiveData<List<Hospital>> = mHospitalDao.getTopHospitals(qty)

    fun insert(vararg hospital: Hospital) {
        executorService.execute { mHospitalDao.insert(*hospital) }
    }
}