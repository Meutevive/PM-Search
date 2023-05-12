package com.meutevive.pmsearch.data.repository

import com.meutevive.pmsearch.models.PM

interface PMRepository {
    fun registerPM(pm: PM, callback: (success: Boolean) -> Unit)
    fun getAllPMs(callback: (pmList: List<PM>?) -> Unit)
}
