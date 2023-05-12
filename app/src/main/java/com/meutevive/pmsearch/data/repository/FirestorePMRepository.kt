package com.meutevive.pmsearch.data.repository


import com.google.firebase.firestore.FirebaseFirestore
import com.meutevive.pmsearch.models.PM

class FirestorePMRepository : PMRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    override fun registerPM(pm: PM, callback: (success: Boolean) -> Unit) {
        firestore.collection("LesPM")
            .add(pm)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }


}
