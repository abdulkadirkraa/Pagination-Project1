package com.abdulkadirkara.paginationsimple.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class İd(
    val name: String,
    val value: String
) : Parcelable