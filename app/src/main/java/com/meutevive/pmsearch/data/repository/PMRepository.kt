package com.meutevive.pmsearch.data.repository

import com.meutevive.pmsearch.models.PM

interface PMRepository {
    fun registerPM(pm: PM, callback: (success: Boolean) -> Unit)
    fun savePM(pm: PM, callback: (success: Boolean) -> Unit)

}
