package com.hosnimal.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hosnimal.model.User

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(user: User)

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)

    @Query("SELECT * from user ORDER BY id ASC")
    fun getAllUsers(): LiveData<List<User>>

    @Query("SELECT DISTINCT * from user WHERE email = :email")
    suspend fun getUserByEmail(email: String): User

    @Query("SELECT EXISTS (SELECT 1 from user WHERE email = :email)")
    suspend fun isUserRegistered(email: String): Boolean
}