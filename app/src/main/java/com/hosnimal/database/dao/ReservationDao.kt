package com.hosnimal.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hosnimal.model.Reservation
import com.hosnimal.model.relational.HospitalReservation
import com.hosnimal.model.relational.UserReservation
import java.time.OffsetDateTime

@Dao
interface ReservationDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg reservation: Reservation)

    @Delete
    fun delete(vararg reservation: Reservation)

    @Query("SELECT EXISTS (SELECT * from `reservation` WHERE hospital_id = :hospitalId AND ((DATETIME(`start`) <= DATETIME(:reservationStart) AND DATETIME(`end`) >= DATETIME(:reservationStart)) OR (DATETIME(`start`) <= DATETIME(:reservationEnd) AND DATETIME(`end`) >= DATETIME(:reservationEnd))))")
    suspend fun checkReservation(hospitalId: Int, reservationStart: OffsetDateTime, reservationEnd: OffsetDateTime): Boolean

    @Transaction
    @Query("SELECT * from `reservation` WHERE user_id = :userId AND DATETIME(`end`) >= DATETIME('now') ORDER BY DATETIME(start) ASC")
    fun getUserReservations(userId: Int): LiveData<List<UserReservation>>

    @Transaction
    @Query("SELECT * from `reservation` WHERE hospital_id = :hospitalId AND DATETIME(`end`) >= DATETIME('now') ORDER BY DATETIME(start) ASC")
    fun getHospitalReservations(hospitalId: Int): LiveData<List<HospitalReservation>>
}