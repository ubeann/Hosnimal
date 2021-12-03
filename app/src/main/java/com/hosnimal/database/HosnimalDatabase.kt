package com.hosnimal.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.hosnimal.database.dao.UserDao
import com.hosnimal.model.User

@Database(entities = [User::class], version = 1, exportSchema = true)
abstract class HosnimalDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE : HosnimalDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): HosnimalDatabase {
            if (INSTANCE == null) {
                synchronized(HosnimalDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        HosnimalDatabase::class.java, "hosnimal_database")
                        .build()
                }
            }
            return INSTANCE as HosnimalDatabase
        }
    }
}