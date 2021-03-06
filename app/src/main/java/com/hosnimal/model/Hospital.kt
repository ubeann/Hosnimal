package com.hosnimal.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.OffsetTime

@Parcelize
@Entity(tableName = "hospital")
data class Hospital (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "name")
    var name: String,

    @ColumnInfo(name = "open")
    var open: OffsetTime,

    @ColumnInfo(name = "close")
    var close: OffsetTime,

    @ColumnInfo(name = "location")
    var location: String,

    @ColumnInfo(name = "instagram")
    var instagram: String? = null,

    @ColumnInfo(name = "x")
    var x: Double,

    @ColumnInfo(name = "y")
    var y: Double,

    @ColumnInfo(name = "images")
    var images: List<String>
) : Parcelable