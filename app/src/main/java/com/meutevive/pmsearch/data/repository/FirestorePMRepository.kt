package com.meutevive.pmsearch.data.repository


import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayoutStates.TAG
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

    //search pm
    fun searchPM(query: String, callback: (List<PM>, Any?) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        // First, we search by pmNumber
        db.collection("LesPM")
            .orderBy("pmNumber")
            .startAt(query)
            .endAt(query+"\uf8ff")
            .get()
            .addOnSuccessListener { documents ->
                val pms = documents.mapNotNull { it.toObject(PM::class.java) }
                callback(pms, null)
            }
            .addOnFailureListener { exception ->
                callback(listOf(), exception)
            }
    }



}
