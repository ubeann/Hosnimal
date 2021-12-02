package com.hosnimal.repository

import android.app.Application
import androidx.lifecycle.LiveData
import com.hosnimal.database.HosnimalDatabase
import com.hosnimal.database.dao.UserDao
import com.hosnimal.model.User
import kotlinx.coroutines.runBlocking
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UserRepository(application: Application) {
    private val mUserDao: UserDao
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        val db = HosnimalDatabase.getDatabase(application)
        mUserDao = db.userDao()
    }

    fun getAllUsers(): LiveData<List<User>> = mUserDao.getAllUsers()

    fun getUserByEmail(email: String): User = runBlocking { mUserDao.getUserByEmail(email) }

    fun isUserRegistered(email: String): Boolean = runBlocking { mUserDao.isUserRegistered(email) }

    fun insert(user: User) {
        executorService.execute { mUserDao.insert(user) }
    }

    fun update(user: User) {
        executorService.execute { mUserDao.update(user) }
    }

    fun delete(user: User) {
        executorService.execute { mUserDao.delete(user) }
    }
}