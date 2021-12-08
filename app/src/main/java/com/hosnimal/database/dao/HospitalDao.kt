package com.hosnimal.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.hosnimal.model.Hospital

@Dao
interface HospitalDao {
    @Insert
    fun insert(vararg hospital: Hospital)

    @Query("SELECT * from hospital ORDER BY time(close) DESC")
    fun getAllHospitals(): LiveData<List<Hospital>>

    @Query("SELECT * from hospital ORDER BY time(close) DESC LIMIT :qty")
    fun getTopHospitals(qty: Int): LiveData<List<Hospital>>

    @Query("SELECT * from hospital WHERE id = :id")
    fun getHospital(id: Int): LiveData<Hospital>
}