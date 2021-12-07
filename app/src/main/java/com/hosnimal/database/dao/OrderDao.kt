package com.hosnimal.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.hosnimal.model.Order
import com.hosnimal.model.relational.UserOrder

@Dao
interface OrderDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(vararg order: Order)

    @Delete
    fun delete(vararg order: Order)

    @Transaction
    @Query("SELECT * from `order` WHERE user_id = :userId ORDER BY id ASC")
    fun getUserOrders(userId: Int): LiveData<List<UserOrder>>
}