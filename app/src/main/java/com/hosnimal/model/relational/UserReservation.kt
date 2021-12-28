package com.hosnimal.model.relational

import androidx.room.Embedded
import androidx.room.Relation
import com.hosnimal.model.Hospital
import com.hosnimal.model.Reservation

data class UserReservation (
    @Embedded
    var detailReservation: Reservation,

    @Relation(
        parentColumn = "hospital_id",
        entity = Hospital::class,
        entityColumn = "id"
    )
    var hospital: Hospital
)