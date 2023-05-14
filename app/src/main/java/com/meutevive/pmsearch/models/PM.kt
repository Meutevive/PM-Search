package com.meutevive.pmsearch.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class PM(
    var id: String = "",
    var pmNumber: String = "",
    var city: String = "",
    var address: String = "",
    var comment: String = "",
    var date: Long = 0L,
    var photoUrl: String = ""
) : Parcelable
