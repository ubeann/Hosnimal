package com.hosnimal.ui.detail_product

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.hosnimal.helper.Event
import com.hosnimal.model.Order
import com.hosnimal.model.Product
import com.hosnimal.model.User
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.repository.OrderRepository
import com.hosnimal.repository.ProductRepository
import com.hosnimal.repository.UserRepository
import java.util.*

class DetailProductViewModel(application: Application, private val preferences: UserPreferences) : ViewModel()  {
    private val mOrderRepository: OrderRepository = OrderRepository(application)
    private val mUserRepository: UserRepository = UserRepository(application)
    private val mProductRepository: ProductRepository = ProductRepository(application)
    private val _notificationText = MutableLiveData<Event<String>>()
    val notificationText: LiveData<Event<String>> = _notificationText

    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

    fun getProduct(id: Int): LiveData<Product> = mProductRepository.getProduct(id)

    fun placeOrder(emailUser: String, product: Product, qtyOrder: Int) {
        // Get Data User
        val user = mUserRepository.getUserByEmail(emailUser)

        // Create Data Order
        val data = Order(
            userId = user.id,
            productId = product.id,
            qty = qtyOrder,
            orderAt = Date()
        )

        // Send Data Order to Database
        mOrderRepository.insert(data)

        // Update Stock of Product
        mProductRepository.updateStock(product, qtyOrder, false)

        // Send notification
        _notificationText.value = Event("Berhasil memesan ${qtyOrder}x ${product.name}")
    }
}