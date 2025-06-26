package com.abdulkadirkara.paginationsimple.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Result(
    val cell: String,
    val dob: Dob,
    val email: String,
    val gender: String,
    val id: İd,
    val location: Location,
    val login: Login,
    val name: Name,
    val nat: String,
    val phone: String,
    val picture: Picture,
    val registered: Registered
) : Parcelable