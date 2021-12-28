package com.hosnimal.model.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.hosnimal.model.Reservation
import com.hosnimal.model.User

data class HospitalReservation(
    @Embedded
    var detailReservation: Reservation,

    @Relation(
        parentColumn = "user_id",
        entity = User::class,
        entityColumn = "id"
    )
    var user: User
)