package com.meutevive.pmsearch.data.repository

import android.net.Uri
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.meutevive.pmsearch.models.PM
import kotlinx.coroutines.tasks.await

class FirebasePMRepository : PMRepository {

    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun registerPM(pm: PM, callback: (success: Boolean) -> Unit) {
        val pmId = database.child("PM").push().key
        if (pmId == null) {
            callback(false)
            return
        }

        pm.id = pmId
        database.child("PM").child(pmId).setValue(pm)
            .addOnCompleteListener { task ->
                callback(task.isSuccessful)
            }

    }


}
