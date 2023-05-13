package com.meutevive.pmsearch.data.repository


import com.google.firebase.firestore.FirebaseFirestore
import com.meutevive.pmsearch.models.PM

class FirestorePMRepository : PMRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val pmCollection = firestore.collection("LesPM")

    override fun registerPM(pm: PM, callback: (success: Boolean) -> Unit) {
        pmCollection.add(pm)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }


    //delete pm
    fun deletePM(pmId: String, callback: (success: Boolean) -> Unit) {
        pmCollection.document(pmId)
            .delete()
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }



}
