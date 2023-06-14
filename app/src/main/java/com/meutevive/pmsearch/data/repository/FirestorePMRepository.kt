package com.meutevive.pmsearch.data.repository


import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayoutStates.TAG
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.meutevive.pmsearch.models.PM

class FirestorePMRepository : PMRepository {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    // Register a new PM
    override fun registerPM(pm: PM, callback: (success: Boolean, id: String?) -> Unit) {
        val reference = database.getReference("LesPM").push() // Create new node with auto generated id
        pm.id = reference.key // Update the id of PM with the Firebase node key
        reference.setValue(pm)
            .addOnSuccessListener { callback(true, pm.id) }
            .addOnFailureListener { callback(false, null) }
    }

    // Update an existing PM
    override fun updatePM(pm: PM, callback: (success: Boolean) -> Unit) {
        pm.id?.let {
            database.getReference("LesPM").child(it).setValue(pm)
                .addOnSuccessListener { callback(true) }
                .addOnFailureListener { callback(false) }
        }
    }

    // Delete a PM
    fun deletePM(pmId: String, callback: (success: Boolean) -> Unit) {
        database.getReference("LesPM").child(pmId)
            .removeValue()
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    // Search for PMs
    fun searchPM(query: String, callback: (List<PM>?, Exception?) -> Unit) {
        val ref = database.getReference("LesPM")
        ref.orderByChild("pmNumber").startAt(query).endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val pms = snapshot.children.mapNotNull { it.getValue(PM::class.java) }
                    if (pms.isEmpty()) {
                        callback(emptyList(), null)
                    } else {
                        callback(pms, null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null, error.toException())
                }
            })
    }

    // Get a PM by id
    fun getPM(pmId: String, callback: (PM) -> Unit) {
        database.getReference("LesPM").child(pmId).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.exists()) {
                    val pm = snapshot.getValue(PM::class.java)
                    if (pm != null) {
                        callback(pm)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }


}
