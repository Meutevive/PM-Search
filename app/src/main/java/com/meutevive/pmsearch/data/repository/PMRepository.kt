package com.meutevive.pmsearch.data.repository

import com.meutevive.pmsearch.models.PM

interface PMRepository {
    fun registerPM(pm: PM, callback: (Boolean, String?) -> Unit)
    fun updatePM(pm: PM, callback: (success: Boolean) -> Unit)

}
