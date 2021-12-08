package com.hosnimal.ui.main

import android.app.Application
import androidx.lifecycle.*
import com.hosnimal.helper.AESEncryption
import com.hosnimal.helper.Event
import com.hosnimal.model.Hospital
import com.hosnimal.model.Product
import com.hosnimal.model.User
import com.hosnimal.model.relational.UserOrder
import com.hosnimal.preferences.UserPreferences
import com.hosnimal.repository.HospitalRepository
import com.hosnimal.repository.OrderRepository
import com.hosnimal.repository.ProductRepository
import com.hosnimal.repository.UserRepository
import kotlinx.coroutines.launch
import java.time.OffsetDateTime

class MainViewModel(application: Application, private val preferences: UserPreferences) : ViewModel() {
    private val mUserRepository: UserRepository = UserRepository(application)
    private val mProductRepository: ProductRepository = ProductRepository(application)
    private val mOrderRepository: OrderRepository = OrderRepository(application)
    private val mHospitalRepository: HospitalRepository = HospitalRepository(application)
    private val _notificationText = MutableLiveData<Event<String>>()
    val notificationText: LiveData<Event<String>> = _notificationText

    fun getUserSetting(): LiveData<User> = preferences.getUserSetting().asLiveData()

    fun isRegistered(email: String): Boolean = mUserRepository.isUserRegistered(email)

    fun isUserPasswordMatch(emailUser: String, oldPassword: String): Boolean {
        // Get User Data
        val user = mUserRepository.getUserByEmail(emailUser)

        // Return
        return AESEncryption.decrypt(user.password.toString()) == oldPassword
    }

    fun forgetUserLogin(isForget: Boolean) {
        viewModelScope.launch {
            preferences.forgetUserLogin(!isForget)
        }
    }

    fun updateUser(emailOld: String, userName: String, userEmail: String, userPhone: String, userBirthDay: OffsetDateTime) {
        // Get User Data
        val user = mUserRepository.getUserByEmail(emailOld)

        // Update User Data
        user.name = userName
        user.email = userEmail
        user.phone = userPhone
        user.birthday = userBirthDay

        // Send data to Database
        mUserRepository.update(user)

        // Send data to Setting
        viewModelScope.launch {
            preferences.saveUserSetting(userName, userEmail, userPhone, userBirthDay, user.createdAt)
        }

        // Send notification
        _notificationText.value = Event("Data user ${user.name} berhasil disimpan pada database")
    }

    fun updateUserPassword(emailUser: String, newPassword: String) {
        // Get User Data
        val user = mUserRepository.getUserByEmail(emailUser)

        // Update User Data
        user.password = AESEncryption.encrypt(newPassword)

        // Send Data to Database
        mUserRepository.update(user)

        // Send notification
        _notificationText.value = Event("Password user ${user.name} berhasil diganti")
    }

    fun getTopProduct(qty: Int): LiveData<List<Product>> = mProductRepository.getTopProducts(qty)

    fun getAllProduct(): LiveData<List<Product>> = mProductRepository.getAllProducts()

    fun getUserOrders(email: String): LiveData<List<UserOrder>> {
        // Get User Data
        val user = mUserRepository.getUserByEmail(email)

        // Return User Orders
        return mOrderRepository.getUserOrders(user.id)
    }

    fun cancelOrder(userOrder: UserOrder) {
        // Save data of product
        val qty = userOrder.detailOrder.qty
        val product = userOrder.product

        // Delete Order on Database
        mOrderRepository.delete(userOrder.detailOrder)

        // Update stock of product
        mProductRepository.updateStock(product, qty, true)

        // Send notification
        _notificationText.value = Event("Berhasil membatalkan pesanan ${qty}x ${product.name}")
    }

    fun getTopHospital(qty: Int): LiveData<List<Hospital>> = mHospitalRepository.getTopHospital(qty)

    fun getAllHospital(): LiveData<List<Hospital>> = mHospitalRepository.getAllHospital()
}