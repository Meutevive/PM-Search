package com.meutevive.pmsearch.screens.manage

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.meutevive.pmsearch.R
import com.meutevive.pmsearch.data.repository.FirestorePMRepository
import com.meutevive.pmsearch.models.PM
import com.meutevive.pmsearch.screens.edit.EditPMActivity
import com.meutevive.pmsearch.screens.register.RegisterPMActivity

class PMDetailsActivity : AppCompatActivity() {

    private lateinit var pm: PM
    private val repository = FirestorePMRepository()
    private lateinit var pmNameTextView: TextView
    private lateinit var pmDetailTextView: TextView
    private lateinit var pmAdresse: TextView
    private lateinit var pmCity: TextView
    private lateinit var pmImageView: ImageView
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var addButton: FloatingActionButton
    private lateinit var routeButton: FloatingActionButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pm_details)
        supportActionBar?.hide()

        // retrieve the PM object from intent
        val pmId = intent.getStringExtra("PM_ID")

        if (pmId != null) {
            loadPMData(pmId)
        } else {
            Toast.makeText(this, "PM ID is missing", Toast.LENGTH_SHORT).show()
        }

        initializeViews()

    }

    private fun initializeViews() {
        pmNameTextView = findViewById(R.id.pm_number)
        pmDetailTextView = findViewById(R.id.pm_comment)
        pmAdresse = findViewById(R.id.pm_address)
        pmCity = findViewById(R.id.pm_city)
        pmImageView = findViewById(R.id.pm_photo)
        editButton = findViewById(R.id.edit_button)
        deleteButton = findViewById(R.id.delete_button)
        addButton = findViewById(R.id.add_pm)
        routeButton = findViewById(R.id.route_button)

        val states = arrayOf(
            intArrayOf(android.R.attr.state_enabled), // enabled
            intArrayOf(-android.R.attr.state_enabled) // disabled
        )

        val colors = intArrayOf(
            ContextCompat.getColor(this, R.color.red), // enabled collor red
            Color.argb((0.6f * 255).toInt(), 213, 26, 26) // disabled

        )

        val colorStateList = ColorStateList(states, colors)

        ViewCompat.setBackgroundTintList(deleteButton, colorStateList)


        // handle click on the edit button
        editButton.setOnClickListener {
            val editIntent = Intent(this, EditPMActivity::class.java)
            editIntent.putExtra("pm", pm)
            startActivity(editIntent)
        }

        // handle click on the delete button
        deleteButton.setOnClickListener {
            pm.id?.let { pmId ->
                FirestorePMRepository().deletePM(pmId) { success ->
                    if (success) {
                        // PM was deleted successfully
                        Toast.makeText(this, "PM deleted", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        // PM could not be deleted
                        Toast.makeText(this, "Could not delete PM", Toast.LENGTH_SHORT).show()
                    }
                }
            } ?: run {
                // PM ID is null
                Toast.makeText(this, "PM ID is missing", Toast.LENGTH_SHORT).show()
            }
        }

        // handle click on the add button
        addButton.setOnClickListener {
            // start the add PM activity
            val addIntent = Intent(this, RegisterPMActivity::class.java)
            startActivity(addIntent)
        }

        // New - handle click on the route button
        routeButton.setOnClickListener {
            // start the navigation to the PM location
            val gmmIntentUri = Uri.parse("geo:0,0?q=${pm.address}")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(this, "No map application found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateButtonVisibility(role: String) {
        if (role == "admin") {
            deleteButton.visibility = View.VISIBLE
            editButton.visibility = View.VISIBLE
        } else {
            deleteButton.visibility = View.GONE
            editButton.visibility = View.GONE
        }
    }

    private fun loadPMData(pmId: String) {
        repository.getPM(pmId) { pm ->
            this.pm = pm // update local PM object

            pmNameTextView.text = pm.pmNumber
            pmDetailTextView.text = pm.comment
            pmAdresse.text = pm.address

            // load image with Glide
            Glide.with(this)
                .load(pm.photoUrl)
                .into(pmImageView)

            // Get the ID of the currently logged in user
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            // If the user is logged in (i.e., userId is not null), fetch their role
            if (userId != null) {
                // Assuming that your repository has a method getUserRole(userId: String, callback: (role: String) -> Unit)
                // that fetches the role of the user based on their user ID.
                repository.getUserRole(userId) { role ->
                    updateButtonVisibility(role)
                }
            }
        }
    }

}
