package com.hosnimal.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.format.DateTimeFormatter
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

    companion object {
        // Format Key
        private val formatDateTime = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        private val formatTime = DateTimeFormatter.ISO_OFFSET_TIME

        @TypeConverter
        @JvmStatic
        fun restoreOffsetDateTime(value: String?): OffsetDateTime? {
            return value?.let {
                return formatDateTime.parse(value, OffsetDateTime::from)
            }
        }

        @TypeConverter
        @JvmStatic
        fun saveOffsetDateTime(date: OffsetDateTime?): String? {
            return date?.format(formatDateTime)
        }

        @TypeConverter
        @JvmStatic
        fun restoreOffsetTime(value: String?): OffsetTime? {
            return value?.let {
                return formatTime.parse(value, OffsetTime::from)
            }
        }

        @TypeConverter
        @JvmStatic
        fun saveOffsetTime(date: OffsetTime?): String? {
            return date?.format(formatTime)
        }
    }
}