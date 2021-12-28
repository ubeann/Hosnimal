package com.hosnimal.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.time.OffsetDateTime

@Parcelize
@Entity(
    tableName = "reservation",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = Hospital::class,
            parentColumns = ["id"],
            childColumns = ["hospital_id"],
            onDelete = CASCADE
        )
    ]
)
data class Reservation (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "user_id")
    var userId: Int,

    @ColumnInfo(name = "hospital_id")
    var hospitalId: Int,

    @ColumnInfo(name = "start")
    var start: OffsetDateTime,

    @ColumnInfo(name = "end")
    var end: OffsetDateTime
) : Parcelable