package com.hosnimal.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.hosnimal.database.HosnimalDatabase
import com.hosnimal.database.dao.ProductDao
import com.hosnimal.model.Product
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ProductRepository (application: Application) {
    private val mProductDao: ProductDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = HosnimalDatabase.getDatabase(application)
        mProductDao = db.productDao()
    }

    fun getAllProducts(): LiveData<List<Product>> = mProductDao.getAllProducts()

    fun getTopProducts(qty: Int): LiveData<List<Product>> = mProductDao.getTopProducts(qty)

    fun getProduct(id: Int): LiveData<Product> = mProductDao.getProduct(id)

    fun insert(vararg product: Product) {
        executorService.execute { mProductDao.insert(*product) }
    }

    fun updateStock(product: Product, qty: Int, isAddStock: Boolean) {
        if (isAddStock) {
            product.stock += qty
        } else {
            product.stock -= qty
        }
        executorService.execute { mProductDao.update(product) }
    }
}