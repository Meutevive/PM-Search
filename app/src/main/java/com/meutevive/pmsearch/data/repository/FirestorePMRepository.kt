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
        db.collection("pms")
            .whereEqualTo("pmNumber", query)
            .get()
            .addOnSuccessListener { documents ->
                val pms = documents.mapNotNull { it.toObject(PM::class.java) }
                if (pms.isNotEmpty()) {
                    // If we found PMs by pmNumber, we return these results
                    callback(pms)
                } else {
                    // If no PM was found by pmNumber, we search by city
                    db.collection("pms")
                        .whereEqualTo("city", query)
                        .get()
                        .addOnSuccessListener { cityDocuments ->
                            val cityPms = cityDocuments.mapNotNull { it.toObject(PM::class.java) }
                            // Return the results of the city search
                            callback(cityPms)
                        }
                        .addOnFailureListener { exception ->
                            Log.w(TAG, "Error getting documents: ", exception)
                        }
                }
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
    }




}
