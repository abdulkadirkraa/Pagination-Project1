package com.abdulkadirkara.paginationsimple.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Timezone(
    val description: String,
    val offset: String
) : Parcelable