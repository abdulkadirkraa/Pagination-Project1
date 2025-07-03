package com.abdulkadirkara.paginationsimple.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Id(
    val name: String? = null,
    val value: String? = null
) : Parcelable