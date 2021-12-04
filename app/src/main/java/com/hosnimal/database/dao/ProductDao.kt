package com.hosnimal.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.hosnimal.model.Product

@Dao
interface ProductDao {
    @Insert
    fun insert(vararg product: Product)

    @Update
    fun update(product: Product)

    @Query("SELECT * from product ORDER BY id DESC")
    fun getAllProducts(): LiveData<List<Product>>

    @Query("SELECT * from product ORDER BY id DESC LIMIT :qty")
    fun getTopProducts(qty: Int): LiveData<List<Product>>

    @Query("SELECT * from product WHERE id = :id")
    fun getProduct(id: Int): LiveData<Product>
}