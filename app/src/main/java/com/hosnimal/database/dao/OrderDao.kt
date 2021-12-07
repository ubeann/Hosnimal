package com.hosnimal.database.dao

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
    suspend fun getUserOrders(userId: Int): List<UserOrder>
}