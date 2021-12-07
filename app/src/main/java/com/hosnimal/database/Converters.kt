package com.hosnimal.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class Converters {
    @TypeConverter
    fun restoreList(listOfString: String?): List<String?>? {
        return Gson().fromJson(listOfString, object : TypeToken<List<String?>?>() {}.type)
    }

    @TypeConverter
    fun saveList(listOfString: List<String?>?): String? {
        return Gson().toJson(listOfString)
    }

    @TypeConverter
    fun restoreDate(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun saveDate(date: Date?): Long? {
        return date?.time
    }
}