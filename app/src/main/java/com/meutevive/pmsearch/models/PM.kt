package com.meutevive.pmsearch.models

import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Parcelize
data class PM(
    var id: String? = null,
    var pmNumber: String = "",
    var address: String = "",
    var comment: String = "",
    var date: Long = 0L,
    var photoUrl: String = "",
    var signalements: MutableList<String> = mutableListOf()
) : Parcelable
