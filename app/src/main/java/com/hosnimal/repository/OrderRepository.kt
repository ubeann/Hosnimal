package com.hosnimal.repository

import android.app.Application
import com.hosnimal.database.HosnimalDatabase
import com.hosnimal.database.dao.OrderDao
import com.hosnimal.model.Order
import com.hosnimal.model.relational.UserOrder
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class OrderRepository(application: Application) {
    private val mOrderDao: OrderDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = HosnimalDatabase.getDatabase(application)
        mOrderDao = db.orderDao()
    }

    fun getUserOrders(userId: Int): List<UserOrder> = runBlocking { mOrderDao.getUserOrders(userId) }

    fun insert(vararg order: Order) {
        executorService.execute { mOrderDao.insert(*order) }
    }

    fun delete(vararg order: Order) {
        executorService.execute { mOrderDao.delete(*order) }
    }
}