package com.hosnimal.model.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.hosnimal.model.Order
import com.hosnimal.model.Product

data class UserOrder(
    @Embedded
    var detailOrder: Order,

    @Relation(
        parentColumn = "product_id",
        entity = Product::class,
        entityColumn = "id"
    )
    var product: Product
)