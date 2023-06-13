package com.meutevive.pmsearch.data.repository


import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayoutStates.TAG
import com.google.firebase.firestore.FirebaseFirestore
import com.meutevive.pmsearch.models.PM

class FirestorePMRepository : PMRepository {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val pmCollection = firestore.collection("LesPM")




    override fun registerPM(pm: PM, callback: (success: Boolean, id: String?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        val documentRef = db.collection("LesPM").document() // Create new document with auto generated id
        pm.id = documentRef.id // Update the id of PM with the Firestore document id
        documentRef
            .set(pm)
            .addOnSuccessListener { callback(true, pm.id) }
            .addOnFailureListener { callback(false, null) }
    }





    //update pm
    override fun updatePM(pm: PM, callback: (success: Boolean) -> Unit) {
        // Get a reference to your Firestore database
        val db = FirebaseFirestore.getInstance()

        // Get a reference to the specific PM document in the database
        val pmRef = pm.id?.let { db.collection("LesPM").document(it) }

        // Set the PM document to the updated PM
        if (pmRef != null) {
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

    fun searchPM(query: String, callback: (List<PM>?, Exception?) -> Unit) {
        val db = FirebaseFirestore.getInstance()
        db.collection("LesPM")
            .orderBy("pmNumber")
            .startAt(query)
            .endAt(query+"\uf8ff")
            .get()
            .addOnSuccessListener { documents ->
                val pms = documents.toObjects(PM::class.java)
                if (pms.isEmpty()) {
                    callback(emptyList(), null)
                } else {
                    callback(pms, null)
                }
            }
            .addOnFailureListener { exception ->
                callback(null, exception)
            }
    }

    //get all pms
    fun getPM(pmId: String, callback: (PM) -> Unit) {
        firestore.collection("LesPM").document(pmId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val pm = document.toObject(PM::class.java)
                    callback(pm!!)
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }











}
