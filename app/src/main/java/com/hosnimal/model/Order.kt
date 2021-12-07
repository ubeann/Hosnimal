package com.hosnimal.model

import android.os.Parcelable
import androidx.room.*
import androidx.room.ForeignKey.CASCADE
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(
    tableName = "order",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = CASCADE
        )
    ]
)
data class Order (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "user_id")
    var userId: Int,

    @ColumnInfo(name = "product_id")
    var productId: Int,

    @ColumnInfo(name = "qty")
    var qty: Int,

    @ColumnInfo(name = "order_at")
    var orderAt: Date
) : Parcelable