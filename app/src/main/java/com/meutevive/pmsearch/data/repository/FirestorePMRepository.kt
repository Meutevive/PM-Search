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

    //get all PM from firestore
    override fun getAllPMs(callback: (pmList: List<PM>?) -> Unit) {
        pmCollection.get().addOnSuccessListener { documents ->
            val pmList = documents.mapNotNull { document ->
                document.toObject(PM::class.java)
            }
            callback(pmList)
        }.addOnFailureListener {
            callback(null)
        }
    }
}
