package com.meutevive.pmsearch.data.repository


import android.util.Log
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

    //save pm
    override fun savePM(pm: PM, callback: (success: Boolean) -> Unit) {
        // Get a reference to your Firestore database
        val db = FirebaseFirestore.getInstance()

        // Get a reference to the specific PM document in the database
        val pmRef = db.collection("LesPM").document(pm.id)

        // Set the PM document to the updated PM
        pmRef.set(pm)
            .addOnSuccessListener {
                Log.d("EditPMActivity", "PM successfully updated!")
                callback(true)
            }
            .addOnFailureListener { e ->
                Log.w("EditPMActivity", "Error updating PM", e)
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
